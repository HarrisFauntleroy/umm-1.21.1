package com.harrisfauntleroy.leap.item;

import com.harrisfauntleroy.leap.LEAPMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(LEAPMod.MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, LEAPMod.MODID);

    public static final DeferredItem<Item> WAND = ITEMS.register("wand", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> RAW_WAND = ITEMS.register("raw_wand", () -> new Item(new Item.Properties()));

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.register("example_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.LEAPMod")) //The language key for the title of your CreativeModeTab
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> WAND.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(WAND.get()); // Add the example item to the tab. For your own tabs, this method is preferred over the event
            }).build());

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
