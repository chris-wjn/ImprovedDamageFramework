package net.cwjn.idf.api.event;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.eventbus.api.Event;

public class PreMitigationDamageEvent extends Event {

    private float fireDmg, waterDmg, lightningDmg, magicDmg, darkDmg, holyDmg, physicalDmg, pen, lifesteal;
    private float fireDef, waterDef, lightningDef, magicDef, darkDef, holyDef, physicalDef;
    private final LivingEntity target;

    public PreMitigationDamageEvent(LivingEntity target,
                                    float fireDmg, float waterDmg, float lightningDmg, float magicDmg, float darkDmg, float holyDmg, float physicalDmg,
                                    float pen, float lifesteal,
                                    float fireDef, float waterDef, float lightningDef, float magicDef, float darkDef, float holyDef, float physicalDef) {
        this.fireDmg = fireDmg;
        this.waterDmg = waterDmg;
        this.lightningDmg = lightningDmg;
        this.magicDmg = magicDmg;
        this.darkDmg = darkDmg;
        this.physicalDmg = physicalDmg;
        this.holyDmg = holyDmg;
        this.pen = pen;
        this.lifesteal = lifesteal;
        this.fireDef = fireDef;
        this.waterDef = waterDef;
        this.lightningDef = lightningDef;
        this.magicDef = magicDef;
        this.darkDef = darkDef;
        this.holyDef = holyDef;
        this.physicalDef = physicalDef;
        this.target = target;
    }

    public float getFireDmg() {
        return fireDmg;
    }

    public void setFireDmg(float fireDmg) {
        this.fireDmg = fireDmg;
    }

    public float getWaterDmg() {
        return waterDmg;
    }

    public void setWaterDmg(float waterDmg) {
        this.waterDmg = waterDmg;
    }

    public float getLightningDmg() {
        return lightningDmg;
    }

    public void setLightningDmg(float lightningDmg) {
        this.lightningDmg = lightningDmg;
    }

    public float getMagicDmg() {
        return magicDmg;
    }

    public void setMagicDmg(float magicDmg) {
        this.magicDmg = magicDmg;
    }

    public float getDarkDmg() {
        return darkDmg;
    }

    public void setDarkDmg(float darkDmg) {
        this.darkDmg = darkDmg;
    }

    public float getPhysicalDmg() {
        return physicalDmg;
    }

    public void setPhysicalDmg(float physicalDmg) {
        this.physicalDmg = physicalDmg;
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

    public LivingEntity getTarget() {
        return target;
    }

    public float getFireDef() {
        return fireDef;
    }

    public void setFireDef(float fireDef) {
        this.fireDef = fireDef;
    }

    public float getWaterDef() {
        return waterDef;
    }

    public void setWaterDef(float waterDef) {
        this.waterDef = waterDef;
    }

    public float getLightningDef() {
        return lightningDef;
    }

    public void setLightningDef(float lightningDef) {
        this.lightningDef = lightningDef;
    }

    public float getMagicDef() {
        return magicDef;
    }

    public void setMagicDef(float magicDef) {
        this.magicDef = magicDef;
    }

    public float getDarkDef() {
        return darkDef;
    }

    public void setDarkDef(float darkDef) {
        this.darkDef = darkDef;
    }

    public float getPhysicalDef() {
        return physicalDef;
    }

    public void setPhysicalDef(float physicalDef) {
        this.physicalDef = physicalDef;
    }

    public float getHolyDmg() {
        return holyDmg;
    }

    public void setHolyDmg(float holyDmg) {
        this.holyDmg = holyDmg;
    }

    public float getHolyDef() {
        return holyDef;
    }

    public void setHolyDef(float holyDef) {
        this.holyDef = holyDef;
    }

    @Override
    public boolean isCancelable() {
        return false;
    }

}
