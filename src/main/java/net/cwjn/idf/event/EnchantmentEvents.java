package net.cwjn.idf.event;

import net.cwjn.idf.api.event.PreMitigationDamageEvent;
import net.cwjn.idf.attribute.IDFAttributes;
import net.cwjn.idf.effect.IDFEffects;
import net.cwjn.idf.enchantment.IDFEnchantments;
import net.cwjn.idf.util.Util;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

@Mod.EventBusSubscriber
public class EnchantmentEvents {

    @SubscribeEvent
    public static void onPreMitigationEvent(PreMitigationDamageEvent event) {
        int level = 0;
        for (ItemStack item : event.getTarget().getArmorSlots()) {
            if (item.getEnchantmentLevel(IDFEnchantments.ADAPTIVE.get()) > 0) {
                level++;
            }
        }
        if (level > 0) {
            switch (Util.findHighest(new float[]{event.getFireDmg(), event.getWaterDmg(), event.getLightningDmg(),
            event.getMagicDmg(), event.getDarkDmg(), event.getPhysicalDmg()})) {
                case 0: event.getTarget().addEffect(new MobEffectInstance(IDFEffects.POSITIVE_ATTRIBUTE_MODIFIER_EFFECT.get()
                        .addAttributeModifier(IDFAttributes.FIRE_RESISTANCE.get(), UUID.randomUUID().toString(), level * 0.05D, AttributeModifier.Operation.MULTIPLY_TOTAL), 10));
                case 1: event.getTarget().addEffect(new MobEffectInstance(IDFEffects.POSITIVE_ATTRIBUTE_MODIFIER_EFFECT.get()
                        .addAttributeModifier(IDFAttributes.WATER_RESISTANCE.get(), UUID.randomUUID().toString(), level * 0.05D, AttributeModifier.Operation.MULTIPLY_TOTAL)));
                case 2: event.getTarget().addEffect(new MobEffectInstance(IDFEffects.POSITIVE_ATTRIBUTE_MODIFIER_EFFECT.get()
                        .addAttributeModifier(IDFAttributes.LIGHTNING_RESISTANCE.get(), UUID.randomUUID().toString(), level * 0.05D, AttributeModifier.Operation.MULTIPLY_TOTAL)));
                case 3: event.getTarget().addEffect(new MobEffectInstance(IDFEffects.POSITIVE_ATTRIBUTE_MODIFIER_EFFECT.get()
                        .addAttributeModifier(IDFAttributes.MAGIC_RESISTANCE.get(), UUID.randomUUID().toString(), level * 0.05D, AttributeModifier.Operation.MULTIPLY_TOTAL)));
                case 4: event.getTarget().addEffect(new MobEffectInstance(IDFEffects.POSITIVE_ATTRIBUTE_MODIFIER_EFFECT.get()
                        .addAttributeModifier(IDFAttributes.DARK_RESISTANCE.get(), UUID.randomUUID().toString(), level * 0.05D, AttributeModifier.Operation.MULTIPLY_TOTAL)));
                case 5: event.getTarget().addEffect(new MobEffectInstance(IDFEffects.POSITIVE_ATTRIBUTE_MODIFIER_EFFECT.get()
                        .addAttributeModifier(Attributes.ARMOR, UUID.randomUUID().toString(), level * 0.05D, AttributeModifier.Operation.MULTIPLY_TOTAL)));
            }
        }
    }

}
