package com.cwjn.idf.mixin;

import com.cwjn.idf.Attributes.AttributeRegistry;
import com.cwjn.idf.Damage.IDFEntityDamageSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.IronGolem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(IronGolem.class)
public class MixinIronGolem {

    @Overwrite
    private void doHurtTarget(Entity target, CallbackInfoReturnable<Boolean> callback) {
        LivingEntity thisEntity = (LivingEntity)((Object)this);
        this.attackAnimationTick = 10;
        thisEntity.level.broadcastEntityEvent(thisEntity, (byte)4);
        float ad = (float)thisEntity.getAttributeValue(Attributes.ATTACK_DAMAGE);
        float fd = (float)thisEntity.getAttributeValue(AttributeRegistry.FIRE_DAMAGE.get());
        float wd = (float)thisEntity.getAttributeValue(AttributeRegistry.WATER_DAMAGE.get());
        float ld = (float)thisEntity.getAttributeValue(AttributeRegistry.LIGHTNING_DAMAGE.get());
        float md = (float)thisEntity.getAttributeValue(AttributeRegistry.MAGIC_DAMAGE.get());
        float dd = (float)thisEntity.getAttributeValue(AttributeRegistry.DARK_DAMAGE.get());
        IDFEntityDamageSource source = new IDFEntityDamageSource("mob", thisEntity, fd, wd, ld, md, dd, "strike");
        boolean flag = target.hurt(source, ad);
        if (flag) {
            target.setDeltaMovement(target.getDeltaMovement().add(0.0D, (double)0.4F, 0.0D));
            thisEntity.doEnchantDamageEffects(thisEntity, target);
        }
        thisEntity.playSound(SoundEvents.IRON_GOLEM_ATTACK, 1.0F, 1.0F);
        callback.setReturnValue(flag);
    }

    @Shadow
    private int attackAnimationTick;


}
