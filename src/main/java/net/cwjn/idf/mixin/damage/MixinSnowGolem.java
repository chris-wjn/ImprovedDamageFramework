package net.cwjn.idf.mixin.damage;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.animal.SnowGolem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SnowGolem.class)
public class MixinSnowGolem {

    @Redirect(
            method = "aiStep",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/animal/SnowGolem;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"
            )
    )
    private boolean hurtRedirect(SnowGolem instance, DamageSource damageSource, float v) {
        return instance.hurt(damageSource, instance.getMaxHealth()*0.1f);
    }

}
