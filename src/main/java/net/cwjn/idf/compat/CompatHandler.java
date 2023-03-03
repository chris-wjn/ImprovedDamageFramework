package net.cwjn.idf.compat;

import net.cwjn.idf.ImprovedDamageFramework;
import net.cwjn.idf.compat.patchouli.PatchouliCompat;
import net.cwjn.idf.compat.tetra.TetraCompat;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import java.net.URISyntaxException;

public class CompatHandler {

    public static void init(FMLCommonSetupEvent event) {
        if (ModList.get().isLoaded("patchouli")) {
            PatchouliCompat.register();
            ImprovedDamageFramework.LOGGER.info("Patchouli compat loaded.");
        }
        if (ModList.get().isLoaded("tetra")) {
            TetraCompat.register();
            ImprovedDamageFramework.LOGGER.info("Tetra compat loaded.");
        }
        if (ImprovedDamageFramework.IAFLoaded) {
            ImprovedDamageFramework.LOGGER.info("Detected ImprovedAdventureFramework. Good choice.");
        }
    }

    public static void initClient(FMLClientSetupEvent event) {
        if (ModList.get().isLoaded("tetra")) {
            TetraCompat.registerClient();
        }
    }

}
