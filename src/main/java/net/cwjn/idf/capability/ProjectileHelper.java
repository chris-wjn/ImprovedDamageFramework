package net.cwjn.idf.capability;

import net.minecraft.nbt.CompoundTag;

public class ProjectileHelper {

    private float fire = 0;
    private float water = 0;
    private float lightning = 0;
    private float magic = 0;
    private float dark = 0;
    private float pen = 0;
    private float crit = 0;
    private float lifesteal = 0;
    private String damageClass = "strike";

    public void setFire(float fire) {
        this.fire = fire;
    }

    public void setWater(float water) {
        this.water = water;
    }

    public void setLightning(float lightning) {
        this.lightning = lightning;
    }

    public void setMagic(float magic) {
        this.magic = magic;
    }

    public void setDark(float dark) {
        this.dark = dark;
    }

    public void setPen(float pen) {
        this.pen = pen;
    }

    public void setCrit(float crit) {
        this.crit = crit;
    }

    public void setLifesteal(float lifesteal) {
        this.lifesteal = lifesteal;
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

    public float getCrit() {
        return crit;
    }

    public float getLifesteal() {
        return lifesteal;
    }

    public void saveNBTData(CompoundTag tag) {
        tag.putFloat("fire", fire);
        tag.putFloat("fire", water);
        tag.putFloat("fire", lightning);
        tag.putFloat("fire", magic);
        tag.putFloat("fire", dark);
        tag.putFloat("fire", pen);
        tag.putFloat("fire", crit);
        tag.putFloat("fire", lifesteal);
    }

    public void loadNBTData(CompoundTag tag) {
        fire = tag.getFloat("fire");
        water = tag.getFloat("water");
        lightning = tag.getFloat("lightning");
        magic = tag.getFloat("magic");
        dark = tag.getFloat("dark");
        pen = tag.getFloat("pen");
        crit = tag.getFloat("crit");
        lifesteal = tag.getFloat("lifesteal");
    }

    public String getDamageClass() {
        return damageClass;
    }

    public void setDamageClass(String damageClass) {
        this.damageClass = damageClass;
    }
}
