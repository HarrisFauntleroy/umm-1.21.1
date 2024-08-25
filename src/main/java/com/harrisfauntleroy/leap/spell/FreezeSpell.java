package com.harrisfauntleroy.leap.spell;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

public class FreezeSpell extends SpellBeam {
    private static final int COOLDOWN_TICKS = 20; // 1 second
    private static final int MIN_LEVEL = 4;
    private static final float BASE_STRENGTH = 0.2F;
    private static final float STRENGTH_PER_LEVEL = 0.2F;

    private static final int BASE_FREEZE_DURATION = 100; // 5 seconds
    private static final int BASE_FREEZE_RADIUS = 5;

    @Override
    protected void onEntityHit(ServerLevel level, Player player, EntityHitResult hitResult, float strength) {
        if (hitResult.getEntity() instanceof LivingEntity target) {
            freezeEntity(level, target, strength);
        }
    }

    @Override
    protected void onBlockHit(ServerLevel level, Player player, BlockHitResult hitResult, float strength) {
        BlockPos hitPos = hitResult.getBlockPos();
        freezeArea(level, hitPos, strength);
    }

    @Override
    protected ParticleOptions getParticle() {
        return ParticleTypes.SNOWFLAKE;
    }

    @Override
    protected SoundEvent getSound() {
        return SoundEvents.GLASS_BREAK;
    }

    @Override
    public int getCooldown() {
        return COOLDOWN_TICKS;
    }

    @Override
    public String getName() {
        return "Glacies (Freeze Spell)";
    }

    @Override
    public boolean canCast(Player player) {
        return player.experienceLevel >= MIN_LEVEL;
    }

    @Override
    public float getSpellStrength(Player player) {
        return BASE_STRENGTH + (player.experienceLevel - MIN_LEVEL) * STRENGTH_PER_LEVEL;
    }

    private void freezeEntity(ServerLevel level, LivingEntity entity, float strength) {
        int freezeDuration = (int) (BASE_FREEZE_DURATION * strength);
        entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, freezeDuration, 4));
        entity.addEffect(new MobEffectInstance(MobEffects.JUMP, freezeDuration, 128));

        BlockPos entityPos = entity.blockPosition();
        level.setBlockAndUpdate(entityPos, Blocks.ICE.defaultBlockState());
        level.setBlockAndUpdate(entityPos.above(), Blocks.ICE.defaultBlockState());

        createEntityHitEffect(level, entity, ParticleTypes.SNOWFLAKE);
    }

    private void freezeArea(ServerLevel level, BlockPos center, float strength) {
        int freezeRadius = (int) (BASE_FREEZE_RADIUS * strength);
        for (int x = -freezeRadius; x <= freezeRadius; x++) {
            for (int y = -freezeRadius; y <= freezeRadius; y++) {
                for (int z = -freezeRadius; z <= freezeRadius; z++) {
                    if (x * x + y * y + z * z <= freezeRadius * freezeRadius) {
                        BlockPos pos = center.offset(x, y, z);
                        if (level.getBlockState(pos).liquid()) {
                            level.setBlockAndUpdate(pos, Blocks.ICE.defaultBlockState());
                        } else if (level.getBlockState(pos).isAir()) {
                            level.setBlockAndUpdate(pos, Blocks.SNOW.defaultBlockState());
                        }
                    }
                }
            }
        }
        createBlockHitEffect(level, center, ParticleTypes.SNOWFLAKE);
    }
}