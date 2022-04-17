package com.cwjn.idf.mixin;

import com.cwjn.idf.Attributes.AuxiliaryData;
import com.cwjn.idf.Attributes.CapabilityProvider;
import com.cwjn.idf.Damage.IDFEntityDamageSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Dolphin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(Dolphin.class)
public class MixinDolphin {

    /**
     * @author cwJn
     */
    @Overwrite
    public boolean doHurtTarget(Entity target) {
        Dolphin thisEntity = (Dolphin)(Object)this;
        String damageClass = "strike";
        AuxiliaryData data = thisEntity.getCapability(CapabilityProvider.AUXILIARY_DATA).orElse(null);
        if (data != null) damageClass = data.getDamageClass();
        boolean flag = target.hurt(new IDFEntityDamageSource("mob", thisEntity, damageClass), (float)thisEntity.getAttributeValue(Attributes.ATTACK_DAMAGE));
        if (flag) {
            thisEntity.doEnchantDamageEffects(thisEntity, target);
            thisEntity.playSound(SoundEvents.DOLPHIN_ATTACK, 1.0F, 1.0F);
        }
        return flag;
    }

}
