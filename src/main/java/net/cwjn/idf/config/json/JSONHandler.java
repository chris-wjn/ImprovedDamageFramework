package net.cwjn.idf.config.json;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.cwjn.idf.ImprovedDamageFramework;
import net.cwjn.idf.Util;
import net.cwjn.idf.config.json.data.DamageData;
import net.cwjn.idf.config.json.data.EntityData;
import net.cwjn.idf.config.json.data.ResistanceData;
import net.cwjn.idf.config.json.data.SourceCatcherData;
import net.cwjn.idf.network.IDFPacketHandler;
import net.cwjn.idf.network.SendServerDamageJsonMessage;
import net.cwjn.idf.network.SendServerResistanceJsonMessage;
import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static net.minecraft.world.entity.ai.attributes.Attributes.ARMOR;
import static net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE;

@SuppressWarnings(value = "UnstableApiUsage")
@Mod.EventBusSubscriber
public class JSONHandler {
    public static Map<ResourceLocation, EntityData> entityMap = new HashMap<>();
    public static Map<ResourceLocation, ResistanceData> resistanceMap = new HashMap<>();
    public static Map<ResourceLocation, DamageData> damageMap = new HashMap<>();
    public static Map<String, SourceCatcherData> sourceMap = new HashMap<>();
    private static final Gson gson = new Gson();

    public static void init(File configDir) {
        Map<String, EntityData> defaultEntityData = gson.fromJson(new BufferedReader(new InputStreamReader(Objects.requireNonNull(JSONHandler.class.getClassLoader().getResourceAsStream("data/idf/default/entity_data.json")))), new TypeToken<Map<String, EntityData>>(){}.getType());
        Map<String, ResistanceData> defaultResistanceData = gson.fromJson(new BufferedReader(new InputStreamReader(Objects.requireNonNull(JSONHandler.class.getClassLoader().getResourceAsStream("data/idf/default/resistance_data.json")))), new TypeToken<Map<String, ResistanceData>>(){}.getType());
        Map<String, DamageData> defaultDamageData = gson.fromJson(new BufferedReader(new InputStreamReader(Objects.requireNonNull(JSONHandler.class.getClassLoader().getResourceAsStream("data/idf/default/damage_data.json")))), new TypeToken<Map<String, DamageData>>(){}.getType());
        Map<String, SourceCatcherData> defaultSourceData = gson.fromJson(new BufferedReader(new InputStreamReader(Objects.requireNonNull(JSONHandler.class.getClassLoader().getResourceAsStream("data/idf/default/source_catcher.json")))), new TypeToken<Map<String, SourceCatcherData>>(){}.getType());
        //TODO: iron golems, snow golems, villagers not included in this list.
        for (EntityType<?> entityType : ForgeRegistries.ENTITY_TYPES.getValues()) {
            if (entityType.getCategory() != MobCategory.MISC) { //make sure this isn't an arrow entity or something
                defaultEntityData.putIfAbsent(Util.getEntityRegistryName(entityType).toString(), new EntityData(
                        0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, "strike",
                        25.0D, 25.0D, 25.0D, 25.0D, 25.0D, 0.0D, 0.0D,
                        1.0D, 1.0D, 1.0D, 1.0D, 1.0D,
                        0.0D, 0.0D, 0.4, -1.0D, 0.0D, 0.0D, 0.0D, 0.0D));
            }
        }
        for (Item item : ForgeRegistries.ITEMS.getValues()) {
            Collection<AttributeModifier> armour0 = item.getDefaultInstance().getAttributeModifiers(EquipmentSlot.HEAD).get(ARMOR);
            Collection<AttributeModifier> armour1 = item.getDefaultInstance().getAttributeModifiers(EquipmentSlot.FEET).get(ARMOR);
            Collection<AttributeModifier> armour2 = item.getDefaultInstance().getAttributeModifiers(EquipmentSlot.LEGS).get(ARMOR);
            Collection<AttributeModifier> armour3 = item.getDefaultInstance().getAttributeModifiers(EquipmentSlot.CHEST).get(ARMOR);
            double armorVal = armour0.stream().mapToDouble(AttributeModifier::getAmount).sum() +
                              armour1.stream().mapToDouble(AttributeModifier::getAmount).sum() +
                              armour2.stream().mapToDouble(AttributeModifier::getAmount).sum() +
                              armour3.stream().mapToDouble(AttributeModifier::getAmount).sum();
            if (armorVal > 0) {
                if (Util.getItemRegistryName(item).toString().contains("iron") || (Util.getItemRegistryName(item).toString().contains("chainmail")) || (Util.getItemRegistryName(item).toString().contains("netherite"))) {
                    defaultResistanceData.putIfAbsent(Util.getItemRegistryName(item).toString(), new ResistanceData(0, 0, 0, 0 ,0, 0, 0, 0, 0, 0, 0, 0, 0.05, 0, -0.05, 0.1, 0));
                } else if (Util.getItemRegistryName(item).toString().contains("leather")) {
                    defaultResistanceData.putIfAbsent(Util.getItemRegistryName(item).toString(), new ResistanceData(0, 0, 0, 0 ,0, 0, 0, 0, 0, 0, 0, 0, -0.05, 0.1, 0, -0.03, 0));
                } else if (Util.getItemRegistryName(item).toString().contains("diamond")) {
                    defaultResistanceData.putIfAbsent(Util.getItemRegistryName(item).toString(), new ResistanceData(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -0.07, 0.0, 0.02, -0.05, 0));
                } else if (Util.getItemRegistryName(item).toString().contains("gold")) {
                    defaultResistanceData.putIfAbsent(Util.getItemRegistryName(item).toString(), new ResistanceData(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -0.1, -0.1, -0.1, 0.03, 0));
                } else {
                    defaultResistanceData.putIfAbsent(Util.getItemRegistryName(item).toString(), new ResistanceData(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
                }
            }
        }
        for (Item item : ForgeRegistries.ITEMS.getValues()) {
            Collection<AttributeModifier> weapon0 = item.getDefaultInstance().getAttributeModifiers(EquipmentSlot.MAINHAND).get(ATTACK_DAMAGE);
            Collection<AttributeModifier> weapon1 = item.getDefaultInstance().getAttributeModifiers(EquipmentSlot.OFFHAND).get(ATTACK_DAMAGE);
            double damageVal = weapon0.stream().mapToDouble(AttributeModifier::getAmount).sum() +
                    weapon1.stream().mapToDouble(AttributeModifier::getAmount).sum();
            if (damageVal > 0 || item instanceof BowItem || item instanceof CrossbowItem) {
                String dc = "strike";
                if (Util.getItemRegistryName(item).toString().contains("sword") || Util.getItemRegistryName(item).toString().contains("axe")) {
                    dc = "slash";
                }
                if (Util.getItemRegistryName(item).toString().contains("pickaxe") || Util.getItemRegistryName(item).toString().contains("bow")) {
                    dc = "pierce";
                }
                defaultDamageData.putIfAbsent(Util.getItemRegistryName(item).toString(), new DamageData(0, 0, 0, 0, 0, 0, dc, 0, 0, 5, 0));
            }
        }
        entityMap.clear();
        resistanceMap.clear();
        damageMap.clear();
        sourceMap.clear();
        Map<String, EntityData> tempEntityMap = JSONUtil.getOrCreateConfigFile(configDir, "entity_data.json", defaultEntityData, new TypeToken<Map<String, EntityData>>() {
        }.getType());
        Map<String, ResistanceData> tempResistanceMap = JSONUtil.getOrCreateConfigFile(configDir, "resistance_data.json", defaultResistanceData, new TypeToken<Map<String, ResistanceData>>() {
        }.getType());
        Map<String, DamageData> tempDamageMap = JSONUtil.getOrCreateConfigFile(configDir, "damage_data.json", defaultDamageData, new TypeToken<Map<String, DamageData>>() {
        }.getType());
        sourceMap = JSONUtil.getOrCreateConfigFile(configDir, "source_catcher.json", defaultSourceData, new TypeToken<Map<String, SourceCatcherData>>() {
        }.getType());
        if (tempEntityMap != null && !tempEntityMap.isEmpty()) {
            for (Map.Entry<String, EntityData> entry : tempEntityMap.entrySet()) {
                entityMap.put(new ResourceLocation(entry.getKey()), entry.getValue());
            }
        }
        if (tempResistanceMap != null && !tempResistanceMap.isEmpty()) {
            for (Map.Entry<String, ResistanceData> entry : tempResistanceMap.entrySet()) {
                resistanceMap.put(new ResourceLocation(entry.getKey()), entry.getValue());
            }
        }
        if (tempDamageMap != null && !tempDamageMap.isEmpty()) {
            for (Map.Entry<String, DamageData> entry : tempDamageMap.entrySet()) {
                damageMap.put(new ResourceLocation(entry.getKey()), entry.getValue());
            }
        }
    }
    @OnlyIn(Dist.DEDICATED_SERVER)
    public static void updateServerFiles() {
        ImprovedDamageFramework.LOGGER.info("Updating server's json configs...");
        Path modConfigPath = Paths.get(FMLPaths.CONFIGDIR.get().toAbsolutePath().toString(), "ImprovedDamageFramework");
        Map<String, ResistanceData> tempResistanceMap = JSONUtil.getConfigFile(modConfigPath.toFile(), "resistance_data.json", new TypeToken<Map<String, ResistanceData>>() {}.getType());
        Map<String, DamageData> tempDamageMap = JSONUtil.getConfigFile(modConfigPath.toFile(), "damage_data.json", new TypeToken<Map<String, DamageData>>() {}.getType());
        sourceMap = JSONUtil.getConfigFile(modConfigPath.toFile(), "source_catcher.json", new TypeToken<Map<String, SourceCatcherData>>() {}.getType());
        ImprovedDamageFramework.LOGGER.info("Updated source map");

        if (tempResistanceMap != null && !tempResistanceMap.isEmpty()) {
            for (Map.Entry<String, ResistanceData> entry : tempResistanceMap.entrySet()) {
                resistanceMap.put(new ResourceLocation(entry.getKey()), entry.getValue());
            }
            ImprovedDamageFramework.LOGGER.info("Updated armour map.");
        }
        if (tempDamageMap != null && !tempDamageMap.isEmpty()) {
            for (Map.Entry<String, DamageData> entry : tempDamageMap.entrySet()) {
                damageMap.put(new ResourceLocation(entry.getKey()), entry.getValue());
            }
            ImprovedDamageFramework.LOGGER.info("Updated weapon map.");
        }
        if (!resistanceMap.isEmpty()) {
            IDFPacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new SendServerResistanceJsonMessage(resistanceMap));
            ImprovedDamageFramework.LOGGER.info("Sending server resistance map to all clients...");
        }
        if (!damageMap.isEmpty()) {
            IDFPacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new SendServerDamageJsonMessage(damageMap));
            ImprovedDamageFramework.LOGGER.info("Sending server damage map to all clients...");
        }
    }

    @OnlyIn(Dist.DEDICATED_SERVER)
    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        ServerPlayer player = (ServerPlayer) event.getEntity();
        if (!resistanceMap.isEmpty()) {
            IDFPacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new SendServerResistanceJsonMessage(resistanceMap));
            ImprovedDamageFramework.LOGGER.info("Sending server resistance data values to player: " + player.getScoreboardName());
        }
        if (!damageMap.isEmpty()) {
            IDFPacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new SendServerDamageJsonMessage(damageMap));
            ImprovedDamageFramework.LOGGER.info("Sending server damage data values to player: " + player.getScoreboardName());
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void updateClientResistanceData(Map<ResourceLocation, ResistanceData> map) {
        resistanceMap = map;
        if (Minecraft.getInstance().player != null) {
            Minecraft.getInstance().player.sendSystemMessage(net.cwjn.idf.Util.translationComponent("idf.client.armour.map.updated"));
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void updateClientDamageData(Map<ResourceLocation, DamageData> map) {
        damageMap = map;
        if (Minecraft.getInstance().player != null) {
            Minecraft.getInstance().player.sendSystemMessage(net.cwjn.idf.Util.translationComponent("idf.client.weapon.map.updated"));
        }
    }

    public static EntityData getEntityData(ResourceLocation key) {
        return entityMap.getOrDefault(key, null);
    }

    public static DamageData getDamageData(ResourceLocation key) {
        return damageMap.getOrDefault(key, null);
    }

    public static ResistanceData getResistanceData(ResourceLocation key) {
        return resistanceMap.getOrDefault(key, null);
    }

}
