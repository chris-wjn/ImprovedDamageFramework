package net.cwjn.idf.util;

import com.google.common.collect.Multimap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public interface ItemInterface {

    Multimap<Attribute, AttributeModifier> getDefaultModifiers();

    void setDefaultAttributes(Multimap<Attribute, AttributeModifier> x);

    String getDamageClass();

    void setDamageClass(String s);

    void setMaxDamage(int i);

    int getMaxDamage();

    CompoundTag getDefaultTags();

    void setDefaultTag(CompoundTag tag);

}
