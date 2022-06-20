package net.cwjn.idf.mixin;

import net.cwjn.idf.capability.ArrowHelperProvider;
import net.cwjn.idf.capability.ProjectileHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import se.mickelus.tetra.items.modular.impl.bow.ModularBowItem;

@Mixin(ModularBowItem.class)
public class MixinModularBowItem {

    @Redirect(
            method = "fireArrow",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/projectile/AbstractArrow;setCritArrow(Z)V")
    )
    protected void releaseUsingInject(AbstractArrow instance, boolean pCritical, ItemStack pStack, Level pLevel, LivingEntity pEntityLiving, int pTimeLeft) {
        ProjectileHelper helper = pEntityLiving.getCapability(ArrowHelperProvider.PROJECTILE_HELPER).orElseGet(null);
        if (helper == null) instance.setCritArrow(pCritical);
        boolean isCrit = (helper.getCrit()/100) >= Math.random();
        System.out.println("Crit chance was " + helper.getCrit() + "%" + ", and the crit result was " + isCrit);
        instance.setCritArrow(isCrit);
    }

}
