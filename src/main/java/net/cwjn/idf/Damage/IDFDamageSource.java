package net.cwjn.idf.Damage;

import net.minecraft.world.damagesource.DamageSource;

public class IDFDamageSource extends DamageSource implements IDFInterface {

    /* Damage source that has flat values for all elements, and also contains armor pen and damage class data.*/

    private float fire, water, lightning, magic, dark;
    private float pen;
    private String damageClass; //strike, pierce, _slash, _crush, genric
    private boolean isTrue = false;

    public IDFDamageSource(String msgId, String dc) {
        super(msgId);
        fire = 0;
        water = 0;
        lightning = 0;
        magic = 0;
        dark = 0;
        pen = 0;
        damageClass = dc;
    }

    public IDFDamageSource(String msgId, float f, float w, float l, float m, float d, String dc) {
        super(msgId);
        fire = f;
        water = w;
        lightning = l;
        magic = m;
        dark = d;
        damageClass = dc;
        pen = 0;
    }

    public IDFDamageSource(String msgId, float f, float w, float l, float m, float d, float pen, String dc) {
        super(msgId);
        fire = f;
        water = w;
        lightning = l;
        magic = m;
        dark = d;
        damageClass = dc;
        this.pen = pen;
    }

    public IDFDamageSource setTrue() {
        this.isTrue = true;
        return this;
    }

    public boolean isTrue() {
        return isTrue;
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

    public String getDamageClass() {
        return damageClass;
    }
}
