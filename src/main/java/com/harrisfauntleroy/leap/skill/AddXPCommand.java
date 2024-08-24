package com.harrisfauntleroy.leap.skill;

import com.harrisfauntleroy.leap.LEAPMod;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

public class AddXPCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("addleapxp")
                .requires(source -> source.hasPermission(2))
                .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                        .executes(context -> addXP(context.getSource(), IntegerArgumentType.getInteger(context, "amount")))));
    }

    private static int addXP(CommandSourceStack source, int amount) {
        if (source.getEntity() instanceof ServerPlayer player) {
            CustomXPSystem customXP = player.getData(LEAPMod.CUSTOM_XP_SYSTEM);
            customXP.addXP(amount);
            return Command.SINGLE_SUCCESS;
        }
        return 0;
    }
}