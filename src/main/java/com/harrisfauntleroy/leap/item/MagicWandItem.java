package com.harrisfauntleroy.leap.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class MagicWandItem extends Item {
    public static final int MAX_RANGE = 20;
    public static final int LEVITATION_DURATION = 100; // 5 seconds
    public static final int LEVITATION_AMPLIFIER = 1;

    public MagicWandItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);

        if (!level.isClientSide) {
            Vec3 lookVec = player.getLookAngle();
            Vec3 startVec = player.getEyePosition(1.0F);
            Vec3 endVec = startVec.add(lookVec.scale(MAX_RANGE));

            AABB boundingBox = new AABB(startVec, endVec).inflate(1.0D);
            List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, boundingBox,
                    entity -> entity != player && entity.isAlive());

            for (LivingEntity target : entities) {
                if (player.hasLineOfSight(target)) {
                    target.addEffect(new MobEffectInstance(MobEffects.LEVITATION, LEVITATION_DURATION, LEVITATION_AMPLIFIER));
                    break; // Only affect the first entity hit
                }
            }
        }

        return InteractionResultHolder.success(itemstack);
    }
}