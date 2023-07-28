package net.cwjn.idf.mixin.equipment;

import com.google.common.collect.Multimap;
import net.cwjn.idf.util.ItemInterface;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.TridentItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TridentItem.class)
public abstract class MixinTridentItem implements ItemInterface {

    @Accessor("defaultModifiers")
    @Mutable
    public abstract void setDefaultAttributes(Multimap<Attribute, AttributeModifier> x);

    @Accessor("defaultModifiers")
    public abstract Multimap<Attribute, AttributeModifier> getDefaultModifiers();

}
