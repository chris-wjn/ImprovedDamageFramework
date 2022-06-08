package net.cwjn.idf.Enchantments;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import org.jetbrains.annotations.NotNull;
import se.mickelus.tetra.items.modular.ModularItem;

public class AttributeDamageEnchantment extends Enchantment {

    public static EnchantmentCategory ATTRIBUTE_DAMAGE = EnchantmentCategory.create("attribute_damage", (Item i) -> i instanceof TieredItem || i instanceof ModularItem || i instanceof BowItem || i instanceof CrossbowItem || i instanceof TridentItem);

    private boolean isMultiplier = false;

    public boolean isMultiplier() {
        return isMultiplier;
    }

    protected AttributeDamageEnchantment(Enchantment.Rarity rarity) {
        super (rarity, ATTRIBUTE_DAMAGE, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    protected AttributeDamageEnchantment(Enchantment.Rarity rarity, boolean isMult) {
        super (rarity, ATTRIBUTE_DAMAGE, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
        isMultiplier = isMult;
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

}
