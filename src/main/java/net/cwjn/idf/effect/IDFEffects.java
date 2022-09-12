package net.cwjn.idf.effect;

import net.cwjn.idf.ImprovedDamageFramework;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.UUID;

import static net.cwjn.idf.attribute.IDFAttributes.*;

public class IDFEffects {

    public static DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, ImprovedDamageFramework.MOD_ID);

    public static RegistryObject<MobEffect> FIRE_RES_UP = reg(FIRE_RESISTANCE.get(), true);
    public static RegistryObject<MobEffect> FIRE_RES_DOWN = reg(FIRE_RESISTANCE.get(), false);

    public static RegistryObject<MobEffect> WATER_RES_UP = reg(WATER_RESISTANCE.get(), true);
    public static RegistryObject<MobEffect> WATER_RES_DOWN = reg(WATER_RESISTANCE.get(), false);

    public static RegistryObject<MobEffect> LTNG_RES_UP = reg(LIGHTNING_RESISTANCE.get(), true);
    public static RegistryObject<MobEffect> LTNG_RES_DOWN = reg(LIGHTNING_RESISTANCE.get(), false);

    public static RegistryObject<MobEffect> MAGIC_RES_UP = reg(MAGIC_RESISTANCE.get(), true);
    public static RegistryObject<MobEffect> MAGIC_RES_DOWN = reg(MAGIC_RESISTANCE.get(), false);

    public static RegistryObject<MobEffect> DARK_RES_UP = reg(DARK_RESISTANCE.get(), true);
    public static RegistryObject<MobEffect> DARK_RES_DOWN = reg(DARK_RESISTANCE.get(), false);

    public static RegistryObject<MobEffect> PHYS_RES_UP = reg(Attributes.ARMOR, true);
    public static RegistryObject<MobEffect> PHYS_RES_DOWN = reg(Attributes.ARMOR, false);

    private static RegistryObject<MobEffect> reg(Attribute a, boolean b) {
        if (b) {
            return EFFECTS.register(a.getDescriptionId() + "positive_modifier_effect",
                    () -> new AttributeModifierEffect(MobEffectCategory.BENEFICIAL, true)
                            .addAttributeModifier(a, UUID.randomUUID().toString(), 0.1D, AttributeModifier.Operation.MULTIPLY_TOTAL));
        }
        else {
            return EFFECTS.register(a.getDescriptionId() + "negative_modifier_effect",
                    () -> new AttributeModifierEffect(MobEffectCategory.HARMFUL, false)
                            .addAttributeModifier(a, UUID.randomUUID().toString(), -0.1D, AttributeModifier.Operation.MULTIPLY_TOTAL));
        }
    }

}
