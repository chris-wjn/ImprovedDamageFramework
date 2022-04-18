package com.cwjn.idf.Enchantments;

import com.cwjn.idf.Attributes.AttributeRegistry;
import com.cwjn.idf.ImprovedDamageFramework;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber
public class EnchantmentApplyAttribute {

    public static UUID blastProtectionPhysical = UUID.randomUUID();
    public static UUID blastProtectionFire = UUID.randomUUID();
    public static UUID fireProtectionFire = UUID.randomUUID();
    public static UUID protectionPhysical = UUID.randomUUID();
    public static UUID protectionFire = UUID.randomUUID();
    public static UUID protectionWater = UUID.randomUUID();
    public static UUID protectionLightning = UUID.randomUUID();
    public static UUID protectionMagic = UUID.randomUUID();
    public static UUID protectionDark = UUID.randomUUID();

    @SubscribeEvent
    public static void ItemModifierEvent(ItemAttributeModifierEvent event) {
        ItemStack item = event.getItemStack();
        if (LivingEntity.getEquipmentSlotForItem(item) == event.getSlotType()) {
            Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(item);
            for (Map.Entry<Enchantment, Integer> enchant : enchantments.entrySet()) {
                if (enchant.getKey() == Enchantments.BLAST_PROTECTION) {
                    event.addModifier(Attributes.ARMOR, new AttributeModifier(blastProtectionPhysical, () -> "blast_protection_physical", (1D/6) * (double) enchant.getValue(), AttributeModifier.Operation.ADDITION));
                    event.addModifier(AttributeRegistry.FIRE_RESISTANCE.get(), new AttributeModifier(blastProtectionFire, () -> "blast_protection_fire", 0.5D * (double) enchant.getValue(), AttributeModifier.Operation.ADDITION));
                }
                if (enchant.getKey() == Enchantments.FIRE_PROTECTION) {
                    event.addModifier(AttributeRegistry.FIRE_RESISTANCE.get(), new AttributeModifier(fireProtectionFire, () -> "fire_protection_fire", 1.25D * (double) enchant.getValue(), AttributeModifier.Operation.ADDITION));
                }
                if (enchant.getKey() == Enchantments.ALL_DAMAGE_PROTECTION) {
                    event.addModifier(Attributes.ARMOR, new AttributeModifier(protectionPhysical, () -> "protection_physical", (double)enchant.getValue()/3, AttributeModifier.Operation.ADDITION));
                    event.addModifier(AttributeRegistry.FIRE_RESISTANCE.get(), new AttributeModifier(protectionFire, () -> "protection_dark", (double) enchant.getValue(), AttributeModifier.Operation.ADDITION));
                    event.addModifier(AttributeRegistry.WATER_RESISTANCE.get(), new AttributeModifier(protectionWater, () -> "protection_fire", (double) enchant.getValue(), AttributeModifier.Operation.ADDITION));
                    event.addModifier(AttributeRegistry.LIGHTNING_RESISTANCE.get(), new AttributeModifier(protectionLightning, () -> "protection_water", (double) enchant.getValue(), AttributeModifier.Operation.ADDITION));
                    event.addModifier(AttributeRegistry.MAGIC_RESISTANCE.get(), new AttributeModifier(protectionMagic, () -> "protection_lightning", (double) enchant.getValue(), AttributeModifier.Operation.ADDITION));
                    event.addModifier(AttributeRegistry.DARK_RESISTANCE.get(), new AttributeModifier(protectionDark, () -> "protection_magic", (double) enchant.getValue(), AttributeModifier.Operation.ADDITION));
                }
            }
        }
    }

}
