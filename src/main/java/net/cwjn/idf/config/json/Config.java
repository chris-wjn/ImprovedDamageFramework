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
        Path configPath = FMLPaths.CONFIGDIR.get();
        Path modConfigPath = Paths.get(configPath.toAbsolutePath().toString(), "ImprovedDamageFramework");

        try
        {
            Files.createDirectory(modConfigPath);
        }
        catch (FileAlreadyExistsException e) {}
        catch (IOException e)
        {
            ImprovedDamageFramework.getLog().error("Failed to create IDF config directory", e);
        }

        JSONHandler.init(modConfigPath.toFile());

        System.out.println("Initialized Data config!");
    }
    public static void initServer()
    {
        Path configPath = FMLPaths.CONFIGDIR.get();
        Path modConfigPath = Paths.get(configPath.toAbsolutePath().toString(), "ImprovedDamageFramework");

        try
        {
            Files.createDirectory(modConfigPath);
        }
        catch (FileAlreadyExistsException e) {}
        catch (IOException e)
        {
            ImprovedDamageFramework.getLog().error("Failed to create ImprovedDamageFramework config directory", e);
        }

        JSONHandler.serverInit(modConfigPath.toFile());

        System.out.println("Initialized Server Data config!");
    }
}
