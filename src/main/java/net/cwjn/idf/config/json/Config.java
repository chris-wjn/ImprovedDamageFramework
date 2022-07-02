package net.cwjn.idf.config.json;

import net.cwjn.idf.ImprovedDamageFramework;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Config {
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
        JSONHandler.init(dir.toFile());
        ImprovedDamageFramework.LOGGER.info("Loaded ImprovedDamageFramework config.");
    }
}
