package net.cwjn.idf.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class AttributeResistanceEnchantment extends Enchantment {

    private final Attribute attribute;
    private final String prefix;
    private final UUID[] attributeModifierUUID;
    //first array index is a burner value, since the index for armour equipment slots are 1 2 3 4
    private static final String[] slotPrefix = {"null", "feet", "legs", "chest", "head"};
    private boolean isMultiplier = false;

    public boolean isMultiplier() {
        return isMultiplier;
    }

    protected AttributeResistanceEnchantment(Rarity rarity, UUID[] id, Attribute attr, String prefix) {
        super (rarity, EnchantmentCategory.ARMOR, new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET});
        attributeModifierUUID = id;
        attribute = attr;
        this.prefix = prefix;
    }

    protected AttributeResistanceEnchantment(Rarity rarity, boolean isMult, UUID[] id, Attribute attr, String prefix) {
        super (rarity, EnchantmentCategory.ARMOR, new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET});
        isMultiplier = isMult;
        attributeModifierUUID = id;
        attribute = attr;
        this.prefix = prefix;
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

    public UUID[] getUUIDs() {
        return attributeModifierUUID;
    }

    public Attribute getAttribute() {
        return attribute;
    }

    public String getPrefix() {
        return prefix;
    }

    public String[] getSlotPrefix() {
        return slotPrefix;
    }

}

