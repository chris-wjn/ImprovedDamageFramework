package net.cwjn.idf.config.json.data;

public class DamageData {

    private final double speed, fire, water, lightning, magic, dark, attackDamage, lifesteal, armourPenetration, critChance, weight;
    private final String damageClass;
    private final int durability;

    public DamageData(int durability, double f, double w, double l, double m, double d, double phys, double speed, String c, double ls, double pen, double wt, double crit) {
        damageClass = c;
        fire = f;
        water = w;
        lightning = l;
        magic = m;
        dark = d;
        attackDamage = phys;
        this.speed = speed;
        lifesteal = ls;
        armourPenetration = pen;
        critChance = crit;
        weight = wt;
        this.durability = durability;
    }

    public String getDamageClass() {
        return damageClass;
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

    public double getLifesteal() {
        return lifesteal;
    }

    public double getArmourPenetration() {
        return armourPenetration;
    }

    public double getCritChance() {
        return critChance;
    }

    public double getAttackDamage() {
        return attackDamage;
    }

    public double getWeight() {
        return weight;
    }

    public double getSpeed() {
        return speed;
    }

    public int getDurability() {
        return durability;
    }
}
