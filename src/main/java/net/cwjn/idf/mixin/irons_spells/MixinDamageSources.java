package net.cwjn.idf.mixin.irons_spells;

import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.damage.ISpellDamageSource;
import net.cwjn.idf.damage.IDFDamageSource;
import net.cwjn.idf.damage.IDFEntityDamageSource;
import net.cwjn.idf.damage.IDFIndirectEntityDamageSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.damagesource.IndirectEntityDamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(DamageSources.class)
public class MixinDamageSources {

    @Redirect(
            method = "applyDamage(Lnet/minecraft/world/entity/Entity;FLnet/minecraft/world/damagesource/DamageSource;)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z",
                    ordinal = 0))
    //from the original method, the method is guaranteed to be a spell damage source
    private static boolean convertToIDFSource(LivingEntity instance, DamageSource ev, float amount) {
        ISpellDamageSource ds = (ISpellDamageSource) ev;
        if (ds.schoolType() == null) {
            return instance.hurt(new IDFDamageSource(ev.msgId,
                    0, 0, 0, 1, 0, 0, 0f, 0f, "none").setIsConversion(), amount);
        }
        float f=0, w=0, l=0, m=0, d=0, h=0;
        switch (ds.schoolType().getId().toString()) {
            case "irons_spellbooks:fire" -> f = 1;
            case "irons_spellbooks:ice" -> w = 1;
            case "irons_spellbooks:lightning" -> l = 1;
            case "irons_spellbooks:ender","irons_spellbooks:nature" -> m = 1;
            case "irons_spellbooks:blood","irons_spellbooks:evocation" -> d = 1;
            case "irons_spellbooks:holy" -> h = 1;
            default -> {}
        }
        if (ds instanceof IndirectEntityDamageSource) {
            return instance.hurt(new IDFIndirectEntityDamageSource(ev.msgId, ev.getDirectEntity(), ev.getEntity(),
                    f, w, l, m, d, h, 0f, 0f, "none").setIsConversion(), amount);
        }
        else if (ds instanceof EntityDamageSource) {
            return instance.hurt(new IDFEntityDamageSource(ev.msgId, ev.getEntity(),
                    f, w, l, m, d, h, 0f, 0f, "none").setIsConversion(), amount);
        }
        else {
            return instance.hurt(new IDFDamageSource(ev.msgId,
                    f, w, l, m, d, h, 0f, 0f, "none").setIsConversion(), amount);
        }

    }

}
