package net.cwjn.idf.mixin.damage;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Axolotl.class)
public class MixinAxolotl {

    @Redirect(method = "handleAirSupply", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/axolotl/Axolotl;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    private boolean hurtRedirect(Axolotl instance, DamageSource source, float val) {
        return instance.hurt(source, instance.getMaxHealth()*0.1f);
    }

}
