package net.cwjn.idf.compat;

import net.cwjn.idf.ImprovedDamageFramework;
import net.minecraftforge.fml.ModList;

public class CompatHandler {

    public static void init() {
        if (ModList.get().isLoaded("tetra")) {
            TetraCompat.register();
        }
        if (ModList.get().isLoaded("patchouli")) {
            PatchouliCompat.register();
        }
        if (ModList.get().isLoaded("oculus")) {
            OculusCompat.register();
        }
        if (ImprovedDamageFramework.IAFLoaded) {
            ImprovedDamageFramework.LOGGER.info("Detected ImprovedAdventureFramework. Good choice.");
        }
    }

    public static void initClient() {
        if (ModList.get().isLoaded("tetra")) {
            TetraCompat.registerClient();
        }
    }

}
