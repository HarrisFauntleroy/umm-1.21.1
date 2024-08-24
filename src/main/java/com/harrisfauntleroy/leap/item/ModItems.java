package com.harrisfauntleroy.leap.item;

import com.harrisfauntleroy.leap.LEAPMod;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(LEAPMod.MODID);

    public static final DeferredItem<Item> MAGIC_WAND = ITEMS.register("magic_wand", () -> new MagicWandItem(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> MATERIAL = ITEMS.register("material", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> RAW_MATERIAL = ITEMS.register("raw_material", () -> new Item(new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
