package net.cwjn.idf.api.event;

import com.google.common.collect.ImmutableMultimap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.eventbus.api.Event;

public class ItemAttributeReworkEvent extends Event {

    private final ImmutableMultimap.Builder<Attribute, AttributeModifier> attributeBuilder;
    public final CompoundTag defaultTag;
    private final ResourceLocation item;

    public ItemAttributeReworkEvent(ImmutableMultimap.Builder<Attribute, AttributeModifier> b, CompoundTag tag, ResourceLocation s) {
        attributeBuilder = b;
        defaultTag = tag;
        item = s;
    }

    public void addAttributeModifier(Attribute a, AttributeModifier am) {
        attributeBuilder.put(a, am);
    }

    public ResourceLocation getItem() {
        return item;
    }

    @Override
    public boolean isCancelable() {
        return false;
    }
}
