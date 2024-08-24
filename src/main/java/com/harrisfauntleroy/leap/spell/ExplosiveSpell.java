package com.harrisfauntleroy.leap.spell;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ExplosiveSpell implements Spell {
    private static final float EXPLOSION_POWER = 4.0F;
    private static final int PARTICLE_COUNT = 100;
    private static final double MAX_RANGE = 30.0;

    @Override
    public void cast(ServerLevel level, Player player, Vec3 startPos, Vec3 endPos) {
        playSound(level, player);
        Vec3 direction = endPos.subtract(startPos).normalize();
        Vec3 explosionPos = startPos.add(direction.scale(MAX_RANGE));
        createParticleTrail(level, startPos, explosionPos);
        createExplosion(level, explosionPos);
    }

    @Override
    public int getCooldown() {
        return 200; // 10 second cooldown
    }

    @Override
    public String getName() {
        return "Explosive Orb";
    }

    private void playSound(ServerLevel level, Player player) {
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.FIREWORK_ROCKET_LAUNCH, SoundSource.PLAYERS, 1.0F, 0.5F);
    }

    private void createParticleTrail(ServerLevel level, Vec3 startPos, Vec3 endPos) {
        for (int i = 0; i < PARTICLE_COUNT; i++) {
            double t = i / (double) PARTICLE_COUNT;
            Vec3 particlePos = startPos.lerp(endPos, t);
            level.sendParticles(ParticleTypes.FLAME,
                    particlePos.x, particlePos.y, particlePos.z,
                    1, 0.1, 0.1, 0.1, 0.05);
        }
    }

    private void createExplosion(ServerLevel level, Vec3 pos) {
        level.explode(null, pos.x, pos.y, pos.z, EXPLOSION_POWER, Level.ExplosionInteraction.TNT);
        level.sendParticles(ParticleTypes.EXPLOSION_EMITTER,
                pos.x, pos.y, pos.z,
                10, 1.0, 1.0, 1.0, 0.1);
    }
}
