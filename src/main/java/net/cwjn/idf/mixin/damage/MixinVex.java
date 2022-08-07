package net.cwjn.idf.mixin.damage;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.monster.Vex;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Vex.class)
public class MixinVex {

    @Redirect(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/monster/Vex;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"
            )
    )
    private boolean hurtRedirect(Vex instance, DamageSource damageSource, float amt) {
        return instance.hurt(damageSource, instance.getMaxHealth()*0.05f);
    }

}
