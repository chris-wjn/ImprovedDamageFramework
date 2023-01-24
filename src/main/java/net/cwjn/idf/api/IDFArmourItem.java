package net.cwjn.idf.api;

import com.google.common.collect.ImmutableMultimap;
import net.cwjn.idf.config.json.JSONHandler;
import net.cwjn.idf.config.json.data.ArmourData;
import net.cwjn.idf.config.json.data.ItemData;
import net.cwjn.idf.util.ItemInterface;
import net.cwjn.idf.util.Util;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;

import java.util.Map;

import static net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADDITION;

public class IDFArmourItem extends ArmorItem implements IDFCustomEquipment {

    private final double armour, defense, fireRes, waterRes, lightningRes, magicRes, darkRes;

    public IDFArmourItem(ArmorMaterial material, EquipmentSlot slot, Properties p, int durability, double physicalDamage, double fireDamage,
                         double waterDamage, double lightningDamage, double magicDamage, double darkDamage,
                         double lifesteal, double armourPenetration, double criticalChance, double force, double knockback,
                         double attackSpeed, double defense, double physicalResistance, double fireResistance,
                         double waterResistance, double lightningResistance, double magicResistance,
                         double darkResistance, double evasion, double maxHP, double movespeed,
                         double knockbackResistance, double luck, double strikeMultiplier, double pierceMultiplier,
                         double slashMultiplier,
                         Map<Attribute, AttributeModifier> bonusAttributes) {
        super(material, slot, p);
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        ArmourData data = new ArmourData(durability, physicalDamage, fireDamage, waterDamage, lightningDamage, magicDamage, darkDamage,
                        lifesteal, armourPenetration, criticalChance, force, knockback, attackSpeed, defense, physicalResistance, fireResistance,
                        waterResistance, lightningResistance, magicResistance, darkResistance, evasion, maxHP, movespeed, knockbackResistance,
                        luck, strikeMultiplier, pierceMultiplier, slashMultiplier);
        if (material instanceof IDFArmourMaterial idfMaterial) {
            data = ArmourData.combine(data,
                    new ArmourData(0, idfMaterial.getPhysicalDamage(slot), idfMaterial.getFireDamage(slot), idfMaterial.getWaterDamage(slot),
                            idfMaterial.getLightningDamage(slot), idfMaterial.getMagicDamage(slot), idfMaterial.getDarkDamage(slot), idfMaterial.getLifesteal(slot), idfMaterial.getArmourPenetration(slot),
                            idfMaterial.getCriticalChance(slot), idfMaterial.getForce(slot), idfMaterial.getKnockback(slot), idfMaterial.getAttackSpeed(slot), idfMaterial.getRealDefenseForSlot(slot), idfMaterial.getPhysicalResistanceForSlot(slot),
                            idfMaterial.getFireResForSlot(slot), idfMaterial.getWaterResForSlot(slot), idfMaterial.getLightningResForSlot(slot), idfMaterial.getMagicResForSlot(slot), idfMaterial.getDarkResForSlot(slot),
                            idfMaterial.getEvasionForSlot(slot), idfMaterial.getMaxHPForSlot(slot), idfMaterial.getMovespeedForSlot(slot), idfMaterial.getKnockbackResistance(), idfMaterial.getLuckForSlot(slot), idfMaterial.getStrikeForSlot(slot),
                            idfMaterial.getPierceForSlot(slot), idfMaterial.getSlashForSlot(slot)));
            bonusAttributes.putAll(idfMaterial.getBonusAttributes());
        }
        armour = data.physicalResistance();
        this.defense = data.defense();
        fireRes = data.fireResistance();
        waterRes = data.waterResistance();
        lightningRes = data.lightningResistance();
        magicRes = data.magicResistance();
        darkRes = data.darkResistance();
        data.forEach(pair -> {
            if (pair.getB() != 0) {
                builder.put(pair.getA(), new AttributeModifier("baseAttributes", pair.getB(), ADDITION));
            }
        });
        for (Map.Entry<Attribute, AttributeModifier> entry : bonusAttributes.entrySet()) {
            builder.put(entry.getKey(), entry.getValue());
        }
        this.defaultModifiers = builder.build();
    }

    @Override
    public int getDefense() {
        return (int) (armour + fireRes + waterRes + lightningRes + magicRes + darkRes);
    }

    @Override
    public float getToughness() {
        return (float) defense;
    }

}
