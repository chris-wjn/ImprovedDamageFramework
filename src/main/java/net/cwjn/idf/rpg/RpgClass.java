package net.cwjn.idf.rpg;

import org.apache.commons.lang3.tuple.MutablePair;

public abstract class RpgClass {

    public MutablePair<Integer, Double> CONSTITUTION, STRENGTH, DEXTERITY, AGILITY, INTELLIGENCE, WISDOM, FAITH;

    public static String getTierForScalar(double d) {
        if (d > 0.9) {
            return "S";
        } else if (d > 0.8) {
            return "A";
        } else if (d > 0.7) {
            return "B";
        } else if (d > 0.6) {
            return "C";
        } else if (d > 0.5) {
            return "D";
        } else return "E";
    }

}
