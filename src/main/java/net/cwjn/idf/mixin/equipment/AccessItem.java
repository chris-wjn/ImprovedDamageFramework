package net.cwjn.idf.mixin.equipment;

import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Item.class)
public interface AccessItem {

    @Accessor
    int getMaxDamage();

    @Accessor
    @Mutable
    void setMaxDamage(int i);

}
