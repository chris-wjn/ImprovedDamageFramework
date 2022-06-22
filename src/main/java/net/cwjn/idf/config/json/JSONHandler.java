package net.cwjn.idf.config.json;

import net.cwjn.idf.ImprovedDamageFramework;
import net.cwjn.idf.network.IDFPackerHandler;
import net.cwjn.idf.network.SendServerDamageJsonMessage;
import net.cwjn.idf.network.SendServerResistanceJsonMessage;
import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.TranslatableComponent;
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

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static net.minecraft.world.entity.ai.attributes.Attributes.ARMOR;
import static net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE;

@Mod.EventBusSubscriber
public class JSONHandler {

    public static Map<ResourceLocation, EntityData> entityMap = new HashMap<>();
    public static Map<ResourceLocation, ResistanceData> resistanceMap = new HashMap<>();
    public static Map<ResourceLocation, DamageData> damageMap = new HashMap<>();
    public static Map<String, SourceCatcherData> sourceMap = new HashMap<>();
    public static Map<ResourceLocation, ResistanceData> serverResistanceMap = new HashMap<>();
    public static Map<ResourceLocation, DamageData> serverDamageDataMap = new HashMap<>();

    public static void init(File configDir) {
        Map<String, EntityData> defaultEntityData = Maps.newHashMap();
        Map<String, ResistanceData> defaultResistanceData = Maps.newHashMap();
        Map<String, DamageData> defaultDamageData = Maps.newHashMap();
        Map<String, SourceCatcherData> defaultSourceData = Maps.newHashMap();
        defaultSourceData.put("bullet", new SourceCatcherData(0, 0, 0, 0, 0, 25, "pierce"));
        //TODO: iron golems, snow golems, villagers not included in this list.
        for (EntityType<?> entityType : ForgeRegistries.ENTITIES.getValues()) {
            MobCategory type = entityType.getCategory();
            if (type != MobCategory.MISC) { //make sure this isnt an arrow entity or something
                defaultEntityData.put(entityType.getRegistryName().toString(), new EntityData(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, "strike", 30.0D, 30.0D, 30.0D, 30.0D, 30.0D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D));
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
                if (item.getRegistryName().toString().contains("iron") || (item.getRegistryName().toString().contains("chainmail")) || (item.getRegistryName().toString().contains("netherite"))) {
                    defaultResistanceData.put(item.getRegistryName().toString(), new ResistanceData(0, 0, 0, 0 ,0, 0, 0, 0, 0, 0, 0.05, 0, 0.1, -0.05, 0));
                } else if (item.getRegistryName().toString().contains("leather")) {
                    defaultResistanceData.put(item.getRegistryName().toString(), new ResistanceData(0, 0, 0, 0 ,0, 0, 0, 0, 0, 0, -0.05, 0.1, -0.03, 0, 0));
                } else if (item.getRegistryName().toString().contains("diamond")) {
                    defaultResistanceData.put(item.getRegistryName().toString(), new ResistanceData(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -0.07, 0.0, -0.05, 0.02, 0));
                } else if (item.getRegistryName().toString().contains("gold")) {
                    defaultResistanceData.put(item.getRegistryName().toString(), new ResistanceData(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -0.1, -0.1, 0.03, -0.1, 0));
                } else {
                    defaultResistanceData.put(item.getRegistryName().toString(), new ResistanceData(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
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
                if (item.getRegistryName().toString().contains("sword") || item.getRegistryName().toString().contains("axe")) {
                    dc = "_slash";
                }
                if (item.getRegistryName().toString().contains("pickaxe") || (item.getRegistryName().toString().contains("bow"))) {
                    dc = "pierce";
                }
                defaultDamageData.put(item.getRegistryName().toString(), new DamageData(0, 0, 0, 0, 0, dc, 0, 0, 0));
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

    public static void serverInit(File configDir) {
        Map<String, ResistanceData> defaultResistanceData = Maps.newHashMap();
        Map<String, DamageData> defaultDamageData = Maps.newHashMap();
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
                defaultResistanceData.put(item.getRegistryName().toString(), new ResistanceData(0, 0, 0, 0 ,0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
            }
        }
        for (Item item : ForgeRegistries.ITEMS.getValues()) {
            Collection<AttributeModifier> weapon0 = item.getDefaultInstance().getAttributeModifiers(EquipmentSlot.MAINHAND).get(ATTACK_DAMAGE);
            Collection<AttributeModifier> weapon1 = item.getDefaultInstance().getAttributeModifiers(EquipmentSlot.OFFHAND).get(ATTACK_DAMAGE);
            double damageVal = weapon0.stream().mapToDouble(AttributeModifier::getAmount).sum() +
                    weapon1.stream().mapToDouble(AttributeModifier::getAmount).sum();
            if (damageVal > 0 || item instanceof BowItem || item instanceof CrossbowItem) {
                String dc = "strike";
                if (item.getRegistryName().toString().contains("sword") || item.getRegistryName().toString().contains("axe")) {
                    dc = "_slash";
                }
                if (item.getRegistryName().toString().contains("pickaxe") || (item.getRegistryName().toString().contains("bow"))) {
                    dc = "pierce";
                }
                defaultDamageData.put(item.getRegistryName().toString(), new DamageData(0, 0, 0, 0, 0, dc, 0, 0, 0));
            }
        }
        serverResistanceMap.clear();
        serverDamageDataMap.clear();
        Map<String, ResistanceData> tempResistanceMap = JSONUtil.getOrCreateConfigFile(configDir, "resistance_data.json", defaultResistanceData, new TypeToken<Map<String, ResistanceData>>() {
        }.getType());
        Map<String, DamageData> tempDamageMap = JSONUtil.getOrCreateConfigFile(configDir, "damage_data.json", defaultDamageData, new TypeToken<Map<String, DamageData>>() {
        }.getType());
        if (tempResistanceMap != null && !tempResistanceMap.isEmpty()) {
            for (Map.Entry<String, ResistanceData> entry : tempResistanceMap.entrySet()) {
                serverResistanceMap.put(new ResourceLocation(entry.getKey()), entry.getValue());
            }
        }
        if (tempDamageMap != null && !tempDamageMap.isEmpty()) {
            for (Map.Entry<String, DamageData> entry : tempDamageMap.entrySet()) {
                serverDamageDataMap.put(new ResourceLocation(entry.getKey()), entry.getValue());
            }
        }
    }

    @OnlyIn(Dist.DEDICATED_SERVER)
    public static void updateServerFiles() {
        ImprovedDamageFramework.getLog().info("Updating server's json configs...");
        Path modConfigPath = Paths.get(FMLPaths.CONFIGDIR.get().toAbsolutePath().toString(), "ImprovedDamageFramework");
        Map<String, ResistanceData> tempResistanceMap = JSONUtil.getConfigFile(modConfigPath.toFile(), "resistance_data.json", new TypeToken<Map<String, ResistanceData>>() {
        }.getType());
        Map<String, DamageData> tempDamageMap = JSONUtil.getConfigFile(modConfigPath.toFile(), "damage_data.json", new TypeToken<Map<String, DamageData>>() {
        }.getType());
        Map<String, EntityData> tempEntityMap = JSONUtil.getConfigFile(modConfigPath.toFile(), "entity_data.json", new TypeToken<Map<String, EntityData>>() {
        }.getType());
        sourceMap = JSONUtil.getConfigFile(modConfigPath.toFile(), "source_catcher.json", new TypeToken<Map<String, SourceCatcherData>>() {
        }.getType());
        ImprovedDamageFramework.getLog().info("Updated source map");

        if (tempResistanceMap != null && !tempResistanceMap.isEmpty()) {
            for (Map.Entry<String, ResistanceData> entry : tempResistanceMap.entrySet()) {
                serverResistanceMap.put(new ResourceLocation(entry.getKey()), entry.getValue());
                resistanceMap.put(new ResourceLocation(entry.getKey()), entry.getValue());
            }
            ImprovedDamageFramework.getLog().info("Updated armour map.");
        }
        if (tempDamageMap != null && !tempDamageMap.isEmpty()) {
            for (Map.Entry<String, DamageData> entry : tempDamageMap.entrySet()) {
                serverDamageDataMap.put(new ResourceLocation(entry.getKey()), entry.getValue());
                damageMap.put(new ResourceLocation(entry.getKey()), entry.getValue());
            }
            ImprovedDamageFramework.getLog().info("Updated weapon map.");
        }
        if (tempEntityMap != null && !tempEntityMap.isEmpty()) {
            for (Map.Entry<String, EntityData> entry : tempEntityMap.entrySet()) {
                entityMap.put(new ResourceLocation(entry.getKey()), entry.getValue());
            }
            ImprovedDamageFramework.getLog().info("Updated entity map.");
        }
        if (!serverResistanceMap.isEmpty()) {
            IDFPackerHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new SendServerResistanceJsonMessage(serverResistanceMap));
            ImprovedDamageFramework.getLog().info("Sending server resistance map to all clients...");
        }
        if (!serverDamageDataMap.isEmpty()) {
            IDFPackerHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new SendServerDamageJsonMessage(serverDamageDataMap));
            ImprovedDamageFramework.getLog().info("Sending server damage map to all clients...");
        }
    }

    @OnlyIn(Dist.DEDICATED_SERVER)
    @SubscribeEvent
    public static void playerJoinWorldEvent(PlayerEvent.PlayerLoggedInEvent event) {
        ServerPlayer player = (ServerPlayer) event.getPlayer();
        if (!serverResistanceMap.isEmpty()) {
            IDFPackerHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new SendServerResistanceJsonMessage(serverResistanceMap));
            ImprovedDamageFramework.getLog().info("Sending server resistance data values to player: " + player.getScoreboardName());
        }
        if (!serverDamageDataMap.isEmpty()) {
            IDFPackerHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new SendServerDamageJsonMessage(serverDamageDataMap));
            ImprovedDamageFramework.getLog().info("Sending server damage data values to player: " + player.getScoreboardName());
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

    @OnlyIn(Dist.CLIENT)
    public static void updateClientResistanceData(Map<ResourceLocation, ResistanceData> map) {
        resistanceMap = map;
        if (Minecraft.getInstance().player != null) {
            Minecraft.getInstance().player.sendMessage(new TranslatableComponent("idf.client.armour.map.updated"), Util.NIL_UUID);
            System.out.println("post update(CLIENT): " + resistanceMap.get(new ResourceLocation("minecraft:leather_chestplate")).getAuxiliary()[0]);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void updateClientDamageData(Map<ResourceLocation, DamageData> map) {
        damageMap = map;
        if (Minecraft.getInstance().player != null) {
            Minecraft.getInstance().player.sendMessage(new TranslatableComponent("idf.client.weapon.map.updated"), Util.NIL_UUID);
        }
    }

}
