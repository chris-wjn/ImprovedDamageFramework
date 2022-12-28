package net.cwjn.idf.mixin.damage;

import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ThornsEnchantment;
import org.checkerframework.checker.units.qual.A;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(ThornsEnchantment.class)
public class MixinThornsEnchantment {

    /**
     * @author cwJn
     * @reason
     * Go look at the normal version of this method. Tell me it makes sense.
     */
    @Inject(method = "doPostHurt", at= @At("HEAD"), cancellable = true)
    private void doPostHurt(LivingEntity target, Entity thingGettingThorned, int level, CallbackInfo callback) {
        Map.Entry<EquipmentSlot, ItemStack> entry = EnchantmentHelper.getRandomItemWith(Enchantments.THORNS, target);
        if (thingGettingThorned != null) {
            thingGettingThorned.hurt(DamageSource.thorns(target), target.getMaxHealth()*0.01f*level);
        }
        if (entry != null) {
            entry.getValue().hurtAndBreak(2, target, (p_45208_) -> {
                p_45208_.broadcastBreakEvent(entry.getKey());
            });
        }
        callback.cancel();
    }


}
