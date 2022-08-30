package net.cwjn.idf.mixin.equipment;

import com.google.common.collect.Multimap;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ArmorItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ArmorItem.class)
public interface AccessArmorItem {

    @Accessor
    @Mutable
    void setDefaultModifiers(Multimap<Attribute, AttributeModifier> defaultModifiers);

}
