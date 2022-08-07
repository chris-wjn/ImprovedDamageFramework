package net.cwjn.idf.config.json.data;

public class EntityData {

    private final double fireDamage, waterDamage, lightningDamage, magicDamage, darkDamage, attackDamage,
                         fireResistance, waterResistance,lightningResistance, magicResistance, darkResistance, armour, armourToughness,
                         strikeMult, pierceMult, slashMult, crushMult, genericMult,
                         lifesteal, armourPenetration, knockback, weight,
                         evasion, maxHP, movespeed, knockbackRes;
    private final String damageClass;

    public EntityData(double f, double w, double l, double m, double d, double ad, String c,
                      double fr, double wr, double lr, double mr, double dr, double a, double at,
                      double dcS, double dcP, double dcSl, double dcC, double dcG,
                      double lifesteal, double pen, double kb, double wt,
                      double eva, double hp, double ms, double kr) {
        fireDamage = f;
        waterDamage = w;
        lightningDamage = l;
        magicDamage = m;
        darkDamage = d;
        attackDamage = ad;
        damageClass = c;
        fireResistance = fr;
        waterResistance = wr;
        lightningResistance = lr;
        magicResistance = mr;
        darkResistance = dr;
        armour = a;
        armourToughness = at;
        strikeMult = dcS;
        pierceMult = dcP;
        slashMult = dcSl;
        crushMult= dcC;
        genericMult = dcG;
        this.lifesteal = lifesteal;
        armourPenetration = pen;
        knockback = kb;
        weight = wt;
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

    public double getCrushMult() {
        return crushMult;
    }

    public double getGenericMult() {
        return genericMult;
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

    public double getAttackDamage() {
        return attackDamage;
    }

    public double getArmour() {
        return armour;
    }

    public double getArmourToughness() {
        return armourToughness;
    }

    public double getWeight() {
        return weight;
    }

    public double getKnockback() {
        return knockback;
    }
}
