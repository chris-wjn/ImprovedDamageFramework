package net.cwjn.idf.mixin.damage;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.BaseFireBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BaseFireBlock.class)
public class MixinBaseFireBlock {

    @Redirect(
            method = "entityInside",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"
            )
    )
    private boolean hurtRedirect(Entity instance, DamageSource pSource, float pAmount) {
        if (instance instanceof LivingEntity livingEntity) {
            return livingEntity.hurt(pSource, livingEntity.getMaxHealth()*0.1f);
        } else {
            return instance.hurt(pSource, pAmount);
        }
    }


}
