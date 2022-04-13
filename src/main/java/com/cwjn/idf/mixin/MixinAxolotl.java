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

@Mixin(Axolotl.class)
public class MixinAxolotl {

    @Overwrite
    public boolean doHurtTarget(Entity target) {
        LivingEntity entity = (LivingEntity)((Object)this);
        float ad = (float)entity.getAttributeValue(Attributes.ATTACK_DAMAGE);
        float fd = (float)entity.getAttributeValue(AttributeRegistry.FIRE_DAMAGE.get());
        float wd = (float)entity.getAttributeValue(AttributeRegistry.WATER_DAMAGE.get());
        float ld = (float)entity.getAttributeValue(AttributeRegistry.LIGHTNING_DAMAGE.get());
        float md = (float)entity.getAttributeValue(AttributeRegistry.MAGIC_DAMAGE.get());
        float dd = (float)entity.getAttributeValue(AttributeRegistry.DARK_DAMAGE.get());

        IDFEntityDamageSource source = new IDFEntityDamageSource("mob", entity, fd, wd, ld, md, dd, "strike");
        boolean flag = target.hurt(source, ad);
        if (flag) {
            entity.doEnchantDamageEffects(entity, target);
            entity.playSound(SoundEvents.AXOLOTL_ATTACK, 1.0F, 1.0F);
        }
        return flag;
    }

}
