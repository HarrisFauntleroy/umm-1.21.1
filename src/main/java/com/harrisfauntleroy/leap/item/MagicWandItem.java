package com.harrisfauntleroy.leap.item;

import com.harrisfauntleroy.leap.spell.BurySpell;
import com.harrisfauntleroy.leap.spell.CraftingSpell;
import com.harrisfauntleroy.leap.spell.ExplosiveSpell;
import com.harrisfauntleroy.leap.spell.FireSpell;
import com.harrisfauntleroy.leap.spell.FlightSpell;
import com.harrisfauntleroy.leap.spell.FreezeSpell;
import com.harrisfauntleroy.leap.spell.LevitationSpell;
import com.harrisfauntleroy.leap.spell.LightningBoltSpell;
import com.harrisfauntleroy.leap.spell.MiningBeamSpell;
import com.harrisfauntleroy.leap.spell.Spell;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class MagicWandItem extends Item {
    private static final Logger LOGGER = Logger.getLogger(MagicWandItem.class.getName());
    private static final List<Spell> spells = new ArrayList<>();
    private static int currentSpellIndex = 0;

    static {
        spells.add(new BurySpell());
        spells.add(new CraftingSpell());
        spells.add(new ExplosiveSpell());
        spells.add(new FireSpell());
        spells.add(new FlightSpell());
        spells.add(new FreezeSpell());
        spells.add(new LevitationSpell());
        spells.add(new LightningBoltSpell());
        spells.add(new MiningBeamSpell());
    }

    public MagicWandItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);

        LOGGER.info("MagicWandItem used by player: " + player.getName().getString());

        if (player.isShiftKeyDown()) {
            LOGGER.info("Player is shift-clicking. Cycling spell.");
            if (!level.isClientSide) {
                cycleSpell(player);
            }
            return InteractionResultHolder.success(itemstack);
        }

        if (!level.isClientSide) {
            ServerLevel serverLevel = (ServerLevel) level;
            Vec3 startVec = getWandTip(player, hand);
            Vec3 endVec = startVec.add(player.getLookAngle().scale(20));

            Spell currentSpell = getCurrentSpell();
            LOGGER.info("Current spell: " + currentSpell.getName());

            if (currentSpell.canCast(player)) {
                currentSpell.cast(serverLevel, player, startVec, endVec);

                if (!player.isCreative()) {
                    player.getCooldowns().addCooldown(this, currentSpell.getCooldown());
                }

                player.displayClientMessage(Component.literal("Cast " + currentSpell.getName() + " (Strength: " + currentSpell.getSpellStrength(player) + ")"), true);
            } else {
                player.displayClientMessage(Component.literal("You can't cast " + currentSpell.getName() + " yet."), true);
            }
        }

        return InteractionResultHolder.success(itemstack);
    }

    private void cycleSpell(Player player) {
        currentSpellIndex = (currentSpellIndex + 1) % spells.size();
        Spell nextSpell = getCurrentSpell();
        LOGGER.info("Cycled to next spell: " + nextSpell.getName());
        player.displayClientMessage(Component.literal("Switched to " + nextSpell.getName()), true);
    }

    private Vec3 getWandTip(Player player, InteractionHand hand) {
        Vec3 lookVec = player.getLookAngle();
        Vec3 rightVec = lookVec.cross(new Vec3(0, 1, 0)).normalize();
        double handOffset = (hand == InteractionHand.MAIN_HAND) ? 0.4 : -0.4;
        return player.getEyePosition(1.0F).add(rightVec.scale(handOffset)).add(0, -0.3, 0);
    }

    private Spell getCurrentSpell() {
        return spells.get(currentSpellIndex);
    }
}
