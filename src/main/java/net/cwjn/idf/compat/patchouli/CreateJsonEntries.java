package net.cwjn.idf.compat.patchouli;

import com.google.common.io.Resources;
import com.google.gson.Gson;
import net.cwjn.idf.config.json.JSONHandler;
import net.cwjn.idf.config.json.JSONUtil;
import net.cwjn.idf.util.Util;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class CreateJsonEntries {

    @SubscribeEvent
    public static void onEntityRegistry(EntityAttributeModificationEvent event) {
        for (EntityType<? extends LivingEntity> entityType : event.getTypes()) {
            JsonEntry entry = new JsonEntry(Util.getEntityRegistryName(entityType).toString(), "", getFormattedCategory(entityType),
                    new DefaultPage("patchouli:entity", Util.getEntityRegistryName(entityType).toString(), ""),
                    new IDFPage(getFormattedIdfCategory(entityType), Util.getEntityRegistryName(entityType).toString()));
            FileWriter file = new FileWriter();
            if (entityType.getCategory().isFriendly()) JSONUtil.writeFile(new File(JSONHandler.class.getClassLoader().getResource("data/idf/patchouli_books/en_us/entries/passive/" + Util.getEntityRegistryName(entityType).toString().replace(":", "_") + ".json").getPath()), entry);
            else JSONUtil.writeFile(new File(JSONHandler.class.getClassLoader().getResource("data/idf/patchouli_books/en_us/entries/hostile/" + Util.getEntityRegistryName(entityType).toString().replace(":", "_") + ".json").getPath()), entry);

        }
    }

    private static String getFormattedCategory(EntityType<? extends LivingEntity> type) {
        if (type.getCategory().isFriendly()) return "idf:passive";
        return "idf:hostile";
    }

    private static String getFormattedIdfCategory(EntityType<? extends LivingEntity> type) {
        if (type.getCategory().isFriendly()) return "idf:passive_entity";
        return "idf:hostile_entity";
    }

    private static class JsonEntry {

        String name, icon, category;
        ArrayList<Page> pages = new ArrayList<>();
        public JsonEntry (String name, String icon, String category, Page... pages) {
            this.name = name;
            this.icon = icon;
            this.category = category;
            this.pages.addAll(Arrays.asList(pages));
        }

    }

    private static class DefaultPage implements Page {
        String type;
        String entity;
        String text;
        public DefaultPage(String type, String entity, String text) {
            this.type = type;
            this.entity = entity;
            this.text = text;
        }
    }

    private static class IDFPage implements Page {
        String type;
        String entity;
        public IDFPage(String type, String entity) {
            this.type = type;
            this.entity = entity;
        }
    }

    interface Page {

    }

}
