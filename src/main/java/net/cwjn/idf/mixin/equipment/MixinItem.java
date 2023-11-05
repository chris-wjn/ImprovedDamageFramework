package net.cwjn.idf.mixin.equipment;

import com.google.common.collect.Multimap;
import net.cwjn.idf.util.ItemInterface;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public abstract class MixinItem implements ItemInterface {

    @Shadow public abstract ItemStack getDefaultInstance();

    @Unique @Final @Mutable
    private Multimap<Attribute, AttributeModifier> idfDefaultModifiers;

    @Unique @Final @Mutable
    private String damageClass;

    @Unique @Final @Mutable
    private CompoundTag defaultTag;

    @Inject(method = "getDefaultAttributeModifiers", at = @At("HEAD"), cancellable = true)
    private void getDefaultAttributes(EquipmentSlot slot, CallbackInfoReturnable<Multimap<Attribute, AttributeModifier>> callback) {
        if (slot == LivingEntity.getEquipmentSlotForItem(((Item)(Object)this).getDefaultInstance()) && idfDefaultModifiers != null) callback.setReturnValue(idfDefaultModifiers);
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
    @Mutable
    public abstract void setMaxDamage(int i);

    public CompoundTag getDefaultTags() {
        return defaultTag;
    }

    public void setDefaultTag(CompoundTag tag) {
        defaultTag = tag;
    }

}
