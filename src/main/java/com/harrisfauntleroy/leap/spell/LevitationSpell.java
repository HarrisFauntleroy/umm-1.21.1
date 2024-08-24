package com.harrisfauntleroy.leap.spell;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class LevitationSpell implements Spell {
    private static final int LEVITATION_DURATION = 100; // 5 seconds
    private static final int LEVITATION_AMPLIFIER = 2;
    private static final int SLOW_FALLING_DURATION = 100; // 5 seconds
    private static final int PARTICLE_COUNT = 100;
    private static final double RANGE = 30.0; // Maximum range of the spell

    @Override
    public void cast(ServerLevel level, Player player, Vec3 startPos, Vec3 endPos) {
        playSound(level, player);
        Vec3 direction = endPos.subtract(startPos).normalize();
        Vec3 scaledEnd = startPos.add(direction.scale(RANGE));
        createParticleBeam(level, startPos, scaledEnd);
        applyEffectToTarget(level, player, startPos, scaledEnd);
    }

    @Override
    public int getCooldown() {
        return 100; // 5 second cooldown
    }

    @Override
    public String getName() {
        return "Levitation";
    }

    private void playSound(ServerLevel level, Player player) {
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.ENDER_EYE_LAUNCH, SoundSource.PLAYERS, 1.0F, 1.5F);
    }

    private void createParticleBeam(ServerLevel level, Vec3 startPos, Vec3 endPos) {
        for (int i = 0; i < PARTICLE_COUNT; i++) {
            double t = i / (double) PARTICLE_COUNT;
            Vec3 particlePos = startPos.lerp(endPos, t);
            level.sendParticles(ParticleTypes.REVERSE_PORTAL,
                    particlePos.x, particlePos.y, particlePos.z,
                    1, 0.1, 0.1, 0.1, 0);
        }
    }

    private void applyEffectToTarget(ServerLevel level, Player player, Vec3 startPos, Vec3 endPos) {
        AABB boundingBox = new AABB(startPos, endPos).inflate(1.0D);
        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, boundingBox,
                entity -> entity != player && entity.isAlive());

        for (LivingEntity target : entities) {
            if (player.hasLineOfSight(target)) {
                target.addEffect(new MobEffectInstance(MobEffects.LEVITATION, LEVITATION_DURATION, LEVITATION_AMPLIFIER));
                target.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, LEVITATION_DURATION + SLOW_FALLING_DURATION, 0));
                createHitEffect(level, target);
                break;
            }
        }
    }

    private void createHitEffect(ServerLevel level, LivingEntity target) {
        level.sendParticles(ParticleTypes.REVERSE_PORTAL,
                target.getX(), target.getY() + target.getBbHeight() / 2, target.getZ(),
                50, 0.5, 0.5, 0.5, 0.1);
    }
}