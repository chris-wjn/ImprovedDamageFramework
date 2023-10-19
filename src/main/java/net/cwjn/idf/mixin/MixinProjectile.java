package net.cwjn.idf.mixin;

import net.cwjn.idf.attribute.IDFAttributes;
import net.cwjn.idf.util.Util;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import javax.annotation.Nullable;

@Mixin(Projectile.class)
public abstract class MixinProjectile {

    @Shadow @Nullable public abstract Entity getOwner();

    @Redirect(method = "shoot", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/RandomSource;triangle(DD)D"))
    private double factorAccuracyAttribute(RandomSource instance, double pMin, double pMax) {
        if (this.getOwner() instanceof LivingEntity livingEntity) {
            if (livingEntity.getUsedItemHand().equals(InteractionHand.MAIN_HAND)) {
                return instance.triangle(pMin, pMax * (10 / Math.max(livingEntity.getAttributeValue(IDFAttributes.ACCURACY.get()), 1)));
            }
            else {
                return instance.triangle(pMin, pMax * (10 / Math.max(
                        Util.getAttributeAmount(livingEntity.getOffhandItem().getAttributeModifiers(EquipmentSlot.MAINHAND).get(IDFAttributes.ACCURACY.get())), 1)));
            }
        }
        else return instance.triangle(pMin, pMax);
    }

}