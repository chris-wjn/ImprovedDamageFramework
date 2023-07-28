package net.cwjn.idf.mixin.damage;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FoodData.class)
public class MixinFoodData {

    @Redirect(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/player/Player;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"
            )
    )
    private boolean hurtRedirect(Player instance, DamageSource pSource, float pAmount) {
        return instance.hurt(pSource, instance.getMaxHealth()*0.05f);
    }

    @Redirect(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/player/Player;heal(F)V",
                    ordinal = 0
            )
    )
    private void healRedirect(Player instance, float v) {
        float val = net.minecraftforge.event.ForgeEventFactory.onLivingHeal(instance, instance.getMaxHealth()*0.02f);
        if (val <= 0) return;
        float f = instance.getHealth();
        if (f > 0.0F) {
            instance.setHealth(f + val);
        }
    }

}
