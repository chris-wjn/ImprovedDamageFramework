package net.cwjn.idf.data;

import com.google.common.collect.Multimap;
import net.cwjn.idf.attribute.IDFAttributes;
import net.cwjn.idf.attribute.IDFElement;
import net.cwjn.idf.config.json.records.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;

import static net.cwjn.idf.attribute.IDFAttributes.*;

public class CommonData {

    public static Map<ResourceLocation, ArmourData> LOGICAL_ARMOUR_MAP_FLAT = new HashMap<>();
    public static Map<ResourceLocation, ItemData> LOGICAL_ARMOUR_MAP_MULT = new HashMap<>();
    public static Map<ResourceLocation, WeaponData> LOGICAL_WEAPON_MAP_FLAT = new HashMap<>();
    public static Map<ResourceLocation, ItemData> LOGICAL_WEAPON_MAP_MULT = new HashMap<>();
    public static Map<ResourceLocation, EntityData> LOGICAL_ENTITY_MAP = new HashMap<>();
    public static Map<String, SourceCatcherData> LOGICAL_SOURCE_MAP = new HashMap<>();
    public static Map<String, PresetData> LOGICAL_PRESET_MAP = new HashMap<>();
    public static List<ResourceLocation> COMPAT_ITEMS = new ArrayList<>();
    public static List<String> COMPAT_MODS = new ArrayList<>();
    public static Map<String, Attribute> SCALABLE_ATTRIBUTES = new HashMap<>();
    public static Multimap<UUID, ResourceLocation> BESTIARY_MAP;
    public static final Map<String, Attribute> ATTRIBUTES = new HashMap<>();
    public static final List<Attribute> OFFENSIVE_ATTRIBUTES = new ArrayList<>(8);
    public static final List<Attribute> DEFENSIVE_ATTRIBUTES = new ArrayList<>(8);
    public static final List<Attribute> AUXILIARY_ATTRIBUTES = new ArrayList<>(8);
    public static Set<Attribute> ELEMENTS = new HashSet<>();
    public static final String EQUIPMENT_TAG = "idf.equipment";
    public static final String RANGED_TAG = "idf.ranged_weapon";
    public static final String THROWN_TAG = "idf.thrown_weapon";
    public static final String WEAPON_TAG = "idf.damage_class";
    public static final String ENTITY_BONUS = "idf.base_bonus";
    public static final String COMPAT_ITEM = "idf.needs_compat";
    public static final String BORDER_TAG = "idf.tooltip_border";
    public static final String DEFAULT_TAG_APPLIED = "idf.default_applied";
    public static final String TETRA_CRIT_LEVEL = "idf.tetra_crit_level";
    public static final String TETRA_CRIT_EFFICIENCY = "idf.tetra_crit_efficiency";

    static {
        ELEMENTS.add(FIRE_DAMAGE.get());
        ELEMENTS.add(FIRE_DEFENCE.get());
        ELEMENTS.add(WATER_DAMAGE.get());
        ELEMENTS.add(WATER_DEFENCE.get());
        ELEMENTS.add(LIGHTNING_DAMAGE.get());
        ELEMENTS.add(LIGHTNING_DEFENCE.get());
        ELEMENTS.add(MAGIC_DAMAGE.get());
        ELEMENTS.add(MAGIC_DEFENCE.get());
        ELEMENTS.add(DARK_DAMAGE.get());
        ELEMENTS.add(DARK_DEFENCE.get());
        ELEMENTS.add(HOLY_DAMAGE.get());
        ELEMENTS.add(HOLY_DEFENCE.get());
        for (Attribute a : ForgeRegistries.ATTRIBUTES.getValues()) {
            ATTRIBUTES.put(a.getDescriptionId(), a);
        }
        {
            SCALABLE_ATTRIBUTES.put("FORCE", IDFAttributes.FORCE.get());
            SCALABLE_ATTRIBUTES.put("WEIGHT", Attributes.ARMOR_TOUGHNESS);
            SCALABLE_ATTRIBUTES.put("PHYSICAL_DAMAGE", Attributes.ATTACK_DAMAGE);
            SCALABLE_ATTRIBUTES.put("PHYSICAL_DEFENCE", Attributes.ARMOR);
            SCALABLE_ATTRIBUTES.put("FIRE_DAMAGE", IDFElement.FIRE.damage);
            SCALABLE_ATTRIBUTES.put("FIRE_DEFENCE", IDFElement.FIRE.defence);
            SCALABLE_ATTRIBUTES.put("WATER_DAMAGE", IDFElement.WATER.damage);
            SCALABLE_ATTRIBUTES.put("WATER_DEFENCE", IDFElement.WATER.defence);
            SCALABLE_ATTRIBUTES.put("LIGHTNING_DAMAGE", IDFElement.LIGHTNING.damage);
            SCALABLE_ATTRIBUTES.put("LIGHTNING_DEFENCE", IDFElement.LIGHTNING.defence);
            SCALABLE_ATTRIBUTES.put("MAGIC_DAMAGE", IDFElement.MAGIC.damage);
            SCALABLE_ATTRIBUTES.put("MAGIC_DEFENCE", IDFElement.MAGIC.defence);
            SCALABLE_ATTRIBUTES.put("DARK_DAMAGE", IDFElement.DARK.damage);
            SCALABLE_ATTRIBUTES.put("DARK_DEFENCE", IDFElement.DARK.defence);
            SCALABLE_ATTRIBUTES.put("HOLY_DAMAGE", IDFElement.HOLY.damage);
            SCALABLE_ATTRIBUTES.put("HOLY_DEFENCE", IDFElement.HOLY.defence);
        } //scalables
        {
            OFFENSIVE_ATTRIBUTES.add(IDFAttributes.FORCE.get());
            OFFENSIVE_ATTRIBUTES.add(IDFAttributes.LIFESTEAL.get());
            OFFENSIVE_ATTRIBUTES.add(IDFAttributes.CRIT_CHANCE.get());
            OFFENSIVE_ATTRIBUTES.add(IDFAttributes.CRIT_DAMAGE.get());
            OFFENSIVE_ATTRIBUTES.add(IDFAttributes.PENETRATING.get());
            OFFENSIVE_ATTRIBUTES.add(IDFAttributes.ACCURACY.get());
            OFFENSIVE_ATTRIBUTES.add(Attributes.ATTACK_SPEED);
            OFFENSIVE_ATTRIBUTES.add(Attributes.ATTACK_KNOCKBACK);
            OFFENSIVE_ATTRIBUTES.add(Attributes.ATTACK_DAMAGE);
            OFFENSIVE_ATTRIBUTES.add(IDFElement.FIRE.damage);
            OFFENSIVE_ATTRIBUTES.add(IDFElement.WATER.damage);
            OFFENSIVE_ATTRIBUTES.add(IDFElement.LIGHTNING.damage);
            OFFENSIVE_ATTRIBUTES.add(IDFElement.MAGIC.damage);
            OFFENSIVE_ATTRIBUTES.add(IDFElement.DARK.damage);
            OFFENSIVE_ATTRIBUTES.add(IDFElement.HOLY.damage);
        } //offensive
        {
            DEFENSIVE_ATTRIBUTES.add(Attributes.ARMOR);
            DEFENSIVE_ATTRIBUTES.add(Attributes.ARMOR_TOUGHNESS);
            DEFENSIVE_ATTRIBUTES.add(Attributes.KNOCKBACK_RESISTANCE);
            DEFENSIVE_ATTRIBUTES.add(IDFElement.FIRE.defence);
            DEFENSIVE_ATTRIBUTES.add(IDFElement.WATER.defence);
            DEFENSIVE_ATTRIBUTES.add(IDFElement.LIGHTNING.defence);
            DEFENSIVE_ATTRIBUTES.add(IDFElement.MAGIC.defence);
            DEFENSIVE_ATTRIBUTES.add(IDFElement.DARK.defence);
            DEFENSIVE_ATTRIBUTES.add(IDFElement.HOLY.defence);
            DEFENSIVE_ATTRIBUTES.add(IDFAttributes.STRIKE_MULT.get());
            DEFENSIVE_ATTRIBUTES.add(IDFAttributes.PIERCE_MULT.get());
            DEFENSIVE_ATTRIBUTES.add(IDFAttributes.SLASH_MULT.get());
            DEFENSIVE_ATTRIBUTES.add(IDFAttributes.EVASION.get());
        } //defensive
        {
            AUXILIARY_ATTRIBUTES.add(Attributes.LUCK);
            AUXILIARY_ATTRIBUTES.add(Attributes.MAX_HEALTH);
            AUXILIARY_ATTRIBUTES.add(Attributes.MOVEMENT_SPEED);
        } //auxiliary
    }

}
