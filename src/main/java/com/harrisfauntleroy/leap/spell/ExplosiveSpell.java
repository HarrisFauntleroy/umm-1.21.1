package com.harrisfauntleroy.leap.spell;

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
    private static final float EXPLOSION_POWER = 4.0F;

    @Override
    protected void onEntityHit(ServerLevel level, Player player, EntityHitResult hitResult) {
        createExplosion(level, hitResult.getLocation());
    }

    @Override
    protected void onBlockHit(ServerLevel level, Player player, BlockHitResult hitResult) {
        createExplosion(level, hitResult.getLocation());
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
        return 200; // 10 second cooldown
    }

    @Override
    public String getName() {
        return "Explosive Orb";
    }

    private void createExplosion(ServerLevel level, Vec3 pos) {
        level.explode(null, pos.x, pos.y, pos.z, EXPLOSION_POWER, Level.ExplosionInteraction.TNT);
        level.sendParticles(ParticleTypes.EXPLOSION_EMITTER, pos.x, pos.y, pos.z, 1, 0, 0, 0, 0);
    }
}