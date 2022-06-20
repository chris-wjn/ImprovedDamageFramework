package net.cwjn.idf.mixin;

import net.cwjn.idf.capability.ArrowHelperProvider;
import net.cwjn.idf.capability.ProjectileHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BowItem.class)
public class MixinBowItem {

    @Redirect(
            method = "releaseUsing(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;I)V",
            at = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/world/entity/projectile/AbstractArrow;setCritArrow(Z)V")
    )
    private void releaseUsingInject(AbstractArrow instance, boolean pCritical, ItemStack pStack, Level pLevel, LivingEntity pEntityLiving, int pTimeLeft) {
        ProjectileHelper helper = pEntityLiving.getCapability(ArrowHelperProvider.PROJECTILE_HELPER).orElseGet(null);
        if (helper == null) instance.setCritArrow(pCritical);
        boolean isCrit = (helper.getCrit()/100) >= Math.random();
        System.out.println("Crit chance was " + helper.getCrit() + "%" + ", and the crit result was " + isCrit);
        instance.setCritArrow(isCrit);
    }

}
