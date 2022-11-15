package net.cwjn.idf.mixin.equipment;

import com.google.common.collect.Multimap;
import net.cwjn.idf.util.ItemInterface;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ArmorItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ArmorItem.class)
public abstract class MixinArmorItem implements ItemInterface {

    @Accessor("defaultModifiers")
    @Mutable
    public abstract void setDefaultAttributes(Multimap<Attribute, AttributeModifier> x);

}
