package net.cwjn.idf.compat;

import net.cwjn.idf.ImprovedDamageFramework;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class CompatHandler {

    public static void init(FMLCommonSetupEvent event) {
        if (ModList.get().isLoaded("patchouli")) {
            PatchouliCompat.register();
            ImprovedDamageFramework.LOGGER.info("IMPROVED DAMAGE FRAMEWORK: Patchouli compat loaded.");
        }
        if (ModList.get().isLoaded("silentgear")) {
            SilentGearCompat.register();
            ImprovedDamageFramework.LOGGER.info("IMPROVED DAMAGE FRAMEWORK: SilentGear compat loaded.");
        }
    }

    public static void initClient(FMLClientSetupEvent event) {

    }

}
