package net.cwjn.idf.event.post;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.eventbus.api.Event;

public class PreMitigationDamageEvent extends Event {

    private float fire, water, lightning, magic, dark, physical, pen, lifesteal;
    private String damageClass;
    private final LivingEntity target;

    public PreMitigationDamageEvent(LivingEntity target, float fire, float water, float lightning, float magic, float dark, float physical, float pen, float lifesteal, String damageClass) {
        this.fire = fire;
        this.water = water;
        this.lightning = lightning;
        this.magic = magic;
        this.dark = dark;
        this.physical = physical;
        this.pen = pen;
        this.lifesteal = lifesteal;
        this.damageClass = damageClass;
        this.target = target;
    }

    public float getFire() {
        return fire;
    }

    public void setFire(float fire) {
        this.fire = fire;
    }

    public float getWater() {
        return water;
    }

    public void setWater(float water) {
        this.water = water;
    }

    public float getLightning() {
        return lightning;
    }

    public void setLightning(float lightning) {
        this.lightning = lightning;
    }

    public float getMagic() {
        return magic;
    }

    public void setMagic(float magic) {
        this.magic = magic;
    }

    public float getDark() {
        return dark;
    }

    public void setDark(float dark) {
        this.dark = dark;
    }

    public float getPhysical() {
        return physical;
    }

    public void setPhysical(float physical) {
        this.physical = physical;
    }

    public float getPen() {
        return pen;
    }

    public void setPen(float pen) {
        this.pen = pen;
    }

    public float getLifesteal() {
        return lifesteal;
    }

    public void setLifesteal(float lifesteal) {
        this.lifesteal = lifesteal;
    }

    public String getDamageClass() {
        return damageClass;
    }

    public void setDamageClass(String damageClass) {
        this.damageClass = damageClass;
    }

    public LivingEntity getTarget() {
        return target;
    }

    @Override
    public boolean isCancelable() {
        return false;
    }

}
