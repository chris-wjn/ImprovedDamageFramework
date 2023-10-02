package net.cwjn.idf.mixin.irons_spells;

import io.redspace.ironsspellbooks.api.spells.SchoolType;
import io.redspace.ironsspellbooks.damage.DamageSources;
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

@Mixin(DamageSources.class)
public class MixinDamageSources {

    @Redirect(method = "applyDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    private static boolean convertToIDFSource(LivingEntity instance, DamageSource source, float amount, Entity target, float baseAmount, DamageSource damageSource, SchoolType school) {
        DamageSource ds;
        if (school == null) {
            return instance.hurt(new IDFDamageSource(source.msgId,
                    0, 0, 0, 1, 0, 0, 0f, 0f, "none").setIsConversion(), amount);
        }
        float f=0, w=0, l=0, m=0, d=0, h=0;
        switch (school.getId().toString()) {
            case "irons_spellbooks:fire" -> f = 1;
            case "irons_spellbooks:ice" -> w = 1;
            case "irons_spellbooks:lightning" -> l = 1;
            case "irons_spellbooks:ender","irons_spellbooks:nature" -> m = 1;
            case "irons_spellbooks:blood","irons_spellbooks:evocation" -> d = 1;
            case "irons_spellbooks:holy" -> h = 1;
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
