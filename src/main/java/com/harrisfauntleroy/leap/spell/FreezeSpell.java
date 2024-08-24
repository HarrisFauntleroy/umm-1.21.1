package com.harrisfauntleroy.leap.spell;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class FreezeSpell implements Spell {
    private static final int FREEZE_DURATION = 100; // 5 seconds
    private static final int FREEZE_RADIUS = 5;
    private static final int PARTICLE_COUNT = 200;

    @Override
    public void cast(ServerLevel level, Player player, Vec3 startPos, Vec3 endPos) {
        playSound(level, player);
        createFrostEffect(level, endPos);
        freezeEntities(level, player, endPos);
        createIceBlocks(level, endPos);
    }

    @Override
    public int getCooldown() {
        return 200; // 10 second cooldown
    }

    @Override
    public String getName() {
        return "Arctic Blast";
    }

    private void playSound(ServerLevel level, Player player) {
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.GLASS_BREAK, SoundSource.PLAYERS, 1.0F, 0.5F);
    }

    private void createFrostEffect(ServerLevel level, Vec3 center) {
        for (int i = 0; i < PARTICLE_COUNT; i++) {
            double angle = Math.random() * 2 * Math.PI;
            double radius = Math.random() * FREEZE_RADIUS;
            double x = center.x + radius * Math.cos(angle);
            double z = center.z + radius * Math.sin(angle);
            double y = center.y + Math.random() * 2;

            level.sendParticles(ParticleTypes.SNOWFLAKE,
                    x, y, z,
                    1, 0, 0, 0, 0.05);
        }
    }

    private void freezeEntities(ServerLevel level, Player player, Vec3 center) {
        AABB boundingBox = new AABB(center.subtract(FREEZE_RADIUS, FREEZE_RADIUS, FREEZE_RADIUS),
                center.add(FREEZE_RADIUS, FREEZE_RADIUS, FREEZE_RADIUS));

        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, boundingBox,
                entity -> entity != player && entity.isAlive());

        for (LivingEntity entity : entities) {
            entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, FREEZE_DURATION, 4));
            entity.addEffect(new MobEffectInstance(MobEffects.JUMP, FREEZE_DURATION, 128));
        }
    }

    private void createIceBlocks(ServerLevel level, Vec3 center) {
        BlockPos centerPos = new BlockPos((int) center.x, (int) center.y, (int) center.z);

        for (int x = -FREEZE_RADIUS; x <= FREEZE_RADIUS; x++) {
            for (int z = -FREEZE_RADIUS; z <= FREEZE_RADIUS; z++) {
                if (x * x + z * z <= FREEZE_RADIUS * FREEZE_RADIUS) {
                    BlockPos pos = centerPos.offset(x, -1, z);
                    if (level.getBlockState(pos).liquid()) {
                        level.setBlockAndUpdate(pos, Blocks.ICE.defaultBlockState());
                    } else if (level.getBlockState(pos).isAir()) {
                        level.setBlockAndUpdate(pos, Blocks.SNOW.defaultBlockState());
                    }
                }
            }
        }
    }
}