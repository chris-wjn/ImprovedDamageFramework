package net.cwjn.idf.mixin;

import net.cwjn.idf.capability.data.ProjectileHelper;
import net.cwjn.idf.capability.provider.TridentHelperProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrownTrident;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ThrownTrident.class)
public class MixinThrownTrident {

    @Redirect(method = "onHitEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    private boolean onHitEntity(Entity instance, DamageSource pSource, float pAmount) {
        ThrownTrident thisTrident = (ThrownTrident)(Object)this;
        if (thisTrident.getOwner() instanceof LivingEntity livingEntity) {
            ProjectileHelper helper = livingEntity.getCapability(TridentHelperProvider.PROJECTILE_HELPER).orElseGet(ProjectileHelper::new);
            return instance.hurt(pSource, (float) (helper.getPhys() * (helper.getCrit()? helper.getCritDmg()*0.01 : 1)));
        } else {
            return instance.hurt(pSource, pAmount);
        }
    }

}
