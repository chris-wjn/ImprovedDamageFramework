package net.cwjn.idf.config.json;

public class DamageData {

    private double[] damageValues = new double[5];
    private double[] auxiliary = new double[3];
    private String damageClass;

    public DamageData(double f, double w, double l, double m, double d, String c, double ls, double pen, double crit) {
        damageValues[0] = f;
        damageValues[1] = w;
        damageValues[2] = l;
        damageValues[3] = m;
        damageValues[4] = d;
        damageClass = c;
        auxiliary[0] = ls;
        auxiliary[1] = pen;
        auxiliary[2] = crit;
    }

    public double[] getDamageValues() {
        return damageValues;
    }

    public String getDamageClass() {
        return damageClass;
    }

    public double[] getAuxiliary() {
        return auxiliary;
    }

}
