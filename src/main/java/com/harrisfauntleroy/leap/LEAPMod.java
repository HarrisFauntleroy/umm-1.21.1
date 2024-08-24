
package com.harrisfauntleroy.leap;

import com.harrisfauntleroy.leap.block.ModBlocks;
import com.harrisfauntleroy.leap.item.ModCreativeModTabs;
import com.harrisfauntleroy.leap.item.ModItems;
import com.harrisfauntleroy.leap.skill.CustomXPSystem;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.registries.*;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(LEAPMod.MODID)
public class LEAPMod
{
    public static final String MODID = "leap";
    private static final Logger LOGGER = LogUtils.getLogger();

    // WIP XP system
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, MODID);
    public static final AttachmentType<CustomXPSystem> CUSTOM_XP_SYSTEM = AttachmentType.builder(CustomXPSystem::new).build();


    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public LEAPMod(IEventBus modEventBus, ModContainer modContainer)
    {
        // Register the commonSetup method for mod loading
        modEventBus.addListener(this::commonSetup);

        // Register Creative tabs, Items and Blocks
        ModCreativeModTabs.register(modEventBus);
        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);

        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (LEAPMod) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        NeoForge.EVENT_BUS.register(this);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        // WIP XP system
        ATTACHMENT_TYPES.register("custom_xp", () -> CUSTOM_XP_SYSTEM);
        ATTACHMENT_TYPES.register(modEventBus);

        NeoForge.EVENT_BUS.addListener(this::onPlayerJoin);
        NeoForge.EVENT_BUS.addListener(this::onPlayerClone);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("Common setup...");
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        LOGGER.info("Server starting...");
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            LOGGER.info("Client setup...");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }
    }

    private void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        CustomXPSystem customXP = player.getData(CUSTOM_XP_SYSTEM);
        customXP.setPlayer(player);

        // Initialize with some XP for testing
        customXP.addXP(50);
    }

    private void onPlayerClone(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            CustomXPSystem oldCustomXP = event.getOriginal().getData(CUSTOM_XP_SYSTEM);
            CustomXPSystem newCustomXP = event.getEntity().getData(CUSTOM_XP_SYSTEM);
            // Copy data from old to new
            newCustomXP.deserializeNBT(oldCustomXP.serializeNBT());
            newCustomXP.setPlayer(event.getEntity());
        }
    }
}
