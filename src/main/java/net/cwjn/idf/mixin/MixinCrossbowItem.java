package net.cwjn.idf.mixin;

import net.cwjn.idf.capability.ArrowHelperProvider;
import net.cwjn.idf.capability.ProjectileHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CrossbowItem.class)
public class MixinCrossbowItem {

    @Redirect(
            method = "getArrow",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/projectile/AbstractArrow;setCritArrow(Z)V"
            )
    )
    private static void getArrowInject(AbstractArrow instance, boolean pCritical, Level pLevel, LivingEntity pLivingEntity, ItemStack pCrossbowStack, ItemStack pAmmoStack) {
        ProjectileHelper helper = pLivingEntity.getCapability(ArrowHelperProvider.PROJECTILE_HELPER).orElseGet(null);
        if (helper == null) instance.setCritArrow(pCritical);
        boolean isCrit = (helper.getCrit()/100) >= Math.random();
        System.out.println("Crit chance was " + helper.getCrit() + "%" + ", and the crit result was " + isCrit);
        instance.setCritArrow(isCrit);
    }

}
