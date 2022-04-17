package com.cwjn.idf.mixin;

import com.cwjn.idf.Attributes.AttributeRegistry;
import com.cwjn.idf.Attributes.AuxiliaryData;
import com.cwjn.idf.Attributes.CapabilityProvider;
import com.cwjn.idf.Damage.IDFEntityDamageSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Bee;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Bee.class)
public class MixinBee {

    /**
     * @author cwJn
     */
    @Overwrite
    public boolean doHurtTarget(Entity target) {
        Bee thisEntity = (Bee)((Object)this);
        String damageClass = "strike";
        AuxiliaryData data = thisEntity.getCapability(CapabilityProvider.AUXILIARY_DATA).orElse(null);
        if (data != null) damageClass = data.getDamageClass();
        float ad = (float)thisEntity.getAttributeValue(Attributes.ATTACK_DAMAGE);
        float fd = (float)thisEntity.getAttributeValue(AttributeRegistry.FIRE_DAMAGE.get());
        float wd = (float)thisEntity.getAttributeValue(AttributeRegistry.WATER_DAMAGE.get());
        float ld = (float)thisEntity.getAttributeValue(AttributeRegistry.LIGHTNING_DAMAGE.get());
        float md = (float)thisEntity.getAttributeValue(AttributeRegistry.MAGIC_DAMAGE.get());
        float dd = (float)thisEntity.getAttributeValue(AttributeRegistry.DARK_DAMAGE.get());
        IDFEntityDamageSource source = new IDFEntityDamageSource("sting", thisEntity, fd, wd, ld, md, dd, damageClass);
        boolean flag = target.hurt(source, ad);
        if (flag) {
            thisEntity.doEnchantDamageEffects(thisEntity, target);
            if (target instanceof LivingEntity) {
                ((LivingEntity)target).setStingerCount(((LivingEntity)target).getStingerCount() + 1);
                int i = 0;
                if (thisEntity.level.getDifficulty() == Difficulty.NORMAL) {
                    i = 10;
                } else if (thisEntity.level.getDifficulty() == Difficulty.HARD) {
                    i = 18;
                }
                if (i > 0) {
                    ((LivingEntity)target).addEffect(new MobEffectInstance(MobEffects.POISON, i * 20, 0), thisEntity);
                }
            }
            this.setHasStung(true);
            thisEntity.stopBeingAngry();
            thisEntity.playSound(SoundEvents.BEE_STING, 1.0F, 1.0F);
        }
        return flag;
    }
    @Shadow
    private void setHasStung(boolean p_27926_) {
        throw new IllegalStateException("Mixin failed to shadow setHasStung(boolean b)");
    }
}
