package com.cwjn.idf.Config;

public class ResistanceData {

    private double[] resistanceValues = new double[5];
    private double[] auxiliary = new double[1];

    public ResistanceData(double f, double w, double l, double m, double d, double e) {
        resistanceValues[0] = f;
        resistanceValues[1] = w;
        resistanceValues[2] = l;
        resistanceValues[3] = m;
        resistanceValues[4] = d;
        auxiliary[0] = e;
    }

    public double[] getResistanceValues() {
        return resistanceValues;
    }

    public double[] getAuxiliary() {
        return auxiliary;
    }
}
