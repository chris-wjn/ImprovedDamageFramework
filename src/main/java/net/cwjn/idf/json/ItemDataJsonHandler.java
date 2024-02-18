package net.cwjn.idf.json;

import com.google.gson.Gson;
import net.cwjn.idf.ImprovedDamageFramework;
import net.cwjn.idf.json.config_data.ItemData;
import net.cwjn.idf.util.Util;
import net.minecraft.world.item.*;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@SuppressWarnings(value = "UnstableApiUsage")
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
        initItemData(dir);
        ImprovedDamageFramework.LOGGER.info(" Loaded configuration mappings.");
    }

    private static final Predicate<Item> isKnownWeapon = (item) -> item instanceof SwordItem || item instanceof DiggerItem || item instanceof BowItem || item instanceof CrossbowItem || item instanceof TridentItem;
    private static void initItemData(Path dir) {
        File itemsFile = new File(dir.toFile(), "items.json");
        Gson gson = new Gson();
        List<ItemData> items = new ArrayList<>();
        for (Item item : ForgeRegistries.ITEMS) {
            String name = Util.getItemRegistryName(item).toString();
            if (isKnownWeapon.test(item)) {
                boolean ranged = item instanceof BowItem || item instanceof CrossbowItem;
                boolean thrown = item instanceof TridentItem;
                String damageClass = "strike";
                if (name.contains("sword") || name.contains("axe")) {
                    damageClass = "slash";
                }
                if (name.contains("pickaxe") || name.contains("bow")) {
                    damageClass = "pierce";
                }
            }
        }
    }

}
