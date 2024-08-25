package com.harrisfauntleroy.leap.spell;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

public class BurySpell extends SpellBeam {
    private static final int COOLDOWN_TICKS = 20; // 1 second
    private static final int MIN_LEVEL = 4;
    private static final float BASE_STRENGTH = 0.2F;
    private static final float STRENGTH_PER_LEVEL = 0.2F;

    private static final int BASE_GLOWING_DURATION = 200; // 10 seconds
    private static final int BASE_BURY_DEPTH = 3;

    @Override
    protected void onEntityHit(ServerLevel level, Player player, EntityHitResult hitResult, float strength) {
        Entity entity = hitResult.getEntity();
        if (entity instanceof LivingEntity target) {
            buryEntity(level, target, strength);
            createEntityHitEffect(level, target, ParticleTypes.DUST_PLUME);
        }
    }

    @Override
    protected void onBlockHit(ServerLevel level, Player player, BlockHitResult hitResult, float strength) {
        // Bury spell doesn't affect blocks directly
        createBlockHitEffect(level, hitResult.getBlockPos(), ParticleTypes.DUST_PLUME);
    }

    @Override
    protected ParticleOptions getParticle() {
        return ParticleTypes.DUST_PLUME;
    }

    @Override
    protected SoundEvent getSound() {
        return SoundEvents.GRAVEL_FALL;
    }

    @Override
    public int getCooldown() {
        return COOLDOWN_TICKS;
    }

    @Override
    public String getName() {
        return "Sepelio (Bury Spell)";
    }

    @Override
    public boolean canCast(Player player) {
        return player.experienceLevel >= MIN_LEVEL;
    }

    @Override
    public float getSpellStrength(Player player) {
        return BASE_STRENGTH + (player.experienceLevel - MIN_LEVEL) * STRENGTH_PER_LEVEL;
    }

    private void buryEntity(ServerLevel level, LivingEntity entity, float strength) {
        BlockPos entityPos = entity.blockPosition();

        int glowingDuration = (int) (BASE_GLOWING_DURATION * strength);
        int buryDepth = (int) (BASE_BURY_DEPTH * strength);

        // Make the entity glow
        entity.addEffect(new MobEffectInstance(MobEffects.GLOWING, glowingDuration, 0));

        // Move the entity down
        entity.teleportTo(entityPos.getX(), entityPos.getY() - buryDepth, entityPos.getZ());
    }
}