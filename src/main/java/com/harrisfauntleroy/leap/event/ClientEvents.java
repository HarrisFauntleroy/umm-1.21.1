package com.harrisfauntleroy.leap.event;

import com.harrisfauntleroy.leap.LEAPMod;
import com.harrisfauntleroy.leap.client.SpellLevelIndicator;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

@EventBusSubscriber(modid = LEAPMod.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEvents {
    private static SpellLevelIndicator spellLevelIndicator;
    private static final Logger LOGGER = LogUtils.getLogger();

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        LOGGER.info("Client setup...");
        LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());

        // Register the render GUI event here
        NeoForge.EVENT_BUS.addListener(ClientEvents::onRenderGui);
    }

    private static void onRenderGui(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player != null) {
            GuiGraphics guiGraphics = event.getGuiGraphics();

            // Initialize spellLevelIndicator if it's null
            if (spellLevelIndicator == null) {
                spellLevelIndicator = new SpellLevelIndicator(player);
            }

            // Render spell information
            String spellInfo = spellLevelIndicator.getCurrentSpellInfo();
            guiGraphics.drawString(mc.font, spellInfo, 10, 10, 0xFFFFFF);
        }
    }
}