package net.cwjn.idf.config.json.data;

public record ArmourData(int durability, double physicalDamage, double fireDamage,
                         double waterDamage, double lightningDamage, double magicDamage, double darkDamage,
                         double lifesteal, double armourPenetration, double criticalChance, double force, double knockback,
                         double attackSpeed, double defense, double physicalResistance, double fireResistance,
                         double waterResistance, double lightningResistance, double magicResistance,
                         double darkResistance, double evasion, double maxHP, double movespeed,
                         double knockbackResistance, double luck, double strikeMultiplier, double pierceMultiplier,
                         double slashMultiplier, double crushMultiplier, double genericMultiplier) {

    public static ArmourData combine(ArmourData data1, ArmourData data2) {
        return new ArmourData(data1.durability + data2.durability, data1.physicalDamage + data2.physicalDamage,
                data1.fireDamage + data2.fireDamage, data1.waterDamage + data2.waterDamage, data1.lightningDamage + data2.lightningDamage,
                data1.magicDamage + data2.magicDamage, data1.darkDamage + data2.darkDamage, data1.lifesteal + data2.lifesteal,
                data1.armourPenetration + data2.armourPenetration, data1.criticalChance + data2.criticalChance, data1.force + data2.force,
                data1.knockback + data2.knockback, data1.attackSpeed + data2.attackSpeed, data1.defense + data2.defense,
                data1.physicalResistance + data2.physicalResistance, data1.fireResistance + data2.fireResistance,
                data1.waterResistance + data2.waterResistance, data1.lightningResistance + data2.lightningResistance, data1.magicResistance + data2.magicResistance,
                data1.darkResistance + data2.darkResistance, data1.evasion + data2.evasion, data1.maxHP + data2.maxHP, data1.movespeed + data1.movespeed,
                data1.knockbackResistance + data2.knockbackResistance, data1.luck + data2.luck, data1.strikeMultiplier + data2.strikeMultiplier,
                data1.pierceMultiplier + data2.pierceMultiplier, data1.slashMultiplier + data2.slashMultiplier, data1.crushMultiplier + data2.crushMultiplier,
                data1.genericMultiplier + data2.genericMultiplier);
    }

    public static ArmourData empty() {
        return new ArmourData(0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
    }

}