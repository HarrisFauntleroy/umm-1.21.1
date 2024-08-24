package com.harrisfauntleroy.leap.spell;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class MiningBeamSpell implements Spell {
    private static final int BEAM_LENGTH = 10;
    private static final int PARTICLE_COUNT = 50;

    @Override
    public void cast(ServerLevel level, Player player, Vec3 startPos, Vec3 endPos) {
        playSound(level, player);
        Vec3 direction = endPos.subtract(startPos).normalize();
        Vec3 scaledEnd = startPos.add(direction.scale(BEAM_LENGTH));
        createParticleBeam(level, startPos, scaledEnd);
        mineBlocks(level, startPos, scaledEnd);
    }

    @Override
    public int getCooldown() {
        return 20; // 1 second cooldown
    }

    @Override
    public String getName() {
        return "Mining Beam";
    }

    private void playSound(ServerLevel level, Player player) {
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.STONE_BREAK, SoundSource.PLAYERS, 1.0F, 1.0F);
    }

    private void createParticleBeam(ServerLevel level, Vec3 startPos, Vec3 endPos) {
        for (int i = 0; i < PARTICLE_COUNT; i++) {
            double t = i / (double) PARTICLE_COUNT;
            Vec3 particlePos = startPos.lerp(endPos, t);
            level.sendParticles(ParticleTypes.CRIT,
                    particlePos.x, particlePos.y, particlePos.z,
                    1, 0.1, 0.1, 0.1, 0);
        }
    }

    private void mineBlocks(ServerLevel level, Vec3 startPos, Vec3 endPos) {
        Vec3 direction = endPos.subtract(startPos).normalize();
        for (int i = 0; i < BEAM_LENGTH; i++) {
            Vec3 pos = startPos.add(direction.scale(i));
            BlockPos blockPos = new BlockPos((int) pos.x, (int) pos.y, (int) pos.z);
            BlockState blockState = level.getBlockState(blockPos);

            if (!blockState.isAir() && blockState.getDestroySpeed(level, blockPos) >= 0) {
                level.destroyBlock(blockPos, true);
            }
        }
    }
}