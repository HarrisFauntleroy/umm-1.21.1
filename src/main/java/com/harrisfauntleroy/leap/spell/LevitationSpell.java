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
    private static final int LEVITATION_DURATION = 100; // 5 seconds
    private static final int LEVITATION_AMPLIFIER = 2;
    private static final int SLOW_FALLING_DURATION = 100; // 5 seconds
    private static final int GLOWING_DURATION = 200; // 10 seconds

    @Override
    protected void onEntityHit(ServerLevel level, Player player, EntityHitResult hitResult) {
        if (hitResult.getEntity() instanceof LivingEntity target) {
            applyLevitationEffect(target);
            createEntityHitEffect(level, target, ParticleTypes.REVERSE_PORTAL);
        }
    }

    @Override
    protected void onBlockHit(ServerLevel level, Player player, BlockHitResult hitResult) {
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
        return 100; // 5 second cooldown
    }

    @Override
    public String getName() {
        return "Levitation";
    }

    private void applyLevitationEffect(LivingEntity target) {
        target.addEffect(new MobEffectInstance(MobEffects.LEVITATION, LEVITATION_DURATION, LEVITATION_AMPLIFIER));
        target.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, LEVITATION_DURATION + SLOW_FALLING_DURATION, 0));
        target.addEffect(new MobEffectInstance(MobEffects.GLOWING, GLOWING_DURATION, 0));
    }
}