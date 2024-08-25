package com.harrisfauntleroy.leap.event;

import com.harrisfauntleroy.leap.client.SpellLevelIndicator;
import com.mojang.logging.LogUtils;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;

public class CommonEvents {
    private static SpellLevelIndicator spellLevelIndicator;
    private static final Logger LOGGER = LogUtils.getLogger();

    public static void onCommonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("Common setup...");
    }
}