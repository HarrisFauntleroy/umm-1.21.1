package com.harrisfauntleroy.leap.spell;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class FlightSpell extends AbstractSpell {
    private static final int BASE_DURATION = 600; // 30 seconds
    private static final int PARTICLE_COUNT = 50;
    private static final int MIN_LEVEL = 10;
    private static final int COOLDOWN_TICKS = 1200; // 60 seconds

    private static final int TIER_2_LEVEL = 20; // Level for elytra-like flight
    private static final int TIER_3_LEVEL = 30; // Level for launch ability

    private static final int LAUNCH_HEIGHT = 64; // Blocks to launch upward
    private static final float LAUNCH_SPEED = 2.0F; // Launch speed multiplier

    public FlightSpell() {
        super(MIN_LEVEL, COOLDOWN_TICKS);
    }

    @Override
    public String getName() {
        return "Volatus (Flight Spell)";
    }

    @Override
    protected void performSpellEffect(ServerLevel level, Player player, Vec3 startPos, Vec3 endPos, float strength) {
        playSound(level, player);
        createParticleEffect(level, player);

        int playerLevel = player.experienceLevel;
        if (playerLevel >= TIER_3_LEVEL) {
            applyTier3Effect(level, player, strength);
        } else if (playerLevel >= TIER_2_LEVEL) {
            applyTier2Effect(player, strength);
        } else {
            applyTier1Effect(player, strength);
        }
    }

    private void applyTier1Effect(Player player, float strength) {
        int duration = (int) (BASE_DURATION * strength);
        player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, duration, 0, false, false));
    }

    private void applyTier2Effect(Player player, float strength) {
        int duration = (int) (BASE_DURATION * strength);
        player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, duration, 0, false, false));
        if (player instanceof ServerPlayer) {
            ServerPlayer serverPlayer = (ServerPlayer) player;
            serverPlayer.startFallFlying();
        }
    }

    private void applyTier3Effect(ServerLevel level, Player player, float strength) {
        applyTier2Effect(player, strength);

        // Launch the player upward
        Vec3 motion = player.getDeltaMovement();
        Vec3 launchVector = new Vec3(motion.x, LAUNCH_SPEED, motion.z);
        player.setDeltaMovement(launchVector);
        player.hurtMarked = true; // Ensure the new motion is sent to clients

        // Create a trail of particles
        Vec3 playerPos = player.position();
        for (int y = 0; y < LAUNCH_HEIGHT; y++) {
            level.sendParticles(ParticleTypes.FIREWORK,
                    playerPos.x, playerPos.y + y, playerPos.z,
                    5, 0.5, 0, 0.5, 0.05);
        }

        // Play a sound for the launch
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.FIREWORK_ROCKET_LAUNCH, SoundSource.PLAYERS, 1.0F, 1.0F);
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
}