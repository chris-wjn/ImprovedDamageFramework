package net.cwjn.idf.compat;

import net.cwjn.idf.ImprovedDamageFramework;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CompatHandler {

    private static final Map<String, Class<? extends CompatClass>> classes = new HashMap<>();
    private static final Set<CompatClass> loaded = new HashSet<>();
    static {
        classes.put("tetra", TetraCompat.class);
        classes.put("patchouli", PatchouliCompat.class);
        classes.put("oculus", OculusCompat.class);
    }

    public static void preInit() {
        for (Map.Entry<String, Class<? extends CompatClass>> clazz : classes.entrySet()) {
            if (ModList.get().isLoaded(clazz.getKey())) {
                try {
                    loaded.add(clazz.getValue().newInstance());
                    ImprovedDamageFramework.LOGGER.info("Loaded: " + clazz.getKey());
                } catch (Exception e) {
                    ImprovedDamageFramework.LOGGER.error("Failed to load: " + clazz.getKey() + ", {} error", e.getLocalizedMessage());
                    ImprovedDamageFramework.LOGGER.catching(e.fillInStackTrace());
                }
            }
        }
        if (ImprovedDamageFramework.IAFLoaded) {
            ImprovedDamageFramework.LOGGER.info("Detected ImprovedAdventureFramework. Good choice.");
        }
    }

    public static void init(FMLCommonSetupEvent event) {
        for (CompatClass cl : loaded) {
            cl.register();
        }
    }

    public static void initClient(FMLClientSetupEvent event) {
        for (CompatClass cl : loaded) {
            cl.registerClient();
        }
    }

}
