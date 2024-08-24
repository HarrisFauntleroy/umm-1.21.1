package com.harrisfauntleroy.leap.skill;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public class CustomXPSystem {
    private int xp;
    private int level;
    private static final int[] XP_PER_LEVEL = {100, 250, 500, 1000, 2000, 4000, 8000, 16000};
    private Player player;

    public CustomXPSystem() {
        this.xp = 0;
        this.level = 0;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void addXP(int amount) {
        int oldLevel = this.level;
        this.xp += amount;
        updateLevel();

        if (player != null) {
            player.sendSystemMessage(Component.literal("You gained " + amount + " XP. Total XP: " + this.xp));

            if (this.level > oldLevel) {
                player.sendSystemMessage(Component.literal("Congratulations! You reached level " + this.level + "!"));
            }
        }
    }

    private void updateLevel() {
        while (level < XP_PER_LEVEL.length && xp >= XP_PER_LEVEL[level]) {
            xp -= XP_PER_LEVEL[level];
            level++;
        }
    }

    public int getXP() {
        return xp;
    }

    public int getLevel() {
        return level;
    }

    public int getXPForNextLevel() {
        return level < XP_PER_LEVEL.length ? XP_PER_LEVEL[level] : -1;
    }

    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("xp", xp);
        nbt.putInt("level", level);
        return nbt;
    }

    public void deserializeNBT(CompoundTag nbt) {
        xp = nbt.getInt("xp");
        level = nbt.getInt("level");
    }
}