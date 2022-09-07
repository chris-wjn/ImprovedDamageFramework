package net.cwjn.idf.effect;

import net.cwjn.idf.ImprovedDamageFramework;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class IDFEffects {

    public static DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, ImprovedDamageFramework.MOD_ID);

    public static final RegistryObject<MobEffect> POSITIVE_ATTRIBUTE_MODIFIER_EFFECT = EFFECTS.register("positive_attribute_modifier_effect",
            () -> new AttributeModifierEffect(MobEffectCategory.BENEFICIAL, true));

    public static final RegistryObject<MobEffect> NEGATIVE_ATTRIBUTE_MODIFIER_EFFECT = EFFECTS.register("negative_attribute_modifier_effect",
            () -> new AttributeModifierEffect(MobEffectCategory.HARMFUL, false));

}
