package net.cwjn.idf.api.event;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.eventbus.api.Event;

public class PostMitigationDamageEvent extends Event {

    private float[] damage = new float[7];
    private final LivingEntity target;

    public PostMitigationDamageEvent(LivingEntity target, float fire, float water, float lightning, float magic, float dark, float holy, float physical) {
        damage[0] = fire;
        damage[1] = water;
        damage[2] = lightning;
        damage[3] = magic;
        damage[4] = dark;
        damage[5] = holy;
        damage[6] = physical;
        this.target = target;
    }

    public void setDamage(int i, float f) {
        damage[i] = f;
    }

    public float[] getDamage() {
        return damage;
    }

    public float getFire() {
        return damage[0];
    }

    public void setFire(float fire) {
        damage[0] = fire;
    }

    public float getWater() {
        return damage[1];
    }

    public void setWater(float water) {
        damage[1] = water;
    }

    public float getLightning() {
        return damage[2];
    }

    public void setLightning(float lightning) {
        damage[2] = lightning;
    }

    public float getMagic() {
        return damage[3];
    }

    public void setMagic(float magic) {
        damage[3] = magic;
    }

    public float getDark() {
        return damage[4];
    }

    public void setDark(float dark) {
        damage[4] = dark;
    }

    public float getHoly() {
        return damage[5];
    }

    public void setHoly(float holy) {
        damage[5] = holy;
    }

    public float getPhysical() {
        return damage[6];
    }

    public void setPhysical(float physical) {
        damage[6] = physical;
    }

    public LivingEntity getTarget() {
        return target;
    }

    @Override
    public boolean isCancelable() {
        return false;
    }

}
