package com.harrisfauntleroy.leap.spell;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public abstract class AbstractSpell implements Spell {
    private static final int BASE_XP_REQUIREMENT = 4;
    private static final float BASE_STRENGTH = 1.0F;
    private static final float STRENGTH_PER_LEVEL = 0.1F;

    protected final int minLevel;
    protected final int cooldownTicks;

    protected AbstractSpell(int minLevel, int cooldownTicks) {
        this.minLevel = minLevel;
        this.cooldownTicks = cooldownTicks;
    }

    @Override
    public void cast(ServerLevel level, Player player, Vec3 startPos, Vec3 endPos) {
        if (!canCast(player)) {
            player.displayClientMessage(Component.literal("You can't cast this spell yet."), true);
            return;
        }

        float strength = getSpellStrength(player);
        performSpellEffect(level, player, startPos, endPos, strength);
    }

    protected abstract void performSpellEffect(ServerLevel level, Player player, Vec3 startPos, Vec3 endPos, float strength);

    @Override
    public int getCooldown() {
        return cooldownTicks;
    }

    @Override
    public boolean canCast(Player player) {
        return player.experienceLevel >= BASE_XP_REQUIREMENT && player.experienceLevel >= minLevel;
    }

    @Override
    public float getSpellStrength(Player player) {
        return BASE_STRENGTH + (player.experienceLevel - minLevel) * STRENGTH_PER_LEVEL;
    }
}