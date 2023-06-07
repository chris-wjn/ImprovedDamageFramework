package net.cwjn.idf.api;

import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Supplier;

public class IDFTier implements Tier {

    private final Supplier<Ingredient> repairIngredient;
    private final int level, durability, enchantability;
    private final float speed;
    private final double
            physicalDamage, fireDamage, waterDamage, lightningDamage, magicDamage, darkDamage, holyDamage,
            lifesteal, armourPenetration, criticalChance, force, accuracy, knockback,
            weight, physicalDefence, fireDefence, waterDefence, lightningDefence, magicDefence, darkDefence, holyDefence,
            evasion, maxHP, movespeed, knockbackResistance, luck,
            strikeMultiplier, pierceMultiplier, slashMultiplier;
    private final Map<Attribute, AttributeModifier> bonusAttributes;
    private final TagKey<Block> tag;

    public IDFTier(int level, int durability, float speed, int enchantability, Supplier<Ingredient> item,
                   double physicalDamage, double fireDamage, double waterDamage, double lightningDamage, double magicDamage, double darkDamage, double holyDamage,
                   double lifesteal, double armourPenetration, double criticalChance, double force, double accuracy, double knockback,
                   double weight, double physicalDefence, double fireDefence, double waterDefence, double lightningDefence, double magicDefence, double darkDefence, double holyDefence,
                   double evasion, double maxHP, double movespeed, double knockbackResistance, double luck,
                   double strikeMultiplier, double pierceMultiplier, double slashMultiplier,
                   Map<Attribute, AttributeModifier> bonusAttributes, @NotNull TagKey<Block> tag) {
        this.level = level;
        this.durability = durability;
        this.speed = speed;
        this.enchantability = enchantability;
        this.repairIngredient = item;
        this.physicalDamage = physicalDamage;
        this.fireDamage = fireDamage;
        this.waterDamage = waterDamage;
        this.lightningDamage = lightningDamage;
        this.magicDamage = magicDamage;
        this.darkDamage = darkDamage;
        this.holyDamage = holyDamage;
        this.lifesteal = lifesteal;
        this.armourPenetration = armourPenetration;
        this.criticalChance = criticalChance;
        this.force = force;
        this.accuracy = accuracy;
        this.knockback = knockback;
        this.weight = weight;
        this.physicalDefence = physicalDefence;
        this.fireDefence = fireDefence;
        this.waterDefence = waterDefence;
        this.lightningDefence = lightningDefence;
        this.magicDefence = magicDefence;
        this.darkDefence = darkDefence;
        this.holyDefence = holyDefence;
        this.evasion = evasion;
        this.maxHP = maxHP;
        this.movespeed = movespeed;
        this.knockbackResistance = knockbackResistance;
        this.luck = luck;
        this.strikeMultiplier = strikeMultiplier;
        this.pierceMultiplier = pierceMultiplier;
        this.slashMultiplier = slashMultiplier;
        this.bonusAttributes = bonusAttributes;
        this.tag = tag;
    }

    @Override
    public float getAttackDamageBonus() {
        return (float) physicalDamage;
    }

    @Override
    public int getUses() {
        return durability;
    }

    @Override
    public float getSpeed() {
        return speed;
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public int getEnchantmentValue() {
        return enchantability;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return repairIngredient.get();
    }

    @Override
    public @Nullable TagKey<Block> getTag() {
        return tag;
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

    public double getLifesteal() {
        return lifesteal;
    }

    public double getArmourPenetration() {
        return armourPenetration;
    }

    public double getCriticalChance() {
        return criticalChance;
    }

    public double getForce() {
        return force;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public double getKnockback() {
        return knockback;
    }

    public double getWeight() {
        return weight;
    }

    public double getPhysicalDefence() {
        return physicalDefence;
    }

    public double getFireDefence() {
        return fireDefence;
    }

    public double getWaterDefence() {
        return waterDefence;
    }

    public double getLightningDefence() {
        return lightningDefence;
    }

    public double getMagicDefence() {
        return magicDefence;
    }

    public double getDarkDefence() {
        return darkDefence;
    }

    public double getEvasion() {
        return evasion;
    }

    public double getMaxHP() {
        return maxHP;
    }

    public double getMovespeed() {
        return movespeed;
    }

    public double getKnockbackResistance() {
        return knockbackResistance;
    }

    public double getLuck() {
        return luck;
    }

    public double getStrikeMultiplier() {
        return strikeMultiplier;
    }

    public double getPierceMultiplier() {
        return pierceMultiplier;
    }

    public double getSlashMultiplier() {
        return slashMultiplier;
    }

    public Map<Attribute, AttributeModifier> getBonusAttributes() {
        return bonusAttributes;
    }

    public double getHolyDefence() {
        return holyDefence;
    }

    public double getHolyDamage() {
        return holyDamage;
    }
}
