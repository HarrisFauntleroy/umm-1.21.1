package com.harrisfauntleroy.leap.spell;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class ExplosiveSpell extends SpellBeam {
    private static final int COOLDOWN_TICKS = 20; // 1 second
    private static final int MIN_LEVEL = 4;
    private static final float BASE_STRENGTH = 0.2F;
    private static final float STRENGTH_PER_LEVEL = 0.2F;

    private static final float BASE_EXPLOSION_POWER = 4.0F;

    @Override
    protected void onEntityHit(ServerLevel level, Player player, EntityHitResult hitResult, float strength) {
        createExplosion(level, hitResult.getLocation(), strength);
    }

    @Override
    protected void onBlockHit(ServerLevel level, Player player, BlockHitResult hitResult, float strength) {
        BlockPos hitPos = hitResult.getBlockPos().relative(hitResult.getDirection());

        createExplosion(level, hitResult.getLocation(), strength);
        createBlockHitEffect(level, hitPos, ParticleTypes.LAVA);
    }

    @Override
    protected ParticleOptions getParticle() {
        return ParticleTypes.FLAME;
    }

    @Override
    protected SoundEvent getSound() {
        return SoundEvents.FIREWORK_ROCKET_LAUNCH;
    }

    @Override
    public int getCooldown() {
        return COOLDOWN_TICKS;
    }

    @Override
    public String getName() {
        return "Deflagratio (Explosive Spell)";
    }

    @Override
    public boolean canCast(Player player) {
        return player.experienceLevel >= MIN_LEVEL;
    }

    @Override
    public float getSpellStrength(Player player) {
        return BASE_STRENGTH + (player.experienceLevel - MIN_LEVEL) * STRENGTH_PER_LEVEL;
    }

    private void createExplosion(ServerLevel level, Vec3 pos, float strength) {
        float explosionPower = BASE_EXPLOSION_POWER * strength;
        level.explode(null, pos.x, pos.y, pos.z, explosionPower, Level.ExplosionInteraction.TNT);
        level.sendParticles(ParticleTypes.EXPLOSION_EMITTER, pos.x, pos.y, pos.z, 1, 0, 0, 0, 0);
    }
}