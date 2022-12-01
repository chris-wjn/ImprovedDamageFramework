package net.cwjn.idf.compat;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class CompatHandler {

    public static void init(FMLCommonSetupEvent event) {
        if (ModList.get().isLoaded("patchouli")) PatchouliCompat.register();
        if (ModList.get().isLoaded("legendarytooltips")) TooltipsCompat.register();
    }

    public static void initClient(FMLClientSetupEvent event) {

    }

}
