package net.cwjn.idf.config.json;

public class ResistanceData {

    private double[] resistanceValues = new double[5]; //fire, water, lightning, magic, dark
    private double[] auxiliary = new double[5]; //evasion, maxhp, movespeed, knockback resistance, luck
    private double[] multipliers = new double[5]; //strike, pierce, crush, slash, genric

    public ResistanceData(double f, double w, double l, double m, double d, double e, double h, double ms, double kr, double luck, double str, double prc, double crs, double sls, double gen) {
        resistanceValues[0] = f;
        resistanceValues[1] = w;
        resistanceValues[2] = l;
        resistanceValues[3] = m;
        resistanceValues[4] = d;
        auxiliary[0] = e;
        auxiliary[1] = h;
        auxiliary[2] = ms;
        auxiliary[3] = kr;
        auxiliary[4] = luck;
        multipliers[0] = str;
        multipliers[1] = prc;
        multipliers[2] = crs;
        multipliers[3] = sls;
        multipliers[4] = gen;
    }

    public double[] getResistanceValues() {
        return resistanceValues;
    }

    public double[] getAuxiliary() {
        return auxiliary;
    }

    public double[] getMultipliers() {
        return multipliers;
    }

}
