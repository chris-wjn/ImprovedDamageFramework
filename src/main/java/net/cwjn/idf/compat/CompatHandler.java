package net.cwjn.idf.compat;

import net.cwjn.idf.ImprovedDamageFramework;
import net.cwjn.idf.compat.patchouli.PatchouliCompat;
import net.cwjn.idf.compat.tetra.TetraCompat;
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.net.URISyntaxException;

public class CompatHandler {

    public static boolean tetraLoaded = false;
    public static boolean patchouliLoaded = false;

    public static void init(FMLCommonSetupEvent event) {
        if (ModList.get().isLoaded("patchouli")) {
            patchouliLoaded = true;
            PatchouliCompat.register();
            ImprovedDamageFramework.LOGGER.info("Patchouli compat loaded.");
        }
        if (ModList.get().isLoaded("tetra")) {
            tetraLoaded = true;
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
