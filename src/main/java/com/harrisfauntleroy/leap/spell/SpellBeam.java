package com.harrisfauntleroy.leap.spell;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.ClipContext;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

public abstract class SpellBeam implements Spell {
    protected static final double MAX_DISTANCE = 30.0;
    protected static final int PARTICLE_COUNT = 50;

    @Override
    public void cast(ServerLevel level, Player player, Vec3 startPos, Vec3 endPos) {
        playSound(level, player);
        HitResult hitResult = getPlayerPOVHitResult(level, player, MAX_DISTANCE);
        Vec3 hitPos = hitResult.getLocation();
        createParticleBeam(level, startPos, hitPos);

        if (hitResult instanceof EntityHitResult entityHit) {
            onEntityHit(level, player, entityHit);
        } else if (hitResult instanceof BlockHitResult blockHit) {
            onBlockHit(level, player, blockHit);
        }
    }

    protected abstract void onEntityHit(ServerLevel level, Player player, EntityHitResult hitResult);
    protected abstract void onBlockHit(ServerLevel level, Player player, BlockHitResult hitResult);
    protected abstract ParticleOptions getParticle();
    protected abstract SoundEvent getSound();

    private void playSound(ServerLevel level, Player player) {
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                getSound(), SoundSource.PLAYERS, 1.0F, 1.0F);
    }

    private void createParticleBeam(ServerLevel level, Vec3 startPos, Vec3 endPos) {
        for (int i = 0; i < PARTICLE_COUNT; i++) {
            double t = i / (double) PARTICLE_COUNT;
            Vec3 particlePos = startPos.lerp(endPos, t);
            level.sendParticles(getParticle(),
                    particlePos.x, particlePos.y, particlePos.z,
                    1, 0.1, 0.1, 0.1, 0);
        }
    }

    protected void createEntityHitEffect(ServerLevel level, Entity target, ParticleOptions particle) {
        level.sendParticles(particle,
                target.getX(), target.getY() + target.getBbHeight() / 2, target.getZ(),
                20, 0.5, 0.5, 0.5, 0.1);
    }

    protected void createBlockHitEffect(ServerLevel level, BlockPos pos, ParticleOptions particle) {
        level.sendParticles(particle,
                pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                10, 0.2, 0.2, 0.2, 0.1);
    }

    private static HitResult getPlayerPOVHitResult(ServerLevel level, Player player, double range) {
        Vec3 eyePosition = player.getEyePosition();
        Vec3 viewVector = player.getViewVector(1.0F);
        Vec3 endPos = eyePosition.add(viewVector.scale(range));
        ClipContext clipContext = new ClipContext(eyePosition, endPos, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player);
        return level.clip(clipContext);
    }
}