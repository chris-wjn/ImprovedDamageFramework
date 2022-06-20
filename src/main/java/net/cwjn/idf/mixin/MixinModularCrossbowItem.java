package net.cwjn.idf.mixin;

import net.cwjn.idf.capability.ArrowHelperProvider;
import net.cwjn.idf.capability.ProjectileHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import se.mickelus.tetra.items.modular.impl.crossbow.ModularCrossbowItem;

@Mixin(ModularCrossbowItem.class)
public class MixinModularCrossbowItem {

    @Redirect(
            method = "fireProjectile",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/projectile/AbstractArrow;setCritArrow(Z)V"
            )
    )
    protected void getArrowInject(AbstractArrow instance, boolean pCritical, Level world, ItemStack crossbowStack, ItemStack ammoStack, Player player, double yaw, boolean isDupe) {
        ProjectileHelper helper = player.getCapability(ArrowHelperProvider.PROJECTILE_HELPER).orElseGet(null);
        if (helper == null) instance.setCritArrow(pCritical);
        boolean isCrit = (helper.getCrit()/100) >= Math.random();
        System.out.println("Crit chance was " + helper.getCrit() + "%" + ", and the crit result was " + isCrit);
        instance.setCritArrow(isCrit);
    }

}
