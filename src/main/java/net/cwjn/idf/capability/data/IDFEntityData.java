package net.cwjn.idf.capability.data;

import net.minecraft.nbt.CompoundTag;

public class IDFEntityData {

    private String damageClass = "strike";

    public String getDamageClass() {
        return damageClass;
    }

    public void setDamageClass(String dc) {
        damageClass = dc;
    }

    public void copyFrom(IDFEntityData source) {
        damageClass = source.getDamageClass();
    }

    public void saveNBTData(CompoundTag tag) {
        tag.putString("damageClass", damageClass);
    }

    public void loadNBTData(CompoundTag tag) {
        damageClass = tag.getString("damageClass");
    }

}
