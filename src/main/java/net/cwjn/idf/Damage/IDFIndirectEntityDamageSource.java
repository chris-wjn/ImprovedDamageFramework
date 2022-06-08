package net.cwjn.idf.Damage;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.IndirectEntityDamageSource;
import net.minecraft.world.entity.Entity;

public class IDFIndirectEntityDamageSource extends IndirectEntityDamageSource implements IDFInterface {

    private float fire = 0, water = 0, lightning = 0, magic = 0, dark = 0;
    private float pen = 0;
    private String damageClass = "strike"; //strike, pierce, _slash, _crush, genric
    private boolean isTrue = false;

    public IDFIndirectEntityDamageSource (String msgId, Entity source, Entity trueSource, float f, float w, float l, float m, float d, float p, String dc) {
        super(msgId, source, trueSource);
        fire = f;
        water = w;
        lightning = l;
        magic = m;
        dark = d;
        pen = p;
        damageClass = dc;
    }

    public IDFIndirectEntityDamageSource setTrue() {
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