package com.cwjn.idf.mixin;

import com.cwjn.idf.Attributes.AttributeRegistry;
import com.cwjn.idf.Damage.IDFEntityDamageSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Axolotl.class)
public class MixinAxolotl {

    @Overwrite
    private void doHurtTarget(Entity target, CallbackInfoReturnable<Boolean> callback) {
        LivingEntity thisEntity = (LivingEntity)((Object)this);
        float ad = (float)thisEntity.getAttributeValue(Attributes.ATTACK_DAMAGE);
        float fd = (float)thisEntity.getAttributeValue(AttributeRegistry.FIRE_DAMAGE.get());
        float wd = (float)thisEntity.getAttributeValue(AttributeRegistry.WATER_DAMAGE.get());
        float ld = (float)thisEntity.getAttributeValue(AttributeRegistry.LIGHTNING_DAMAGE.get());
        float md = (float)thisEntity.getAttributeValue(AttributeRegistry.MAGIC_DAMAGE.get());
        float dd = (float)thisEntity.getAttributeValue(AttributeRegistry.DARK_DAMAGE.get());

        IDFEntityDamageSource source = new IDFEntityDamageSource("mob", thisEntity, fd, wd, ld, md, dd, "strike");
        boolean flag = target.hurt(source, ad);
        if (flag) {
            thisEntity.doEnchantDamageEffects(thisEntity, target);
            thisEntity.playSound(SoundEvents.AXOLOTL_ATTACK, 1.0F, 1.0F);
        }
        callback.setReturnValue(flag);
    }

}
