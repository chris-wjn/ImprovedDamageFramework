package net.cwjn.idf.mixin.equipment;

import com.google.common.collect.Multimap;
import net.cwjn.idf.util.ItemInterface;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.SwordItem;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SwordItem.class)
public abstract class MixinSwordItem implements ItemInterface {

    @Accessor("defaultModifiers")
    @Mutable
    public abstract void setDefaultAttributes(Multimap<Attribute, AttributeModifier> x);

    @Accessor("defaultModifiers")
    public abstract Multimap<Attribute, AttributeModifier> getDefaultModifiers();

}
