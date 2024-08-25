package com.harrisfauntleroy.leap.spell;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;
import java.util.Random;

public abstract class SpellBeam implements Spell {
    protected static final double MAX_DISTANCE = 30.0;
    protected static final int PARTICLE_COUNT = 50;
    private static final double WAVE_AMPLITUDE = 0.4; // Maximum deviation from the straight line
    private static final double WAVE_FREQUENCY = 0.5; // How often the wave completes a full cycle

    @Override
    public void cast(ServerLevel level, Player player, Vec3 startPos, Vec3 endPos) {
        playSound(level, player);
        HitResult hitResult = getPlayerPOVHitResult(level, player, MAX_DISTANCE);
        Vec3 hitPos = hitResult.getLocation();
        createParticleBeam(level, startPos, hitPos);

        if (hitResult.getType() == HitResult.Type.ENTITY && hitResult instanceof EntityHitResult entityHit) {
            onEntityHit(level, player, entityHit);
        } else if (hitResult.getType() == HitResult.Type.BLOCK && hitResult instanceof BlockHitResult blockHit) {
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

    private void createParticleBeamSimple(ServerLevel level, Vec3 startPos, Vec3 endPos) {
        for (int i = 0; i < PARTICLE_COUNT; i++) {
            double t = i / (double) PARTICLE_COUNT;
            Vec3 particlePos = startPos.lerp(endPos, t);
            level.sendParticles(getParticle(),
                    particlePos.x, particlePos.y, particlePos.z,
                    1, 0.1, 0.1, 0.1, 0);
        }
    }

    private void createParticleBeam(ServerLevel level, Vec3 startPos, Vec3 endPos) {
        Vec3 beamVector = endPos.subtract(startPos);
        Vec3 beamDirection = beamVector.normalize();
        double beamLength = beamVector.length();

        // Create two perpendicular vectors for the wave effect
        Vec3 perpVector1 = beamDirection.cross(new Vec3(0, 1, 0)).normalize();
        Vec3 perpVector2 = beamDirection.cross(perpVector1).normalize();

        Random random = new Random();

        for (int i = 0; i < PARTICLE_COUNT; i++) {
            double t = i / (double) PARTICLE_COUNT;
            Vec3 basePos = startPos.add(beamVector.scale(t));

            // Calculate offsets using sine waves
            double offset1 = Math.sin(t * beamLength * WAVE_FREQUENCY * 2 * Math.PI) * WAVE_AMPLITUDE;
            double offset2 = Math.sin(t * beamLength * WAVE_FREQUENCY * 2 * Math.PI + Math.PI / 2) * WAVE_AMPLITUDE;

            // Apply random scaling to the offsets for more natural look
            offset1 *= random.nextDouble() * 0.5 + 0.5;
            offset2 *= random.nextDouble() * 0.5 + 0.5;

            Vec3 finalPos = basePos.add(perpVector1.scale(offset1)).add(perpVector2.scale(offset2));

            level.sendParticles(getParticle(),
                    finalPos.x, finalPos.y, finalPos.z,
                    1, 0.01, 0.01, 0.01, 0);
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

    private HitResult getPlayerPOVHitResult(ServerLevel level, Player player, double range) {
        Vec3 eyePosition = player.getEyePosition();
        Vec3 viewVector = player.getViewVector(1.0F);
        Vec3 endPos = eyePosition.add(viewVector.scale(range));

        // Check for entity collisions first
        EntityHitResult entityHit = rayTraceEntities(level, player, eyePosition, endPos);
        if (entityHit != null) {
            return entityHit;
        }

        // If no entity was hit, check for block collisions
        ClipContext clipContext = new ClipContext(eyePosition, endPos, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player);
        return level.clip(clipContext);
    }

    private EntityHitResult rayTraceEntities(ServerLevel level, Player player, Vec3 startVec, Vec3 endVec) {
        Entity entity = null;
        Vec3 vec3 = null;
        double d = 0.0D;
        AABB aabb = new AABB(startVec, endVec).inflate(1.0D);
        for (Entity e : level.getEntities(player, aabb, entity1 -> !entity1.isSpectator() && entity1.isPickable())) {
            AABB entityAabb = e.getBoundingBox().inflate(0.3D);
            Optional<Vec3> optional = entityAabb.clip(startVec, endVec);
            if (optional.isPresent()) {
                Vec3 vec31 = optional.get();
                double d1 = startVec.distanceToSqr(vec31);
                if (d1 < d || d == 0.0D) {
                    entity = e;
                    vec3 = vec31;
                    d = d1;
                }
            }
        }
        return entity != null ? new EntityHitResult(entity, vec3) : null;
    }
}