package com.harrisfauntleroy.leap.spell;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class FlightSpell implements Spell {
    private static final int BASE_FLIGHT_DURATION = 600; // 30 seconds
    private static final int PARTICLE_COUNT = 50;
    private static final int COOLDOWN_TICKS = 1200; // 60 seconds
    private static final int MIN_LEVEL = 10;
    private static final float BASE_STRENGTH = 1.0F;
    private static final float STRENGTH_PER_LEVEL = 0.1F;

    @Override
    public void cast(ServerLevel level, Player player, Vec3 startPos, Vec3 endPos) {
        if (!canCast(player)) {
            player.displayClientMessage(Component.literal("You can't cast this spell yet."), true);
            return;
        }

        float strength = getSpellStrength(player);
        playSound(level, player);
        createParticleEffect(level, player);
        applyFlightEffect(player, strength);
    }

    @Override
    public int getCooldown() {
        return COOLDOWN_TICKS;
    }

    @Override
    public String getName() {
        return "Volatus (Flight Spell)";
    }

    @Override
    public boolean canCast(Player player) {
        return player.experienceLevel >= MIN_LEVEL;
    }

    @Override
    public float getSpellStrength(Player player) {
        return BASE_STRENGTH + (player.experienceLevel - MIN_LEVEL) * STRENGTH_PER_LEVEL;
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

    private void applyFlightEffect(Player player, float strength) {
        int flightDuration = (int) (BASE_FLIGHT_DURATION * strength);
        player.addEffect(new MobEffectInstance(MobEffects.LEVITATION, flightDuration, 0, false, false));
        player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, flightDuration + 100, 0, false, false));
    }
}