package net.cwjn.idf.compat;

import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class CompatHandler {

    public static void init(FMLCommonSetupEvent event) {
        PatchouliCompat.register();
    }

    public static void initClient(FMLClientSetupEvent event) {

    }

}
