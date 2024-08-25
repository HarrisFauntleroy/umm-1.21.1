package com.harrisfauntleroy.leap;

import com.mojang.logging.LogUtils;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;

public class CommonEvents {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static void onCommonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("Common setup...");
    }
}