package net.cwjn.idf.util;

import com.google.common.collect.Multimap;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public interface ItemInterface {

    Multimap<Attribute, AttributeModifier> getDefaultModifiers();

    void setDefaultModifiers(Multimap<Attribute, AttributeModifier> x);

    String getDamageClass();

    void setDamageClass(String s);

}
