package net.cwjn.idf.api.event;

import com.google.common.collect.ImmutableMultimap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.eventbus.api.Event;

public class OnItemAttributeRework extends Event {

    private final ImmutableMultimap.Builder<Attribute, AttributeModifier> attributeBuilder;
    public final CompoundTag defaultTag;
    private final String item;

    public OnItemAttributeRework(ImmutableMultimap.Builder<Attribute, AttributeModifier> b, CompoundTag tag, String s) {
        attributeBuilder = b;
        defaultTag = tag;
        item = s;
    }

    public void addAttributeModifier(Attribute a, AttributeModifier am) {
        attributeBuilder.put(a, am);
    }

    public String getItem() {
        return item;
    }

    @Override
    public boolean isCancelable() {
        return false;
    }
}
