package net.cwjn.idf.api.event;

import com.google.common.collect.ImmutableMultimap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.eventbus.api.Event;

public class OnItemAttributeRework extends Event {

    private final ImmutableMultimap.Builder<Attribute, AttributeModifier> attributeBuilder;
    public final CompoundTag defaultTag;

    public OnItemAttributeRework(ImmutableMultimap.Builder<Attribute, AttributeModifier> b, CompoundTag tag) {
        attributeBuilder = b;
        defaultTag = tag;
    }

    public void addAttributeModifier(Attribute a, AttributeModifier am) {
        attributeBuilder.put(a, am);
    }

    @Override
    public boolean isCancelable() {
        return false;
    }
}
