package com.cwjn.idf.Config;

public class DamageData {

    private double[] damageValues = new double[5];
    private String damageClass;

    public DamageData(double f, double w, double l, double m, double d, String c) {
        damageValues[0] = f;
        damageValues[1] = w;
        damageValues[2] = l;
        damageValues[3] = m;
        damageValues[4] = d;
        damageClass = c;
    }

    public double[] getDamageValues() {
        return damageValues;
    }

    public String getDamageClass() {
        return damageClass;
    }

}
