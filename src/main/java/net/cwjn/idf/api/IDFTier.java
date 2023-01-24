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
            physicalDamage, fireDamage, waterDamage, lightningDamage, magicDamage, darkDamage,
            lifesteal, armourPenetration, criticalChance, force, knockback,
            defense, physicalResistance, fireResistance, waterResistance, lightningResistance, magicResistance, darkResistance,
            evasion, maxHP, movespeed, knockbackResistance, luck,
            strikeMultiplier, pierceMultiplier, slashMultiplier;
    private final Map<Attribute, AttributeModifier> bonusAttributes;
    private final TagKey<Block> tag;

    public IDFTier(int level, int durability, float speed, int enchantability, Supplier<Ingredient> item,
                   double physicalDamage, double fireDamage, double waterDamage, double lightningDamage, double magicDamage, double darkDamage,
                   double lifesteal, double armourPenetration, double criticalChance, double force, double knockback,
                   double defense, double physicalResistance, double fireResistance, double waterResistance, double lightningResistance, double magicResistance, double darkResistance,
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
        this.lifesteal = lifesteal;
        this.armourPenetration = armourPenetration;
        this.criticalChance = criticalChance;
        this.force = force;
        this.knockback = knockback;
        this.defense = defense;
        this.physicalResistance = physicalResistance;
        this.fireResistance = fireResistance;
        this.waterResistance = waterResistance;
        this.lightningResistance = lightningResistance;
        this.magicResistance = magicResistance;
        this.darkResistance = darkResistance;
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

    public double getKnockback() {
        return knockback;
    }

    public double getDefense() {
        return defense;
    }

    public double getPhysicalResistance() {
        return physicalResistance;
    }

    public double getFireResistance() {
        return fireResistance;
    }

    public double getWaterResistance() {
        return waterResistance;
    }

    public double getLightningResistance() {
        return lightningResistance;
    }

    public double getMagicResistance() {
        return magicResistance;
    }

    public double getDarkResistance() {
        return darkResistance;
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
}
