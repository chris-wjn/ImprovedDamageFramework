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
    private final float attackSpeed;
    private final double physicalDamage;
    private final Map<Attribute, AttributeModifier> bonusAttributes;
    private final TagKey<Block> tag;

    public IDFTier(int level, int durability, float attackSpeed, int enchantability, Supplier<Ingredient> item,
                   double physicalDamage, Map<Attribute, AttributeModifier> bonusAttributes, @NotNull TagKey<Block> tag) {
        this.level = level;
        this.durability = durability;
        this.attackSpeed = attackSpeed;
        this.enchantability = enchantability;
        this.repairIngredient = item;
        this.physicalDamage = physicalDamage;
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
        return attackSpeed;
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

    public Map<Attribute, AttributeModifier> getBonusAttributes() {
        return bonusAttributes;
    }

}
