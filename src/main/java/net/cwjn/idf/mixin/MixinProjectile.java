package net.cwjn.idf.mixin;

import net.cwjn.idf.attribute.IDFAttributes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
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
            return instance.triangle(pMin, pMax * (10/livingEntity.getAttributeValue(IDFAttributes.ACCURACY.get())));
        }
        else return instance.triangle(pMin, pMax);
    }

}