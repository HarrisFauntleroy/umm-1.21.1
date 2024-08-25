package com.harrisfauntleroy.leap.spell;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class FlightSpell implements Spell {
    private static final int FLIGHT_DURATION = 600; // 30 seconds
    private static final int PARTICLE_COUNT = 50;
    private static final int COOLDOWN_TICKS = 1200; // 60 seconds

    @Override
    public void cast(ServerLevel level, Player player, Vec3 startPos, Vec3 endPos) {
        playSound(level, player);
        createParticleEffect(level, player);
        applyFlightEffect(player);
    }

    @Override
    public int getCooldown() {
        return COOLDOWN_TICKS;
    }

    @Override
    public String getName() {
        return "Volatus";
    }

    private void playSound(ServerLevel level, Player player) {
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.ELYTRA_FLYING, SoundSource.PLAYERS, 1.0F, 1.0F);
    }

    private void createParticleEffect(ServerLevel level, Player player) {
        for (int i = 0; i < PARTICLE_COUNT; i++) {
            double offsetX = (Math.random() - 0.5) * 2;
            double offsetY = Math.random() * 2;
            double offsetZ = (Math.random() - 0.5) * 2;

            level.sendParticles(ParticleTypes.END_ROD,
                    player.getX() + offsetX, player.getY() + offsetY, player.getZ() + offsetZ,
                    1, 0, 0, 0, 0.05);
        }
    }

    private void applyFlightEffect(Player player) {
        player.addEffect(new MobEffectInstance(MobEffects.LEVITATION, FLIGHT_DURATION, 0, false, false));
        player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, FLIGHT_DURATION + 100, 0, false, false));
    }
}