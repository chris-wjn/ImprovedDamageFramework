package net.cwjn.idf.util;

import net.cwjn.idf.ImprovedDamageFramework;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ItemDataJsonHandler {

    //Initialize the config directory
    public static void init()
    {
        Path dir = Paths.get(FMLPaths.CONFIGDIR.get().toAbsolutePath().toString(), "ImprovedDamageFramework");
        try {
            Files.createDirectory(dir);
        }
        catch (FileAlreadyExistsException ignored) {}
        catch (IOException e) {
            ImprovedDamageFramework.LOGGER.error("Failed to create IDF config directory", e);
        }
        ImprovedDamageFramework.LOGGER.info(" Loaded configuration mappings.");
    }

    private static void initItemData() {
        for (Item item : ForgeRegistries.ITEMS) {
            if (item.)
        }
    }

}
