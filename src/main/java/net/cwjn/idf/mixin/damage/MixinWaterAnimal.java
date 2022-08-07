package net.cwjn.idf.mixin.damage;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.animal.WaterAnimal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WaterAnimal.class)
public class MixinWaterAnimal {

    @Redirect(method = "handleAirSupply", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/WaterAnimal;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    private boolean hurtRedirect(WaterAnimal instance, DamageSource damageSource, float v) {
        return instance.hurt(damageSource, instance.getMaxHealth()*0.1f);
    }

}
