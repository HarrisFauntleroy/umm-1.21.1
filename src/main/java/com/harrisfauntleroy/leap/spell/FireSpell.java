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
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

public class FireSpell extends SpellBeam {
    private static final int COOLDOWN_TICKS = 20; // 1 second
    private static final int MIN_LEVEL = 4;
    private static final float BASE_STRENGTH = 0.2F;
    private static final float STRENGTH_PER_LEVEL = 0.2F;

    private static final int FIRE_DURATION = 5; // 5 seconds
    private static final int DAMAGE_DURATION = 60; // 3 seconds of damage effect
    private static final int DAMAGE_AMPLIFIER = 1; // Amplifier for the damage effect

    @Override
    protected void onEntityHit(ServerLevel level, Player player, EntityHitResult hitResult, float strength) {
        Entity entity = hitResult.getEntity();

        if (entity instanceof LivingEntity target) {
            int fireDuration = (int) (FIRE_DURATION * strength);
            int damageDuration = (int) (DAMAGE_DURATION * strength);
            int damageAmplifier = (int) (DAMAGE_AMPLIFIER * strength);

            target.setRemainingFireTicks(fireDuration * 20); // Convert seconds to ticks
            target.addEffect(new MobEffectInstance(MobEffects.HARM, damageDuration, damageAmplifier));
            createEntityHitEffect(level, target, ParticleTypes.LAVA);
        }
    }

    @Override
    protected void onBlockHit(ServerLevel level, Player player, BlockHitResult hitResult, float strength) {
        BlockPos hitPos = hitResult.getBlockPos().relative(hitResult.getDirection());

        if (BaseFireBlock.canBePlacedAt(level, hitPos, hitResult.getDirection())) {
            level.setBlockAndUpdate(hitPos, BaseFireBlock.getState(level, hitPos));
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
        return COOLDOWN_TICKS;
    }

    @Override
    public String getName() {
        return "Incendium (Fire Spell)";
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