package com.harrisfauntleroy.leap;

import com.harrisfauntleroy.leap.skill.CustomXPSystem;
import com.mojang.logging.LogUtils;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.slf4j.Logger;

public class ServerEvents {
    private static final Logger LOGGER = LogUtils.getLogger();

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("Server starting...");
    }

    @SubscribeEvent
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        CustomXPSystem customXP = player.getData(LEAPMod.CUSTOM_XP_SYSTEM);
        customXP.setPlayer(player);
        customXP.addXP(50);
    }

    @SubscribeEvent
    public void onPlayerClone(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            CustomXPSystem oldCustomXP = event.getOriginal().getData(LEAPMod.CUSTOM_XP_SYSTEM);
            CustomXPSystem newCustomXP = event.getEntity().getData(LEAPMod.CUSTOM_XP_SYSTEM);
            newCustomXP.deserializeNBT(oldCustomXP.serializeNBT());
            newCustomXP.setPlayer(event.getEntity());
        }
    }
}