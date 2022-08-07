package net.cwjn.idf.config.json.data;

import net.cwjn.idf.config.json.JSONHandler;
import net.cwjn.idf.damage.IDFDamageSource;
import net.cwjn.idf.damage.IDFEntityDamageSource;
import net.cwjn.idf.damage.IDFIndirectEntityDamageSource;
import net.cwjn.idf.damage.IDFInterface;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.damagesource.IndirectEntityDamageSource;

public class SourceCatcherData {

    private final float fire;
    private final float water;
    private final float lightning;
    private final float magic;
    private final float dark;
    private final float pen;
    private final float lifesteal;
    private final float weight;
    private final String damageClass;
    private final boolean isTrueDamage;

    public float getFire() {
        return fire;
    }

    public float getWater() {
        return water;
    }

    public float getLightning() {
        return lightning;
    }

    public float getMagic() {
        return magic;
    }

    public float getDark() {
        return dark;
    }

    public float getPen() {
        return pen;
    }

    public float getLifesteal() {
        return lifesteal;
    }

    public String getDamageClass() {
        return damageClass;
    }

    public float getWeight() {
        return weight;
    }

    public boolean isTrueDamage() {
        return isTrueDamage;
    }

    public SourceCatcherData(float f, float w, float l, float m, float d, float p, float ls, float wt, String dc, boolean isTrue) {
        damageClass = dc;
        fire = f;
        water = w;
        lightning = l;
        magic = m;
        dark = d;
        pen = p;
        lifesteal = ls;
        weight = wt;
        this.isTrueDamage = isTrue;
    }

    public static DamageSource convert(DamageSource source) {
        final boolean isFall = source.isFall();
        final boolean isProjectile = source.isProjectile();
        final boolean isExplosion = source.isExplosion();
        DamageSource newSource;
        if (source instanceof IndirectEntityDamageSource) {
            if (JSONHandler.sourceMap.containsKey(source.msgId)) {
                SourceCatcherData data = JSONHandler.sourceMap.get(source.msgId);
                if (data.isTrueDamage() || source.isBypassInvul()) {
                    newSource = new IDFIndirectEntityDamageSource(source.msgId, source.getDirectEntity(), source.getEntity(),
                            data.getFire(),
                            data.getWater(),
                            data.getLightning(),
                            data.getMagic(),
                            data.getDark(),
                            data.getPen(),
                            data.getLifesteal(),
                            data.getWeight(),
                            data.getDamageClass()).setIsConversion().setTrue();
                } else {
                    newSource = new IDFIndirectEntityDamageSource(source.msgId, source.getDirectEntity(), source.getEntity(),
                            data.getFire(),
                            data.getWater(),
                            data.getLightning(),
                            data.getMagic(),
                            data.getDark(),
                            data.getPen(),
                            data.getLifesteal(),
                            data.getWeight(),
                            data.getDamageClass()).setIsConversion();
                }
                if (isFall) newSource.setIsFall();
                if (isProjectile) newSource.setProjectile();
                if (isExplosion) newSource.setExplosion();
                return newSource;
            }
        } else if (source instanceof EntityDamageSource) {
            if (JSONHandler.sourceMap.containsKey(source.msgId)) {
                SourceCatcherData data = JSONHandler.sourceMap.get(source.msgId);
                if (data.isTrueDamage() || source.isBypassInvul()) {
                    newSource = new IDFEntityDamageSource(source.msgId, source.getEntity(),
                            data.getFire(),
                            data.getWater(),
                            data.getLightning(),
                            data.getMagic(),
                            data.getDark(),
                            data.getPen(),
                            data.getLifesteal(),
                            data.getWeight(),
                            data.getDamageClass()).setIsConversion().setTrue();
                } else {
                    newSource = new IDFEntityDamageSource(source.msgId, source.getEntity(),
                            data.getFire(),
                            data.getWater(),
                            data.getLightning(),
                            data.getMagic(),
                            data.getDark(),
                            data.getPen(),
                            data.getLifesteal(),
                            data.getWeight(),
                            data.getDamageClass()).setIsConversion();
                }
                if (isFall) newSource.setIsFall();
                if (isProjectile) newSource.setProjectile();
                if (isExplosion) newSource.setExplosion();
                return newSource;
            }
        } else {
            if (JSONHandler.sourceMap.containsKey(source.msgId)) {
                SourceCatcherData data = JSONHandler.sourceMap.get(source.msgId);
                if (data.isTrueDamage() || source.isBypassInvul()) {
                    newSource = new IDFDamageSource(source.msgId,
                            data.getFire(),
                            data.getWater(),
                            data.getLightning(),
                            data.getMagic(),
                            data.getDark(),
                            data.getPen(),
                            data.getLifesteal(),
                            data.getWeight(),
                            data.getDamageClass()).setIsConversion().setTrue();
                } else {
                    newSource = new IDFDamageSource(source.msgId,
                            data.getFire(),
                            data.getWater(),
                            data.getLightning(),
                            data.getMagic(),
                            data.getDark(),
                            data.getPen(),
                            data.getLifesteal(),
                            data.getWeight(),
                            data.getDamageClass()).setIsConversion();
                }
                if (isFall) newSource.setIsFall();
                if (isProjectile) newSource.setProjectile();
                if (isExplosion) newSource.setExplosion();
                return newSource;
            }
        }
        if (source.isBypassInvul()) {
            newSource =  new IDFDamageSource(source.msgId, "strike").setTrue();
        } else {
            newSource = new IDFDamageSource(source.msgId, "strike");
        }
        if (isFall) newSource.setIsFall();
        if (isProjectile) newSource.setProjectile();
        if (isExplosion) newSource.setExplosion();
        return newSource;
    }
}
