package com.harrisfauntleroy.leap.spell;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class MagicHoleSpell implements Spell {
    private static final int HOLE_RADIUS = 3;
    private static final int HOLE_DEPTH = 5;
    private static final int PARTICLE_COUNT = 200;
    private static final int DURATION = 200; // 10 seconds

    private List<BlockPos> removedBlocks = new ArrayList<>();

    @Override
    public void cast(ServerLevel level, Player player, Vec3 startPos, Vec3 endPos) {
        playSound(level, player);
        createHoleEffect(level, endPos);
        createMagicHole(level, endPos);

        // Schedule the hole to be filled after DURATION ticks
        level.getServer().tell(new net.minecraft.server.TickTask(level.getServer().getTickCount() + DURATION, () -> {
            fillMagicHole(level);
        }));
    }

    @Override
    public int getCooldown() {
        return 300; // 15 second cooldown
    }

    @Override
    public String getName() {
        return "Magic Hole";
    }

    private void playSound(ServerLevel level, Player player) {
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 1.0F, 0.5F);
    }

    private void createHoleEffect(ServerLevel level, Vec3 center) {
        for (int i = 0; i < PARTICLE_COUNT; i++) {
            double angle = Math.random() * 2 * Math.PI;
            double radius = Math.random() * HOLE_RADIUS;
            double x = center.x + radius * Math.cos(angle);
            double z = center.z + radius * Math.sin(angle);
            double y = center.y + Math.random() * HOLE_DEPTH;

            level.sendParticles(ParticleTypes.PORTAL,
                    x, y, z,
                    1, 0, -1, 0, 0.5);
        }
    }

    private void createMagicHole(ServerLevel level, Vec3 center) {
        BlockPos centerPos = new BlockPos((int) center.x, (int) center.y, (int) center.z);

        for (int y = 0; y >= -HOLE_DEPTH; y--) {
            for (int x = -HOLE_RADIUS; x <= HOLE_RADIUS; x++) {
                for (int z = -HOLE_RADIUS; z <= HOLE_RADIUS; z++) {
                    if (x * x + z * z <= HOLE_RADIUS * HOLE_RADIUS) {
                        BlockPos pos = centerPos.offset(x, y, z);
                        BlockState currentState = level.getBlockState(pos);
                        if (!currentState.isAir() && !currentState.liquid()) {
                            removedBlocks.add(pos);
                            level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                        }
                    }
                }
            }
        }
    }

    private void fillMagicHole(ServerLevel level) {
        for (BlockPos pos : removedBlocks) {
            level.setBlockAndUpdate(pos, level.getBlockState(pos.below()));
        }
        removedBlocks.clear();
    }
}