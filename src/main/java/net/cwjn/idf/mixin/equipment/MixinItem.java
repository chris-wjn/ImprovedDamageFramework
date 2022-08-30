package net.cwjn.idf.mixin.equipment;

import com.google.common.collect.Multimap;
import net.cwjn.idf.util.ItemInterface;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class MixinItem implements ItemInterface {

    @Unique @Final @Mutable
    private Multimap<Attribute, AttributeModifier> defaultModifiers;

    @Unique @Final @Mutable
    private String damageClass;

    @Inject(method = "getDefaultAttributeModifiers", at = @At("HEAD"), cancellable = true)
    private void getDefaultAttributes(EquipmentSlot slot, CallbackInfoReturnable<Multimap<Attribute, AttributeModifier>> callback) {
        if (slot == EquipmentSlot.MAINHAND && defaultModifiers != null) callback.setReturnValue(defaultModifiers);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultModifiers() {
        return defaultModifiers;
    }

    @Override
    public void setDefaultModifiers(Multimap<Attribute, AttributeModifier> x) {
        defaultModifiers = x;
    }

    @Override
    public String getDamageClass() {
        return damageClass;
    }

    @Override
    public void setDamageClass(String s) {
        damageClass = s;
    }

}
