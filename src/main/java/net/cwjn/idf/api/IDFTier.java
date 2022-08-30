package net.cwjn.idf.api;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class IDFTier implements Tier {

    private final Supplier<Ingredient> repairIngredient;
    private final int level, durability, enchantability;
    private final float speed, fire, water, lightning, magic, dark, physical, crit, weight, pen, lifesteal;
    private final TagKey<Block> tag;

    public IDFTier(int level, int durability, float speed, int enchantability, Supplier<Ingredient> item,
                   float physical, float fire, float water, float lightning, float magic, float dark,
                   float crit, float weight, float pen, float lifesteal, @NotNull TagKey<Block> tag) {
        this.level = level;
        this.durability = durability;
        this.speed = speed;
        this.enchantability = enchantability;
        this.repairIngredient = item;
        this.physical = physical;
        this.fire = fire;
        this.water = water;
        this.lightning = lightning;
        this.magic = magic;
        this.dark = dark;
        this.crit = crit;
        this.weight = weight;
        this.pen = pen;
        this.lifesteal = lifesteal;
        this.tag = tag;
    }

    @Override
    public float getAttackDamageBonus() {
        return physical;
    }

    public float getFire() {
        return fire;
    }

    public float getWater() {
        return water;
    }

    public float getLightning() {
        return lightning;
    }

    public float getMagic() {
        return magic;
    }

    public float getDark() {
        return dark;
    }

    public float getCrit() {
        return crit;
    }

    public float getWeight() {
        return weight;
    }

    public float getPen() {
        return pen;
    }

    public float getLifesteal() {
        return lifesteal;
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

}
