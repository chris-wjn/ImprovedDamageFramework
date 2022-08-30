package net.cwjn.idf.mixin.equipment;

import com.google.common.collect.Multimap;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.DiggerItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(DiggerItem.class)
public interface AccessDiggerItem {

    @Accessor("defaultModifiers")
    @Mutable
    void setDefaultAttributes(Multimap<Attribute, AttributeModifier> x);

    @Accessor
    float getAttackDamageBaseline();

}
