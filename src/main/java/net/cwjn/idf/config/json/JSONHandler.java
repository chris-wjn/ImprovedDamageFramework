package net.cwjn.idf.config.json;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.cwjn.idf.ImprovedDamageFramework;
import net.cwjn.idf.api.IDFCustomEquipment;
import net.cwjn.idf.api.event.ItemAttributeReworkEvent;
import net.cwjn.idf.config.json.records.*;
import net.cwjn.idf.config.json.records.subtypes.AuxiliaryData;
import net.cwjn.idf.config.json.records.subtypes.DefenceData;
import net.cwjn.idf.config.json.records.subtypes.OffenseData;
import net.cwjn.idf.data.ClientData;
import net.cwjn.idf.data.CommonData;
import net.cwjn.idf.iaf.RpgItemData;
import net.cwjn.idf.util.ItemInterface;
import net.cwjn.idf.util.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Predicate;

import static net.cwjn.idf.data.CommonData.*;
import static net.cwjn.idf.util.Util.UUIDS_BASE_STAT_ADDITION;
import static net.cwjn.idf.util.Util.UUIDS_BASE_STAT_MULTIPLY_BASE;
import static net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADDITION;
import static net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.MULTIPLY_BASE;

@SuppressWarnings(value = "UnstableApiUsage")
@Mod.EventBusSubscriber
public class JSONHandler {
    public static final Map<ResourceLocation, Multimap<Attribute, AttributeModifier>> baseModifiers = new HashMap<>();
    private static final Predicate<Item> isKnownWeapon = (item) -> item instanceof SwordItem || item instanceof DiggerItem || item instanceof BowItem || item instanceof CrossbowItem || item instanceof TridentItem;
    public static final Map<ResourceLocation, Integer> vanillaDurability = new HashMap<>();
    public static final Path compatDir = Paths.get(FMLPaths.CONFIGDIR.get().toAbsolutePath().toString(), "ImprovedDamageFramework", "compat");
    public static final Gson SERIALIZER = new GsonBuilder().
            setPrettyPrinting().
            registerTypeAdapter(ArmourData.class, new ArmourData.ArmourSerializer()).
            registerTypeAdapter(WeaponData.class, new WeaponData.WeaponSerializer()).
            registerTypeAdapter(ItemData.class, new ItemData.ItemSerializer()).
            registerTypeAdapter(EntityData.class, new EntityData.EntityDataSerializer()).
            registerTypeAdapter(SourceCatcherData.class, new SourceCatcherData.SourceCatcherDataSerializer()).
            registerTypeAdapter(OffenseData.class, new OffenseData.OffensiveDataSerializer()).
            registerTypeAdapter(DefenceData.class, new DefenceData.DefensiveDataSerializer()).
            registerTypeAdapter(AuxiliaryData.class, new AuxiliaryData.AuxiliaryDataSerializer()).
            registerTypeAdapter(RpgItemData.StatObject.class, new RpgItemData.StatObject.StatObjectSerializer()).
            registerTypeAdapter(RpgItemData.class, new RpgItemData.RpgItemSerializer()).
            registerTypeAdapter(PresetData.class, new PresetData.PresetSerializer()).
            registerTypeAdapter(PresetData.AttributeAndModifier.class, new PresetData.AttributeAndModifierSerializer()).
            create();

    public static void init(File configDir) {

        //instantiate default maps
        Map<String, PresetData> DEFAULT_PRESETS = SERIALIZER.fromJson(new BufferedReader(new InputStreamReader(Objects.requireNonNull(JSONHandler.class.getClassLoader().getResourceAsStream("data/idf/default/presets.json")))), new TypeToken<Map<String, PresetData>>(){}.getType());
        Map<String, ArmourData> DEFAULT_ARMOUR_FLAT = SERIALIZER.fromJson(new BufferedReader(new InputStreamReader(Objects.requireNonNull(JSONHandler.class.getClassLoader().getResourceAsStream("data/idf/default/armour_items_flat.json")))), new TypeToken<Map<String, ArmourData>>(){}.getType());
        Map<String, ItemData> DEFAULT_ARMOUR_MULT = SERIALIZER.fromJson(new BufferedReader(new InputStreamReader(Objects.requireNonNull(JSONHandler.class.getClassLoader().getResourceAsStream("data/idf/default/armour_items_multiply.json")))), new TypeToken<Map<String, ItemData>>(){}.getType());
        Map<String, WeaponData> DEFAULT_WEAPON_FLAT = SERIALIZER.fromJson(new BufferedReader(new InputStreamReader(Objects.requireNonNull(JSONHandler.class.getClassLoader().getResourceAsStream("data/idf/default/weapon_items_flat.json")))), new TypeToken<Map<String, WeaponData>>(){}.getType());
        Map<String, ItemData> DEFAULT_WEAPON_MULT = SERIALIZER.fromJson(new BufferedReader(new InputStreamReader(Objects.requireNonNull(JSONHandler.class.getClassLoader().getResourceAsStream("data/idf/default/weapon_items_multiply.json")))), new TypeToken<Map<String, ItemData>>(){}.getType());
        Map<String, EntityData> DEFAULT_ENTITY = SERIALIZER.fromJson(new BufferedReader(new InputStreamReader(Objects.requireNonNull(JSONHandler.class.getClassLoader().getResourceAsStream("data/idf/default/entity_data.json")))), new TypeToken<Map<String, EntityData>>(){}.getType());
        Map<String, SourceCatcherData> DEFAULT_SOURCE = SERIALIZER.fromJson(new BufferedReader(new InputStreamReader(Objects.requireNonNull(JSONHandler.class.getClassLoader().getResourceAsStream("data/idf/default/source_catcher.json")))), new TypeToken<Map<String, SourceCatcherData>>(){}.getType());

        //ENTITIES
        for (EntityType<?> entityType : ForgeRegistries.ENTITY_TYPES.getValues()) {
            if (entityType.getCategory() != MobCategory.MISC) { //make sure this isn't an arrow entity or something
                DEFAULT_ENTITY.putIfAbsent(Util.getEntityRegistryName(entityType).toString(), new EntityData(List.of(),
                        "strike", OffenseData.empty(), DefenceData.entityStandard(), AuxiliaryData.fullSpeed()));
            }
        }

        //ITEMS
        for (Item item : ForgeRegistries.ITEMS.getValues()) {
            if (isKnownWeapon.test(item)) {
                boolean ranged = item instanceof BowItem || item instanceof CrossbowItem;
                boolean thrown = item instanceof TridentItem;
                if (item instanceof IDFCustomEquipment modItem) {
                    DEFAULT_WEAPON_FLAT.putIfAbsent(Util.getItemRegistryName(item).toString(),
                            new WeaponData(Collections.emptyList(), 0, ((ItemInterface) modItem).getDamageClass(), ranged, thrown,
                                    OffenseData.empty(),
                                    DefenceData.empty(),
                                    AuxiliaryData.empty()));
                } else {
                    String damageClass = "strike";
                    if (Util.getItemRegistryName(item).toString().contains("sword") || Util.getItemRegistryName(item).toString().contains("axe")) {
                        damageClass = "slash";
                    }
                    if (Util.getItemRegistryName(item).toString().contains("pickaxe") || Util.getItemRegistryName(item).toString().contains("bow")) {
                        damageClass = "pierce";
                    }
                    double damage = item.getDefaultInstance().getAttributeModifiers(EquipmentSlot.MAINHAND).get(Attributes.ATTACK_DAMAGE)
                                    .stream().mapToDouble(AttributeModifier::getAmount).sum();
                    double speed = item.getDefaultInstance().getAttributeModifiers(EquipmentSlot.MAINHAND).get(Attributes.ATTACK_SPEED)
                            .stream().mapToDouble(AttributeModifier::getAmount).sum();
                    List<String> materialPresets = new ArrayList<>();
                    DEFAULT_WEAPON_FLAT.putIfAbsent(Util.getItemRegistryName(item).toString(),
                            new WeaponData(materialPresets, 0, damageClass, ranged, thrown,
                                    ranged? OffenseData.rangedDefault() : OffenseData.guessForceFromDamageSpeed(damage, speed),
                                    DefenceData.empty(),
                                    AuxiliaryData.empty()));
                }
                DEFAULT_WEAPON_MULT.putIfAbsent(Util.getItemRegistryName(item).toString(), new ItemData(
                        OffenseData.empty(), DefenceData.empty(), AuxiliaryData.empty()
                ));
            } //In the case the item is a sword, tool, bow, crossbow, or trident, the case is trivial, and we know it's a weapon.
            else {
                if (item instanceof IDFCustomEquipment) {
                    DEFAULT_ARMOUR_FLAT.putIfAbsent(Util.getItemRegistryName(item).toString(), new ArmourData(Collections.emptyList(),
                            0,
                            OffenseData.empty(), DefenceData.empty(), AuxiliaryData.empty()));
                    DEFAULT_ARMOUR_MULT.putIfAbsent(Util.getItemRegistryName(item).toString(), new ItemData(
                            OffenseData.empty(), DefenceData.empty(), AuxiliaryData.empty()));
                } //If the item is an instance of IDF equipment that isn't a weapon, it is guaranteed to be armour.
                else {
                    double damage = item.getDefaultInstance().getAttributeModifiers(EquipmentSlot.MAINHAND).get(Attributes.ATTACK_DAMAGE)
                            .stream().mapToDouble(AttributeModifier::getAmount).sum();
                    double speed = item.getDefaultInstance().getAttributeModifiers(EquipmentSlot.MAINHAND).get(Attributes.ATTACK_SPEED)
                            .stream().mapToDouble(AttributeModifier::getAmount).sum();
                    if (damage != 0) {
                        String dc = "strike";
                        if (Util.getItemRegistryName(item).toString().contains("sword") || Util.getItemRegistryName(item).toString().contains("axe")) {
                            dc = "slash";
                        }
                        if (Util.getItemRegistryName(item).toString().contains("pickaxe") || Util.getItemRegistryName(item).toString().contains("bow")) {
                            dc = "pierce";
                        }
                        DEFAULT_WEAPON_FLAT.putIfAbsent(Util.getItemRegistryName(item).toString(),
                                new WeaponData(Collections.emptyList(), 0, dc, false, false,
                                        OffenseData.guessForceFromDamageSpeed(damage, speed),
                                        DefenceData.empty(),
                                        AuxiliaryData.empty()));
                        DEFAULT_WEAPON_MULT.putIfAbsent(Util.getItemRegistryName(item).toString(), new ItemData(
                                OffenseData.empty(), DefenceData.empty(), AuxiliaryData.empty()
                        ));
                    } //Now that we know it isn't an instance of IDF equipment, we can check for custom items meant to act as weapons.
                    else {
                        Multimap<Attribute, AttributeModifier> armour0 = item.getDefaultInstance().getAttributeModifiers(EquipmentSlot.HEAD);
                        Multimap<Attribute, AttributeModifier> armour1 = item.getDefaultInstance().getAttributeModifiers(EquipmentSlot.FEET);
                        Multimap<Attribute, AttributeModifier> armour2 = item.getDefaultInstance().getAttributeModifiers(EquipmentSlot.LEGS);
                        Multimap<Attribute, AttributeModifier> armour3 = item.getDefaultInstance().getAttributeModifiers(EquipmentSlot.CHEST);
                        if (!armour0.isEmpty() || !armour1.isEmpty() || !armour2.isEmpty() || !armour3.isEmpty()) {
                            EquipmentSlot slot = LivingEntity.getEquipmentSlotForItem(item.getDefaultInstance());
                            List<String> tempList = new ArrayList<>(1);
                            double guessWeight = 0;
                            switch (slot) {
                                case HEAD:
                                    double headArmour = armour0.get(Attributes.ARMOR).stream().mapToDouble(AttributeModifier::getAmount).sum();
                                    tempList.add("HELMET");
                                    guessWeight = 0.75*headArmour;
                                    if (headArmour <= 2.5) tempList.add("LIGHT_ARMOUR");
                                    else if (headArmour <= 5) tempList.add("MEDIUM_ARMOUR");
                                    else tempList.add("HEAVY_ARMOUR");
                                    break;
                                case CHEST:
                                    double chestArmour = armour3.get(Attributes.ARMOR).stream().mapToDouble(AttributeModifier::getAmount).sum();
                                    tempList.add("CHESTPLATE");
                                    guessWeight = 0.5*chestArmour;
                                    if (chestArmour <= 5) tempList.add("LIGHT_ARMOUR");
                                    else if (chestArmour <= 8) tempList.add("MEDIUM_ARMOUR");
                                    else tempList.add("HEAVY_ARMOUR");
                                    break;
                                case LEGS:
                                    double legArmour = armour2.get(Attributes.ARMOR).stream().mapToDouble(AttributeModifier::getAmount).sum();
                                    tempList.add("LEGGINGS");
                                    guessWeight = 0.6*legArmour;
                                    if (legArmour <= 4) tempList.add("LIGHT_ARMOUR");
                                    else if (legArmour <= 7) tempList.add("MEDIUM_ARMOUR");
                                    else tempList.add("HEAVY_ARMOUR");
                                    break;
                                case FEET:
                                    double footArmour = armour1.get(Attributes.ARMOR).stream().mapToDouble(AttributeModifier::getAmount).sum();
                                    tempList.add("BOOTS");
                                    guessWeight = 0.75*footArmour;
                                    if (footArmour <= 2.5) tempList.add("LIGHT_ARMOUR");
                                    else if (footArmour <= 5) tempList.add("MEDIUM_ARMOUR");
                                    else tempList.add("HEAVY_ARMOUR");
                                    break;
                            }
                            DefenceData tempData = DefenceData.empty();
                            if (item.getDefaultInstance().getAttributeModifiers(slot).get(Attributes.ARMOR_TOUGHNESS).stream().mapToDouble(AttributeModifier::getAmount).sum() == 0) {
                                tempData = DefenceData.setWeight(guessWeight);
                            }
                            DEFAULT_ARMOUR_FLAT.putIfAbsent(Util.getItemRegistryName(item).toString(), new ArmourData(tempList,
                                    0,
                                    OffenseData.empty(), tempData, AuxiliaryData.empty()));
                            DEFAULT_ARMOUR_MULT.putIfAbsent(Util.getItemRegistryName(item).toString(), new ItemData(
                                    OffenseData.empty(), DefenceData.empty(), AuxiliaryData.empty()));
                        }
                    } //The only other case is if it is armour, so check that now.
                }
            } //If it isn't one of the former item tags, then it could either be an armour item or a custom item that is meant to act as a weapon.
        }

        //ensure we're starting with clean maps
        LOGICAL_PRESET_MAP.clear();
        LOGICAL_ARMOUR_MAP_FLAT.clear();
        LOGICAL_ARMOUR_MAP_MULT.clear();
        LOGICAL_WEAPON_MAP_FLAT.clear();
        LOGICAL_WEAPON_MAP_MULT.clear();
        LOGICAL_ENTITY_MAP.clear();
        LOGICAL_SOURCE_MAP.clear();

        //grab the maps as strings instead of ResourceLocations from the config folder. If it doesn't exist, just use the default map
        Map<String, ArmourData> tempArmourOp0Map = JSONUtil.getOrCreateConfigFile(configDir, "armour_items_flat.json", DEFAULT_ARMOUR_FLAT, new TypeToken<Map<String, ArmourData>>() {}.getType());
        Map<String, ItemData> tempArmourOp2Map = JSONUtil.getOrCreateConfigFile(configDir, "armour_items_multiply.json", DEFAULT_ARMOUR_MULT, new TypeToken<Map<String, ItemData>>() {}.getType());
        Map<String, WeaponData> tempWeaponOp0Map = JSONUtil.getOrCreateConfigFile(configDir, "weapon_items_flat.json", DEFAULT_WEAPON_FLAT, new TypeToken<Map<String, WeaponData>>() {}.getType());
        Map<String, ItemData> tempWeaponOp2Map = JSONUtil.getOrCreateConfigFile(configDir, "weapon_items_multiply.json", DEFAULT_WEAPON_MULT, new TypeToken<Map<String, ItemData>>() {}.getType());
        Map<String, EntityData> tempEntityMap = JSONUtil.getOrCreateConfigFile(configDir, "entity_data.json", DEFAULT_ENTITY, new TypeToken<Map<String, EntityData>>() {}.getType());
        Map<String, SourceCatcherData> tempSourceMap = JSONUtil.getOrCreateConfigFile(configDir, "source_catcher.json", DEFAULT_SOURCE, new TypeToken<Map<String, SourceCatcherData>>() {}.getType());
        Map<String, PresetData> tempPresetMap = JSONUtil.getOrCreateConfigFile(configDir, "presets.json", DEFAULT_PRESETS, new TypeToken<Map<String, PresetData>>() {}.getType());
        List<String> compatItems = JSONUtil.getOrCreateConfigFile(compatDir.toFile(), "compat_items.json", new ArrayList<>(), new TypeToken<List<String>>() {}.getType());
        List<String> compatMods = JSONUtil.getOrCreateConfigFile(compatDir.toFile(), "compat_mods.json", new ArrayList<>(), new TypeToken<List<String>>() {}.getType());

        //if the maps taken from the config folder are missing entries, fill them in
        //this is helpful if the user accidentally deletes an entry, or adds new mods
        for (Map.Entry<String, ArmourData> entry : DEFAULT_ARMOUR_FLAT.entrySet()) {
            tempArmourOp0Map.putIfAbsent(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, ItemData> entry : DEFAULT_ARMOUR_MULT.entrySet()) {
            tempArmourOp2Map.putIfAbsent(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, WeaponData> entry : DEFAULT_WEAPON_FLAT.entrySet()) {
            tempWeaponOp0Map.putIfAbsent(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, ItemData> entry : DEFAULT_WEAPON_MULT.entrySet()) {
            tempWeaponOp2Map.putIfAbsent(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, EntityData> entry : DEFAULT_ENTITY.entrySet()) {
            tempEntityMap.putIfAbsent(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, SourceCatcherData> entry : DEFAULT_SOURCE.entrySet()) {
            tempSourceMap.putIfAbsent(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, PresetData> entry : DEFAULT_PRESETS.entrySet()) {
            tempPresetMap.putIfAbsent(entry.getKey(), entry.getValue());
        }

        //sort the maps before converting to resource locations so the json files get sorted automatically
        //by mod first then item/entity name
        SortedMap<String, ArmourData> sortedArmourOp0Map = new TreeMap<>(tempArmourOp0Map);
        SortedMap<String, ItemData> sortedArmourOp2Map = new TreeMap<>(tempArmourOp2Map);
        SortedMap<String, WeaponData> sortedWeaponOp0Map = new TreeMap<>(tempWeaponOp0Map);
        SortedMap<String, ItemData> sortedWeaponOp2Map = new TreeMap<>(tempWeaponOp2Map);
        SortedMap<String, EntityData> sortedEntityMap = new TreeMap<>(tempEntityMap);
        SortedMap<String, SourceCatcherData> sortedSourceMap = new TreeMap<>(tempSourceMap);
        SortedMap<String, PresetData> sortedPresetMap = new TreeMap<>(tempPresetMap);

        //now we fill the actual maps that get used by the game
        if (!sortedArmourOp0Map.isEmpty()) {
            for (Map.Entry<String, ArmourData> entry : sortedArmourOp0Map.entrySet()) {
                LOGICAL_ARMOUR_MAP_FLAT.put(new ResourceLocation(entry.getKey()), entry.getValue());
            }
        }
        if (!sortedArmourOp2Map.isEmpty()) {
            for (Map.Entry<String, ItemData> entry : sortedArmourOp2Map.entrySet()) {
                LOGICAL_ARMOUR_MAP_MULT.put(new ResourceLocation(entry.getKey()), entry.getValue());
            }
        }
        if (!sortedWeaponOp0Map.isEmpty()) {
            for (Map.Entry<String, WeaponData> entry : sortedWeaponOp0Map.entrySet()) {
                LOGICAL_WEAPON_MAP_FLAT.put(new ResourceLocation(entry.getKey()), entry.getValue());
            }
        }
        if (!sortedWeaponOp2Map.isEmpty()) {
            for (Map.Entry<String, ItemData> entry : sortedWeaponOp2Map.entrySet()) {
                LOGICAL_WEAPON_MAP_MULT.put(new ResourceLocation(entry.getKey()), entry.getValue());
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
        if (!sortedSourceMap.isEmpty()) {
            LOGICAL_PRESET_MAP = sortedPresetMap;
        }
        if (!compatItems.isEmpty()) {
            for (String s : compatItems) {
                COMPAT_ITEMS.add(new ResourceLocation(s));
            }
        }
        if (!compatMods.isEmpty()) {
            COMPAT_MODS.addAll(compatMods);
        }

        //this is for ImprovedAdventureFramework
        if (ModList.get().isLoaded("iaf")) {
            ImprovedDamageFramework.IAFLoaded = true;
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
            JSONUtil.getOrCreateConfigFile(iafDir, "weapons.json", sortedWeapons, new TypeToken<Map<String, RpgItemData>>() {}.getType());
            JSONUtil.getOrCreateConfigFile(iafDir, "armour.json", sortedArmour, new TypeToken<Map<String, RpgItemData>>() {}.getType());
        }

        //Now use the maps to update items and then save the physical client's mappings
        saveVanillaStats();
        updateItems();
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> ClientData::saveClientMappings);

        //Write the maps to config json files so users can edit items
        JSONUtil.writeFile(new File(configDir, "entity_data.json"), sortedEntityMap);
        JSONUtil.writeFile(new File(configDir, "armour_items_flat.json"), sortedArmourOp0Map);
        JSONUtil.writeFile(new File(configDir, "armour_items_multiply.json"), sortedArmourOp2Map);
        JSONUtil.writeFile(new File(configDir, "weapon_items_flat.json"), sortedWeaponOp0Map);
        JSONUtil.writeFile(new File(configDir, "weapon_items_multiply.json"), sortedWeaponOp2Map);
        JSONUtil.writeFile(new File(configDir, "source_catcher.json"), sortedSourceMap);
        JSONUtil.writeFile(new File(compatDir.toFile(), "compat_items.json"), compatItems);
        JSONUtil.writeFile(new File(compatDir.toFile(), "compat_mods.json"), compatMods);
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

    public static void updateMaps() {
        File configDir = Paths.get(FMLPaths.CONFIGDIR.get().toAbsolutePath().toString(), "ImprovedDamageFramework").toFile();
        Map<String, ArmourData> tempArmourOp0Map = JSONUtil.getConfigFile(configDir, "armour_items_flat.json", new TypeToken<Map<String, ArmourData>>() {}.getType());
        Map<String, ItemData> tempArmourOp2Map = JSONUtil.getConfigFile(configDir, "armour_items_multiply.json", new TypeToken<Map<String, ItemData>>() {}.getType());
        Map<String, WeaponData> tempWeaponOp0Map = JSONUtil.getConfigFile(configDir, "weapon_items_flat.json", new TypeToken<Map<String, WeaponData>>() {}.getType());
        Map<String, ItemData> tempWeaponOp2Map = JSONUtil.getConfigFile(configDir, "weapon_items_multiply.json", new TypeToken<Map<String, ItemData>>() {}.getType());
        Map<String, SourceCatcherData> tempSourceMap = JSONUtil.getConfigFile(configDir, "source_catcher.json", new TypeToken<Map<String, SourceCatcherData>>() {}.getType());
        Map<String, PresetData> tempPresetMap = JSONUtil.getConfigFile(configDir, "presets.json", new TypeToken<Map<String, PresetData>>() {}.getType());
        List<String> compatItems = JSONUtil.getConfigFile(compatDir.toFile(), "compat_items.json", new TypeToken<List<String>>() {}.getType());
        List<String> compatMods = JSONUtil.getConfigFile(compatDir.toFile(), "compat_mods.json", new TypeToken<List<String>>() {}.getType());
        if (!tempArmourOp0Map.isEmpty()) {
            for (Map.Entry<String, ArmourData> entry : tempArmourOp0Map.entrySet()) {
                LOGICAL_ARMOUR_MAP_FLAT.put(new ResourceLocation(entry.getKey()), entry.getValue());
            }
        }
        if (!tempArmourOp2Map.isEmpty()) {
            for (Map.Entry<String, ItemData> entry : tempArmourOp2Map.entrySet()) {
                LOGICAL_ARMOUR_MAP_MULT.put(new ResourceLocation(entry.getKey()), entry.getValue());
            }
        }
        if (!tempWeaponOp0Map.isEmpty()) {
            for (Map.Entry<String, WeaponData> entry : tempWeaponOp0Map.entrySet()) {
                LOGICAL_WEAPON_MAP_FLAT.put(new ResourceLocation(entry.getKey()), entry.getValue());
            }
        }
        if (!tempWeaponOp2Map.isEmpty()) {
            for (Map.Entry<String, ItemData> entry : tempWeaponOp2Map.entrySet()) {
                LOGICAL_WEAPON_MAP_MULT.put(new ResourceLocation(entry.getKey()), entry.getValue());
            }
        }
        if (!tempSourceMap.isEmpty()) {
            LOGICAL_SOURCE_MAP = tempSourceMap;
        }
        if (!tempPresetMap.isEmpty()) {
            LOGICAL_PRESET_MAP = tempPresetMap;
        }
        if (!compatItems.isEmpty()) {
            for (String s : compatItems) {
                COMPAT_ITEMS.add(new ResourceLocation(s));
            }
        }
        if (!compatMods.isEmpty()) {
            COMPAT_MODS.addAll(compatMods);
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
            if (LOGICAL_WEAPON_MAP_FLAT.containsKey(loc)) {
                defaultTag.putBoolean(CommonData.EQUIPMENT_TAG, true);
                WeaponData data0 = LOGICAL_WEAPON_MAP_FLAT.get(loc);
                defaultTag.putBoolean(RANGED_TAG, data0.ranged());
                defaultTag.putBoolean(THROWN_TAG, data0.thrown());
                ItemData data2 = LOGICAL_WEAPON_MAP_MULT.get(loc);
                for (String s : data0.presets()) {
                    PresetData preset = LOGICAL_PRESET_MAP.get(s);
                    if (preset == null) {
                        ImprovedDamageFramework.LOGGER.warn("Preset " + s + " does not exist!");
                        continue;
                    }
                    for (PresetData.AttributeAndModifier combo : preset) {
                        builder.put(combo.getAttribute(), new AttributeModifier("preset_modifier", combo.getAmount(), AttributeModifier.Operation.valueOf(combo.getOperation())));
                    }
                }
                data0.forEach(pair -> {
                    if (pair.getB() != 0) {
                        builder.put(pair.getA(), new AttributeModifier(UUIDS_BASE_STAT_ADDITION[equipmentSlot], "json_flat", pair.getB(), ADDITION));
                    }
                });
                if (data2 != null) data2.forEach(pair -> {
                    if (pair.getB() != 0) {
                        builder.put(pair.getA(), new AttributeModifier(UUIDS_BASE_STAT_MULTIPLY_BASE[equipmentSlot],"json_mult", pair.getB(), MULTIPLY_BASE));
                    }
                });
                defaultTag.putString(WEAPON_TAG, data0.damageClass());
                idfItem.setMaxDamage(vanillaDurability.getOrDefault(loc, 0) + data0.durability());
            } else if (LOGICAL_ARMOUR_MAP_FLAT.containsKey(loc)) {
                defaultTag.putBoolean(CommonData.EQUIPMENT_TAG, true);
                ArmourData data0 = LOGICAL_ARMOUR_MAP_FLAT.get(loc);
                ItemData data2 = LOGICAL_ARMOUR_MAP_MULT.get(loc);
                for (String s : data0.presets()) {
                    PresetData preset = LOGICAL_PRESET_MAP.get(s);
                    if (preset == null) {
                        ImprovedDamageFramework.LOGGER.warn("Preset " + s + " does not exist!");
                        continue;
                    }
                    for (PresetData.AttributeAndModifier combo : preset) {
                        builder.put(combo.getAttribute(), new AttributeModifier("preset_modifier", combo.getAmount(), AttributeModifier.Operation.valueOf(combo.getOperation())));
                    }
                }
                data0.forEach(pair -> {
                    if (pair.getB() != 0) {
                        builder.put(pair.getA(), new AttributeModifier(UUIDS_BASE_STAT_ADDITION[equipmentSlot], "json_flat", pair.getB(), ADDITION));
                    }
                });
                if (data2 != null) data2.forEach(pair -> {
                    if (pair.getB() != 0) {
                        builder.put(pair.getA(), new AttributeModifier(UUIDS_BASE_STAT_MULTIPLY_BASE[equipmentSlot], "json_mult", pair.getB(), MULTIPLY_BASE));
                    }
                });
                idfItem.setMaxDamage(vanillaDurability.getOrDefault(loc, 0) + data0.durability());
            }
            if (COMPAT_ITEMS.contains(loc) || COMPAT_MODS.contains(loc.getNamespace())) {
                defaultTag.putBoolean(COMPAT_ITEM, true);
            }
            MinecraftForge.EVENT_BUS.post(new ItemAttributeReworkEvent(builder, defaultTag, Util.getItemRegistryName(item)));
            if (!defaultTag.isEmpty()) idfItem.setDefaultTag(defaultTag);
            idfItem.setDefaultAttributes(builder.build());
        }
    }

    public static Map<String, RpgItemData> retrieveRpgItems() {
        Map<String, RpgItemData> rpgArmourItems = JSONUtil.getConfigFile(Paths.get(FMLPaths.CONFIGDIR.get().toAbsolutePath().toString(),
                "ImprovedAdventureFramework").toFile(),
                "armour.json",
                new TypeToken<Map<String, RpgItemData>>() {}.getType());
        Map<String, RpgItemData> rpgWeaponItems = JSONUtil.getConfigFile(Paths.get(FMLPaths.CONFIGDIR.get().toAbsolutePath().toString(),
                        "ImprovedAdventureFramework").toFile(),
                "weapons.json",
                new TypeToken<Map<String, RpgItemData>>() {}.getType());
        rpgArmourItems.putAll(rpgWeaponItems);
        return rpgArmourItems;
    }

}
