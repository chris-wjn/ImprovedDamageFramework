package net.cwjn.idf.mixin.irons_spells;

import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.spells.SchoolType;
import net.cwjn.idf.damage.IDFDamageSource;
import net.cwjn.idf.damage.IDFEntityDamageSource;
import net.cwjn.idf.damage.IDFIndirectEntityDamageSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.damagesource.IndirectEntityDamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import javax.annotation.Nullable;

import static io.redspace.ironsspellbooks.spells.SchoolType.*;

@Mixin(DamageSources.class)
public class MixinDamageSources {

    @Redirect(method = "applyDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    private static boolean convertToIDFSource(LivingEntity instance, DamageSource source, float amount, Entity e, float baseAmount, DamageSource oSource, SchoolType school) {
        DamageSource ds;
        float f=0, w=0, l=0, m=0, d=0, h=0;
        switch (school) {
            case FIRE -> f = 1;
            case ICE -> w = 1;
            case LIGHTNING -> l = 1;
            case ENDER -> m = 1;
            case BLOOD -> d = 1;
            case HOLY -> h = 1;
            default -> {}
        }
        if (source instanceof IndirectEntityDamageSource) {
            ds = new IDFIndirectEntityDamageSource(source.msgId, source.getDirectEntity(), source.getEntity(),
                    f, w, l, m, d, h, 0f, 0f, "none").setIsConversion();
        }
        else if (source instanceof EntityDamageSource) {
            ds = new IDFEntityDamageSource(source.msgId, source.getEntity(),
                    f, w, l, m, d, h, 0f, 0f, "none").setIsConversion();
        }
        else {
            ds = new IDFDamageSource(source.msgId,
                    f, w, l, m, d, h, 0f, 0f, "none").setIsConversion();
        }
        return instance.hurt(ds, amount);
    }

}
