package net.cwjn.idf.config.json;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.cwjn.idf.Data;
import net.cwjn.idf.ImprovedDamageFramework;
import net.cwjn.idf.api.IDFCustomEquipment;
import net.cwjn.idf.api.event.OnItemAttributeRework;
import net.cwjn.idf.config.json.data.*;
import net.cwjn.idf.config.json.data.subtypes.AuxiliaryData;
import net.cwjn.idf.config.json.data.subtypes.DefensiveData;
import net.cwjn.idf.config.json.data.subtypes.OffensiveData;
import net.cwjn.idf.network.PacketHandler;
import net.cwjn.idf.network.packets.SyncClientConfigPacket;
import net.cwjn.idf.util.ItemInterface;
import net.cwjn.idf.util.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.*;

import static net.cwjn.idf.Data.LogicalData.*;
import static net.cwjn.idf.Data.ClientData.*;
import static net.cwjn.idf.config.json.data.EntityDataTemplate.NONE;
import static net.cwjn.idf.util.Util.UUID_BASE_STAT_ADDITION;
import static net.cwjn.idf.util.Util.UUID_BASE_STAT_MULTIPLY_BASE;
import static net.cwjn.idf.util.Util.UUID_BASE_STAT_MULTIPLY_TOTAL;
import static net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.*;
import static net.minecraft.world.entity.ai.attributes.Attributes.ARMOR;
import static net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE;

@SuppressWarnings(value = "UnstableApiUsage")
@Mod.EventBusSubscriber
public class JSONHandler {
    public static final Map<ResourceLocation, Multimap<Attribute, AttributeModifier>> baseModifiers = new HashMap<>();
    public static final Map<ResourceLocation, Integer> vanillaDurability = new HashMap<>();
    public static final Gson SERIALIZER = new GsonBuilder().
            setPrettyPrinting().
            registerTypeAdapter(ArmourData.class, new ArmourData.ArmourSerializer()).
            registerTypeAdapter(WeaponData.class, new WeaponData.WeaponSerializer()).
            registerTypeAdapter(ItemData.class, new ItemData.ItemSerializer()).
            registerTypeAdapter(EntityData.class, new EntityData.EntityDataSerializer()).
            registerTypeAdapter(OffensiveData.class, new OffensiveData.OffensiveDataSerializer()).
            registerTypeAdapter(DefensiveData.class, new DefensiveData.DefensiveDataSerializer()).
            registerTypeAdapter(AuxiliaryData.class, new AuxiliaryData.AuxiliaryDataSerializer()).
            registerTypeAdapter(RpgItemData.StatObject.class, new RpgItemData.StatObjectSerializer()).
            create();

    public static void init(File configDir) {

        //instantiate default maps
        Map<String, ArmourData> DEFAULT_ARMOUR_0  = SERIALIZER.fromJson(new BufferedReader(new InputStreamReader(Objects.requireNonNull(JSONHandler.class.getClassLoader().getResourceAsStream("data/idf/default/armour_items_operation_addition.json")))), new TypeToken<Map<String, ArmourData>>(){}.getType());
        Map<String, ItemData> DEFAULT_ARMOUR_1  = SERIALIZER.fromJson(new BufferedReader(new InputStreamReader(Objects.requireNonNull(JSONHandler.class.getClassLoader().getResourceAsStream("data/idf/default/armour_items_operation_multiply_base.json")))), new TypeToken<Map<String, ItemData>>(){}.getType());
        Map<String, ItemData> DEFAULT_ARMOUR_2  = SERIALIZER.fromJson(new BufferedReader(new InputStreamReader(Objects.requireNonNull(JSONHandler.class.getClassLoader().getResourceAsStream("data/idf/default/armour_items_operation_multiply_total.json")))), new TypeToken<Map<String, ItemData>>(){}.getType());
        Map<String, WeaponData> DEFAULT_WEAPON_0  = SERIALIZER.fromJson(new BufferedReader(new InputStreamReader(Objects.requireNonNull(JSONHandler.class.getClassLoader().getResourceAsStream("data/idf/default/weapon_items_operation_addition.json")))), new TypeToken<Map<String, WeaponData>>(){}.getType());
        Map<String, ItemData> DEFAULT_WEAPON_1 = SERIALIZER.fromJson(new BufferedReader(new InputStreamReader(Objects.requireNonNull(JSONHandler.class.getClassLoader().getResourceAsStream("data/idf/default/weapon_items_operation_multiply_base.json")))), new TypeToken<Map<String, ItemData>>(){}.getType());
        Map<String, ItemData> DEFAULT_WEAPON_2  = SERIALIZER.fromJson(new BufferedReader(new InputStreamReader(Objects.requireNonNull(JSONHandler.class.getClassLoader().getResourceAsStream("data/idf/default/weapon_items_operation_multiply_total.json")))), new TypeToken<Map<String, ItemData>>(){}.getType());
        Map<String, EntityData> DEFAULT_ENTITY = SERIALIZER.fromJson(new BufferedReader(new InputStreamReader(Objects.requireNonNull(JSONHandler.class.getClassLoader().getResourceAsStream("data/idf/default/entity_data.json")))), new TypeToken<Map<String, EntityData>>(){}.getType());
        Map<String, SourceCatcherData> DEFAULT_SOURCE = SERIALIZER.fromJson(new BufferedReader(new InputStreamReader(Objects.requireNonNull(JSONHandler.class.getClassLoader().getResourceAsStream("data/idf/default/source_catcher.json")))), new TypeToken<Map<String, SourceCatcherData>>(){}.getType());

        //add any extra mobs and items to them from other mods
        for (EntityType<?> entityType : ForgeRegistries.ENTITY_TYPES.getValues()) {
            if (entityType.getCategory() != MobCategory.MISC) { //make sure this isn't an arrow entity or something
                DEFAULT_ENTITY.putIfAbsent(Util.getEntityRegistryName(entityType).toString(), new EntityData(NONE,
                        "strike", OffensiveData.entityStandard(), DefensiveData.entityStandard(), AuxiliaryData.empty()));
            }
        }
        for (Item item : ForgeRegistries.ITEMS.getValues()) {
            if (item instanceof SwordItem || item instanceof DiggerItem || item instanceof BowItem || item instanceof CrossbowItem || item instanceof TridentItem) {
                boolean r = item instanceof BowItem || item instanceof CrossbowItem;
                if (item instanceof IDFCustomEquipment modItem) {
                    DEFAULT_WEAPON_0.putIfAbsent(Util.getItemRegistryName(item).toString(),
                            new WeaponData(0, ((ItemInterface) modItem).getDamageClass(), r,
                                    OffensiveData.empty(),
                                    DefensiveData.empty(),
                                    AuxiliaryData.empty()));
                } else {
                    String dc = "strike";
                    if (Util.getItemRegistryName(item).toString().contains("sword") || Util.getItemRegistryName(item).toString().contains("axe")) {
                        dc = "slash";
                    }
                    if (Util.getItemRegistryName(item).toString().contains("pickaxe") || Util.getItemRegistryName(item).toString().contains("bow")) {
                        dc = "pierce";
                    }
                    DEFAULT_WEAPON_0.putIfAbsent(Util.getItemRegistryName(item).toString(),
                            new WeaponData(0, dc, r,
                                    OffensiveData.empty(),
                                    DefensiveData.empty(),
                                    AuxiliaryData.empty()));
                }
                DEFAULT_WEAPON_1.putIfAbsent(Util.getItemRegistryName(item).toString(), new ItemData(
                        OffensiveData.empty(), DefensiveData.empty(), AuxiliaryData.empty()
                ));
                DEFAULT_WEAPON_2.putIfAbsent(Util.getItemRegistryName(item).toString(), new ItemData(
                        OffensiveData.empty(), DefensiveData.empty(), AuxiliaryData.empty()
                ));
            }
            else {
                if (item instanceof IDFCustomEquipment) {
                    DEFAULT_ARMOUR_0.putIfAbsent(Util.getItemRegistryName(item).toString(), new ArmourData(0,
                            OffensiveData.empty(), DefensiveData.empty(), AuxiliaryData.empty()));
                    DEFAULT_ARMOUR_1.putIfAbsent(Util.getItemRegistryName(item).toString(), new ItemData(
                            OffensiveData.empty(), DefensiveData.empty(), AuxiliaryData.empty()));
                    DEFAULT_ARMOUR_2.putIfAbsent(Util.getItemRegistryName(item).toString(), new ItemData(
                            OffensiveData.empty(), DefensiveData.empty(), AuxiliaryData.empty()));
                } else {
                    Collection<AttributeModifier> weapon0 = item.getDefaultInstance().getAttributeModifiers(EquipmentSlot.MAINHAND).get(ATTACK_DAMAGE);
                    double damageVal = weapon0.stream().mapToDouble(AttributeModifier::getAmount).sum();
                    if (damageVal != 0) {
                        String dc = "strike";
                        if (Util.getItemRegistryName(item).toString().contains("sword") || Util.getItemRegistryName(item).toString().contains("axe")) {
                            dc = "slash";
                        }
                        if (Util.getItemRegistryName(item).toString().contains("pickaxe") || Util.getItemRegistryName(item).toString().contains("bow")) {
                            dc = "pierce";
                        }
                        DEFAULT_WEAPON_0.putIfAbsent(Util.getItemRegistryName(item).toString(),
                                new WeaponData(0, dc, false,
                                        OffensiveData.empty(),
                                        DefensiveData.empty(),
                                        AuxiliaryData.empty()));
                        DEFAULT_WEAPON_1.putIfAbsent(Util.getItemRegistryName(item).toString(), new ItemData(
                                OffensiveData.empty(), DefensiveData.empty(), AuxiliaryData.empty()
                        ));
                        DEFAULT_WEAPON_2.putIfAbsent(Util.getItemRegistryName(item).toString(), new ItemData(
                                OffensiveData.empty(), DefensiveData.empty(), AuxiliaryData.empty()
                        ));
                    }
                    Collection<AttributeModifier> armour0 = item.getDefaultInstance().getAttributeModifiers(EquipmentSlot.HEAD).get(ARMOR);
                    Collection<AttributeModifier> armour1 = item.getDefaultInstance().getAttributeModifiers(EquipmentSlot.FEET).get(ARMOR);
                    Collection<AttributeModifier> armour2 = item.getDefaultInstance().getAttributeModifiers(EquipmentSlot.LEGS).get(ARMOR);
                    Collection<AttributeModifier> armour3 = item.getDefaultInstance().getAttributeModifiers(EquipmentSlot.CHEST).get(ARMOR);
                    double armorVal = armour0.stream().mapToDouble(AttributeModifier::getAmount).sum() +
                            armour1.stream().mapToDouble(AttributeModifier::getAmount).sum() +
                            armour2.stream().mapToDouble(AttributeModifier::getAmount).sum() +
                            armour3.stream().mapToDouble(AttributeModifier::getAmount).sum();
                    if (armorVal != 0) {
                        DEFAULT_ARMOUR_0.putIfAbsent(Util.getItemRegistryName(item).toString(), new ArmourData(0,
                                OffensiveData.empty(), DefensiveData.empty(), AuxiliaryData.empty()));
                        DEFAULT_ARMOUR_1.putIfAbsent(Util.getItemRegistryName(item).toString(), new ItemData(
                                OffensiveData.empty(), DefensiveData.empty(), AuxiliaryData.empty()));
                        DEFAULT_ARMOUR_2.putIfAbsent(Util.getItemRegistryName(item).toString(), new ItemData(
                                OffensiveData.empty(), DefensiveData.empty(), AuxiliaryData.empty()));
                    }
                }
            }
        }

        //ensure we're starting with clean maps
        LOGICAL_ARMOUR_MAP_OP_0.clear();
        LOGICAL_ARMOUR_MAP_OP_1.clear();
        LOGICAL_ARMOUR_MAP_OP_2.clear();
        CLIENT_WEAPON_MAP_OP_0.clear();
        CLIENT_WEAPON_MAP_OP_1.clear();
        CLIENT_WEAPON_MAP_OP_2.clear();
        LOGICAL_ENTITY_MAP.clear();
        LOGICAL_SOURCE_MAP.clear();

        //grab the maps as strings instead of ResourceLocations from the config folder. If it doesn't exist, just use the default map
        Map<String, ArmourData> tempArmourOp0Map = JSONUtil.getOrCreateConfigFile(configDir, "armour_items_operation_addition.json", DEFAULT_ARMOUR_0, new TypeToken<Map<String, ArmourData>>() {}.getType());
        Map<String, ItemData> tempArmourOp1Map = JSONUtil.getOrCreateConfigFile(configDir, "armour_items_operation_multiply_base.json", DEFAULT_ARMOUR_1, new TypeToken<Map<String, ItemData>>() {}.getType());
        Map<String, ItemData> tempArmourOp2Map = JSONUtil.getOrCreateConfigFile(configDir, "armour_items_operation_multiply_total.json", DEFAULT_ARMOUR_2, new TypeToken<Map<String, ItemData>>() {}.getType());
        Map<String, WeaponData> tempWeaponOp0Map = JSONUtil.getOrCreateConfigFile(configDir, "weapon_items_operation_addition.json", DEFAULT_WEAPON_0, new TypeToken<Map<String, WeaponData>>() {}.getType());
        Map<String, ItemData> tempWeaponOp1Map = JSONUtil.getOrCreateConfigFile(configDir, "weapon_items_operation_multiply_base.json", DEFAULT_WEAPON_1, new TypeToken<Map<String, ItemData>>() {}.getType());
        Map<String, ItemData> tempWeaponOp2Map = JSONUtil.getOrCreateConfigFile(configDir, "weapon_items_operation_multiply_total.json", DEFAULT_WEAPON_2, new TypeToken<Map<String, ItemData>>() {}.getType());
        Map<String, EntityData> tempEntityMap = JSONUtil.getOrCreateConfigFile(configDir, "entity_data.json", DEFAULT_ENTITY, new TypeToken<Map<String, EntityData>>() {}.getType());
        Map<String, SourceCatcherData> tempSourceMap = JSONUtil.getOrCreateConfigFile(configDir, "source_catcher.json", DEFAULT_SOURCE, new TypeToken<Map<String, SourceCatcherData>>() {}.getType());

        //if the maps taken from the config folder are missing entries, fill them in
        //this is helpful if the user accidentally deletes an entry, or adds new mods
        for (Map.Entry<String, ArmourData> entry : DEFAULT_ARMOUR_0.entrySet()) {
            tempArmourOp0Map.putIfAbsent(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, ItemData> entry : DEFAULT_ARMOUR_1.entrySet()) {
            tempArmourOp1Map.putIfAbsent(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, ItemData> entry : DEFAULT_ARMOUR_2.entrySet()) {
            tempArmourOp2Map.putIfAbsent(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, WeaponData> entry : DEFAULT_WEAPON_0.entrySet()) {
            tempWeaponOp0Map.putIfAbsent(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, ItemData> entry : DEFAULT_WEAPON_1.entrySet()) {
            tempWeaponOp1Map.putIfAbsent(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, ItemData> entry : DEFAULT_WEAPON_2.entrySet()) {
            tempWeaponOp2Map.putIfAbsent(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, EntityData> entry : DEFAULT_ENTITY.entrySet()) {
            tempEntityMap.putIfAbsent(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, SourceCatcherData> entry : DEFAULT_SOURCE.entrySet()) {
            tempSourceMap.putIfAbsent(entry.getKey(), entry.getValue());
        }

        //sort the maps before converting to resource locations so the json files get sorted automatically
        //by mod first then item/entity name
        SortedMap<String, ArmourData> sortedArmourOp0Map = new TreeMap<>(tempArmourOp0Map);
        SortedMap<String, ItemData> sortedArmourOp1Map = new TreeMap<>(tempArmourOp1Map);
        SortedMap<String, ItemData> sortedArmourOp2Map = new TreeMap<>(tempArmourOp2Map);
        SortedMap<String, WeaponData> sortedWeaponOp0Map = new TreeMap<>(tempWeaponOp0Map);
        SortedMap<String, ItemData> sortedWeaponOp1Map = new TreeMap<>(tempWeaponOp1Map);
        SortedMap<String, ItemData> sortedWeaponOp2Map = new TreeMap<>(tempWeaponOp2Map);
        SortedMap<String, EntityData> sortedEntityMap = new TreeMap<>(tempEntityMap);
        SortedMap<String, SourceCatcherData> sortedSourceMap = new TreeMap<>(tempSourceMap);

        //now we fill the actual maps that get used by the game
        if (!sortedArmourOp0Map.isEmpty()) {
            for (Map.Entry<String, ArmourData> entry : sortedArmourOp0Map.entrySet()) {
                LOGICAL_ARMOUR_MAP_OP_0.put(new ResourceLocation(entry.getKey()), entry.getValue());
            }
        }
        if (!sortedArmourOp1Map.isEmpty()) {
            for (Map.Entry<String, ItemData> entry : sortedArmourOp1Map.entrySet()) {
                LOGICAL_ARMOUR_MAP_OP_1.put(new ResourceLocation(entry.getKey()), entry.getValue());
            }
        }
        if (!sortedArmourOp2Map.isEmpty()) {
            for (Map.Entry<String, ItemData> entry : sortedArmourOp2Map.entrySet()) {
                LOGICAL_ARMOUR_MAP_OP_2.put(new ResourceLocation(entry.getKey()), entry.getValue());
            }
        }
        if (!sortedWeaponOp0Map.isEmpty()) {
            for (Map.Entry<String, WeaponData> entry : sortedWeaponOp0Map.entrySet()) {
                LOGICAL_WEAPON_MAP_OP_0.put(new ResourceLocation(entry.getKey()), entry.getValue());
            }
        }
        if (!sortedWeaponOp1Map.isEmpty()) {
            for (Map.Entry<String, ItemData> entry : sortedWeaponOp1Map.entrySet()) {
                LOGICAL_WEAPON_MAP_OP_1.put(new ResourceLocation(entry.getKey()), entry.getValue());
            }
        }
        if (!sortedWeaponOp2Map.isEmpty()) {
            for (Map.Entry<String, ItemData> entry : sortedWeaponOp2Map.entrySet()) {
                LOGICAL_WEAPON_MAP_OP_2.put(new ResourceLocation(entry.getKey()), entry.getValue());
            }
        }
        if (!sortedEntityMap.isEmpty()) {
            for (Map.Entry<String, EntityData> entry : sortedEntityMap.entrySet()) {
                LOGICAL_ENTITY_MAP.put(new ResourceLocation(entry.getKey()), entry.getValue());
            }
        }
        if (!sortedSourceMap.isEmpty()) {
            LOGICAL_SOURCE_MAP = sortedSourceMap;
        }

        //Now use the maps to update items and then save the physical client's mappings
        saveVanillaStats();
        updateItems();
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> Data.ClientData::saveClientMappings);

        //Write the maps to config json files so users can edit items
        JSONUtil.writeFile(new File(configDir, "entity_data.json"), sortedEntityMap);
        JSONUtil.writeFile(new File(configDir, "armour_items_operation_addition.json"), sortedArmourOp0Map);
        JSONUtil.writeFile(new File(configDir, "armour_items_operation_multiply_base.json"), sortedArmourOp1Map);
        JSONUtil.writeFile(new File(configDir, "armour_items_operation_multiply_total.json"), sortedArmourOp2Map);
        JSONUtil.writeFile(new File(configDir, "weapon_items_operation_addition.json"), sortedWeaponOp0Map);
        JSONUtil.writeFile(new File(configDir, "weapon_items_operation_multiply_base.json"), sortedWeaponOp1Map);
        JSONUtil.writeFile(new File(configDir, "weapon_items_operation_multiply_total.json"), sortedWeaponOp2Map);

        //this is for ImprovedAdventureFramework
        if (ModList.get().isLoaded("iaf")) {
            File iafDir = Paths.get(FMLPaths.CONFIGDIR.get().toAbsolutePath().toString(), "ImprovedAdventureFramework").toFile();
            Map<String, RpgItemData> weapons = new HashMap<>(); //SERIALIZER.fromJson(new BufferedReader(new InputStreamReader(Objects.requireNonNull(JSONHandler.class.getClassLoader().getResourceAsStream("data/iaf/default/weapons.json")))), new TypeToken<Map<String, RpgItemData>>() {}.getType());
            Map<String, RpgItemData> armour = new HashMap<>(); //SERIALIZER.fromJson(new BufferedReader(new InputStreamReader(Objects.requireNonNull(JSONHandler.class.getClassLoader().getResourceAsStream("data/iaf/default/armour.json")))), new TypeToken<Map<String, RpgItemData>>() {}.getType());
            for (String s : sortedWeaponOp0Map.keySet()) {
                weapons.putIfAbsent(s, RpgItemData.empty());
            }
            for (String s : sortedArmourOp0Map.keySet()) {
                armour.putIfAbsent(s, RpgItemData.empty());
            }
            Map<String, RpgItemData> sortedWeapons = new TreeMap<>(weapons);
            Map<String, RpgItemData> sortedArmour = new TreeMap<>(armour);
            JSONUtil.writeFile(new File(iafDir, "weapons.json"), sortedWeapons);
            JSONUtil.writeFile(new File(iafDir, "armour.json"), sortedArmour);
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onShutDownEvent(ServerStoppedEvent event) {
        if (!event.getServer().isDedicatedServer()) {
            SortedMap<String, ArmourData> sortedArmourOp0Map = new TreeMap<>();
            for (Map.Entry<ResourceLocation, ArmourData> entry : LOGICAL_ARMOUR_MAP_OP_0.entrySet()) {
                sortedArmourOp0Map.put(entry.getKey().toString(), entry.getValue());
            }
            SortedMap<String, ItemData> sortedArmourOp1Map = new TreeMap<>();
            for (Map.Entry<ResourceLocation, ItemData> entry : LOGICAL_ARMOUR_MAP_OP_1.entrySet()) {
                sortedArmourOp1Map.put(entry.getKey().toString(), entry.getValue());
            }
            SortedMap<String, ItemData> sortedArmourOp2Map = new TreeMap<>();
            for (Map.Entry<ResourceLocation, ItemData> entry : LOGICAL_ARMOUR_MAP_OP_2.entrySet()) {
                sortedArmourOp2Map.put(entry.getKey().toString(), entry.getValue());
            }
            SortedMap<String, WeaponData> sortedWeaponOp0Map = new TreeMap<>();
            for (Map.Entry<ResourceLocation, WeaponData> entry : LOGICAL_WEAPON_MAP_OP_0.entrySet()) {
                sortedWeaponOp0Map.put(entry.getKey().toString(), entry.getValue());
            }
            SortedMap<String, ItemData> sortedWeaponOp1Map = new TreeMap<>();
            for (Map.Entry<ResourceLocation, ItemData> entry : LOGICAL_WEAPON_MAP_OP_1.entrySet()) {
                sortedWeaponOp1Map.put(entry.getKey().toString(), entry.getValue());
            }
            SortedMap<String, ItemData> sortedWeaponOp2Map = new TreeMap<>();
            for (Map.Entry<ResourceLocation, ItemData> entry : LOGICAL_WEAPON_MAP_OP_2.entrySet()) {
                sortedWeaponOp2Map.put(entry.getKey().toString(), entry.getValue());
            }
            File configDir = Paths.get(FMLPaths.CONFIGDIR.get().toAbsolutePath().toString(), "ImprovedDamageFramework").toFile();
            JSONUtil.writeFile(new File(configDir, "armour_items_operation_addition.json"), sortedArmourOp0Map);
            JSONUtil.writeFile(new File(configDir, "armour_items_operation_multiply_base.json"), sortedArmourOp1Map);
            JSONUtil.writeFile(new File(configDir, "armour_items_operation_multiply_total.json"), sortedArmourOp2Map);
            JSONUtil.writeFile(new File(configDir, "weapon_items_operation_addition.json"), sortedWeaponOp0Map);
            JSONUtil.writeFile(new File(configDir, "weapon_items_operation_multiply_base.json"), sortedWeaponOp1Map);
            JSONUtil.writeFile(new File(configDir, "weapon_items_operation_multiply_total.json"), sortedWeaponOp2Map);
        }
    }

    @OnlyIn(Dist.DEDICATED_SERVER)
    @SubscribeEvent
    public static void onServerShutdown(ServerStoppedEvent event) {
        SortedMap<String, ArmourData> sortedArmourOp0Map = new TreeMap<>();
        for (Map.Entry<ResourceLocation, ArmourData> entry : LOGICAL_ARMOUR_MAP_OP_0.entrySet()) {
            sortedArmourOp0Map.put(entry.getKey().toString(), entry.getValue());
        }
        SortedMap<String, ItemData> sortedArmourOp1Map = new TreeMap<>();
        for (Map.Entry<ResourceLocation, ItemData> entry : LOGICAL_ARMOUR_MAP_OP_1.entrySet()) {
            sortedArmourOp1Map.put(entry.getKey().toString(), entry.getValue());
        }
        SortedMap<String, ItemData> sortedArmourOp2Map = new TreeMap<>();
        for (Map.Entry<ResourceLocation, ItemData> entry : LOGICAL_ARMOUR_MAP_OP_2.entrySet()) {
            sortedArmourOp2Map.put(entry.getKey().toString(), entry.getValue());
        }
        SortedMap<String, WeaponData> sortedWeaponOp0Map = new TreeMap<>();
        for (Map.Entry<ResourceLocation, WeaponData> entry : LOGICAL_WEAPON_MAP_OP_0.entrySet()) {
            sortedWeaponOp0Map.put(entry.getKey().toString(), entry.getValue());
        }
        SortedMap<String, ItemData> sortedWeaponOp1Map = new TreeMap<>();
        for (Map.Entry<ResourceLocation, ItemData> entry : LOGICAL_WEAPON_MAP_OP_1.entrySet()) {
            sortedWeaponOp1Map.put(entry.getKey().toString(), entry.getValue());
        }
        SortedMap<String, ItemData> sortedWeaponOp2Map = new TreeMap<>();
        for (Map.Entry<ResourceLocation, ItemData> entry : LOGICAL_WEAPON_MAP_OP_2.entrySet()) {
            sortedWeaponOp2Map.put(entry.getKey().toString(), entry.getValue());
        }
        File configDir = Paths.get(FMLPaths.CONFIGDIR.get().toAbsolutePath().toString(), "ImprovedDamageFramework").toFile();
        JSONUtil.writeFile(new File(configDir, "armour_items_operation_addition.json"), sortedArmourOp0Map);
        JSONUtil.writeFile(new File(configDir, "armour_items_operation_multiply_base.json"), sortedArmourOp1Map);
        JSONUtil.writeFile(new File(configDir, "armour_items_operation_multiply_total.json"), sortedArmourOp2Map);
        JSONUtil.writeFile(new File(configDir, "weapon_items_operation_addition.json"), sortedWeaponOp0Map);
        JSONUtil.writeFile(new File(configDir, "weapon_items_operation_multiply_base.json"), sortedWeaponOp1Map);
        JSONUtil.writeFile(new File(configDir, "weapon_items_operation_multiply_total.json"), sortedWeaponOp2Map);
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
            int equipmentSlot = LivingEntity.getEquipmentSlotForItem(item.getDefaultInstance()).getFilterFlag();
            ResourceLocation loc = Util.getItemRegistryName(item);
            ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
            if (baseModifiers.get(loc) != null) {
                for (Map.Entry<Attribute, AttributeModifier> entry : baseModifiers.get(loc).entries()) {
                    builder.put(entry.getKey(), entry.getValue());
                }
            }
            if (LOGICAL_WEAPON_MAP_OP_0.containsKey(loc) || LOGICAL_WEAPON_MAP_OP_1.containsKey(loc) || LOGICAL_WEAPON_MAP_OP_2.containsKey(loc)) {
                defaultTag.putBoolean("idf.equipment", true);
                WeaponData data0 = LOGICAL_WEAPON_MAP_OP_0.get(loc);
                defaultTag.putBoolean("idf.ranged_weapon", data0.ranged());
                ItemData data1 = LOGICAL_WEAPON_MAP_OP_1.get(loc);
                ItemData data2 = LOGICAL_WEAPON_MAP_OP_2.get(loc);
                if (data0 != null) {
                    data0.forEach(pair -> {
                        if (pair.getB() != 0) {
                            builder.put(pair.getA(), new AttributeModifier(UUID_BASE_STAT_ADDITION[equipmentSlot], "data0", pair.getB(), ADDITION));
                        }
                    });
                    defaultTag.putString("idf.damage_class", data0.damageClass());
                    idfItem.setMaxDamage(vanillaDurability.getOrDefault(loc, 0) + data0.durability());
                }
                if (data1 != null) {
                    data1.forEach(pair -> {
                        if (pair.getB() != 0) {
                            builder.put(pair.getA(), new AttributeModifier(UUID_BASE_STAT_MULTIPLY_BASE[equipmentSlot],"data1", pair.getB(), MULTIPLY_BASE));
                        }
                    });
                }
                if (data2 != null) {
                    data2.forEach(pair -> {
                        if (pair.getB() != 0) {
                            builder.put(pair.getA(), new AttributeModifier(UUID_BASE_STAT_MULTIPLY_TOTAL[equipmentSlot],"data2", pair.getB(), MULTIPLY_TOTAL));
                        }
                    });
                }
            }
            else if (LOGICAL_ARMOUR_MAP_OP_0.containsKey(loc) || LOGICAL_ARMOUR_MAP_OP_1.containsKey(loc) || LOGICAL_ARMOUR_MAP_OP_2.containsKey(loc)) {
                defaultTag.putBoolean("idf.equipment", true);
                ArmourData data0 = LOGICAL_ARMOUR_MAP_OP_0.get(loc);
                ItemData data1 = LOGICAL_ARMOUR_MAP_OP_1.get(loc);
                ItemData data2 = LOGICAL_ARMOUR_MAP_OP_2.get(loc);
                if (data0 != null) {
                    data0.forEach(pair -> {
                        if (pair.getB() != 0) {
                            builder.put(pair.getA(), new AttributeModifier(UUID_BASE_STAT_ADDITION[equipmentSlot],"data0", pair.getB(), ADDITION));
                        }
                    });
                    idfItem.setMaxDamage(vanillaDurability.getOrDefault(loc, 0) + data0.durability());
                }
                if (data1 != null) {
                    data1.forEach(pair -> {
                        if (pair.getB() != 0) {
                            builder.put(pair.getA(), new AttributeModifier(UUID_BASE_STAT_MULTIPLY_BASE[equipmentSlot],"data1", pair.getB(), MULTIPLY_BASE));
                        }
                    });
                }
                if (data2 != null) {
                    data2.forEach(pair -> {
                        if (pair.getB() != 0) {
                            builder.put(pair.getA(), new AttributeModifier(UUID_BASE_STAT_MULTIPLY_TOTAL[equipmentSlot],"data2", pair.getB(), MULTIPLY_TOTAL));
                        }
                    });
                }
            }
            MinecraftForge.EVENT_BUS.post(new OnItemAttributeRework(builder, defaultTag));
            if (!defaultTag.isEmpty()) idfItem.setDefaultTag(defaultTag);
            idfItem.setDefaultAttributes(builder.build());
        }
    }

}
