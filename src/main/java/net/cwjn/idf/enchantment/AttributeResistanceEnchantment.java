package net.cwjn.idf.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import org.jetbrains.annotations.NotNull;

public class AttributeResistanceEnchantment extends Enchantment {

    private boolean isMultiplier = false;

    public boolean isMultiplier() {
        return isMultiplier;
    }

    protected AttributeResistanceEnchantment(Rarity rarity) {
        super (rarity, EnchantmentCategory.ARMOR, new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET});
    }

    protected AttributeResistanceEnchantment(Rarity rarity, boolean isMult) {
        super (rarity, EnchantmentCategory.ARMOR, new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET});
        isMultiplier = isMult;
    }


    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    protected boolean checkCompatibility(@NotNull Enchantment otherEnchant) {
        if (otherEnchant instanceof  AttributeResistanceEnchantment) {
            return this.isMultiplier != ((AttributeResistanceEnchantment) otherEnchant).isMultiplier();
        }
        return true;
    }

}

