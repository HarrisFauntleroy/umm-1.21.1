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

public class FrenziedFlameSpell implements Spell {
    private static final int FLAME_DURATION = 100; // 5 seconds
    private static final int FLAME_RADIUS = 8;
    private static final int PARTICLE_COUNT = 300;

    @Override
    public void cast(ServerLevel level, Player player, Vec3 startPos, Vec3 endPos) {
        playSound(level, player);
        createFlameEffect(level, endPos);
        igniteEntities(level, player, endPos);
        spreadFire(level, endPos);
    }

    @Override
    public int getCooldown() {
        return 300; // 15 second cooldown
    }

    @Override
    public String getName() {
        return "Frenzied Flame";
    }

    private void playSound(ServerLevel level, Player player) {
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.BLAZE_SHOOT, SoundSource.PLAYERS, 1.0F, 0.5F);
    }

    private void createFlameEffect(ServerLevel level, Vec3 center) {
        for (int i = 0; i < PARTICLE_COUNT; i++) {
            double angle = Math.random() * 2 * Math.PI;
            double radius = Math.random() * FLAME_RADIUS;
            double x = center.x + radius * Math.cos(angle);
            double z = center.z + radius * Math.sin(angle);
            double y = center.y + Math.random() * 3;

            level.sendParticles(ParticleTypes.FLAME,
                    x, y, z,
                    1, 0, 0, 0, 0.05);
        }
    }

    private void igniteEntities(ServerLevel level, Player player, Vec3 center) {
        AABB boundingBox = new AABB(center.subtract(FLAME_RADIUS, FLAME_RADIUS, FLAME_RADIUS),
                center.add(FLAME_RADIUS, FLAME_RADIUS, FLAME_RADIUS));

        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, boundingBox,
                entity -> entity != player && entity.isAlive());

        for (LivingEntity entity : entities) {
            entity.setRemainingFireTicks(5);
            entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, FLAME_DURATION, 1));
        }
    }

    private void spreadFire(ServerLevel level, Vec3 center) {
        BlockPos centerPos = new BlockPos((int) center.x, (int) center.y, (int) center.z);

        for (int x = -FLAME_RADIUS; x <= FLAME_RADIUS; x++) {
            for (int y = -2; y <= 2; y++) {
                for (int z = -FLAME_RADIUS; z <= FLAME_RADIUS; z++) {
                    if (x * x + z * z <= FLAME_RADIUS * FLAME_RADIUS) {
                        BlockPos pos = centerPos.offset(x, y, z);
                        if (level.getBlockState(pos).isAir() && level.getBlockState(pos.below()).isSolidRender(level, pos.below())) {
                            level.setBlockAndUpdate(pos, Blocks.FIRE.defaultBlockState());
                        }
                    }
                }
            }
        }
    }
}