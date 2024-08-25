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
    private static final int COOLDOWN_TICKS = 20; // 1 second
    private static final int MIN_LEVEL = 4;
    private static final float BASE_STRENGTH = 0.2F;
    private static final float STRENGTH_PER_LEVEL = 0.2F;

    @Override
    protected void onEntityHit(ServerLevel level, Player player, EntityHitResult hitResult, float strength) {
        // Do nothing for entity hits
    }

    @Override
    protected void onBlockHit(ServerLevel level, Player player, BlockHitResult hitResult, float strength) {
        BlockPos hitPos = hitResult.getBlockPos().relative(hitResult.getDirection());
        summonCraftingTable(level, hitPos, strength);
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

    @Override
    public boolean canCast(Player player) {
        return player.experienceLevel >= MIN_LEVEL;
    }

    @Override
    public float getSpellStrength(Player player) {
        return BASE_STRENGTH + (player.experienceLevel - MIN_LEVEL) * STRENGTH_PER_LEVEL;
    }

    private void summonCraftingTable(ServerLevel level, BlockPos pos, float strength) {
        if (level.getBlockState(pos).isAir()) {
            level.setBlockAndUpdate(pos, Blocks.CRAFTING_TABLE.defaultBlockState());

            // As the spell gets stronger, add additional utility blocks around the crafting table
            if (strength >= 2.0F) {
                BlockPos chestPos = pos.above();
                if (level.getBlockState(chestPos).isAir()) {
                    level.setBlockAndUpdate(chestPos, Blocks.CHEST.defaultBlockState());
                }
            }
            if (strength >= 3.0F) {
                BlockPos furnacePos = pos.north();
                if (level.getBlockState(furnacePos).isAir()) {
                    level.setBlockAndUpdate(furnacePos, Blocks.FURNACE.defaultBlockState());
                }
            }
        }
    }
}