package net.cwjn.idf.api;

import com.google.common.collect.ImmutableMultimap;
import net.cwjn.idf.config.json.data.WeaponData;
import net.cwjn.idf.config.json.data.subtypes.AuxiliaryData;
import net.cwjn.idf.config.json.data.subtypes.DefensiveData;
import net.cwjn.idf.config.json.data.subtypes.OffensiveData;
import net.cwjn.idf.util.ItemInterface;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;

import java.util.Map;

import static net.cwjn.idf.util.Util.UUID_BASE_STAT_ADDITION;
import static net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADDITION;

public class IDFSwordItem extends SwordItem implements IDFCustomEquipment {

    private final double physicalDamage, fireDamage, waterDamage, lightningDamage, magicDamage, darkDamage, holyDamage;

    public IDFSwordItem(Tier tier, int durability, String damageClass, double physicalDamage, double fireDamage, double waterDamage, double lightningDamage,
                        double magicDamage, double darkDamage, double holyDamage, double lifesteal, double pen, double crit, double force, double knockback, double speed, Properties p, Map<Attribute, AttributeModifier> bonusAttributes) {
        this(tier, durability, damageClass, physicalDamage, fireDamage, waterDamage, lightningDamage, magicDamage, darkDamage, holyDamage,
                lifesteal, pen, crit, force, 0, knockback, speed, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, p, bonusAttributes);
    }

    public IDFSwordItem(Tier tier, int durability, String damageClass, double physicalDamage, double fireDamage,
                        double waterDamage, double lightningDamage, double magicDamage, double darkDamage, double holyDamage,
                        double lifesteal, double armourPenetration, double criticalChance, double force, double accuracy, double knockback,
                        double attackSpeed, double weight, double physicalDefence, double fireDefence,
                        double waterDefence, double lightningDefence, double magicDefence,
                        double darkDefence, double holyDefence, double evasion, double maxHP, double movespeed,
                        double knockbackResistance, double luck, double strikeMultiplier, double pierceMultiplier,
                        double slashMultiplier,
                        Properties p, Map<Attribute, AttributeModifier> bonusAttributes) {
        super(tier, (int) physicalDamage, (float) attackSpeed, p);
        ((ItemInterface) this).setDamageClass(damageClass);
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        WeaponData data = new WeaponData(durability, damageClass, false,
                new OffensiveData(physicalDamage, fireDamage, waterDamage, lightningDamage, magicDamage, darkDamage, holyDamage,
                        lifesteal, armourPenetration, criticalChance, force, accuracy, knockback, attackSpeed),
                new DefensiveData(weight, physicalDefence, fireDefence, waterDefence, lightningDefence, magicDefence,
                        darkDefence, holyDefence, evasion, knockbackResistance, strikeMultiplier, pierceMultiplier, slashMultiplier),
                new AuxiliaryData(maxHP, movespeed, luck)
        );
        if (tier instanceof IDFTier modTier) {
            data = WeaponData.combine(data,
                    new WeaponData(0, "", false, new OffensiveData(modTier.getAttackDamageBonus(), modTier.getFireDamage(), modTier.getWaterDamage(),
                            modTier.getLightningDamage(), modTier.getMagicDamage(), modTier.getDarkDamage(), modTier.getHolyDamage(),
                            modTier.getLifesteal(), modTier.getArmourPenetration(), modTier.getCriticalChance(),
                            modTier.getForce(), modTier.getAccuracy(), modTier.getKnockback(), modTier.getSpeed()),
                            new DefensiveData(modTier.getWeight(), modTier.getPhysicalDefence(), modTier.getFireDefence(),
                                    modTier.getWaterDefence(), modTier.getLightningDefence(), modTier.getMagicDefence(),
                                    modTier.getDarkDefence(), modTier.getHolyDefence(), modTier.getEvasion(),
                                    modTier.getKnockbackResistance(), modTier.getStrikeMultiplier(), modTier.getPierceMultiplier(),
                                    modTier.getSlashMultiplier()),
                            new AuxiliaryData(modTier.getMaxHP(), modTier.getMovespeed(), modTier.getLuck())));
            bonusAttributes.putAll(modTier.getBonusAttributes());
        }
        this.physicalDamage = physicalDamage;
        this.fireDamage = fireDamage;
        this.waterDamage = waterDamage;
        this.lightningDamage = lightningDamage;
        this.magicDamage = magicDamage;
        this.darkDamage = darkDamage;
        this.holyDamage = holyDamage;
        data.forEach(pair -> {
            if (pair.getB() != 0) {
                builder.put(pair.getA(), new AttributeModifier(UUID_BASE_STAT_ADDITION[0], "data0", pair.getB(), ADDITION));
            }
        });
        for (Map.Entry<Attribute, AttributeModifier> entry : bonusAttributes.entrySet()) {
            builder.put(entry.getKey(), entry.getValue());
        }
        this.defaultModifiers = builder.build();
    }

    @Override
    public float getDamage() {
        return (float) (physicalDamage + fireDamage + waterDamage + lightningDamage + magicDamage + darkDamage);
    }

    public double getFireDamage() {
        return fireDamage;
    }

    public double getWaterDamage() {
        return waterDamage;
    }

    public double getLightningDamage() {
        return lightningDamage;
    }

    public double getMagicDamage() {
        return magicDamage;
    }

    public double getDarkDamage() {
        return darkDamage;
    }

    public double getHolyDamage() {
        return holyDamage;
    }

}
