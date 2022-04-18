package com.cwjn.idf.mixin;

import com.cwjn.idf.Attributes.AttributeRegistry;
import com.cwjn.idf.Attributes.AuxiliaryData;
import com.cwjn.idf.Attributes.CapabilityProvider;
import com.cwjn.idf.Damage.IDFEntityDamageSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Mob.class)
public class MixinMob {

    /**
     * @author cwjn
     */
    @Overwrite
    public boolean doHurtTarget(Entity target) {
        Mob thisMob = (Mob)(Object)this;
        float ad = (float)thisMob.getAttributeValue(Attributes.ATTACK_DAMAGE);
        float fd = (float)thisMob.getAttributeValue(AttributeRegistry.FIRE_DAMAGE.get());
        float wd = (float)thisMob.getAttributeValue(AttributeRegistry.WATER_DAMAGE.get());
        float ld = (float)thisMob.getAttributeValue(AttributeRegistry.LIGHTNING_DAMAGE.get());
        float md = (float)thisMob.getAttributeValue(AttributeRegistry.MAGIC_DAMAGE.get());
        float dd = (float)thisMob.getAttributeValue(AttributeRegistry.DARK_DAMAGE.get());
        float knockback = (float)thisMob.getAttributeValue(Attributes.ATTACK_KNOCKBACK);
        if (target instanceof LivingEntity) {
            ad += EnchantmentHelper.getDamageBonus(thisMob.getMainHandItem(), ((LivingEntity)target).getMobType());
            knockback += (float)EnchantmentHelper.getKnockbackBonus(thisMob);
        }

        int i = EnchantmentHelper.getFireAspect(thisMob);
        if (i > 0) {
            target.setSecondsOnFire(i * 4);
        }
        String damageClass = "strike";
        AuxiliaryData data = thisMob.getCapability(CapabilityProvider.AUXILIARY_DATA).orElse(null);
        if (data != null) damageClass = data.getDamageClass();
        IDFEntityDamageSource source = new IDFEntityDamageSource("mob", thisMob, fd, wd, ld, md, dd, damageClass);
        boolean flag = target.hurt(source, ad);
        if (flag) {
            if (knockback > 0.0F && target instanceof LivingEntity) {
                ((LivingEntity)target).knockback(knockback * 0.5F, Mth.sin(thisMob.getYRot() * ((float)Math.PI / 180F)), -Mth.cos(thisMob.getYRot() * ((float)Math.PI / 180F)));
                thisMob.setDeltaMovement(thisMob.getDeltaMovement().multiply(0.6D, 1.0D, 0.6D));
            }

            if (target instanceof Player) {
                Player player = (Player)target;
                this.maybeDisableShield(player, thisMob.getMainHandItem(), player.isUsingItem() ? player.getUseItem() : ItemStack.EMPTY);
            }

            thisMob.doEnchantDamageEffects(thisMob, target);
            thisMob.setLastHurtMob(target);
        }
        return flag;
    }

    @Shadow private void maybeDisableShield(Player p_21425_, ItemStack p_21426_, ItemStack p_21427_) {
        throw new IllegalStateException("Mixin failed to shadow maybeDisableShield(Player p, ItemStack i, ItemStack i2)");
    }

}
