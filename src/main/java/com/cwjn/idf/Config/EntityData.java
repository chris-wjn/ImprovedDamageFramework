package com.cwjn.idf.Config;

public class EntityData {

    private double[] damageValues = new double[5];
    private double[] resistanceValues = new double[5];
    private String damageClass;
    public EntityData(double f, double w, double l, double m, double d, String c, double fr, double wr, double lr, double mr, double dr) {
        damageValues[0] = f;
        damageValues[1] = w;
        damageValues[2] = l;
        damageValues[3] = m;
        damageValues[4] = d;
        damageClass = c;
        resistanceValues[0] = fr;
        resistanceValues[1] = wr;
        resistanceValues[2] = lr;
        resistanceValues[3] = mr;
        resistanceValues[4] = dr;
    }

    public double[] getDamageValues() {
        return damageValues;
    }

    public double[] getResistanceValues() {
        return resistanceValues;
    }

    public String getDamageClass() {
        return damageClass;
    }

    public void print() {
        System.out.print("DAMAGE: ");
        for (double n : damageValues) {
            System.out.print(n + ", ");
        }
        System.out.println(" ");
        System.out.print("RESISTANCES: ");
        for (double n : resistanceValues) {
            System.out.print(n + ", ");
        }
        System.out.println(" ");
        System.out.println("Damage Class: " + damageClass);
    }

}
