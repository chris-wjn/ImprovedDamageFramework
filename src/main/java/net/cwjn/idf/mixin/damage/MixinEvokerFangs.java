package net.cwjn.idf.mixin.damage;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Evoker;
import net.minecraft.world.entity.projectile.EvokerFangs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import javax.annotation.Nullable;
import java.util.Objects;

@Mixin(EvokerFangs.class)
public abstract class MixinEvokerFangs {

    @Redirect(method = "dealDamageTo", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z", ordinal = 0))
    private boolean hurtRedirect(LivingEntity instance, DamageSource source, float useless) {
        return instance.hurt(DamageSource.MAGIC, instance.getMaxHealth()*0.25f);
    }
    @Redirect(method = "dealDamageTo", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z", ordinal = 1))
    private boolean hurtRedirect2(LivingEntity instance, DamageSource source, float useless) {
        EvokerFangs thisEvokerFangs = (EvokerFangs)(Object)this;
        return instance.hurt(DamageSource.indirectMagic((EvokerFangs)(Object)this, thisEvokerFangs.getOwner()), instance.getMaxHealth()*0.25f);
    }

}
