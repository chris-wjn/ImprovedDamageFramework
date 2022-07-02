package net.cwjn.idf;

import java.util.UUID;

public abstract class UUIDs {

    //utility class to store UUIDs needed for attribute modifiers. Essentially just to make the code neater.
    public static final UUID configFireDamage = UUID.randomUUID();
    public static final UUID configWaterDamage = UUID.randomUUID();
    public static final UUID configLightningDamage = UUID.randomUUID();
    public static final UUID configMagicDamage = UUID.randomUUID();
    public static final UUID configDarkDamage = UUID.randomUUID();
    public static final UUID configAttackDamage = UUID.randomUUID();
    public static final UUID configLifesteal = UUID.randomUUID();
    public static final UUID configPenetration = UUID.randomUUID();
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

    //creates an array of length "length" with random UUIDs. Used for attribute enchantments.
    public static UUID[] createUUIDArray(int length) {
        UUID[] returnArray = new UUID[length];
        for (int i = 0; i < returnArray.length; i++) {
            returnArray[i] = UUID.randomUUID();
        }
        return returnArray;
    }

}
