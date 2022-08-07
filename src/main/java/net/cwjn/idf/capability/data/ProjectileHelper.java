package net.cwjn.idf.capability.data;

import net.minecraft.nbt.CompoundTag;

public class ProjectileHelper {

    private float fire = 0;
    private float water = 0;
    private float lightning = 0;
    private float magic = 0;
    private float dark = 0;
    private float phys = 0;
    private float pen = 0;
    private float crit = 0;
    private float lifesteal = 0;
    private float knockback = 0;
    private float weight = -1;
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

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public float getPhys() {
        return phys;
    }

    public void setPhys(float phys) {
        this.phys = phys;
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
        tag.putFloat("water", water);
        tag.putFloat("lightning", lightning);
        tag.putFloat("magic", magic);
        tag.putFloat("dark", dark);
        tag.putFloat("physical", phys);
        tag.putFloat("pen", pen);
        tag.putFloat("crit", crit);
        tag.putFloat("lifesteal", lifesteal);
        tag.putFloat("weight", weight);
    }

    public void loadNBTData(CompoundTag tag) {
        fire = tag.getFloat("fire");
        water = tag.getFloat("water");
        lightning = tag.getFloat("lightning");
        magic = tag.getFloat("magic");
        dark = tag.getFloat("dark");
        phys = tag.getFloat("physical");
        pen = tag.getFloat("pen");
        crit = tag.getFloat("crit");
        lifesteal = tag.getFloat("lifesteal");
        weight = tag.getFloat("weight");
    }

    public String getDamageClass() {
        return damageClass;
    }

    public void setDamageClass(String damageClass) {
        this.damageClass = damageClass;
    }

    public float getKnockback() {
        return knockback;
    }

    public void setKnockback(float knockback) {
        this.knockback = knockback;
    }
}
