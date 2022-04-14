package com.cwjn.idf.Attributes;

import net.minecraft.nbt.CompoundTag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AuxiliaryData {

    private String damageClass = "strike";

    public String getDamageClass() {
        return damageClass;
    }

    public void setDamageClass(String dc) {
        damageClass = dc;
    }

    public void copyFrom(AuxiliaryData source) {
        damageClass = source.getDamageClass();
    }

    public void saveNBTData(CompoundTag tag) {
        tag.putString("damageClass", damageClass);
    }

    public void loadNBTData(CompoundTag tag) {
        damageClass = tag.getString("damageClass");
    }

}
