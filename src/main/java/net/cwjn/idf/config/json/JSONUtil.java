package net.cwjn.idf.config.json;

import net.cwjn.idf.ImprovedDamageFramework;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.lang.reflect.Type;

@SuppressWarnings(value = "deprecation")
public class JSONUtil {
    public static final Gson SERIALIZER = new GsonBuilder().setPrettyPrinting().create();

    public static <T> T getOrCreateConfigFile(File configDir, String configName, T defaults, Type type) {
        File configFile = new File(configDir, configName);

        if (!configFile.exists()) {
            writeFile(configFile, defaults);
        }

        try {
            return SERIALIZER.fromJson(FileUtils.readFileToString(configFile), type);
        }
        catch (Exception e) {
            ImprovedDamageFramework.LOGGER.error("Error parsing config from json: " + configFile.toString(), e);
        }

        return null;
    }

    public static <T> T getConfigFile(File configDir, String configName, Type type) {
        File configFile = new File(configDir, configName);

        try {
            return SERIALIZER.fromJson(FileUtils.readFileToString(configFile), type);
        }
        catch (Exception e) {
            ImprovedDamageFramework.LOGGER.error("Error parsing config from json: " + configFile.toString(), e);
        }

        return null;
    }

    protected static void writeFile(File outputFile, Object obj) {
        try {
            FileUtils.write(outputFile, SERIALIZER.toJson(obj));
        }
        catch (Exception e) {
            ImprovedDamageFramework.LOGGER.error("Error writing config file " + outputFile.getAbsolutePath() + ": " + e.getMessage());
        }
    }
}
