package net.cwjn.idf.Damage;

import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.entity.Entity;

public class IDFEntityDamageSource extends EntityDamageSource implements IDFInterface {

    private float fire, water, lightning, magic, dark, pen;
    private String damageClass; //strike, pierce, slash, crush, generic
    private boolean isTrue = false;

    public IDFEntityDamageSource(String msgId, Entity entity) {
        super(msgId, entity);
    }

    public IDFEntityDamageSource(String msgId, Entity entity, String dc) {
        super(msgId, entity);
        damageClass = dc;
    }

    public IDFEntityDamageSource(String msgId, Entity entity, float f, float w, float l, float m, float d, String dc) {
        super(msgId, entity);
        fire = f;
        water = w;
        lightning = l;
        magic = m;
        dark = d;
        damageClass = dc;
        pen = 0;
    }

    public IDFEntityDamageSource(String msgId, Entity entity, float f, float w, float l, float m, float d, float pen, String dc) {
        super(msgId, entity);
        fire = f;
        water = w;
        lightning = l;
        magic = m;
        dark = d;
        damageClass = dc;
        this.pen = pen;
    }

    public IDFEntityDamageSource setTrue() {
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
