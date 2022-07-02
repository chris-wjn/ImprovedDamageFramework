package net.cwjn.idf.mixin;

import net.cwjn.idf.capability.provider.ArrowHelperProvider;
import net.cwjn.idf.capability.data.ProjectileHelper;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
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
        if (thisArrow.getOwner() != null) {
            ProjectileHelper helper = thisArrow.getOwner().getCapability(ArrowHelperProvider.PROJECTILE_HELPER).orElseGet(ProjectileHelper::new);
            boolean isCrit = (helper.getCrit()/100) > Math.random();
            thisArrow.setCritArrow(isCrit);
        }
    }

}
