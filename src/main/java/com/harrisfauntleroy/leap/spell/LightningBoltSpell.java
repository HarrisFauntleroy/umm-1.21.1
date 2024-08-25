package com.harrisfauntleroy.leap.spell;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class LightningBoltSpell extends SpellBeam {
    private static final int COOLDOWN_TICKS = 200; // 10 seconds
    private static final int MIN_LEVEL = 15;
    private static final float BASE_STRENGTH = 1.0F;
    private static final float STRENGTH_PER_LEVEL = 0.1F;

    @Override
    protected void onEntityHit(ServerLevel level, Player player, EntityHitResult hitResult, float strength) {
        Vec3 hitPos = hitResult.getLocation();
        spawnLightning(level, player, hitPos, strength);
    }

    @Override
    protected void onBlockHit(ServerLevel level, Player player, BlockHitResult hitResult, float strength) {
        Vec3 hitPos = hitResult.getLocation();
        spawnLightning(level, player, hitPos, strength);
    }

    private void spawnLightning(ServerLevel level, Player player, Vec3 position, float strength) {
        int lightningCount = Math.max(1, (int) strength);
        for (int i = 0; i < lightningCount; i++) {
            LightningBolt lightningBolt = EntityType.LIGHTNING_BOLT.create(level);
            if (lightningBolt != null) {
                lightningBolt.moveTo(position.add(randomOffset(strength)));
                level.addFreshEntity(lightningBolt);
            }
        }
        level.sendParticles(ParticleTypes.FLASH, position.x, position.y, position.z, 10, 0.5, 0.5, 0.5, 0.1);
        level.playSound(null, position.x, position.y, position.z, SoundEvents.LIGHTNING_BOLT_THUNDER, player.getSoundSource(), 1.0F, 1.0F);
    }

    private Vec3 randomOffset(float strength) {
        double radius = strength * 2;
        double x = (Math.random() - 0.5) * radius;
        double z = (Math.random() - 0.5) * radius;
        return new Vec3(x, 0, z);
    }

    @Override
    protected ParticleOptions getParticle() {
        return ParticleTypes.ELECTRIC_SPARK;
    }

    @Override
    protected SoundEvent getSound() {
        return SoundEvents.LIGHTNING_BOLT_THUNDER;
    }

    @Override
    public int getCooldown() {
        return COOLDOWN_TICKS;
    }

    @Override
    public String getName() {
        return "Fulgur (Lightning Bolt Spell)";
    }

    @Override
    public boolean canCast(Player player) {
        return player.experienceLevel >= MIN_LEVEL;
    }

    @Override
    public float getSpellStrength(Player player) {
        return BASE_STRENGTH + (player.experienceLevel - MIN_LEVEL) * STRENGTH_PER_LEVEL;
    }
}