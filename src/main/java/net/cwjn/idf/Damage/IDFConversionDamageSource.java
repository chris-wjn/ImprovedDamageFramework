package net.cwjn.idf.Damage;

import net.minecraft.world.damagesource.DamageSource;

public class IDFConversionDamageSource extends DamageSource {

    /* Damage source that contains percentage (0.0 - 1.0) values for elements, and normal pen and damageclass data.*/

    float fire, water, lightning, magic, dark;
    float pen;
    String damageClass;
    boolean isTrue = false;

    public IDFConversionDamageSource(String msgId, float f, float w, float l, float m, float d, float p, String dc) {
        super(msgId);
        fire = f;
        water = w;
        lightning = l;
        magic = m;
        dark = d;
        pen = p;
        damageClass = dc;
    }

    public IDFConversionDamageSource setTrue() {
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

    public float getDark() {
        return dark;
    }

    public float getPen() {
        return pen;
    }

    public String getDamageClass() {
        return damageClass;
    }

}
