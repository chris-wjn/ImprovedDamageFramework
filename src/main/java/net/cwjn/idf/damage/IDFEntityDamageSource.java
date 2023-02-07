package net.cwjn.idf.damage;

import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class IDFEntityDamageSource extends EntityDamageSource implements IDFInterface {

    private final float fire, water, lightning, magic, dark, holy, pen, lifesteal, weight, knockback;
    private final String damageClass; //strike, pierce, slash, crush, generic
    private boolean isTrue = false, isConversion = false;

    public IDFEntityDamageSource(String msgId, Entity entity, float f, float w, float l, float m, float d, float h, float pen, float ls, String dc) {
        super(msgId, entity);
        fire = f;
        water = w;
        lightning = l;
        magic = m;
        dark = d;
        holy = h;
        this.pen = pen;
        lifesteal = ls;
        weight = -1;
        knockback = 0;
        damageClass = dc;
    }

    public IDFEntityDamageSource(String msgId, Entity entity, float f, float w, float l, float m, float d, float h, float pen, float ls, float wt, String dc) {
        super(msgId, entity);
        fire = f;
        water = w;
        lightning = l;
        magic = m;
        dark = d;
        holy = h;
        this.pen = pen;
        lifesteal = ls;
        weight = wt;
        knockback = 0;
        damageClass = dc;
    }

    public IDFEntityDamageSource(String msgId, Entity entity, float f, float w, float l, float m, float d, float h, float pen, float ls, float kb, float wt, String dc) {
        super(msgId, entity);
        fire = f;
        water = w;
        lightning = l;
        magic = m;
        dark = d;
        holy = h;
        this.pen = pen;
        lifesteal = ls;
        weight = wt;
        knockback = kb;
        damageClass = dc;
    }

    public IDFEntityDamageSource setTrue() {
        this.isTrue = true;
        return this;
    }

    public IDFEntityDamageSource setIsConversion() {
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

    public float getHoly() {
        return holy;
    }

    public float getPen() {
        return pen;
    }

    public float getLifesteal() {
        return lifesteal;
    }

    public float getWeight() {
        return weight;
    }

    public String getDamageClass() {
        return damageClass;
    }

    public String getName() {
        return this.msgId;
    }

    public float getKnockback() {
        return knockback;
    }

}
