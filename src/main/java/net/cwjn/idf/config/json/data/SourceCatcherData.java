package net.cwjn.idf.config.json.data;

import net.cwjn.idf.config.json.JSONHandler;
import net.cwjn.idf.damage.IDFDamageSource;
import net.cwjn.idf.damage.IDFEntityDamageSource;
import net.cwjn.idf.damage.IDFIndirectEntityDamageSource;
import net.cwjn.idf.damage.IDFInterface;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.damagesource.IndirectEntityDamageSource;

public record SourceCatcherData(float fire, float water, float lightning, float magic, float dark, float pen, float lifesteal, float weight, String damageClass, boolean isTrueDamage) {

    public static IDFInterface convert(DamageSource source) {
        if (source instanceof IDFInterface) return (IDFInterface) source;
        final boolean isFall = source.isFall();
        final boolean isFire = source.isFire();
        final boolean isProjectile = source.isProjectile();
        final boolean isExplosion = source.isExplosion();
        final boolean isBypassInvul = source.isBypassInvul();
        DamageSource newSource;
        if (JSONHandler.sourceMap.containsKey(source.msgId)) {
            SourceCatcherData data = JSONHandler.sourceMap.get(source.msgId);
            if (data.isTrueDamage) {
                if (source instanceof IndirectEntityDamageSource) {
                    newSource = new IDFIndirectEntityDamageSource(source.msgId, source.getDirectEntity(), source.getEntity(),
                            data.fire, data.water, data.lightning, data.magic, data.dark, data.pen, data.lifesteal, data.weight,
                            data.damageClass).setIsConversion().setTrue();
                } else if (source instanceof EntityDamageSource) {
                    newSource = new IDFEntityDamageSource(source.msgId, source.getEntity(),
                            data.fire, data.water, data.lightning, data.magic, data.dark, data.pen, data.lifesteal, data.weight,
                            data.damageClass).setIsConversion().setTrue();
                } else {
                    newSource = new IDFDamageSource(source.msgId,
                            data.fire, data.water, data.lightning, data.magic, data.dark, data.pen, data.lifesteal, data.weight,
                            data.damageClass).setIsConversion().setTrue();
                }
            } else {
                if (source instanceof IndirectEntityDamageSource) {
                    newSource = new IDFIndirectEntityDamageSource(source.msgId, source.getDirectEntity(), source.getEntity(),
                            data.fire, data.water, data.lightning, data.magic, data.dark, data.pen, data.lifesteal, data.weight,
                            data.damageClass).setIsConversion();
                } else if (source instanceof EntityDamageSource) {
                    newSource = new IDFEntityDamageSource(source.msgId, source.getEntity(),
                            data.fire, data.water, data.lightning, data.magic, data.dark, data.pen, data.lifesteal, data.weight,
                            data.damageClass).setIsConversion();
                } else {
                    newSource = new IDFDamageSource(source.msgId,
                            data.fire, data.water, data.lightning, data.magic, data.dark, data.pen, data.lifesteal, data.weight,
                            data.damageClass).setIsConversion();
                }
            }
        } else {
            if (source instanceof IndirectEntityDamageSource) {
                newSource = new IDFIndirectEntityDamageSource(source.msgId, source.getDirectEntity(), source.getEntity(),
                        0, 0, 0, 0, 0, 0, 0, -1,
                        "strike").setIsConversion();
            } else if (source instanceof EntityDamageSource) {
                newSource = new IDFEntityDamageSource(source.msgId, source.getEntity(),
                        0, 0, 0, 0, 0, 0, 0, -1,
                        "strike").setIsConversion();
            } else {
                newSource = new IDFDamageSource(source.msgId,
                        0, 0, 0, 0, 0, 0, 0, -1,
                        "strike").setIsConversion();
            }
        }
        if (isBypassInvul) newSource.bypassInvul();
        if (isFire) newSource.setIsFire();
        if (isFall) newSource.setIsFall();
        if (isProjectile) newSource.setProjectile();
        if (isExplosion) newSource.setExplosion();
        return (IDFInterface) newSource;
    }
}
