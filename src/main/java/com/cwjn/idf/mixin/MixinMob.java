package com.cwjn.idf.mixin;

import com.cwjn.idf.Attributes.AttributeRegistry;
import com.cwjn.idf.Damage.IDFEntityDamageSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
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
public abstract class MixinMob {

    /**
     * @author cwjn
     */
    @Overwrite
    public boolean doHurtTarget(Entity target) {
        LivingEntity entity = (LivingEntity)((Object)this);
        float ad = (float)entity.getAttributeValue(Attributes.ATTACK_DAMAGE);
        float fd = (float)entity.getAttributeValue(AttributeRegistry.FIRE_DAMAGE.get());
        float wd = (float)entity.getAttributeValue(AttributeRegistry.WATER_DAMAGE.get());
        float ld = (float)entity.getAttributeValue(AttributeRegistry.LIGHTNING_DAMAGE.get());
        float md = (float)entity.getAttributeValue(AttributeRegistry.MAGIC_DAMAGE.get());
        float dd = (float)entity.getAttributeValue(AttributeRegistry.DARK_DAMAGE.get());
        float knockback = (float)entity.getAttributeValue(Attributes.ATTACK_KNOCKBACK);
        if (target instanceof LivingEntity) {
            ad += EnchantmentHelper.getDamageBonus(entity.getMainHandItem(), ((LivingEntity)target).getMobType());
            knockback += (float)EnchantmentHelper.getKnockbackBonus(entity);
        }

        int i = EnchantmentHelper.getFireAspect(entity);
        if (i > 0) {
            target.setSecondsOnFire(i * 4);
        }
        //TODO: set damageClass based on mob
        IDFEntityDamageSource source = new IDFEntityDamageSource("mob", entity, fd, wd, ld, md, dd, "strike");
        boolean flag = target.hurt(source, ad);
        if (flag) {
            if (knockback > 0.0F && target instanceof LivingEntity) {
                ((LivingEntity)target).knockback(knockback * 0.5F, Mth.sin(entity.getYRot() * ((float)Math.PI / 180F)), -Mth.cos(entity.getYRot() * ((float)Math.PI / 180F)));
                entity.setDeltaMovement(entity.getDeltaMovement().multiply(0.6D, 1.0D, 0.6D));
            }

            if (target instanceof Player) {
                Player player = (Player)target;
                this.maybeDisableShield(player, entity.getMainHandItem(), player.isUsingItem() ? player.getUseItem() : ItemStack.EMPTY);
            }

            entity.doEnchantDamageEffects(entity, target);
            entity.setLastHurtMob(target);
        }
        return flag;
    }

    @Shadow protected abstract void maybeDisableShield(Player p_21425_, ItemStack p_21426_, ItemStack p_21427_);

}
