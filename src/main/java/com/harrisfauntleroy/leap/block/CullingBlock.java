package com.harrisfauntleroy.leap.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderFrameEvent;

public class CullingBlock extends Block {
    public CullingBlock(Properties properties) {
        super(properties);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (level.isClientSide) {
            for (Direction direction : Direction.values()) {
                spawnParticleBeam(level, pos, direction, random);
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void spawnParticleBeam(Level level, BlockPos startPos, Direction direction, RandomSource random) {
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos().set(startPos);
        Vec3 center = Vec3.atCenterOf(startPos);

        for (int i = 1; i <= 32; i++) {
            mutablePos.move(direction);
            if (!level.isEmptyBlock(mutablePos)) {
                break;
            }

            if (random.nextFloat() < 0.3f) {
                Vec3 particlePos = Vec3.atCenterOf(mutablePos);
                level.addParticle(new DustParticleOptions(Vec3.fromRGB24(16711680).toVector3f(), 1.0F),
                        particlePos.x, particlePos.y, particlePos.z,
                        0, 0, 0);
            }
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onRenderFrame(RenderFrameEvent.Pre event) {
        // TODO: Implement the culling logic here
    }
}