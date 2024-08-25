package com.harrisfauntleroy.leap.spell;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public interface Spell {
    void cast(ServerLevel level, Player player, Vec3 startPos, Vec3 endPos);

    int getCooldown();

    String getName();

    boolean canCast(Player player);

    float getSpellStrength(Player player);
}