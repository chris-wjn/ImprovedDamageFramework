package net.cwjn.idf.mixin;

import net.cwjn.idf.attribute.AttributeRegistry;
import net.cwjn.idf.damage.IDFEntityDamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import se.mickelus.tetra.effect.AbilityUseResult;
import se.mickelus.tetra.effect.EffectHelper;
import se.mickelus.tetra.effect.ItemEffectHandler;
import se.mickelus.tetra.items.modular.IModularItem;
import se.mickelus.tetra.items.modular.ItemModularHandheld;

@Mixin(ItemModularHandheld.class)
public abstract class MixinItemModularHandheld implements IModularItem {

    /**
     * @author cwJn
     */
    @Overwrite(remap = false)
    public AbilityUseResult hitEntity(ItemStack itemStack, Player player, LivingEntity target, double damageMultiplier, double damageBonus, float knockbackBase, float knockbackMultiplier) {
        ItemModularHandheld thisModularItem = (ItemModularHandheld) (Object) this;
        float targetModifier = EnchantmentHelper.getDamageBonus(itemStack, target.getMobType());
        //float critMultiplier = (Float) Optional.ofNullable(ForgeHooks.getCriticalHit(player, target, false, 1.5F)).map(CriticalHitEvent::getDamageModifier).orElse(1.0F);
        boolean isCrit = player.getAttributeValue(AttributeRegistry.CRIT_CHANCE.get())/100 >= Math.random();
        float critMultiplier = 1.0F;
        CriticalHitEvent hitResult = ForgeHooks.getCriticalHit(player, target, isCrit, isCrit ? 1.5F : 1.0F);
        isCrit = hitResult != null;
        if (isCrit) {
            critMultiplier = hitResult.getDamageModifier();
        }
        double ad = (1.0D + thisModularItem.getAbilityBaseDamage(itemStack) + (double)targetModifier) * (double)critMultiplier * damageMultiplier + damageBonus;
        double fd = this.getAttributeValue(itemStack, AttributeRegistry.FIRE_DAMAGE.get()) * damageMultiplier;
        double wd = this.getAttributeValue(itemStack, AttributeRegistry.WATER_DAMAGE.get()) * damageMultiplier;
        double ld = this.getAttributeValue(itemStack, AttributeRegistry.LIGHTNING_DAMAGE.get()) * damageMultiplier;
        double md = this.getAttributeValue(itemStack, AttributeRegistry.MAGIC_DAMAGE.get()) * damageMultiplier;
        double dd = this.getAttributeValue(itemStack, AttributeRegistry.DARK_DAMAGE.get()) * damageMultiplier;
        double pen = this.getAttributeValue(itemStack, AttributeRegistry.PENETRATING.get());
        String damageClass = itemStack.getOrCreateTag().getString("idf.damage_class");
        if (damageClass.equals("")) damageClass = "strike";
        boolean success = target.hurt(new IDFEntityDamageSource("player", player, (float) fd, (float) wd, (float) ld, (float) md, (float) dd, (float) pen, damageClass), (float) ad);
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
    }

}
