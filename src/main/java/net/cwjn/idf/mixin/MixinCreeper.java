package net.cwjn.idf.mixin;

import net.cwjn.idf.damage.IDFInterface;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

import static net.minecraft.world.entity.monster.Creeper.DATA_IS_POWERED;

@Mixin(Creeper.class)
public abstract class MixinCreeper extends LivingEntity {

    protected MixinCreeper(EntityType<? extends LivingEntity> p_20966_, Level p_20967_) {
        super(p_20966_, p_20967_);
    }

    @Override
    public boolean hurt(DamageSource source, float amt) {
        if (source instanceof IDFInterface idfSource) {
            if (idfSource.getLightning() > 0) {
                Creeper thisCreeper = (Creeper)(Object)this;
                thisCreeper.getEntityData().set(DATA_IS_POWERED, true);
            }
        }
        return super.hurt(source, amt);
    }

}
