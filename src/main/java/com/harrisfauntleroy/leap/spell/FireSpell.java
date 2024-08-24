package com.harrisfauntleroy.leap.spell;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.sounds.SoundEvent;

public class FireSpell extends SpellBeam {
    private static final int FIRE_DURATION = 5; // 5 seconds
    private static final int DAMAGE_DURATION = 60; // 3 seconds of damage effect
    private static final int DAMAGE_AMPLIFIER = 1; // Amplifier for the damage effect

    @Override
    protected void onEntityHit(ServerLevel level, Player player, EntityHitResult hitResult) {
        Entity entity = hitResult.getEntity();
        if (entity instanceof LivingEntity target) {
            // Set the entity on fire
            target.setRemainingFireTicks(FIRE_DURATION * 20); // Convert seconds to ticks
            // Apply damage over time effect
            target.addEffect(new MobEffectInstance(MobEffects.HARM, DAMAGE_DURATION, DAMAGE_AMPLIFIER));
            // Create hit effect
            createEntityHitEffect(level, target, ParticleTypes.LAVA);
        }
    }

    @Override
    protected void onBlockHit(ServerLevel level, Player player, BlockHitResult hitResult) {
        BlockPos hitPos = hitResult.getBlockPos().relative(hitResult.getDirection());

        if (BaseFireBlock.canBePlacedAt(level, hitPos, hitResult.getDirection())) {
            // Set the block on fire
            level.setBlockAndUpdate(hitPos, BaseFireBlock.getState(level, hitPos));
            // Create hit effect
            createBlockHitEffect(level, hitPos, ParticleTypes.LAVA);
        }
    }

    @Override
    protected ParticleOptions getParticle() {
        return ParticleTypes.FLAME;
    }

    @Override
    protected SoundEvent getSound() {
        return SoundEvents.BLAZE_SHOOT;
    }

    @Override
    public int getCooldown() {
        return 60; // 3 second cooldown
    }

    @Override
    public String getName() {
        return "Fire";
    }
}