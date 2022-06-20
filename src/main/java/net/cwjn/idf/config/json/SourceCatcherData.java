package net.cwjn.idf.config.json;

import net.cwjn.idf.damage.IDFDamageSource;
import net.cwjn.idf.damage.IDFEntityDamageSource;
import net.cwjn.idf.damage.IDFIndirectEntityDamageSource;
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
    private final String damageClass;

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

    public String getDamageClass() {
        return damageClass;
    }

    public SourceCatcherData(float f, float w, float l, float m, float d, float p, String dc) {
        damageClass = dc;
        fire = f;
        water = w;
        lightning = l;
        magic = m;
        dark = d;
        pen = p;
    }

    public static DamageSource convert(DamageSource source) {
        if (source instanceof IndirectEntityDamageSource) {
            if (JSONHandler.sourceMap.containsKey(source.msgId)) {
                SourceCatcherData data = JSONHandler.sourceMap.get(source.msgId);
                return new IDFIndirectEntityDamageSource(source.msgId, source.getDirectEntity(), source.getEntity(),
                        data.getFire(),
                        data.getWater(),
                        data.getLightning(),
                        data.getMagic(),
                        data.getDark(),
                        data.getPen(),
                        data.getDamageClass()).setIsConversion();
            }
        } else if (source instanceof EntityDamageSource) {
            if (JSONHandler.sourceMap.containsKey(source.msgId)) {
                SourceCatcherData data = JSONHandler.sourceMap.get(source.msgId);
                return new IDFEntityDamageSource(source.msgId, source.getEntity(),
                        data.getFire(),
                        data.getWater(),
                        data.getLightning(),
                        data.getMagic(),
                        data.getDark(),
                        data.getPen(),
                        data.getDamageClass()).setIsConversion();
            }
        } else {
            if (JSONHandler.sourceMap.containsKey(source.msgId)) {
                SourceCatcherData data = JSONHandler.sourceMap.get(source.msgId);
                return new IDFDamageSource(source.msgId,
                        data.getFire(),
                        data.getWater(),
                        data.getLightning(),
                        data.getMagic(),
                        data.getDark(),
                        data.getPen(),
                        data.getDamageClass()).setIsConversion();
            }
        }
        return new IDFDamageSource(source.msgId, "strike");
    }
}
