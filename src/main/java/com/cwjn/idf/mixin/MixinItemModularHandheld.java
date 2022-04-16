package com.cwjn.idf.mixin;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import se.mickelus.tetra.effect.AbilityUseResult;
import se.mickelus.tetra.effect.EffectHelper;
import se.mickelus.tetra.effect.ItemEffectHandler;
import se.mickelus.tetra.items.modular.ItemModularHandheld;

import java.util.Optional;

@Mixin(ItemModularHandheld.class)
public class MixinItemModularHandheld {

    /*@Overwrite
    public AbilityUseResult hitEntity(ItemStack itemStack, Player player, LivingEntity target, double damageMultiplier, double damageBonus, float knockbackBase, float knockbackMultiplier) {
        float targetModifier = EnchantmentHelper.getDamageBonus(itemStack, target.getMobType());
        float critMultiplier = (Float) Optional.ofNullable(ForgeHooks.getCriticalHit(player, target, false, 1.5F)).map(CriticalHitEvent::getDamageModifier).orElse(1.0F);
        double damage = (1.0D + this.getAbilityBaseDamage(itemStack) + (double)targetModifier) * (double)critMultiplier * damageMultiplier + damageBonus;
        boolean success = target.hurt(DamageSource.playerAttack(player), (float)damage);
        if (success) {
            EnchantmentHelper.doPostHurtEffects(target, player);
            EffectHelper.applyEnchantmentHitEffects(itemStack, target, player);
            ItemEffectHandler.applyHitEffects(itemStack, target, player);
            float knockbackFactor = knockbackBase + (float)EnchantmentHelper.getItemEnchantmentLevel(Enchantments.KNOCKBACK, itemStack);
            target.knockback((double)(knockbackFactor * knockbackMultiplier), player.getX() - target.getX(), player.getZ() - target.getZ());
            if (targetModifier > 1.0F) {
                player.magicCrit(target);
                return AbilityUseResult.magicCrit;
            } else if (critMultiplier > 1.0F) {
                player.crit(target);
                return AbilityUseResult.crit;
            } else {
                return AbilityUseResult.hit;
            }
        } else {
            return AbilityUseResult.fail;
        }
    }*/

}
