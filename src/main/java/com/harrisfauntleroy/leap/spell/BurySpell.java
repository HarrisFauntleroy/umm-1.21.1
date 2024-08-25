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
    private static final int GLOWING_DURATION = 200; // 10 seconds
    private static final int BURY_DEPTH = 3;
    private static final int COOLDOWN_TICKS = 100; // 5 seconds

    @Override
    protected void onEntityHit(ServerLevel level, Player player, EntityHitResult hitResult) {
        Entity entity = hitResult.getEntity();
        if (entity instanceof LivingEntity target) {
            buryEntity(level, target);
            createEntityHitEffect(level, target, ParticleTypes.DUST_PLUME);
        }
    }

    @Override
    protected void onBlockHit(ServerLevel level, Player player, BlockHitResult hitResult) {
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

    private void buryEntity(ServerLevel level, LivingEntity entity) {
        BlockPos entityPos = entity.blockPosition();

        // Make the entity glow
        entity.addEffect(new MobEffectInstance(MobEffects.GLOWING, GLOWING_DURATION, 0));

        // Move the entity down
        entity.teleportTo(entityPos.getX(), entityPos.getY() - BURY_DEPTH, entityPos.getZ());
    }
}