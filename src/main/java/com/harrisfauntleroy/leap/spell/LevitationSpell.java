package com.harrisfauntleroy.leap.spell;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

public class LevitationSpell extends SpellBeam {
    private static final int COOLDOWN_TICKS = 20; // 1 second
    private static final int MIN_LEVEL = 4;
    private static final float BASE_STRENGTH = 0.2F;
    private static final float STRENGTH_PER_LEVEL = 0.2F;

    private static final int BASE_LEVITATION_DURATION = 100; // 5 seconds
    private static final int BASE_LEVITATION_AMPLIFIER = 2;
    private static final int BASE_SLOW_FALLING_DURATION = 100; // 5 seconds
    private static final int BASE_GLOWING_DURATION = 200; // 10 seconds

    @Override
    protected void onEntityHit(ServerLevel level, Player player, EntityHitResult hitResult, float strength) {
        if (hitResult.getEntity() instanceof LivingEntity target) {
            applyLevitationEffect(target, strength);
            createEntityHitEffect(level, target, ParticleTypes.REVERSE_PORTAL);
        }
    }

    @Override
    protected void onBlockHit(ServerLevel level, Player player, BlockHitResult hitResult, float strength) {
        // Levitation spell doesn't affect blocks
        createBlockHitEffect(level, hitResult.getBlockPos(), ParticleTypes.REVERSE_PORTAL);
    }

    @Override
    protected ParticleOptions getParticle() {
        return ParticleTypes.REVERSE_PORTAL;
    }

    @Override
    protected SoundEvent getSound() {
        return SoundEvents.ENDER_EYE_LAUNCH;
    }

    @Override
    public int getCooldown() {
        return COOLDOWN_TICKS;
    }

    @Override
    public String getName() {
        return "Levitatio (Levitation Spell)";
    }

    @Override
    public boolean canCast(Player player) {
        return player.experienceLevel >= MIN_LEVEL;
    }

    @Override
    public float getSpellStrength(Player player) {
        return BASE_STRENGTH + (player.experienceLevel - MIN_LEVEL) * STRENGTH_PER_LEVEL;
    }

    private void applyLevitationEffect(LivingEntity target, float strength) {
        int levitationDuration = (int) (BASE_LEVITATION_DURATION * strength);
        int levitationAmplifier = (int) (BASE_LEVITATION_AMPLIFIER * strength);
        int slowFallingDuration = (int) (BASE_SLOW_FALLING_DURATION * strength);
        int glowingDuration = (int) (BASE_GLOWING_DURATION * strength);

        target.addEffect(new MobEffectInstance(MobEffects.LEVITATION, levitationDuration, levitationAmplifier));
        target.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, levitationDuration + slowFallingDuration, 0));
        target.addEffect(new MobEffectInstance(MobEffects.GLOWING, glowingDuration, 0));
    }
}