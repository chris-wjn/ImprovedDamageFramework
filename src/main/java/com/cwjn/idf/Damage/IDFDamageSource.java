package com.cwjn.idf.Damage;

import net.minecraft.world.damagesource.DamageSource;

public class IDFDamageSource extends DamageSource implements IDFInterface {

    private float fire, water, lightning, magic, dark;
    private String damageClass; //strike, pierce, _slash, _crush, genric
    private boolean isTrue = false;

    public IDFDamageSource(String dc) {
        super("idfdamage");
        fire = 0;
        water = 0;
        lightning = 0;
        magic = 0;
        dark = 0;
        damageClass = dc;
    }

    public IDFDamageSource(float f, float w, float l, float m, float d, String dc) {
        super("idfdamage");
        fire = f;
        water = w;
        lightning = l;
        magic = m;
        dark = d;
        damageClass = dc;
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

    public String getDamageClass() {
        return damageClass;
    }
}
