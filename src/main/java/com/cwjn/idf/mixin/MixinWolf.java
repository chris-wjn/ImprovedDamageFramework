package com.cwjn.idf.mixin;

import com.cwjn.idf.Attributes.AttributeRegistry;
import com.cwjn.idf.Attributes.AuxiliaryData;
import com.cwjn.idf.Attributes.CapabilityProvider;
import com.cwjn.idf.Damage.IDFEntityDamageSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.PolarBear;
import net.minecraft.world.entity.animal.Wolf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(Wolf.class)
public class MixinWolf {

    /**
     * @author cwJn
     */
    @Overwrite
    public boolean doHurtTarget(Entity target) {
        Wolf thisEntity = (Wolf)(Object)this;
        String damageClass = "strike";
        AuxiliaryData data = thisEntity.getCapability(CapabilityProvider.AUXILIARY_DATA).orElse(null);
        if (data != null) damageClass = data.getDamageClass();
        float ad = (float)thisEntity.getAttributeValue(Attributes.ATTACK_DAMAGE);
        float fd = (float)thisEntity.getAttributeValue(AttributeRegistry.FIRE_DAMAGE.get());
        float wd = (float)thisEntity.getAttributeValue(AttributeRegistry.WATER_DAMAGE.get());
        float ld = (float)thisEntity.getAttributeValue(AttributeRegistry.LIGHTNING_DAMAGE.get());
        float md = (float)thisEntity.getAttributeValue(AttributeRegistry.MAGIC_DAMAGE.get());
        float dd = (float)thisEntity.getAttributeValue(AttributeRegistry.DARK_DAMAGE.get());
        IDFEntityDamageSource source = new IDFEntityDamageSource("mob", thisEntity, fd, wd, ld, md, dd, damageClass);
        boolean flag = target.hurt(source, ad);
        if (flag) {
            thisEntity.doEnchantDamageEffects(thisEntity, target);
        }
        return flag;
    }

}
