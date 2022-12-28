package net.cwjn.idf.config.json;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.cwjn.idf.ImprovedDamageFramework;
import net.cwjn.idf.api.IDFCustomEquipment;
import net.cwjn.idf.config.json.data.*;
import net.cwjn.idf.network.PacketHandler;
import net.cwjn.idf.network.packets.SyncClientConfigPacket;
import net.cwjn.idf.util.ItemInterface;
import net.cwjn.idf.util.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.*;

import static net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.*;
import static net.minecraft.world.entity.ai.attributes.Attributes.ARMOR;
import static net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE;

@SuppressWarnings(value = "UnstableApiUsage")
@Mod.EventBusSubscriber
public class JSONHandler {
    public static final Map<ResourceLocation, Multimap<Attribute, AttributeModifier>> baseModifiers = new HashMap<>();
    public static final Map<ResourceLocation, Integer> vanillaDurability = new HashMap<>();
    public static Map<ResourceLocation, ArmourData> armourItemsOp0 = new HashMap<>();
    public static Map<ResourceLocation, ItemData> armourItemsOp1 = new HashMap<>();
    public static Map<ResourceLocation, ItemData> armourItemsOp2 = new HashMap<>();
    public static Map<ResourceLocation, WeaponData> weaponItemsOp0 = new HashMap<>();
    public static Map<ResourceLocation, ItemData> weaponItemsOp1 = new HashMap<>();
    public static Map<ResourceLocation, ItemData> weaponItemsOp2 = new HashMap<>();
    public static Map<ResourceLocation, ArmourData> originalArmourItemsOp0;
    public static Map<ResourceLocation, ItemData> originalArmourItemsOp1;
    public static Map<ResourceLocation, ItemData> originalArmourItemsOp2;
    public static Map<ResourceLocation, WeaponData> originalWeaponItemsOp0;
    public static Map<ResourceLocation, ItemData> originalWeaponItemsOp1;
    public static Map<ResourceLocation, ItemData> originalWeaponItemsOp2;
    public static Map<ResourceLocation, EntityData> entityMap = new HashMap<>();
    public static Map<String, SourceCatcherData> sourceMap = new HashMap<>();
    public static final Gson SERIALIZER = new GsonBuilder().
            setPrettyPrinting().
            registerTypeAdapter(ArmourData.class, new ArmourData.ArmourSerializer()).
            registerTypeAdapter(WeaponData.class, new WeaponData.WeaponSerializer()).
            registerTypeAdapter(ItemData.class, new ItemData.ItemSerializer()).
            create();

    public static void init(File configDir) {
        Map<String, ArmourData> defaultArmourItemsOp0  = SERIALIZER.fromJson(new BufferedReader(new InputStreamReader(Objects.requireNonNull(JSONHandler.class.getClassLoader().getResourceAsStream("data/idf/default/armour_items_operation_addition.json")))), new TypeToken<Map<String, ArmourData>>(){}.getType());
        Map<String, ItemData> defaultArmourItemsOp1  = SERIALIZER.fromJson(new BufferedReader(new InputStreamReader(Objects.requireNonNull(JSONHandler.class.getClassLoader().getResourceAsStream("data/idf/default/armour_items_operation_multiply_base.json")))), new TypeToken<Map<String, ItemData>>(){}.getType());
        Map<String, ItemData> defaultArmourItemsOp2  = SERIALIZER.fromJson(new BufferedReader(new InputStreamReader(Objects.requireNonNull(JSONHandler.class.getClassLoader().getResourceAsStream("data/idf/default/armour_items_operation_multiply_total.json")))), new TypeToken<Map<String, ItemData>>(){}.getType());
        Map<String, WeaponData> defaultWeaponItemsOp0  = SERIALIZER.fromJson(new BufferedReader(new InputStreamReader(Objects.requireNonNull(JSONHandler.class.getClassLoader().getResourceAsStream("data/idf/default/weapon_items_operation_addition.json")))), new TypeToken<Map<String, WeaponData>>(){}.getType());
        Map<String, ItemData> defaultWeaponItemsOp1 = SERIALIZER.fromJson(new BufferedReader(new InputStreamReader(Objects.requireNonNull(JSONHandler.class.getClassLoader().getResourceAsStream("data/idf/default/weapon_items_operation_multiply_base.json")))), new TypeToken<Map<String, ItemData>>(){}.getType());
        Map<String, ItemData> defaultWeaponItemsOp2  = SERIALIZER.fromJson(new BufferedReader(new InputStreamReader(Objects.requireNonNull(JSONHandler.class.getClassLoader().getResourceAsStream("data/idf/default/weapon_items_operation_multiply_total.json")))), new TypeToken<Map<String, ItemData>>(){}.getType());
        Map<String, EntityData> defaultEntityData = SERIALIZER.fromJson(new BufferedReader(new InputStreamReader(Objects.requireNonNull(JSONHandler.class.getClassLoader().getResourceAsStream("data/idf/default/entity_data.json")))), new TypeToken<Map<String, EntityData>>(){}.getType());
        Map<String, SourceCatcherData> defaultSourceData = SERIALIZER.fromJson(new BufferedReader(new InputStreamReader(Objects.requireNonNull(JSONHandler.class.getClassLoader().getResourceAsStream("data/idf/default/source_catcher.json")))), new TypeToken<Map<String, SourceCatcherData>>(){}.getType());
        for (EntityType<?> entityType : ForgeRegistries.ENTITY_TYPES.getValues()) {
            if (entityType.getCategory() != MobCategory.MISC) { //make sure this isn't an arrow entity or something
                defaultEntityData.putIfAbsent(Util.getEntityRegistryName(entityType).toString(), new EntityData(
                        0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, "strike",
                        0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D,
                        1.0D, 1.0D, 1.0D, 1.0D, 1.0D,
                        0.0D, 0.0D, 0.4, -1.0D, 0.0D, 0.0D, 0.0D, 0.0D));
            }
        }
        for (Item item : ForgeRegistries.ITEMS.getValues()) {
            if (item instanceof SwordItem || item instanceof DiggerItem || item instanceof BowItem || item instanceof CrossbowItem || item instanceof TridentItem) {
                if (item instanceof IDFCustomEquipment modItem) {
                    defaultWeaponItemsOp0.putIfAbsent(Util.getItemRegistryName(item).toString(),
                            new WeaponData(0, ((ItemInterface) modItem).getDamageClass(), 0, 0, 0, 0, 0, 0,
                            0, 0, 0, 0, 0,
                                    0, 0, 0, 0, 0, 0, 0,
                                    0, 0, 0, 0, 0,
                                    0, 0, 0, 0, 0, 0));
                } else {
                    String dc = "strike";
                    if (Util.getItemRegistryName(item).toString().contains("sword") || Util.getItemRegistryName(item).toString().contains("axe")) {
                        dc = "slash";
                    }
                    if (Util.getItemRegistryName(item).toString().contains("pickaxe") || Util.getItemRegistryName(item).toString().contains("bow")) {
                        dc = "pierce";
                    }
                    defaultWeaponItemsOp0.putIfAbsent(Util.getItemRegistryName(item).toString(),
                            new WeaponData(0, dc, 0, 0, 0, 0, 0, 0,
                                    0, 0, 0, 0, 0,
                                    0, 0, 0, 0, 0, 0, 0,
                                    0, 0, 0, 0, 0,
                                    0, 0, 0, 0, 0, 0));
                }
                defaultWeaponItemsOp1.putIfAbsent(Util.getItemRegistryName(item).toString(), ItemData.empty());
                defaultWeaponItemsOp2.putIfAbsent(Util.getItemRegistryName(item).toString(), ItemData.empty());
            }
            else {
                Collection<AttributeModifier> weapon0 = item.getDefaultInstance().getAttributeModifiers(EquipmentSlot.MAINHAND).get(ATTACK_DAMAGE);
                double damageVal = weapon0.stream().mapToDouble(AttributeModifier::getAmount).sum();
                if (damageVal > 0) {
                    String dc = "strike";
                    if (Util.getItemRegistryName(item).toString().contains("sword") || Util.getItemRegistryName(item).toString().contains("axe")) {
                        dc = "slash";
                    }
                    if (Util.getItemRegistryName(item).toString().contains("pickaxe") || Util.getItemRegistryName(item).toString().contains("bow")) {
                        dc = "pierce";
                    }
                    defaultWeaponItemsOp0.putIfAbsent(Util.getItemRegistryName(item).toString(),
                            new WeaponData(0, dc, 0, 0, 0, 0, 0, 0,
                                    0, 0, 0, 0, 0,
                                    0, 0, 0, 0, 0, 0, 0,
                                    0, 0, 0, 0, 0,
                                    0, 0, 0, 0, 0, 0));
                    defaultWeaponItemsOp1.putIfAbsent(Util.getItemRegistryName(item).toString(), ItemData.empty());
                    defaultWeaponItemsOp2.putIfAbsent(Util.getItemRegistryName(item).toString(), ItemData.empty());
                }
                Collection<AttributeModifier> armour0 = item.getDefaultInstance().getAttributeModifiers(EquipmentSlot.HEAD).get(ARMOR);
                Collection<AttributeModifier> armour1 = item.getDefaultInstance().getAttributeModifiers(EquipmentSlot.FEET).get(ARMOR);
                Collection<AttributeModifier> armour2 = item.getDefaultInstance().getAttributeModifiers(EquipmentSlot.LEGS).get(ARMOR);
                Collection<AttributeModifier> armour3 = item.getDefaultInstance().getAttributeModifiers(EquipmentSlot.CHEST).get(ARMOR);
                double armorVal = armour0.stream().mapToDouble(AttributeModifier::getAmount).sum() +
                        armour1.stream().mapToDouble(AttributeModifier::getAmount).sum() +
                        armour2.stream().mapToDouble(AttributeModifier::getAmount).sum() +
                        armour3.stream().mapToDouble(AttributeModifier::getAmount).sum();
                if (armorVal > 0) {
                    defaultArmourItemsOp0.putIfAbsent(Util.getItemRegistryName(item).toString(), ArmourData.empty());
                    defaultArmourItemsOp1.putIfAbsent(Util.getItemRegistryName(item).toString(), ItemData.empty());
                    defaultArmourItemsOp2.putIfAbsent(Util.getItemRegistryName(item).toString(), ItemData.empty());
                }
            }
        }
        armourItemsOp0.clear();
        armourItemsOp1.clear();
        armourItemsOp2.clear();
        weaponItemsOp0.clear();
        weaponItemsOp1.clear();
        weaponItemsOp2.clear();
        entityMap.clear();
        sourceMap.clear();
        Map<String, ArmourData> tempArmourOp0Map = JSONUtil.getOrCreateConfigFile(configDir, "armour_items_operation_addition.json", defaultArmourItemsOp0, new TypeToken<Map<String, ArmourData>>() {}.getType());
        Map<String, ItemData> tempArmourOp1Map = JSONUtil.getOrCreateConfigFile(configDir, "armour_items_operation_multiply_base.json", defaultArmourItemsOp1, new TypeToken<Map<String, ItemData>>() {}.getType());
        Map<String, ItemData> tempArmourOp2Map = JSONUtil.getOrCreateConfigFile(configDir, "armour_items_operation_multiply_total.json", defaultArmourItemsOp2, new TypeToken<Map<String, ItemData>>() {}.getType());
        Map<String, WeaponData> tempWeaponOp0Map = JSONUtil.getOrCreateConfigFile(configDir, "weapon_items_operation_addition.json", defaultWeaponItemsOp0, new TypeToken<Map<String, WeaponData>>() {}.getType());
        Map<String, ItemData> tempWeaponOp1Map = JSONUtil.getOrCreateConfigFile(configDir, "weapon_items_operation_multiply_base.json", defaultWeaponItemsOp1, new TypeToken<Map<String, ItemData>>() {}.getType());
        Map<String, ItemData> tempWeaponOp2Map = JSONUtil.getOrCreateConfigFile(configDir, "weapon_items_operation_multiply_total.json", defaultWeaponItemsOp2, new TypeToken<Map<String, ItemData>>() {}.getType());
        Map<String, EntityData> tempEntityMap = JSONUtil.getOrCreateConfigFile(configDir, "entity_data.json", defaultEntityData, new TypeToken<Map<String, EntityData>>() {}.getType());
        sourceMap = JSONUtil.getOrCreateConfigFile(configDir, "source_catcher.json", defaultSourceData, new TypeToken<Map<String, SourceCatcherData>>() {}.getType());
        for (Map.Entry<String, EntityData> entry : defaultEntityData.entrySet()) {
            tempEntityMap.putIfAbsent(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, ArmourData> entry : defaultArmourItemsOp0.entrySet()) {
            tempArmourOp0Map.putIfAbsent(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, ItemData> entry : defaultArmourItemsOp1.entrySet()) {
            tempArmourOp1Map.putIfAbsent(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, ItemData> entry : defaultArmourItemsOp2.entrySet()) {
            tempArmourOp2Map.putIfAbsent(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, WeaponData> entry : defaultWeaponItemsOp0.entrySet()) {
            tempWeaponOp0Map.putIfAbsent(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, ItemData> entry : defaultWeaponItemsOp1.entrySet()) {
            tempWeaponOp1Map.putIfAbsent(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, ItemData> entry : defaultWeaponItemsOp2.entrySet()) {
            tempWeaponOp2Map.putIfAbsent(entry.getKey(), entry.getValue());
        }
        SortedMap<String, ArmourData> sortedArmourOp0Map = new TreeMap<>(tempArmourOp0Map);
        SortedMap<String, ItemData> sortedArmourOp1Map = new TreeMap<>(tempArmourOp1Map);
        SortedMap<String, ItemData> sortedArmourOp2Map = new TreeMap<>(tempArmourOp2Map);
        SortedMap<String, WeaponData> sortedWeaponOp0Map = new TreeMap<>(tempWeaponOp0Map);
        SortedMap<String, ItemData> sortedWeaponOp1Map = new TreeMap<>(tempWeaponOp1Map);
        SortedMap<String, ItemData> sortedWeaponOp2Map = new TreeMap<>(tempWeaponOp2Map);
        SortedMap<String, EntityData> sortedEntityMap = new TreeMap<>(tempEntityMap);
        if (!sortedEntityMap.isEmpty()) {
            for (Map.Entry<String, EntityData> entry : tempEntityMap.entrySet()) {
                entityMap.put(new ResourceLocation(entry.getKey()), entry.getValue());
            }
        }
        if (!sortedArmourOp0Map.isEmpty()) {
            for (Map.Entry<String, ArmourData> entry : tempArmourOp0Map.entrySet()) {
                armourItemsOp0.put(new ResourceLocation(entry.getKey()), entry.getValue());
            }
        }
        if (!sortedArmourOp1Map.isEmpty()) {
            for (Map.Entry<String, ItemData> entry : tempArmourOp1Map.entrySet()) {
                armourItemsOp1.put(new ResourceLocation(entry.getKey()), entry.getValue());
            }
        }
        if (!sortedArmourOp2Map.isEmpty()) {
            for (Map.Entry<String, ItemData> entry : tempArmourOp2Map.entrySet()) {
                armourItemsOp2.put(new ResourceLocation(entry.getKey()), entry.getValue());
            }
        }
        if (!sortedWeaponOp0Map.isEmpty()) {
            for (Map.Entry<String, WeaponData> entry : tempWeaponOp0Map.entrySet()) {
                weaponItemsOp0.put(new ResourceLocation(entry.getKey()), entry.getValue());
            }
        }
        if (!sortedWeaponOp1Map.isEmpty()) {
            for (Map.Entry<String, ItemData> entry : tempWeaponOp1Map.entrySet()) {
                weaponItemsOp1.put(new ResourceLocation(entry.getKey()), entry.getValue());
            }
        }
        if (!sortedWeaponOp2Map.isEmpty()) {
            for (Map.Entry<String, ItemData> entry : tempWeaponOp2Map.entrySet()) {
                weaponItemsOp2.put(new ResourceLocation(entry.getKey()), entry.getValue());
            }
        }
        saveVanillaStats();
        updateItems();
        originalArmourItemsOp0 = armourItemsOp0;
        originalArmourItemsOp1 = armourItemsOp1;
        originalArmourItemsOp2 = armourItemsOp2;
        originalWeaponItemsOp0 = weaponItemsOp0;
        originalWeaponItemsOp1 = weaponItemsOp1;
        originalWeaponItemsOp2 = weaponItemsOp2;
        JSONUtil.writeFile(new File(configDir, "entity_data.json"), sortedEntityMap);
        JSONUtil.writeFile(new File(configDir, "armour_items_operation_addition.json"), sortedArmourOp0Map);
        JSONUtil.writeFile(new File(configDir, "armour_items_operation_multiply_base.json"), sortedArmourOp1Map);
        JSONUtil.writeFile(new File(configDir, "armour_items_operation_multiply_total.json"), sortedArmourOp2Map);
        JSONUtil.writeFile(new File(configDir, "weapon_items_operation_addition.json"), sortedWeaponOp0Map);
        JSONUtil.writeFile(new File(configDir, "weapon_items_operation_multiply_base.json"), sortedWeaponOp1Map);
        JSONUtil.writeFile(new File(configDir, "weapon_items_operation_multiply_total.json"), sortedWeaponOp2Map);
    }

    @OnlyIn(Dist.DEDICATED_SERVER)
    @SubscribeEvent
    public static void onPlayerLoginEventServer(PlayerEvent.PlayerLoggedInEvent event) {
        PacketHandler.serverToPlayer(
                new SyncClientConfigPacket(weaponItemsOp0, weaponItemsOp1, weaponItemsOp2, armourItemsOp0, armourItemsOp1, armourItemsOp2),
                (ServerPlayer) event.getEntity());
        ImprovedDamageFramework.LOGGER.info("Sent server mappings to player " + event.getEntity().getScoreboardName());
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onPlayerLoginEventClient(PlayerEvent.PlayerLoggedInEvent event) {
        armourItemsOp0 = originalArmourItemsOp0;
        armourItemsOp1 = originalArmourItemsOp1;
        armourItemsOp2 = originalArmourItemsOp2;
        weaponItemsOp0 = originalWeaponItemsOp0;
        weaponItemsOp1 = originalWeaponItemsOp1;
        weaponItemsOp2 = originalWeaponItemsOp2;
        updateItems();
        ImprovedDamageFramework.LOGGER.info("Restored client mappings");
    }

    public static EntityData getEntityData(ResourceLocation key) {
        return entityMap.getOrDefault(key, null);
    }

    private static void saveVanillaStats() {
        for (Item item : ForgeRegistries.ITEMS.getValues()) {
            ItemInterface idfItem = (ItemInterface) item;
            if (idfItem.getDefaultModifiers() != null) {
                baseModifiers.put(Util.getItemRegistryName(item), idfItem.getDefaultModifiers());
            }
            if (item.canBeDepleted()) {
                vanillaDurability.put(Util.getItemRegistryName(item), item.getMaxDamage());
            }
        }
    }

    public static void updateItems() {
        for (Item item : ForgeRegistries.ITEMS.getValues()) {
            ItemInterface idfItem = (ItemInterface) item;
            CompoundTag defaultTag = new CompoundTag();
            ResourceLocation loc = Util.getItemRegistryName(item);
            ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
            if (baseModifiers.get(loc) != null) {
                for (Map.Entry<Attribute, AttributeModifier> entry : baseModifiers.get(loc).entries()) {
                    builder.put(entry.getKey(), entry.getValue());
                }
            }
            if (weaponItemsOp0.containsKey(loc) || weaponItemsOp1.containsKey(loc) || weaponItemsOp2.containsKey(loc)) {
                defaultTag.putBoolean("idf.equipment", true);
                WeaponData data0 = weaponItemsOp0.get(loc);
                ItemData data1 = weaponItemsOp1.get(loc);
                ItemData data2 = weaponItemsOp2.get(loc);
                if (data0 != null) {
                    data0.forEach(pair -> {
                        if (pair.getB() != 0) {
                            builder.put(pair.getA(), new AttributeModifier("data0", pair.getB(), ADDITION));
                        }
                    });
                    defaultTag.putString("idf.damage_class", data0.damageClass());
                    idfItem.setMaxDamage(vanillaDurability.getOrDefault(loc, 0) + data0.durability());
                }
                if (data1 != null) {
                    data1.forEach(pair -> {
                        if (pair.getB() != 0) {
                            builder.put(pair.getA(), new AttributeModifier("data1", pair.getB(), MULTIPLY_BASE));
                        }
                    });
                }
                if (data2 != null) {
                    data2.forEach(pair -> {
                        if (pair.getB() != 0) {
                            builder.put(pair.getA(), new AttributeModifier("data2", pair.getB(), MULTIPLY_TOTAL));
                        }
                    });
                }
            }
            else if (armourItemsOp0.containsKey(loc) || armourItemsOp1.containsKey(loc) || armourItemsOp2.containsKey(loc)) {
                defaultTag.putBoolean("idf.equipment", true);
                ArmourData data0 = armourItemsOp0.get(loc);
                ItemData data1 = armourItemsOp1.get(loc);
                ItemData data2 = armourItemsOp2.get(loc);
                if (data0 != null) {
                    data0.forEach(pair -> {
                        if (pair.getB() != 0) {
                            builder.put(pair.getA(), new AttributeModifier("data0", pair.getB(), ADDITION));
                        }
                    });
                    idfItem.setMaxDamage(vanillaDurability.getOrDefault(loc, 0) + data0.durability());
                }
                if (data1 != null) {
                    data1.forEach(pair -> {
                        if (pair.getB() != 0) {
                            builder.put(pair.getA(), new AttributeModifier("data1", pair.getB(), MULTIPLY_BASE));
                        }
                    });
                }
                if (data2 != null) {
                    data2.forEach(pair -> {
                        if (pair.getB() != 0) {
                            builder.put(pair.getA(), new AttributeModifier("data2", pair.getB(), MULTIPLY_TOTAL));
                        }
                    });
                }
            }
            if (!defaultTag.isEmpty()) idfItem.setDefaultTag(defaultTag);
            idfItem.setDefaultAttributes(builder.build());
        }
    }

}
