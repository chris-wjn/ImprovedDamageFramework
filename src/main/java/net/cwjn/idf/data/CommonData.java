package net.cwjn.idf.data;

import net.cwjn.idf.attribute.IDFAttributes;
import net.cwjn.idf.attribute.IDFElement;
import net.cwjn.idf.config.json.data.*;
import net.cwjn.idf.config.json.data.subtypes.AuxiliaryData;
import net.cwjn.idf.config.json.data.subtypes.DefensiveData;
import net.cwjn.idf.config.json.data.subtypes.OffensiveData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;

public class CommonData {

    public static Map<ResourceLocation, ArmourData> LOGICAL_ARMOUR_MAP_FLAT = new HashMap<>();
    public static Map<ResourceLocation, ItemData> LOGICAL_ARMOUR_MAP_MULT = new HashMap<>();
    public static Map<ResourceLocation, WeaponData> LOGICAL_WEAPON_MAP_FLAT = new HashMap<>();
    public static Map<ResourceLocation, ItemData> LOGICAL_WEAPON_MAP_MULT = new HashMap<>();
    public static Map<ResourceLocation, EntityData> LOGICAL_ENTITY_MAP = new HashMap<>();
    public static Map<String, SourceCatcherData> LOGICAL_SOURCE_MAP = new HashMap<>();
    public static Map<String, Attribute> SCALABLE_ATTRIBUTES = new HashMap<>();
    public static final  Map<String, Attribute> ATTRIBUTES = new HashMap<>();

    public static EntityData getEntityData(ResourceLocation key) {
        EntityData data = LOGICAL_ENTITY_MAP.getOrDefault(key, null);
        if (data == null) return null;
        return new EntityData(null, data.damageClass(),
                OffensiveData.combine(data.oData(), data.template().getOffensiveData()),
                DefensiveData.combine(data.dData(), data.template().getDefensiveData()),
                AuxiliaryData.combine(data.aData(), data.template().getAuxiliaryData()));
    }

    static {
        for (Attribute a : ForgeRegistries.ATTRIBUTES.getValues()) {
            ATTRIBUTES.put(a.getDescriptionId(), a);
        }
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
    }

}
