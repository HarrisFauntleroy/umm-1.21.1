package com.harrisfauntleroy.leap.spell;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

public class CraftingSpell extends SpellBeam {
    private static final int COOLDOWN_TICKS = 60; // 3 seconds

    @Override
    protected void onEntityHit(ServerLevel level, Player player, EntityHitResult hitResult) {
        // Do nothing for entity hits
    }

    @Override
    protected void onBlockHit(ServerLevel level, Player player, BlockHitResult hitResult) {
        BlockPos hitPos = hitResult.getBlockPos().relative(hitResult.getDirection());
        summonCraftingTable(level, hitPos);
        createBlockHitEffect(level, hitPos, ParticleTypes.HAPPY_VILLAGER);
    }

    @Override
    protected ParticleOptions getParticle() {
        return ParticleTypes.HAPPY_VILLAGER;
    }

    @Override
    protected SoundEvent getSound() {
        return SoundEvents.WOOD_PLACE;
    }

    @Override
    public int getCooldown() {
        return COOLDOWN_TICKS;
    }

    @Override
    public String getName() {
        return "Fabrico (Crafting Spell)";
    }

    private void summonCraftingTable(ServerLevel level, BlockPos pos) {
        if (level.getBlockState(pos).isAir()) {
            level.setBlockAndUpdate(pos, Blocks.CRAFTING_TABLE.defaultBlockState());
        }
    }
}