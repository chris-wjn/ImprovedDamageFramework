package net.cwjn.idf.mixin.damage;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

import javax.annotation.Nullable;

@Mixin(MobEffect.class)
public class MixinMobEffect {

    @ModifyConstant(method = "applyEffectTick", constant = @Constant(floatValue = 1.0f, ordinal = 2))
    private float changePoisonDamage(float constant, LivingEntity pLivingEntity, int pAmplifier) {
        return pLivingEntity.getMaxHealth()*0.04f;
    }

    @Redirect(method = "applyEffectTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z", ordinal = 1))
    private boolean hurtRedirectWither(LivingEntity instance, DamageSource source, float useless) {
        return instance.hurt(source, instance.getMaxHealth()*0.08f);
    }

    @Redirect(method = "applyEffectTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z", ordinal = 2))
    private boolean hurtRedirectHarm(LivingEntity instance, DamageSource source, float useless, LivingEntity useless2, int amp) {
        float harmLevel = amp + 1;
        return instance.hurt(source, instance.getMaxHealth()*(harmLevel * 0.3f));
    }

    @Redirect(method = "applyInstantenousEffect", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z", ordinal = 0))
    private boolean hurtRedirectHarm2(LivingEntity instance, DamageSource source, float useless, @Nullable Entity directEntity, @Nullable Entity indirectEntity, LivingEntity useless2, int amp, double useless3) {
        float harmLevel = amp + 1;
        return instance.hurt(DamageSource.MAGIC, instance.getMaxHealth()*(harmLevel * 0.3f));
    }

    @Redirect(method = "applyInstantenousEffect", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z", ordinal = 1))
    private boolean hurtRedirectHarm3(LivingEntity instance, DamageSource source, float useless, @Nullable Entity directEntity, @Nullable Entity indirectEntity, LivingEntity useless2, int amp, double useless3) {
        float harmLevel = amp + 1;
        return instance.hurt(DamageSource.indirectMagic(directEntity, indirectEntity), instance.getMaxHealth()*(harmLevel * 0.3f));
    }

}
