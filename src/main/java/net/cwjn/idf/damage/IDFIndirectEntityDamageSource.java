package net.cwjn.idf.damage;

import net.minecraft.world.damagesource.IndirectEntityDamageSource;
import net.minecraft.world.entity.Entity;

public class IDFIndirectEntityDamageSource extends IndirectEntityDamageSource implements IDFInterface {

    private final float fire, water, lightning, magic, dark, pen, lifesteal;
    private final String damageClass; //strike, pierce, _slash, _crush, genric
    private boolean isTrue = false, isConversion = false;

    public IDFIndirectEntityDamageSource(String msgId, Entity source, Entity trueSource, String dc) {
        super(msgId, source, trueSource);
        fire = 0;
        water = 0;
        lightning = 0;
        magic = 0;
        dark = 0;
        pen = 0;
        lifesteal = 0;
        damageClass = dc;
    }

    public IDFIndirectEntityDamageSource (String msgId, Entity source, Entity trueSource, float f, float w, float l, float m, float d, float p, float ls, String dc) {
        super(msgId, source, trueSource);
        fire = f;
        water = w;
        lightning = l;
        magic = m;
        dark = d;
        pen = p;
        lifesteal = ls;
        damageClass = dc;
    }

    public IDFIndirectEntityDamageSource setTrue() {
        this.isTrue = true;
        return this;
    }

    public IDFIndirectEntityDamageSource setIsConversion() {
        this.isConversion = true;
        return this;
    }

    public boolean isTrue() {
        return isTrue;
    }

    public boolean isConversion() {
        return isConversion;
    }

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

    public float getDark() { return dark; }

    public float getPen() {
        return pen;
    }

    public float getLifesteal() {
        return lifesteal;
    }

    public String getDamageClass() {
        return damageClass;
    }

    public String getMsgId() {
        return this.msgId;
    }
}