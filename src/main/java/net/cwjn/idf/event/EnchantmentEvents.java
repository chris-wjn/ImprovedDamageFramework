package net.cwjn.idf.event;

import net.cwjn.idf.api.event.PreMitigationDamageEvent;
import net.cwjn.idf.attribute.IDFAttributes;
import net.cwjn.idf.effect.IDFEffects;
import net.cwjn.idf.enchantment.IDFEnchantments;
import net.cwjn.idf.util.Util;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Arrays;
import java.util.UUID;

@Mod.EventBusSubscriber
public class EnchantmentEvents {

    private static final String fireUUID = UUID.randomUUID().toString();
    private static final String waterUUID = UUID.randomUUID().toString();
    private static final String lightningUUID = UUID.randomUUID().toString();
    private static final String magicUUID = UUID.randomUUID().toString();
    private static final String darkUUID = UUID.randomUUID().toString();
    private static final String physUUID = UUID.randomUUID().toString();

    @SubscribeEvent
    public static void onPreMitigationEvent(PreMitigationDamageEvent event) {
        int level = 0;
        for (ItemStack item : event.getTarget().getArmorSlots()) {
            if (item.getEnchantmentLevel(IDFEnchantments.ADAPTIVE.get()) > 0) {
                level++;
            }
        }
        if (level > 0) {
            LivingEntity target = event.getTarget();
            switch (Util.findHighest(new float[]{event.getFireDmg(), event.getWaterDmg(), event.getLightningDmg(),
            event.getMagicDmg(), event.getDarkDmg(), event.getPhysicalDmg()})) {
                case 0:
                    if (target.getAttributeValue(IDFAttributes.FIRE_RESISTANCE.get()) < 0)
                        event.getTarget().addEffect(new MobEffectInstance(IDFEffects.POSITIVE_ATTRIBUTE_MODIFIER_EFFECT.get()
                                .addAttributeModifier(IDFAttributes.FIRE_RESISTANCE.get(), fireUUID, level, AttributeModifier.Operation.ADDITION), 200, 0, false, false));
                    else
                        event.getTarget().addEffect(new MobEffectInstance(IDFEffects.POSITIVE_ATTRIBUTE_MODIFIER_EFFECT.get()
                                .addAttributeModifier(IDFAttributes.FIRE_RESISTANCE.get(), fireUUID, level * 0.1D, AttributeModifier.Operation.MULTIPLY_TOTAL), 200, 0, false, false));
                    return;
                case 1:
                    if (target.getAttributeValue(IDFAttributes.WATER_RESISTANCE.get()) < 0)
                        event.getTarget().addEffect(new MobEffectInstance(IDFEffects.POSITIVE_ATTRIBUTE_MODIFIER_EFFECT.get()
                                .addAttributeModifier(IDFAttributes.WATER_RESISTANCE.get(), waterUUID, level, AttributeModifier.Operation.ADDITION), 200, 0, false, false));
                    else
                        event.getTarget().addEffect(new MobEffectInstance(IDFEffects.POSITIVE_ATTRIBUTE_MODIFIER_EFFECT.get()
                                .addAttributeModifier(IDFAttributes.WATER_RESISTANCE.get(), waterUUID, level * 0.1D, AttributeModifier.Operation.MULTIPLY_TOTAL), 200, 0, false, false));
                    return;
                case 2:
                    if (target.getAttributeValue(IDFAttributes.LIGHTNING_RESISTANCE.get()) < 0)
                        event.getTarget().addEffect(new MobEffectInstance(IDFEffects.POSITIVE_ATTRIBUTE_MODIFIER_EFFECT.get()
                                .addAttributeModifier(IDFAttributes.LIGHTNING_RESISTANCE.get(), lightningUUID, level, AttributeModifier.Operation.ADDITION), 200, 0, false, false));
                    else
                        event.getTarget().addEffect(new MobEffectInstance(IDFEffects.POSITIVE_ATTRIBUTE_MODIFIER_EFFECT.get()
                                .addAttributeModifier(IDFAttributes.LIGHTNING_RESISTANCE.get(), lightningUUID, level * 0.1D, AttributeModifier.Operation.MULTIPLY_TOTAL), 200, 0, false, false));
                    return;
                case 3:
                    if (target.getAttributeValue(IDFAttributes.MAGIC_RESISTANCE.get()) < 0)
                        event.getTarget().addEffect(new MobEffectInstance(IDFEffects.POSITIVE_ATTRIBUTE_MODIFIER_EFFECT.get()
                                .addAttributeModifier(IDFAttributes.MAGIC_RESISTANCE.get(), magicUUID, level, AttributeModifier.Operation.ADDITION), 200, 1, false, false));
                    else
                        event.getTarget().addEffect(new MobEffectInstance(IDFEffects.POSITIVE_ATTRIBUTE_MODIFIER_EFFECT.get()
                                .addAttributeModifier(IDFAttributes.MAGIC_RESISTANCE.get(), magicUUID, level * 0.1D, AttributeModifier.Operation.MULTIPLY_TOTAL), 200, 1, false, false));
                    return;
                case 4:
                    if (target.getAttributeValue(IDFAttributes.DARK_RESISTANCE.get()) < 0)
                        event.getTarget().addEffect(new MobEffectInstance(IDFEffects.POSITIVE_ATTRIBUTE_MODIFIER_EFFECT.get()
                                .addAttributeModifier(IDFAttributes.DARK_RESISTANCE.get(), darkUUID, level, AttributeModifier.Operation.ADDITION), 200, 1, false, false));
                    else
                        event.getTarget().addEffect(new MobEffectInstance(IDFEffects.POSITIVE_ATTRIBUTE_MODIFIER_EFFECT.get()
                                .addAttributeModifier(IDFAttributes.DARK_RESISTANCE.get(), darkUUID, level * 0.1D, AttributeModifier.Operation.MULTIPLY_TOTAL), 200, 1, false, false));
                    return;
                case 5:
                    if (target.getAttributeValue(Attributes.ARMOR) < 0)
                        event.getTarget().addEffect(new MobEffectInstance(IDFEffects.POSITIVE_ATTRIBUTE_MODIFIER_EFFECT.get()
                                .addAttributeModifier(Attributes.ARMOR, physUUID, level, AttributeModifier.Operation.ADDITION), 200, 1, false, false));
                    else
                        event.getTarget().addEffect(new MobEffectInstance(IDFEffects.POSITIVE_ATTRIBUTE_MODIFIER_EFFECT.get()
                                .addAttributeModifier(Attributes.ARMOR, physUUID, level * 0.1D, AttributeModifier.Operation.MULTIPLY_TOTAL), 200, 1, false, false));
            }
        }
    }

}
