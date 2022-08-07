package net.cwjn.idf.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class AttributeDamageEnchantment extends Enchantment {

    private final Attribute attribute;
    private final String prefix;
    private final UUID attributeModifierUUID;
    private boolean isMultiplier = false;

    public static EnchantmentCategory ATTRIBUTE_DAMAGE = EnchantmentCategory.create("attribute_damage",
            (Item i) -> i instanceof SwordItem || i instanceof BowItem || i instanceof CrossbowItem || i instanceof TridentItem);


    public boolean isMultiplier() {
        return isMultiplier;
    }

    protected AttributeDamageEnchantment(Enchantment.Rarity rarity, UUID id, Attribute attr, String prefix) {
        super (rarity, ATTRIBUTE_DAMAGE, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
        attributeModifierUUID = id;
        attribute = attr;
        this.prefix = prefix;
    }

    protected AttributeDamageEnchantment(Enchantment.Rarity rarity, boolean isMult, UUID id, Attribute attr, String prefix) {
        super (rarity, ATTRIBUTE_DAMAGE, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
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
        if (otherEnchant instanceof AttributeDamageEnchantment) {
            return this.isMultiplier != ((AttributeDamageEnchantment) otherEnchant).isMultiplier();
        }
        return true;
    }

    public UUID getUUID() {
        return attributeModifierUUID;
    }

    public Attribute getAttribute() {
        return attribute;
    }

    public String getPrefix() {
        return prefix;
    }

}
