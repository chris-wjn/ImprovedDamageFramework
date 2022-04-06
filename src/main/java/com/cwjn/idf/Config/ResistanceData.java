package com.cwjn.idf.Config;

public class ResistanceData {

    private double[] resistanceValues = new double[5];

    public ResistanceData(double f, double w, double l, double m, double d) {
        resistanceValues[0] = f;
        resistanceValues[1] = w;
        resistanceValues[2] = l;
        resistanceValues[3] = m;
        resistanceValues[4] = d;
    }

    public double[] getResistanceValues() {
        return resistanceValues;
    }

}
