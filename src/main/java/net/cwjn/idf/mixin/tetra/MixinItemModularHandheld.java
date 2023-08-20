package net.cwjn.idf.mixin.tetra;

import net.cwjn.idf.attribute.IDFAttributes;
import net.cwjn.idf.attribute.IDFElement;
import net.cwjn.idf.damage.IDFEntityDamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import se.mickelus.tetra.effect.AbilityUseResult;
import se.mickelus.tetra.effect.EffectHelper;
import se.mickelus.tetra.effect.ItemEffectHandler;
import se.mickelus.tetra.items.modular.IModularItem;
import se.mickelus.tetra.items.modular.ItemModularHandheld;

@Mixin(ItemModularHandheld.class)
public abstract class MixinItemModularHandheld implements IModularItem {

    /**
     * @author cwJn
     * @reason
     */
    @Overwrite(remap = false)
    public AbilityUseResult hitEntity(ItemStack itemStack, Player player, LivingEntity target, double damageMultiplier, double damageBonus, float knockbackBase, float knockbackMultiplier) {
        ItemModularHandheld thisModularItem = (ItemModularHandheld) (Object) this;
        float targetModifier = EnchantmentHelper.getDamageBonus(itemStack, target.getMobType());
        boolean isCrit = player.getAttributeValue(IDFAttributes.CRIT_CHANCE.get())*0.01 >= Math.random();
        float critMultiplier = 1.0F;
        CriticalHitEvent hitResult = ForgeHooks.getCriticalHit(player, target, isCrit, isCrit ? 1.5F : 1.0F);
        isCrit = hitResult != null;
        if (isCrit) {
            critMultiplier = hitResult.getDamageModifier();
        }
        double ad = (1.0D + thisModularItem.getAbilityBaseDamage(itemStack) + (double)targetModifier) * (double)critMultiplier * damageMultiplier + damageBonus;
        double fd = this.getAttributeValue(itemStack, IDFAttributes.FIRE_DAMAGE.get()) * (double)critMultiplier * damageMultiplier;
        double wd = this.getAttributeValue(itemStack, IDFAttributes.WATER_DAMAGE.get()) * (double)critMultiplier * damageMultiplier;
        double ld = this.getAttributeValue(itemStack, IDFAttributes.LIGHTNING_DAMAGE.get()) * (double)critMultiplier * damageMultiplier;
        double md = this.getAttributeValue(itemStack, IDFAttributes.MAGIC_DAMAGE.get()) * (double)critMultiplier * damageMultiplier;
        double dd = this.getAttributeValue(itemStack, IDFAttributes.DARK_DAMAGE.get()) * (double)critMultiplier * damageMultiplier;
        double hd = this.getAttributeValue(itemStack, IDFElement.HOLY.damage) * (double)critMultiplier * damageMultiplier;
        double pen = this.getAttributeValue(itemStack, IDFAttributes.PENETRATING.get());
        double force = this.getAttributeValue(itemStack, IDFAttributes.FORCE.get());
        double lifesteal = this.getAttributeValue(itemStack, IDFAttributes.LIFESTEAL.get());
        double knockback = this.getAttributeValue(itemStack, Attributes.ATTACK_KNOCKBACK);
        String damageClass = itemStack.getOrCreateTag().getString("idf.damage_class");
        if (damageClass.equals("")) damageClass = "strike";
        boolean success = target.hurt(new IDFEntityDamageSource("player", player, (float) fd, (float) wd, (float) ld, (float) md, (float) dd, (float) hd, (float) pen, (float) lifesteal, (float) knockback, (float) force, damageClass), (float) ad);
        if (success) {
            EnchantmentHelper.doPostHurtEffects(target, player);
            EffectHelper.applyEnchantmentHitEffects(itemStack, target, player);
            ItemEffectHandler.applyHitEffects(itemStack, target, player);
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
    }

    @ModifyConstant(method = "getAbilityBaseDamage", constant = @Constant(doubleValue = 1.0), remap = false)
    private double updatedBaseDamage(double constant) {
        return 2.0;
    }


}
