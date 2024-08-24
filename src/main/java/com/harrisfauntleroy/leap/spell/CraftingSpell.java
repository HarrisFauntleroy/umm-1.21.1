package com.harrisfauntleroy.leap.spell;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

public class CraftingSpell implements Spell {
    private static final int PARTICLE_COUNT = 100;

    @Override
    public void cast(ServerLevel level, Player player, Vec3 startPos, Vec3 endPos) {
        playSound(level, player);
        createParticleEffect(level, endPos);
        summonCraftingTable(level, endPos);
    }

    @Override
    public int getCooldown() {
        return 60; // 3 second cooldown
    }

    @Override
    public String getName() {
        return "Summon Crafting Table";
    }

    private void playSound(ServerLevel level, Player player) {
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.WOOD_PLACE, SoundSource.PLAYERS, 1.0F, 1.0F);
    }

    private void createParticleEffect(ServerLevel level, Vec3 center) {
        for (int i = 0; i < PARTICLE_COUNT; i++) {
            double offsetX = (Math.random() - 0.5) * 2;
            double offsetY = Math.random() * 2;
            double offsetZ = (Math.random() - 0.5) * 2;

            level.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                    center.x + offsetX, center.y + offsetY, center.z + offsetZ,
                    1, 0, 0, 0, 0.05);
        }
    }

    private void summonCraftingTable(ServerLevel level, Vec3 pos) {
        BlockPos blockPos = new BlockPos((int) pos.x, (int) pos.y, (int) pos.z);

        // Find the first non-air block below the target position
        while (level.getBlockState(blockPos).isAir() && blockPos.getY() > level.getMinBuildHeight()) {
            blockPos = blockPos.below();
        }

        // Place the crafting table above the found block
        BlockPos placePos = blockPos.above();

        if (level.getBlockState(placePos).isAir()) {
            level.setBlockAndUpdate(placePos, Blocks.CRAFTING_TABLE.defaultBlockState());
        }
    }
}