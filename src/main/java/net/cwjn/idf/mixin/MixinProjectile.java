package net.cwjn.idf.mixin;

import net.minecraft.world.entity.projectile.Projectile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Projectile.class)
public class MixinProjectile {

    @ModifyVariable(method = "shoot", at = @At("HEAD"), ordinal = 3)
    private float factorAccuracyAttribute(float f) {

        return null;
    }

}
