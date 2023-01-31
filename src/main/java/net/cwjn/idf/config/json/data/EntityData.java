package net.cwjn.idf.config.json.data;

public record EntityData (String damageClass, double physicalDamage, double fireDamage,
                          double waterDamage, double lightningDamage, double magicDamage, double darkDamage,
                          double lifesteal, double armourPenetration, double criticalChance, double force, double knockback,
                          double attackSpeed, double defense, double physicalResistance, double fireResistance,
                          double waterResistance, double lightningResistance, double magicResistance,
                          double darkResistance, double evasion, double maxHP, double movespeed,
                          double knockbackResistance, double luck, double strikeMultiplier, double pierceMultiplier,
                          double slashMultiplier) {
}
