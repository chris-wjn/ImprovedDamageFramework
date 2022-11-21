package net.cwjn.idf.compat.patchouli;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import net.cwjn.idf.config.json.JSONHandler;
import net.cwjn.idf.config.json.JSONUtil;
import net.cwjn.idf.config.json.data.EntityData;
import net.cwjn.idf.util.Util;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

@Mod.EventBusSubscriber
public class CreateJsonEntries {

    private static final Gson gson = new Gson();

    @SubscribeEvent
    public static void onEntityRegistry(EntityAttributeModificationEvent event) {
        for (EntityType<? extends LivingEntity> entityType : event.getTypes()) {
            JsonEntry entry = new JsonEntry(Util.getEntityRegistryName(entityType).toString(), "", getFormattedCategory(entityType),
                    new DefaultPage("patchouli:entity", Util.getEntityRegistryName(entityType).toString(), ""),
                    new IDFPage(getFormattedIdfCategory(entityType), Util.getEntityRegistryName(entityType).toString()));
            JSONUtil.writeFile(new File(JSONHandler.class.getClassLoader().getResourceAsStream("data/idf/patchouli_books/en_us/entries/hostile/" + Util.getEntityRegistryName(entityType).toString() + ".json").toString()), entry);
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
