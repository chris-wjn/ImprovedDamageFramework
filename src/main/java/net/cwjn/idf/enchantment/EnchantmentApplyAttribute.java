package net.cwjn.idf.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;

@Mod.EventBusSubscriber
public class EnchantmentApplyAttribute {

    @SubscribeEvent
    public static void ItemModifierEvent(ItemAttributeModifierEvent event) {
        ItemStack item = event.getItemStack();
        if (item.getItem() instanceof EnchantedBookItem) return;
        if (LivingEntity.getEquipmentSlotForItem(item) == event.getSlotType()) {
            Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(item);
            if (!enchantments.isEmpty()) {
                for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
                    if (event.getSlotType().getType() != EquipmentSlot.Type.ARMOR && entry.getKey() instanceof AttributeDamageEnchantment attributeDamageEnchantment) {
                        if (!attributeDamageEnchantment.isMultiplier()) {
                            event.addModifier(attributeDamageEnchantment.getAttribute(),
                                    new AttributeModifier(
                                            attributeDamageEnchantment.getUUID(),
                                            attributeDamageEnchantment.getPrefix() + "_enchant_damage_modifier_flat",
                                            entry.getValue(),
                                            AttributeModifier.Operation.ADDITION));
                        } else {
                            event.addModifier(attributeDamageEnchantment.getAttribute(),
                                    new AttributeModifier(
                                            attributeDamageEnchantment.getUUID(),
                                            attributeDamageEnchantment.getPrefix() + "_enchant_damage_modifier_mult",
                                            ((double)entry.getValue())*0.05,
                                            AttributeModifier.Operation.MULTIPLY_TOTAL));
                        }
                    } else if (event.getSlotType().getType() == EquipmentSlot.Type.ARMOR && entry.getKey() instanceof AttributeResistanceEnchantment attributeResistanceEnchantment) {
                        if (!attributeResistanceEnchantment.isMultiplier()) {
                            event.addModifier(attributeResistanceEnchantment.getAttribute(),
                                    new AttributeModifier(
                                            attributeResistanceEnchantment.getUUIDs()[event.getSlotType().getFilterFlag() - 1],
                                            attributeResistanceEnchantment.getSlotPrefix()[event.getSlotType().getFilterFlag()] + "_" + attributeResistanceEnchantment.getPrefix() + "_enchant_modifier_flat",
                                            entry.getValue(),
                                            AttributeModifier.Operation.ADDITION));
                        } else {
                            event.addModifier(attributeResistanceEnchantment.getAttribute(),
                                    new AttributeModifier(
                                            attributeResistanceEnchantment.getUUIDs()[event.getSlotType().getFilterFlag() + 3],
                                            attributeResistanceEnchantment.getSlotPrefix()[event.getSlotType().getFilterFlag()] + "_" + attributeResistanceEnchantment.getPrefix() + "_enchant_modifier_mult",
                                            ((double)entry.getValue())*0.05,
                                            AttributeModifier.Operation.MULTIPLY_TOTAL));
                        }
                    }
                }
            }
        }
    }
}
