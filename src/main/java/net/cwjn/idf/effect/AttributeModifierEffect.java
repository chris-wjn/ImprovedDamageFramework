package net.cwjn.idf.effect;

import net.cwjn.idf.util.Color;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraftforge.common.extensions.IForgeMobEffect;

public class AttributeModifierEffect extends MobEffect implements IForgeMobEffect {

    protected AttributeModifierEffect(MobEffectCategory category, boolean beneficial) {
        super(category, beneficial ? Color.LIGHTGREEN.getColor() : Color.DARKRED.getColor());
    }

}