package net.cwjn.idf.mixin;

import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RangedAttribute.class)
public interface AccessRangedAttribute {

    @Accessor("minValue")
    @Mutable
    void setMin(double x);

    @Accessor("maxValue")
    @Mutable
    void setMax(double x);

}
