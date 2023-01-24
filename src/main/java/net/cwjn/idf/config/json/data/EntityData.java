package net.cwjn.idf.config.json.data;

public class EntityData {

    private final double fireDamage, waterDamage, lightningDamage, magicDamage, darkDamage, physicalDamage,
                         fireResistance, waterResistance,lightningResistance, magicResistance, darkResistance, physicalResistance, defense,
                         strikeMult, pierceMult, slashMult,
                         lifesteal, armourPenetration, knockback, force,
                         evasion, maxHP, movespeed, knockbackRes;
    private final String damageClass;

    public EntityData(double f, double w, double l, double m, double d, double ad, String c,
                      double fr, double wr, double lr, double mr, double dr, double a, double at,
                      double dcS, double dcP, double dcSl,
                      double lifesteal, double pen, double kb, double wt,
                      double eva, double hp, double ms, double kr) {
        fireDamage = f;
        waterDamage = w;
        lightningDamage = l;
        magicDamage = m;
        darkDamage = d;
        physicalDamage = ad;
        damageClass = c;
        fireResistance = fr;
        waterResistance = wr;
        lightningResistance = lr;
        magicResistance = mr;
        darkResistance = dr;
        physicalResistance = a;
        defense = at;
        strikeMult = dcS;
        pierceMult = dcP;
        slashMult = dcSl;
        this.lifesteal = lifesteal;
        armourPenetration = pen;
        knockback = kb;
        force = wt;
        evasion = eva;
        maxHP = hp;
        movespeed = ms;
        knockbackRes = kr;
    }

    public String getDamageClass() {
        return damageClass;
    }

    public double getFireDamage() {
        return fireDamage;
    }

    public double getWaterDamage() {
        return waterDamage;
    }

    public double getLightningDamage() {
        return lightningDamage;
    }

    public double getMagicDamage() {
        return magicDamage;
    }

    public double getDarkDamage() {
        return darkDamage;
    }

    public double getFireResistance() {
        return fireResistance;
    }

    public double getWaterResistance() {
        return waterResistance;
    }

    public double getLightningResistance() {
        return lightningResistance;
    }

    public double getMagicResistance() {
        return magicResistance;
    }

    public double getDarkResistance() {
        return darkResistance;
    }

    public double getStrikeMult() {
        return strikeMult;
    }

    public double getPierceMult() {
        return pierceMult;
    }

    public double getSlashMult() {
        return slashMult;
    }

    public double getLifesteal() {
        return lifesteal;
    }

    public double getArmourPenetration() {
        return armourPenetration;
    }

    public double getEvasion() {
        return evasion;
    }

    public double getMaxHP() {
        return maxHP;
    }

    public double getMovespeed() {
        return movespeed;
    }

    public double getKnockbackRes() {
        return knockbackRes;
    }

    public double getPhysicalDamage() {
        return physicalDamage;
    }

    public double getPhysicalResistance() {
        return physicalResistance;
    }

    public double getDefense() {
        return defense;
    }

    public double getForce() {
        return force;
    }

    public double getKnockback() {
        return knockback;
    }
}
