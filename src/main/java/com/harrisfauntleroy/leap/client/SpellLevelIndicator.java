package com.harrisfauntleroy.leap.client;

import com.harrisfauntleroy.leap.item.MagicWandItem;
import com.harrisfauntleroy.leap.spell.Spell;
import net.minecraft.world.entity.player.Player;

public class SpellLevelIndicator {
    private final Player player;

    public SpellLevelIndicator(Player player) {
        this.player = player;
    }

    public Spell getCurrentSpell() {
        return MagicWandItem.getCurrentSpell();
    }

    public String getCurrentSpellInfo() {
        Spell currentSpell = getCurrentSpell();
        return String.format("%s (Lvl: %d, Str: %.2f)",
                currentSpell.getName(),
                player.experienceLevel,
                currentSpell.getSpellStrength(player));
    }
}