package com.harrisfauntleroy.leap;

import com.harrisfauntleroy.leap.block.ModBlocks;
import com.harrisfauntleroy.leap.event.CommonEvents;
import com.harrisfauntleroy.leap.event.ServerEvents;
import com.harrisfauntleroy.leap.item.ModItems;
import com.harrisfauntleroy.leap.registry.ModCreativeModTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;

@Mod(LEAPMod.MODID)
public class LEAPMod {
    public static final String MODID = "leap";

    public LEAPMod(IEventBus modEventBus, ModContainer modContainer) {
        // Register Creative tabs, Items & Blocks
        ModCreativeModTabs.register(modEventBus);
        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);

        // Register configuration
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        // Server events
        ServerEvents serverEvents = new ServerEvents();
        NeoForge.EVENT_BUS.register(serverEvents);

        // Common events
        modEventBus.addListener(CommonEvents::onCommonSetup);
    }
}