package com.cwjn.idf.mixin;

import com.cwjn.idf.Attributes.AttributeRegistry;
import com.cwjn.idf.Damage.IDFEntityDamageSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.IronGolem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Random;

@Mixin(IronGolem.class)
public class MixinIronGolem {

    @Shadow
    private int attackAnimationTick;
    @Shadow
    protected Random random;

    @Overwrite
    public boolean doHurtTarget(Entity target) {
        LivingEntity entity = (LivingEntity)((Object)this);
        this.attackAnimationTick = 10;
        entity.level.broadcastEntityEvent(entity, (byte)4);
        float ad = (float)entity.getAttributeValue(Attributes.ATTACK_DAMAGE);
        float fd = (float)entity.getAttributeValue(AttributeRegistry.FIRE_DAMAGE.get());
        float wd = (float)entity.getAttributeValue(AttributeRegistry.WATER_DAMAGE.get());
        float ld = (float)entity.getAttributeValue(AttributeRegistry.LIGHTNING_DAMAGE.get());
        float md = (float)entity.getAttributeValue(AttributeRegistry.MAGIC_DAMAGE.get());
        float dd = (float)entity.getAttributeValue(AttributeRegistry.DARK_DAMAGE.get());
        float f = (int)ad > 0 ? ad / 2.0F + (float)this.random.nextInt((int)ad) : ad;
        IDFEntityDamageSource source = new IDFEntityDamageSource("mob", entity, fd, wd, ld, md, dd, "strike");
        boolean flag = target.hurt(source, f);
        if (flag) {
            target.setDeltaMovement(target.getDeltaMovement().add(0.0D, (double)0.4F, 0.0D));
            entity.doEnchantDamageEffects(entity, target);
        }
        entity.playSound(SoundEvents.IRON_GOLEM_ATTACK, 1.0F, 1.0F);
        return flag;
    }

}
