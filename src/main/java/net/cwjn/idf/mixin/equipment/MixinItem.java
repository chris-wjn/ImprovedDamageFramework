package net.cwjn.idf.mixin.equipment;

import com.google.common.collect.Multimap;
import net.cwjn.idf.util.ItemInterface;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public abstract class MixinItem implements ItemInterface {

    @Unique @Final @Mutable
    private Multimap<Attribute, AttributeModifier> idfDefaultModifiers;

    @Unique @Final @Mutable
    private String damageClass;

    @Unique @Final @Mutable
    private boolean isEquipment;

    @Inject(method = "getDefaultAttributeModifiers", at = @At("HEAD"), cancellable = true)
    private void getDefaultAttributes(EquipmentSlot slot, CallbackInfoReturnable<Multimap<Attribute, AttributeModifier>> callback) {
        if (slot == EquipmentSlot.MAINHAND && idfDefaultModifiers != null) callback.setReturnValue(idfDefaultModifiers);
    }

    public Multimap<Attribute, AttributeModifier> getDefaultModifiers() {
        return idfDefaultModifiers;
    }

    public void setDefaultAttributes(Multimap<Attribute, AttributeModifier> x) {
        idfDefaultModifiers = x;
    }

    public String getDamageClass() {
        return damageClass;
    }

    public void setDamageClass(String s) {
        damageClass = s;
    }

    @Accessor
    public abstract int getMaxDamage();

    @Accessor
    @Mutable
    public abstract void setMaxDamage(int i);

    public boolean hasDamageClass() {
        return damageClass != null;
    }

    public boolean isEquipment() {
        return isEquipment;
    }

    public void setIsEquipment(boolean b) {
        isEquipment = b;
    }

}
