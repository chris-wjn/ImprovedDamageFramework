package net.cwjn.idf.data;

import net.cwjn.idf.config.json.data.*;
import net.cwjn.idf.config.json.data.subtypes.AuxiliaryData;
import net.cwjn.idf.config.json.data.subtypes.DefensiveData;
import net.cwjn.idf.config.json.data.subtypes.OffensiveData;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class CommonData {

    public static Map<ResourceLocation, ArmourData> LOGICAL_ARMOUR_MAP_FLAT = new HashMap<>();
    public static Map<ResourceLocation, ItemData> LOGICAL_ARMOUR_MAP_MULT = new HashMap<>();
    public static Map<ResourceLocation, WeaponData> LOGICAL_WEAPON_MAP_FLAT = new HashMap<>();
    public static Map<ResourceLocation, ItemData> LOGICAL_WEAPON_MAP_MULT = new HashMap<>();
    public static Map<ResourceLocation, EntityData> LOGICAL_ENTITY_MAP = new HashMap<>();
    public static Map<String, SourceCatcherData> LOGICAL_SOURCE_MAP = new HashMap<>();

    public static EntityData getEntityData(ResourceLocation key) {
        EntityData data = LOGICAL_ENTITY_MAP.getOrDefault(key, null);
        if (data == null) return null;
        return new EntityData(null, data.damageClass(),
                OffensiveData.combine(data.oData(), data.template().getOffensiveData()),
                DefensiveData.combine(data.dData(), data.template().getDefensiveData()),
                AuxiliaryData.combine(data.aData(), data.template().getAuxiliaryData()));
    }

}
