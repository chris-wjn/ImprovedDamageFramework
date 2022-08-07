package net.cwjn.idf.mixin.damage;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.animal.Dolphin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Dolphin.class)
public class MixinDolphin {

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/Dolphin;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    private boolean hurtRedirect(Dolphin instance, DamageSource damageSource, float v) {
        return instance.hurt(damageSource, instance.getMaxHealth()*0.1f);
    }

}
