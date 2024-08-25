package com.harrisfauntleroy.leap.spell;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class MiningBeamSpell extends SpellBeam {
    private static final int BASE_BEAM_LENGTH = 10;
    private static final int BASE_MINING_SPEED = 10; // Ticks between each block break (adjust for balance)
    private static final int COOLDOWN_TICKS = 20; // 1 second cooldown
    private static final int MIN_LEVEL = 5;
    private static final float BASE_STRENGTH = 1.0F;
    private static final float STRENGTH_PER_LEVEL = 0.2F;

    private BlockPos currentMiningPos;
    private int miningProgress;

    @Override
    public void cast(ServerLevel level, Player player, Vec3 startPos, Vec3 endPos) {
        super.cast(level, player, startPos, endPos);
        startMining(level, player);
    }

    @Override
    protected void onEntityHit(ServerLevel level, Player player, EntityHitResult hitResult, float strength) {
        // Mining beam doesn't affect entities
    }

    @Override
    protected void onBlockHit(ServerLevel level, Player player, BlockHitResult hitResult, float strength) {
        currentMiningPos = hitResult.getBlockPos();
        miningProgress = 0;
    }

    @Override
    protected ParticleOptions getParticle() {
        return ParticleTypes.CRIT;
    }

    @Override
    protected SoundEvent getSound() {
        return SoundEvents.STONE_BREAK;
    }

    @Override
    public int getCooldown() {
        return COOLDOWN_TICKS;
    }

    @Override
    public String getName() {
        return "Effodio (Mining Spell)";
    }

    @Override
    public boolean canCast(Player player) {
        return player.experienceLevel >= MIN_LEVEL;
    }

    @Override
    public float getSpellStrength(Player player) {
        return BASE_STRENGTH + (player.experienceLevel - MIN_LEVEL) * STRENGTH_PER_LEVEL;
    }

    private void startMining(ServerLevel level, Player player) {
        float strength = getSpellStrength(player);
        int miningSpeed = (int) (BASE_MINING_SPEED / strength);
        level.getServer().tell(new net.minecraft.server.TickTask(0, new Runnable() {
            @Override
            public void run() {
                if (currentMiningPos != null) {
                    mineBlock(level, player, strength);
                    level.getServer().tell(new net.minecraft.server.TickTask(miningSpeed, this));
                }
            }
        }));
    }

    private void mineBlock(ServerLevel level, Player player, float strength) {
        BlockState blockState = level.getBlockState(currentMiningPos);
        if (!blockState.isAir() && blockState.getDestroySpeed(level, currentMiningPos) >= 0) {
            miningProgress += 20 * strength; // Simulate enhanced mining speed
            level.destroyBlockProgress(player.getId(), currentMiningPos, (int) ((miningProgress / 200.0f) * 10));

            if (miningProgress >= 200) { // Block fully mined
                level.destroyBlock(currentMiningPos, true, player);
                miningProgress = 0;

                // Move to next block
                int beamLength = (int) (BASE_BEAM_LENGTH * strength);
                HitResult hitResult = getPlayerPOVHitResult(level, player, beamLength);
                if (hitResult.getType() == HitResult.Type.BLOCK) {
                    currentMiningPos = ((BlockHitResult) hitResult).getBlockPos();
                } else {
                    currentMiningPos = null;
                }
            }
        } else {
            currentMiningPos = null;
        }
    }

    private HitResult getPlayerPOVHitResult(ServerLevel level, Player player, double range) {
        Vec3 eyePosition = player.getEyePosition();
        Vec3 viewVector = player.getViewVector(1.0F);
        Vec3 endPos = eyePosition.add(viewVector.scale(range));
        return level.clip(new net.minecraft.world.level.ClipContext(eyePosition, endPos,
                net.minecraft.world.level.ClipContext.Block.OUTLINE,
                net.minecraft.world.level.ClipContext.Fluid.NONE, player));
    }
}