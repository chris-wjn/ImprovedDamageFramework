package net.cwjn.idf.mixin.damage;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.MagmaBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MagmaBlock.class)
public class MixinMagmaBlock {

    @Redirect(
            method = "stepOn",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"
            )
    )
    private boolean redirectHurt(Entity instance, DamageSource pSource, float pAmount) {
        if (instance instanceof LivingEntity livingEntity) {
            return livingEntity.hurt(pSource, livingEntity.getMaxHealth()*0.1f);
        } else {
            return instance.hurt(pSource, pAmount);
        }
    }

}
