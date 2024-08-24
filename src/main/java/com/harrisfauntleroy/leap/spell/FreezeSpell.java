package com.harrisfauntleroy.leap.spell;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.sounds.SoundEvent;

public class FreezeSpell extends SpellBeam {
    private static final int FREEZE_DURATION = 100; // 5 seconds
    private static final int FREEZE_RADIUS = 5;

    @Override
    protected void onEntityHit(ServerLevel level, Player player, EntityHitResult hitResult) {
        if (hitResult.getEntity() instanceof LivingEntity target) {
            freezeEntity(level, target);
        }
    }

    @Override
    protected void onBlockHit(ServerLevel level, Player player, BlockHitResult hitResult) {
        BlockPos hitPos = hitResult.getBlockPos();
        freezeArea(level, hitPos);
    }

    @Override
    protected ParticleOptions getParticle() {
        return ParticleTypes.SNOWFLAKE;
    }

    @Override
    protected SoundEvent getSound() {
        return SoundEvents.GLASS_BREAK;
    }

    @Override
    public int getCooldown() {
        return 200; // 10 second cooldown
    }

    @Override
    public String getName() {
        return "Arctic Blast";
    }

    private void freezeEntity(ServerLevel level, LivingEntity entity) {
        entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, FREEZE_DURATION, 4));
        entity.addEffect(new MobEffectInstance(MobEffects.JUMP, FREEZE_DURATION, 128));

        BlockPos entityPos = entity.blockPosition();
        level.setBlockAndUpdate(entityPos, Blocks.ICE.defaultBlockState());
        level.setBlockAndUpdate(entityPos.above(), Blocks.ICE.defaultBlockState());

        createEntityHitEffect(level, entity, ParticleTypes.SNOWFLAKE);
    }

    private void freezeArea(ServerLevel level, BlockPos center) {
        for (int x = -FREEZE_RADIUS; x <= FREEZE_RADIUS; x++) {
            for (int y = -FREEZE_RADIUS; y <= FREEZE_RADIUS; y++) {
                for (int z = -FREEZE_RADIUS; z <= FREEZE_RADIUS; z++) {
                    if (x*x + y*y + z*z <= FREEZE_RADIUS*FREEZE_RADIUS) {
                        BlockPos pos = center.offset(x, y, z);
                        if (level.getBlockState(pos).liquid()) {
                            level.setBlockAndUpdate(pos, Blocks.ICE.defaultBlockState());
                        } else if (level.getBlockState(pos).isAir()) {
                            level.setBlockAndUpdate(pos, Blocks.SNOW.defaultBlockState());
                        }
                    }
                }
            }
        }
        createBlockHitEffect(level, center, ParticleTypes.SNOWFLAKE);
    }
}