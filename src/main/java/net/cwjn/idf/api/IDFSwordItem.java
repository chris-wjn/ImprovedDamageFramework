package net.cwjn.idf.api;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.cwjn.idf.config.json.data.ItemData;
import net.cwjn.idf.config.json.data.WeaponData;
import net.cwjn.idf.util.ItemInterface;
import net.cwjn.idf.util.Util;
import net.cwjn.idf.attribute.IDFAttributes;
import net.cwjn.idf.config.json.JSONHandler;
import net.cwjn.idf.config.json.data.DamageData;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static net.cwjn.idf.util.UUIDs.*;
import static net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADDITION;

public class IDFSwordItem extends SwordItem implements IDFCustomEquipment {

    private final double physicalDamage, fireDamage, waterDamage, lightningDamage, magicDamage, darkDamage;

    public IDFSwordItem(Tier tier, int durability, String damageClass, double physicalDamage, double fireDamage, double waterDamage, double lightningDamage,
                        double magicDamage, double darkDamage, double lifesteal, double pen, double crit, double force, double knockback, double speed, Properties p, Map<Attribute, AttributeModifier> bonusAttributes) {
        this(tier, durability, damageClass, physicalDamage, fireDamage, waterDamage, lightningDamage, magicDamage, darkDamage,
                lifesteal, pen, crit, force, knockback, speed, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, p, bonusAttributes);
    }

    public IDFSwordItem(Tier tier, int durability, String damageClass, double physicalDamage, double fireDamage,
                        double waterDamage, double lightningDamage, double magicDamage, double darkDamage,
                        double lifesteal, double armourPenetration, double criticalChance, double force, double knockback,
                        double attackSpeed, double defense, double physicalResistance, double fireResistance,
                        double waterResistance, double lightningResistance, double magicResistance,
                        double darkResistance, double evasion, double maxHP, double movespeed,
                        double knockbackResistance, double luck, double strikeMultiplier, double pierceMultiplier,
                        double slashMultiplier, double crushMultiplier, double genericMultiplier,
                        Properties p, Map<Attribute, AttributeModifier> bonusAttributes) {
        super(tier, (int) physicalDamage, (float) attackSpeed, p);
        ((ItemInterface) this).setDamageClass(damageClass);
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        WeaponData data0 = WeaponData.combine(JSONHandler.getWeaponData(Util.getItemRegistryName(this)),
                new WeaponData(durability, damageClass, physicalDamage, fireDamage, waterDamage, lightningDamage, magicDamage, darkDamage,
                        lifesteal, armourPenetration, criticalChance, force, knockback, attackSpeed, defense, physicalResistance, fireResistance,
                        waterResistance, lightningResistance, magicResistance, darkResistance, evasion, maxHP, movespeed, knockbackResistance,
                        luck, strikeMultiplier, pierceMultiplier, slashMultiplier, crushMultiplier, genericMultiplier));
        if (tier instanceof IDFTier modTier) {
            data0 = WeaponData.combine(data0,
                    new WeaponData(0, "strike", modTier.getAttackDamageBonus(), modTier.getFireDamage(), modTier.getWaterDamage(),
                            modTier.getLightningDamage(), modTier.getMagicDamage(), modTier.getDarkDamage(), modTier.getLifesteal(), modTier.getArmourPenetration(),
                            modTier.getCriticalChance(), modTier.getForce(), modTier.getKnockback(), modTier.getSpeed(), modTier.getDefense(), modTier.getPhysicalResistance(),
                            modTier.getFireResistance(), modTier.getWaterResistance(), modTier.getLightningResistance(), modTier.getMagicResistance(), modTier.getDarkResistance(),
                            modTier.getEvasion(), modTier.getMaxHP(), modTier.getMovespeed(), modTier.getKnockbackResistance(), modTier.getLuck(), modTier.getStrikeMultiplier(),
                            modTier.getPierceMultiplier(), modTier.getSlashMultiplier(), modTier.getCrushMultiplier(), modTier.getGenericMultiplier()));
            bonusAttributes.putAll(modTier.getBonusAttributes());
        }
        this.physicalDamage = data0.physicalDamage();
        this.fireDamage = data0.fireDamage();
        this.waterDamage = data0.waterDamage();
        this.lightningDamage = data0.lightningDamage();
        this.magicDamage = data0.magicDamage();
        this.darkDamage = data0.darkDamage();
        ((ItemInterface) this).setMaxDamage(data0.durability() + this.getMaxDamage());
        ItemData data1 = JSONHandler.getItemData(Util.getItemRegistryName(this), 1, true);
        ItemData data2 = JSONHandler.getItemData(Util.getItemRegistryName(this), 2, true);
        Util.buildWeaponAttributesOp0(builder, data0, 0, 0);
        Util.buildWeaponAttributesOp1(builder, data1);
        Util.buildWeaponAttributesOp2(builder, data2);
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

}
