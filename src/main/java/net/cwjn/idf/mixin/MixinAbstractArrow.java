package net.cwjn.idf.mixin;

import net.cwjn.idf.capability.data.ProjectileHelper;
import net.cwjn.idf.capability.provider.ArrowHelperProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractArrow.class)
public class MixinAbstractArrow {

    @Inject(
            method = "onHitEntity",
            at = @At(
                value = "HEAD")
    )
    private void checkCrit(EntityHitResult pResult, CallbackInfo callback) {
        AbstractArrow thisArrow = (AbstractArrow)(Object)this;
        if (thisArrow.getOwner() != null && thisArrow.getOwner() instanceof Player) {
            ProjectileHelper helper = thisArrow.getOwner().getCapability(ArrowHelperProvider.PROJECTILE_HELPER).orElseGet(ProjectileHelper::new);
            boolean isCrit = (helper.getCrit()/100) > Math.random();
            thisArrow.setCritArrow(isCrit);
        }
    }

    @Redirect(
            method = "onHitEntity",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"
            )
    )
    private boolean hurtRedirect(Entity instance, DamageSource source, float amt) {
        AbstractArrow thisArrow = (AbstractArrow)(Object)this;
        if (thisArrow.getOwner() instanceof LivingEntity livingEntity) {
            ProjectileHelper helper = livingEntity.getCapability(ArrowHelperProvider.PROJECTILE_HELPER).orElseGet(ProjectileHelper::new);
            amt += helper.getPhys();
        }
        return instance.hurt(source, amt);
    }

    @Redirect(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/projectile/AbstractArrow;isCritArrow()Z"
            )
    )
    private boolean cancelCritAnim(AbstractArrow instance) {
        return false;
    }


}
