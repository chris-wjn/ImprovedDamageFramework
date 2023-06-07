package net.cwjn.idf.api;

import com.google.common.collect.ImmutableMultimap;
import net.cwjn.idf.config.json.data.ArmourData;
import net.cwjn.idf.config.json.data.subtypes.AuxiliaryData;
import net.cwjn.idf.config.json.data.subtypes.DefensiveData;
import net.cwjn.idf.config.json.data.subtypes.OffensiveData;
import net.cwjn.idf.util.Util;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;

import java.util.Map;

import static net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADDITION;

public class IDFArmourItem extends ArmorItem implements IDFCustomEquipment {

    private final double armour, weight, fireDef, waterDef, lightningDef, magicDef, darkDef, holyDef;

    public IDFArmourItem(ArmorMaterial material, EquipmentSlot slot, Properties p, int durability, double physicalDamage, double fireDamage,
                         double waterDamage, double lightningDamage, double magicDamage, double darkDamage, double holyDamage,
                         double lifesteal, double armourPenetration, double criticalChance, double force, double accuracy, double knockback,
                         double attackSpeed, double weight, double physicalDefence, double fireDefence,
                         double waterDefence, double lightningDefence, double magicDefence,
                         double darkDefence, double holyDefence, double evasion, double maxHP, double movespeed,
                         double knockbackResistance, double luck, double strikeMultiplier, double pierceMultiplier,
                         double slashMultiplier,
                         Map<Attribute, AttributeModifier> bonusAttributes) {
        super(material, slot, p);
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        ArmourData data = new ArmourData(durability,
                new OffensiveData(physicalDamage, fireDamage, waterDamage, lightningDamage, magicDamage, darkDamage, holyDamage,
                        lifesteal, armourPenetration, criticalChance, force, accuracy, knockback, attackSpeed),
                new DefensiveData(weight, physicalDefence, fireDefence, waterDefence, lightningDefence, magicDefence,
                        darkDefence, holyDefence, evasion, knockbackResistance, strikeMultiplier, pierceMultiplier, slashMultiplier),
                new AuxiliaryData(maxHP, movespeed, luck)
                );
        if (material instanceof IDFArmourMaterial idfMaterial) {
            data = ArmourData.combine(data,
                    new ArmourData(0, new OffensiveData(idfMaterial.getPhysicalDamage(slot), idfMaterial.getFireDamage(slot), idfMaterial.getWaterDamage(slot),
                            idfMaterial.getLightningDamage(slot), idfMaterial.getMagicDamage(slot), idfMaterial.getDarkDamage(slot), idfMaterial.getHolyDamage(slot),
                            idfMaterial.getLifesteal(slot), idfMaterial.getArmourPenetration(slot), idfMaterial.getCriticalChance(slot),
                            idfMaterial.getForce(slot), idfMaterial.getAccuracy(slot), idfMaterial.getKnockback(slot), idfMaterial.getAttackSpeed(slot)),
                            new DefensiveData(idfMaterial.getRealDefenseForSlot(slot), idfMaterial.getPhysicalResistanceForSlot(slot), idfMaterial.getFireResForSlot(slot),
                                    idfMaterial.getWaterResForSlot(slot), idfMaterial.getLightningResForSlot(slot), idfMaterial.getMagicResForSlot(slot),
                                    idfMaterial.getDarkResForSlot(slot), idfMaterial.getHolyResistance(slot), idfMaterial.getEvasionForSlot(slot),
                                    idfMaterial.getKnockbackResistance(), idfMaterial.getStrikeForSlot(slot), idfMaterial.getPierceForSlot(slot),
                                    idfMaterial.getSlashForSlot(slot)),
                            new AuxiliaryData(idfMaterial.getMaxHPForSlot(slot), idfMaterial.getMovespeedForSlot(slot), idfMaterial.getLuckForSlot(slot))));
            bonusAttributes.putAll(idfMaterial.getBonusAttributes());
        }
        armour = data.dData().pRes();
        this.weight = data.dData().defense();
        fireDef = data.dData().fRes();
        waterDef = data.dData().wRes();
        lightningDef = data.dData().lRes();
        magicDef = data.dData().mRes();
        darkDef = data.dData().dRes();
        holyDef = data.dData().hRes();
        data.forEach(pair -> {
            if (pair.getB() != 0) {
                builder.put(pair.getA(), new AttributeModifier(Util.UUID_BASE_STAT_ADDITION[this.getSlot().getFilterFlag()], "data0", pair.getB(), ADDITION));
            }
        });
        for (Map.Entry<Attribute, AttributeModifier> entry : bonusAttributes.entrySet()) {
            builder.put(entry.getKey(), entry.getValue());
        }
        this.defaultModifiers = builder.build();
    }

    @Override
    public int getDefense() {
        return (int) (armour + fireDef + waterDef + lightningDef + magicDef + darkDef + holyDef);
    }

    @Override
    public float getToughness() {
        return (float) weight;
    }

}
