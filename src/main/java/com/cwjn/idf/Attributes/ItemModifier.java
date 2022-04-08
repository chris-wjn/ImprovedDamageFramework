package com.cwjn.idf.Attributes;

import com.cwjn.idf.Config.DamageData;
import com.cwjn.idf.Config.JSONHandler;
import com.cwjn.idf.Config.ResistanceData;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

@Mod.EventBusSubscriber
public class ItemModifier {

    public static UUID baseFireDamage = UUID.randomUUID();
    public static UUID baseWaterDamage = UUID.randomUUID();
    public static UUID baseLightningDamage = UUID.randomUUID();
    public static UUID baseMagicDamage = UUID.randomUUID();
    public static UUID baseDarkDamage = UUID.randomUUID();
    public static UUID baseFireResistance = UUID.randomUUID();
    public static UUID baseWaterResistance = UUID.randomUUID();
    public static UUID baseLightningResistance = UUID.randomUUID();
    public static UUID baseMagicResistance = UUID.randomUUID();
    public static UUID baseDarkResistance = UUID.randomUUID();


    /*@SubscribeEvent
    public static void ItemAttachTagsEvent(AttachCapabilitiesEvent<ItemStack> event) {
        ItemStack item = event.getObject();
        if (JSONHandler.damageMap.containsKey(item.getItem().getRegistryName())) {
            item.getOrCreateTag().putBoolean("idf.equipment", true);
            item.getOrCreateTag().putString("idf.damage_class", JSONHandler.damageMap.get(item.getItem().getRegistryName()).getDamageClass());
        }
        if (JSONHandler.resistanceMap.containsKey(item.getItem().getRegistryName())) {
            item.getOrCreateTag().putBoolean("idf.equipment", true);
        }
    }*/

    @SubscribeEvent
    public static void ItemModifierEvent(ItemAttributeModifierEvent event) {
        ItemStack item = event.getItemStack();
        if (JSONHandler.damageMap.containsKey(item.getItem().getRegistryName())) {
            item.getOrCreateTag().putBoolean("idf.equipment", true);
            item.getOrCreateTag().putString("idf.damage_class", JSONHandler.damageMap.get(item.getItem().getRegistryName()).getDamageClass());
            if (event.getSlotType() == EquipmentSlot.MAINHAND) {
                DamageData data = JSONHandler.getDamageData(item.getItem().getRegistryName());
                event.addModifier(AttributeRegistry.FIRE_DAMAGE.get(), new AttributeModifier(baseFireDamage, () -> "base_fire_damage", data.getDamageValues()[0], AttributeModifier.Operation.ADDITION));
                event.addModifier(AttributeRegistry.WATER_DAMAGE.get(), new AttributeModifier(baseWaterDamage, () -> "base_water_damage", data.getDamageValues()[1], AttributeModifier.Operation.ADDITION));
                event.addModifier(AttributeRegistry.LIGHTNING_DAMAGE.get(), new AttributeModifier(baseLightningDamage, () -> "base_lightning_damage", data.getDamageValues()[2], AttributeModifier.Operation.ADDITION));
                event.addModifier(AttributeRegistry.MAGIC_DAMAGE.get(), new AttributeModifier(baseMagicDamage, () -> "base_magic_damage", data.getDamageValues()[3], AttributeModifier.Operation.ADDITION));
                event.addModifier(AttributeRegistry.DARK_DAMAGE.get(), new AttributeModifier(baseDarkDamage, () -> "base_dark_damage", data.getDamageValues()[4], AttributeModifier.Operation.ADDITION));
            }
        }
        if (JSONHandler.resistanceMap.containsKey(item.getItem().getRegistryName())) {
            item.getOrCreateTag().putBoolean("idf.equipment", true);
            if (event.getSlotType() == EquipmentSlot.CHEST) {
                ResistanceData data = JSONHandler.getResistanceData(item.getItem().getRegistryName());
                event.addModifier(AttributeRegistry.FIRE_RESISTANCE.get(), new AttributeModifier(baseFireResistance, () -> "base_fire_resistance", data.getResistanceValues()[0], AttributeModifier.Operation.ADDITION));
                event.addModifier(AttributeRegistry.WATER_RESISTANCE.get(), new AttributeModifier(baseWaterResistance, () -> "base_water_resistance", data.getResistanceValues()[1], AttributeModifier.Operation.ADDITION));
                event.addModifier(AttributeRegistry.LIGHTNING_RESISTANCE.get(), new AttributeModifier(baseLightningResistance, () -> "base_lightning_resistance", data.getResistanceValues()[2], AttributeModifier.Operation.ADDITION));
                event.addModifier(AttributeRegistry.MAGIC_RESISTANCE.get(), new AttributeModifier(baseMagicResistance, () -> "base_magic_resistance", data.getResistanceValues()[3], AttributeModifier.Operation.ADDITION));
                event.addModifier(AttributeRegistry.DARK_RESISTANCE.get(), new AttributeModifier(baseDarkResistance, () -> "base_dark_resistance", data.getResistanceValues()[4], AttributeModifier.Operation.ADDITION));
            }
        }
    }
}
