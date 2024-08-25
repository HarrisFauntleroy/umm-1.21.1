package com.harrisfauntleroy.leap.item;

import com.harrisfauntleroy.leap.LEAPMod;
import com.harrisfauntleroy.leap.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModCreativeModTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, LEAPMod.MODID);

    public static final Supplier<CreativeModeTab> LEAP_ITEMS_TAB = CREATIVE_MODE_TABS.register("leap_items_tab", () -> CreativeModeTab
            .builder()
            .icon(() -> new ItemStack(ModItems.MAGIC_WAND.get()))
            .title(Component.translatable("creativetab.leap.leap_items")).displayItems((itemDisplayParameters, output) -> {
                output.accept(ModItems.MAGIC_WAND);
                output.accept(ModItems.RAW_MATERIAL);
                output.accept(ModItems.MATERIAL);
            }).build());

    public static final Supplier<CreativeModeTab> LEAP_BLOCKS_TAB = CREATIVE_MODE_TABS.register("leap_blocks_tab", () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModBlocks.MATERIAL_BLOCK.get()))
            .withTabsBefore(ResourceLocation.fromNamespaceAndPath(LEAPMod.MODID, "leap_items_tab"))
            .title(Component.translatable("creativetab.leap.leap_blocks"))
            .displayItems((itemDisplayParameters, output) -> {
                output.accept(ModBlocks.MATERIAL_BLOCK);
                output.accept(ModBlocks.MATERIAL_ORE);
                output.accept(ModBlocks.MATERIAL_DEEPSLATE_ORE);
            }).build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
