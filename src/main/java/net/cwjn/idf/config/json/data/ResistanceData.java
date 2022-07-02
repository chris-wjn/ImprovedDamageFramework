package net.cwjn.idf.config.json.data;

public class ResistanceData {

    private final double fire, water, lightning, magic, dark, armour, armourToughness,
                        evasion, maxHP, movespeed, knockbackRes, luck,
                        strikeMult, pierceMult, slashMult, crushMult, genericMult;

    public ResistanceData(double f, double w, double l, double m, double d, double a, double at,
                          double e, double h, double ms, double kr, double luck,
                          double str, double prc, double sls, double crs, double gen) {
        fire = f;
        water = w;
        lightning = l;
        magic = m;
        dark = d;
        armour = a;
        armourToughness = at;
        evasion = e;
        maxHP = h;
        movespeed = ms;
        knockbackRes = kr;
        this.luck = luck;
        strikeMult = str;
        pierceMult = prc;
        slashMult = sls;
        crushMult = crs;
        genericMult = gen;
    }

    public double getFire() {
        return fire;
    }

    public double getWater() {
        return water;
    }

    public double getLightning() {
        return lightning;
    }

    public double getMagic() {
        return magic;
    }

    public double getDark() {
        return dark;
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

    public double getLuck() {
        return luck;
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

    public double getArmour() {
        return armour;
    }

    public double getArmourToughness() {
        return armourToughness;
    }

}
