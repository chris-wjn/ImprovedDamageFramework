package net.cwjn.idf.util;

import java.util.UUID;

public abstract class UUIDs {

    //utility class to store UUIDs needed for attribute modifiers. Essentially just to make the code neater.
    public static final UUID baseFireDamage = UUID.randomUUID();
    public static final UUID baseWaterDamage = UUID.randomUUID();
    public static final UUID baseLightningDamage = UUID.randomUUID();
    public static final UUID baseMagicDamage = UUID.randomUUID();
    public static final UUID baseDarkDamage = UUID.randomUUID();
    public static final UUID baseCrit = UUID.randomUUID();
    public static final UUID baseWeight = UUID.randomUUID();
    public static final UUID basePen = UUID.randomUUID();
    public static final UUID baseLifesteal = UUID.randomUUID();
    public static final UUID configFireDamage = UUID.randomUUID();
    public static final UUID configWaterDamage = UUID.randomUUID();
    public static final UUID configLightningDamage = UUID.randomUUID();
    public static final UUID configMagicDamage = UUID.randomUUID();
    public static final UUID configDarkDamage = UUID.randomUUID();
    public static final UUID configAttackDamage = UUID.randomUUID();
    public static final UUID configLifesteal = UUID.randomUUID();
    public static final UUID configPenetration = UUID.randomUUID();
    public static final UUID configWeight = UUID.randomUUID();
    public static final UUID configCrit = UUID.randomUUID();
    public static final UUID helmetConfigFireResistance = UUID.randomUUID();
    public static final UUID helmetConfigWaterResistance = UUID.randomUUID();
    public static final UUID helmetConfigLightningResistance = UUID.randomUUID();
    public static final UUID helmetConfigMagicResistance = UUID.randomUUID();
    public static final UUID helmetConfigDarkResistance = UUID.randomUUID();
    public static final UUID helmetConfigEvasion = UUID.randomUUID();
    public static final UUID helmetConfigMaxHP = UUID.randomUUID();
    public static final UUID helmetConfigMovespeed = UUID.randomUUID();
    public static final UUID helmetConfigKnockbackResistance = UUID.randomUUID();
    public static final UUID helmetConfigLuck = UUID.randomUUID();
    public static final UUID helmetConfigStrikeMult = UUID.randomUUID();
    public static final UUID helmetConfigPierceMult = UUID.randomUUID();
    public static final UUID helmetConfigCrushMult = UUID.randomUUID();
    public static final UUID helmetConfigSlashMult = UUID.randomUUID();
    public static final UUID helmetConfigGenericMult = UUID.randomUUID();
    public static final UUID helmetConfigArmour = UUID.randomUUID();
    public static final UUID helmetConfigArmourToughness = UUID.randomUUID();
    public static final UUID chestplateConfigFireResistance = UUID.randomUUID();
    public static final UUID chestplateConfigWaterResistance = UUID.randomUUID();
    public static final UUID chestplateConfigLightningResistance = UUID.randomUUID();
    public static final UUID chestplateConfigMagicResistance = UUID.randomUUID();
    public static final UUID chestplateConfigDarkResistance = UUID.randomUUID();
    public static final UUID chestplateConfigEvasion = UUID.randomUUID();
    public static final UUID chestplateConfigMaxHP = UUID.randomUUID();
    public static final UUID chestplateConfigMovespeed = UUID.randomUUID();
    public static final UUID chestplateConfigKnockbackResistance = UUID.randomUUID();
    public static final UUID chestplateConfigLuck = UUID.randomUUID();
    public static final UUID chestplateConfigStrikeMult = UUID.randomUUID();
    public static final UUID chestplateConfigPierceMult = UUID.randomUUID();
    public static final UUID chestplateConfigCrushMult = UUID.randomUUID();
    public static final UUID chestplateConfigSlashMult = UUID.randomUUID();
    public static final UUID chestplateConfigGenericMult = UUID.randomUUID();
    public static final UUID chestplateConfigArmour = UUID.randomUUID();
    public static final UUID chestplateConfigArmourToughness = UUID.randomUUID();
    public static final UUID leggingsConfigFireResistance = UUID.randomUUID();
    public static final UUID leggingsConfigWaterResistance = UUID.randomUUID();
    public static final UUID leggingsConfigLightningResistance = UUID.randomUUID();
    public static final UUID leggingsConfigMagicResistance = UUID.randomUUID();
    public static final UUID leggingsConfigDarkResistance = UUID.randomUUID();
    public static final UUID leggingsConfigEvasion = UUID.randomUUID();
    public static final UUID leggingsConfigMaxHP = UUID.randomUUID();
    public static final UUID leggingsConfigMovespeed = UUID.randomUUID();
    public static final UUID leggingsConfigKnockbackResistance = UUID.randomUUID();
    public static final UUID leggingsConfigLuck = UUID.randomUUID();
    public static final UUID leggingsConfigStrikeMult = UUID.randomUUID();
    public static final UUID leggingsConfigPierceMult = UUID.randomUUID();
    public static final UUID leggingsConfigCrushMult = UUID.randomUUID();
    public static final UUID leggingsConfigSlashMult = UUID.randomUUID();
    public static final UUID leggingsConfigGenericMult = UUID.randomUUID();
    public static final UUID leggingsConfigArmour = UUID.randomUUID();
    public static final UUID leggingsConfigArmourToughness = UUID.randomUUID();
    public static final UUID bootsConfigFireResistance = UUID.randomUUID();
    public static final UUID bootsConfigWaterResistance = UUID.randomUUID();
    public static final UUID bootsConfigLightningResistance = UUID.randomUUID();
    public static final UUID bootsConfigMagicResistance = UUID.randomUUID();
    public static final UUID bootsConfigDarkResistance = UUID.randomUUID();
    public static final UUID bootsConfigEvasion = UUID.randomUUID();
    public static final UUID bootsConfigMaxHP = UUID.randomUUID();
    public static final UUID bootsConfigMovespeed = UUID.randomUUID();
    public static final UUID bootsConfigKnockbackResistance = UUID.randomUUID();
    public static final UUID bootsConfigLuck = UUID.randomUUID();
    public static final UUID bootsConfigStrikeMult = UUID.randomUUID();
    public static final UUID bootsConfigPierceMult = UUID.randomUUID();
    public static final UUID bootsConfigCrushMult = UUID.randomUUID();
    public static final UUID bootsConfigSlashMult = UUID.randomUUID();
    public static final UUID bootsConfigGenericMult = UUID.randomUUID();
    public static final UUID bootsConfigArmour = UUID.randomUUID();
    public static final UUID bootsConfigArmourToughness = UUID.randomUUID();
    /*ResistanceData data = JSONHandler.getResistanceData(loc);
                    if (event.getSlotType() == EquipmentSlot.HEAD) {
                        event.addModifier(Attributes.ARMOR, new AttributeModifier(helmetConfigArmour, () -> "helmet_config_armour", data.getArmour(), AttributeModifier.Operation.ADDITION));
                        event.addModifier(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(helmetConfigArmourToughness, () -> "helmet_config_armour_toughness", data.getArmourToughness(), AttributeModifier.Operation.ADDITION));
                        event.addModifier(IDFAttributes.FIRE_RESISTANCE.get(), new AttributeModifier(helmetConfigFireResistance, () -> "helmet_config_fire_resistance", data.getFire(), AttributeModifier.Operation.ADDITION));
                        event.addModifier(IDFAttributes.WATER_RESISTANCE.get(), new AttributeModifier(helmetConfigWaterResistance, () -> "helmet_config_water_resistance", data.getWater(), AttributeModifier.Operation.ADDITION));
                        event.addModifier(IDFAttributes.LIGHTNING_RESISTANCE.get(), new AttributeModifier(helmetConfigLightningResistance, () -> "helmet_config_lightning_resistance", data.getLightning(), AttributeModifier.Operation.ADDITION));
                        event.addModifier(IDFAttributes.MAGIC_RESISTANCE.get(), new AttributeModifier(helmetConfigMagicResistance, () -> "helmet_config_magic_resistance", data.getMagic(), AttributeModifier.Operation.ADDITION));
                        event.addModifier(IDFAttributes.DARK_RESISTANCE.get(), new AttributeModifier(helmetConfigDarkResistance, () -> "helmet_config_dark_resistance", data.getDark(), AttributeModifier.Operation.ADDITION));
                        event.addModifier(IDFAttributes.EVASION.get(), new AttributeModifier(helmetConfigEvasion, () -> "helmet_config_evasion", data.getEvasion(), AttributeModifier.Operation.ADDITION));
                        event.addModifier(Attributes.MAX_HEALTH, new AttributeModifier(helmetConfigMaxHP, () -> "helmet_config_max_hp", data.getMaxHP(), AttributeModifier.Operation.ADDITION));
                        event.addModifier(Attributes.MOVEMENT_SPEED, new AttributeModifier(helmetConfigMovespeed, () -> "helmet_config_movespeed", data.getMovespeed(), AttributeModifier.Operation.ADDITION));
                        event.addModifier(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(helmetConfigKnockbackResistance, () -> "helmet_config_knockback_resistance", data.getKnockbackRes(), AttributeModifier.Operation.ADDITION));
                        event.addModifier(Attributes.LUCK, new AttributeModifier(helmetConfigLuck, () -> "helmet_config_luck", data.getLuck(), AttributeModifier.Operation.ADDITION));
                        event.addModifier(IDFAttributes.STRIKE_MULT.get(), new AttributeModifier(helmetConfigStrikeMult, () -> "helmet_config_strike", data.getStrikeMult(), AttributeModifier.Operation.ADDITION));
                        event.addModifier(IDFAttributes.PIERCE_MULT.get(), new AttributeModifier(helmetConfigPierceMult, () -> "helmet_config_pierce", data.getPierceMult(), AttributeModifier.Operation.ADDITION));
                        event.addModifier(IDFAttributes.SLASH_MULT.get(), new AttributeModifier(helmetConfigSlashMult, () -> "helmet_config_slash", data.getSlashMult(), AttributeModifier.Operation.ADDITION));
                        event.addModifier(IDFAttributes.CRUSH_MULT.get(), new AttributeModifier(helmetConfigCrushMult, () -> "helmet_config_crush", data.getCrushMult(), AttributeModifier.Operation.ADDITION));
                        event.addModifier(IDFAttributes.GENERIC_MULT.get(), new AttributeModifier(helmetConfigGenericMult, () -> "helmet_config_generic", data.getGenericMult(), AttributeModifier.Operation.ADDITION));
                    }
                    else if (event.getSlotType() == EquipmentSlot.CHEST) {
                        event.addModifier(Attributes.ARMOR, new AttributeModifier(chestplateConfigArmour, () -> "chestplate_config_armour", data.getArmour(), AttributeModifier.Operation.ADDITION));
                        event.addModifier(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(chestplateConfigArmourToughness, () -> "chestplate_config_armour_toughness", data.getArmourToughness(), AttributeModifier.Operation.ADDITION));
                        event.addModifier(IDFAttributes.FIRE_RESISTANCE.get(), new AttributeModifier(chestplateConfigFireResistance, () -> "chestplate_config_fire_resistance", data.getFire(), AttributeModifier.Operation.ADDITION));
                        event.addModifier(IDFAttributes.WATER_RESISTANCE.get(), new AttributeModifier(chestplateConfigWaterResistance, () -> "chestplate_config_water_resistance", data.getWater(), AttributeModifier.Operation.ADDITION));
                        event.addModifier(IDFAttributes.LIGHTNING_RESISTANCE.get(), new AttributeModifier(chestplateConfigLightningResistance, () -> "chestplate_config_lightning_resistance", data.getLightning(), AttributeModifier.Operation.ADDITION));
                        event.addModifier(IDFAttributes.MAGIC_RESISTANCE.get(), new AttributeModifier(chestplateConfigMagicResistance, () -> "chestplate_config_magic_resistance", data.getMagic(), AttributeModifier.Operation.ADDITION));
                        event.addModifier(IDFAttributes.DARK_RESISTANCE.get(), new AttributeModifier(chestplateConfigDarkResistance, () -> "chestplate_config_dark_resistance", data.getDark(), AttributeModifier.Operation.ADDITION));
                        event.addModifier(IDFAttributes.EVASION.get(), new AttributeModifier(chestplateConfigEvasion, () -> "chestplate_config_evasion", data.getEvasion(), AttributeModifier.Operation.ADDITION));
                        event.addModifier(Attributes.MAX_HEALTH, new AttributeModifier(chestplateConfigMaxHP, () -> "chestplate_config_max_hp", data.getMaxHP(), AttributeModifier.Operation.ADDITION));
                        event.addModifier(Attributes.MOVEMENT_SPEED, new AttributeModifier(chestplateConfigMovespeed, () -> "chestplate_config_movespeed", data.getMovespeed(), AttributeModifier.Operation.ADDITION));
                        event.addModifier(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(chestplateConfigKnockbackResistance, () -> "chestplate_config_knockback_resistance", data.getKnockbackRes(), AttributeModifier.Operation.ADDITION));
                        event.addModifier(Attributes.LUCK, new AttributeModifier(chestplateConfigLuck, () -> "chestplate_config_luck", data.getLuck(), AttributeModifier.Operation.ADDITION));
                        event.addModifier(IDFAttributes.STRIKE_MULT.get(), new AttributeModifier(chestplateConfigStrikeMult, () -> "chestplate_config_strike", data.getStrikeMult(), AttributeModifier.Operation.ADDITION));
                        event.addModifier(IDFAttributes.PIERCE_MULT.get(), new AttributeModifier(chestplateConfigPierceMult, () -> "chestplate_config_pierce", data.getPierceMult(), AttributeModifier.Operation.ADDITION));
                        event.addModifier(IDFAttributes.SLASH_MULT.get(), new AttributeModifier(chestplateConfigSlashMult, () -> "chestplate_config_slash", data.getSlashMult(), AttributeModifier.Operation.ADDITION));
                        event.addModifier(IDFAttributes.CRUSH_MULT.get(), new AttributeModifier(chestplateConfigCrushMult, () -> "chestplate_config_crush", data.getCrushMult(), AttributeModifier.Operation.ADDITION));
                        event.addModifier(IDFAttributes.GENERIC_MULT.get(), new AttributeModifier(chestplateConfigGenericMult, () -> "chestplate_config_generic", data.getGenericMult(), AttributeModifier.Operation.ADDITION));
                    }
                    else if (event.getSlotType() == EquipmentSlot.LEGS) {
                        event.addModifier(Attributes.ARMOR, new AttributeModifier(leggingsConfigArmour, () -> "leggings_config_armour", data.getArmour(), AttributeModifier.Operation.ADDITION));
                        event.addModifier(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(leggingsConfigArmourToughness, () -> "leggings_config_armour_toughness", data.getArmourToughness(), AttributeModifier.Operation.ADDITION));
                        event.addModifier(IDFAttributes.FIRE_RESISTANCE.get(), new AttributeModifier(leggingsConfigFireResistance, () -> "leggings_config_fire_resistance", data.getFire(), AttributeModifier.Operation.ADDITION));
                        event.addModifier(IDFAttributes.WATER_RESISTANCE.get(), new AttributeModifier(leggingsConfigWaterResistance, () -> "leggings_config_water_resistance", data.getWater(), AttributeModifier.Operation.ADDITION));
                        event.addModifier(IDFAttributes.LIGHTNING_RESISTANCE.get(), new AttributeModifier(leggingsConfigLightningResistance, () -> "leggings_config_lightning_resistance", data.getLightning(), AttributeModifier.Operation.ADDITION));
                        event.addModifier(IDFAttributes.MAGIC_RESISTANCE.get(), new AttributeModifier(leggingsConfigMagicResistance, () -> "leggings_config_magic_resistance", data.getMagic(), AttributeModifier.Operation.ADDITION));
                        event.addModifier(IDFAttributes.DARK_RESISTANCE.get(), new AttributeModifier(leggingsConfigDarkResistance, () -> "leggings_config_dark_resistance", data.getDark(), AttributeModifier.Operation.ADDITION));
                        event.addModifier(IDFAttributes.EVASION.get(), new AttributeModifier(leggingsConfigEvasion, () -> "leggings_config_evasion", data.getEvasion(), AttributeModifier.Operation.ADDITION));
                        event.addModifier(Attributes.MAX_HEALTH, new AttributeModifier(leggingsConfigMaxHP, () -> "leggings_config_max_hp", data.getMaxHP(), AttributeModifier.Operation.ADDITION));
                        event.addModifier(Attributes.MOVEMENT_SPEED, new AttributeModifier(leggingsConfigMovespeed, () -> "leggings_config_movespeed", data.getMovespeed(), AttributeModifier.Operation.ADDITION));
                        event.addModifier(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(leggingsConfigKnockbackResistance, () -> "leggings_config_knockback_resistance", data.getKnockbackRes(), AttributeModifier.Operation.ADDITION));
                        event.addModifier(Attributes.LUCK, new AttributeModifier(leggingsConfigLuck, () -> "leggings_config_luck", data.getLuck(), AttributeModifier.Operation.ADDITION));
                        event.addModifier(IDFAttributes.STRIKE_MULT.get(), new AttributeModifier(leggingsConfigStrikeMult, () -> "leggings_config_strike", data.getStrikeMult(), AttributeModifier.Operation.ADDITION));
                        event.addModifier(IDFAttributes.PIERCE_MULT.get(), new AttributeModifier(leggingsConfigPierceMult, () -> "leggings_config_pierce", data.getPierceMult(), AttributeModifier.Operation.ADDITION));
                        event.addModifier(IDFAttributes.SLASH_MULT.get(), new AttributeModifier(leggingsConfigSlashMult, () -> "leggings_config_slash", data.getSlashMult(), AttributeModifier.Operation.ADDITION));
                        event.addModifier(IDFAttributes.CRUSH_MULT.get(), new AttributeModifier(leggingsConfigCrushMult, () -> "leggings_config_crush", data.getCrushMult(), AttributeModifier.Operation.ADDITION));
                        event.addModifier(IDFAttributes.GENERIC_MULT.get(), new AttributeModifier(leggingsConfigGenericMult, () -> "leggings_config_generic", data.getGenericMult(), AttributeModifier.Operation.ADDITION));
                    }
                    else if (event.getSlotType() == EquipmentSlot.FEET) {
                        event.addModifier(Attributes.ARMOR, new AttributeModifier(bootsConfigArmour, () -> "boots_config_armour", data.getArmour(), AttributeModifier.Operation.ADDITION));
                        event.addModifier(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(bootsConfigArmourToughness, () -> "boots_config_armour_toughness", data.getArmourToughness(), AttributeModifier.Operation.ADDITION));
                        event.addModifier(IDFAttributes.FIRE_RESISTANCE.get(), new AttributeModifier(bootsConfigFireResistance, () -> "boots_config_fire_resistance", data.getFire(), AttributeModifier.Operation.ADDITION));
                        event.addModifier(IDFAttributes.WATER_RESISTANCE.get(), new AttributeModifier(bootsConfigWaterResistance, () -> "boots_config_water_resistance", data.getWater(), AttributeModifier.Operation.ADDITION));
                        event.addModifier(IDFAttributes.LIGHTNING_RESISTANCE.get(), new AttributeModifier(bootsConfigLightningResistance, () -> "boots_config_lightning_resistance", data.getLightning(), AttributeModifier.Operation.ADDITION));
                        event.addModifier(IDFAttributes.MAGIC_RESISTANCE.get(), new AttributeModifier(bootsConfigMagicResistance, () -> "boots_config_magic_resistance", data.getMagic(), AttributeModifier.Operation.ADDITION));
                        event.addModifier(IDFAttributes.DARK_RESISTANCE.get(), new AttributeModifier(bootsConfigDarkResistance, () -> "boots_config_dark_resistance", data.getDark(), AttributeModifier.Operation.ADDITION));
                        event.addModifier(IDFAttributes.EVASION.get(), new AttributeModifier(bootsConfigEvasion, () -> "boots_config_evasion", data.getEvasion(), AttributeModifier.Operation.ADDITION));
                        event.addModifier(Attributes.MAX_HEALTH, new AttributeModifier(bootsConfigMaxHP, () -> "boots_config_max_hp", data.getMaxHP(), AttributeModifier.Operation.ADDITION));
                        event.addModifier(Attributes.MOVEMENT_SPEED, new AttributeModifier(bootsConfigMovespeed, () -> "boots_config_movespeed", data.getMovespeed(), AttributeModifier.Operation.ADDITION));
                        event.addModifier(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(bootsConfigKnockbackResistance, () -> "boots_config_knockback_resistance", data.getKnockbackRes(), AttributeModifier.Operation.ADDITION));
                        event.addModifier(Attributes.LUCK, new AttributeModifier(bootsConfigLuck, () -> "boots_config_luck", data.getLuck(), AttributeModifier.Operation.ADDITION));
                        event.addModifier(IDFAttributes.STRIKE_MULT.get(), new AttributeModifier(bootsConfigStrikeMult, () -> "boots_config_strike", data.getStrikeMult(), AttributeModifier.Operation.ADDITION));
                        event.addModifier(IDFAttributes.PIERCE_MULT.get(), new AttributeModifier(bootsConfigPierceMult, () -> "boots_config_pierce", data.getPierceMult(), AttributeModifier.Operation.ADDITION));
                        event.addModifier(IDFAttributes.SLASH_MULT.get(), new AttributeModifier(bootsConfigSlashMult, () -> "boots_config_slash", data.getSlashMult(), AttributeModifier.Operation.ADDITION));
                        event.addModifier(IDFAttributes.CRUSH_MULT.get(), new AttributeModifier(bootsConfigCrushMult, () -> "boots_config_crush", data.getCrushMult(), AttributeModifier.Operation.ADDITION));
                        event.addModifier(IDFAttributes.GENERIC_MULT.get(), new AttributeModifier(bootsConfigGenericMult, () -> "boots_config_generic", data.getGenericMult(), AttributeModifier.Operation.ADDITION));
                    }*/
    //creates an array of length "length" with random UUIDs. Used for attribute enchantments.
    public static UUID[] createUUIDArray(int length) {
        UUID[] returnArray = new UUID[length];
        for (int i = 0; i < returnArray.length; i++) {
            returnArray[i] = UUID.randomUUID();
        }
        return returnArray;
    }

}
