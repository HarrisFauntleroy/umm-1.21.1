package com.harrisfauntleroy.leap.spell;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class GrowthSpell implements Spell {
    private static final int GROWTH_RADIUS = 5;
    private static final int PARTICLE_COUNT = 200;

    @Override
    public void cast(ServerLevel level, Player player, Vec3 startPos, Vec3 endPos) {
        playSound(level, player);
        createParticleEffect(level, endPos);
        applyGrowthEffect(level, endPos);
    }

    @Override
    public int getCooldown() {
        return 100; // 5 second cooldown
    }

    @Override
    public String getName() {
        return "Nature's Blessing";
    }

    private void playSound(ServerLevel level, Player player) {
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.CHORUS_FLOWER_GROW, SoundSource.PLAYERS, 1.0F, 1.0F);
    }

    private void createParticleEffect(ServerLevel level, Vec3 center) {
        for (int i = 0; i < PARTICLE_COUNT; i++) {
            double angle = Math.random() * 2 * Math.PI;
            double radius = Math.random() * GROWTH_RADIUS;
            double x = center.x + radius * Math.cos(angle);
            double z = center.z + radius * Math.sin(angle);
            double y = center.y + Math.random() * 2;

            level.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                    x, y, z,
                    1, 0, 0.1, 0, 0.05);
        }
    }

    private void applyGrowthEffect(ServerLevel level, Vec3 center) {
        AABB boundingBox = new AABB(center.subtract(GROWTH_RADIUS, GROWTH_RADIUS, GROWTH_RADIUS),
                center.add(GROWTH_RADIUS, GROWTH_RADIUS, GROWTH_RADIUS));

        BlockPos.betweenClosed(new BlockPos((int) boundingBox.minX, (int) boundingBox.minY, (int) boundingBox.minZ),
                        new BlockPos((int) boundingBox.maxX, (int) boundingBox.maxY, (int) boundingBox.maxZ))
                .forEach(pos -> {
                    BlockState blockState = level.getBlockState(pos);
                    if (blockState.getBlock() instanceof BonemealableBlock bonemealableBlock) {
                        if (bonemealableBlock.isValidBonemealTarget(level, pos, blockState)) {
                            if (level.random.nextFloat() < 0.5f) { // 50% chance to grow each block
                                bonemealableBlock.performBonemeal(level, level.random, pos, blockState);
                                level.sendParticles(ParticleTypes.COMPOSTER,
                                        pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                                        5, 0.25, 0.25, 0.25, 0.1);
                            }
                        }
                    }
                });
    }
}