package net.cwjn.idf.mixin.damage;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Guardian;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Guardian.class)
public class MixinGuardian {

    @Redirect(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z", ordinal = 0))
    private boolean hurtRedirect(LivingEntity instance, DamageSource source, float useless) {
        return instance.hurt(source, instance.getMaxHealth()*0.1f);
    }

}
