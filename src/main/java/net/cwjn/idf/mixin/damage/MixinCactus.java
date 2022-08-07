package net.cwjn.idf.mixin.damage;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.CactusBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CactusBlock.class)
public class MixinCactus {

    @Redirect(
            method = "entityInside",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"
            )
    )
    private boolean hurtRedirect(Entity instance, DamageSource pSource, float pAmount) {
        if (instance instanceof LivingEntity livingEntity) {
            return livingEntity.hurt(pSource, livingEntity.getMaxHealth()*0.05f);
        } else {
            return instance.hurt(pSource, pAmount);
        }
    }

}
