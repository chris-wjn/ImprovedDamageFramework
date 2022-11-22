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

    public boolean compare(RpgClass clazz) {
        return this.CONSTITUTION.getLeft() >= clazz.getCONSTITUTION().getLeft()
                && this.STRENGTH.getLeft() >= clazz.getSTRENGTH().getLeft()
                && this.DEXTERITY.getLeft() >= clazz.getDEXTERITY().getLeft()
                && this.AGILITY.getLeft() >= clazz.getAGILITY().getLeft()
                && this.INTELLIGENCE.getLeft() >= clazz.INTELLIGENCE.getLeft()
                && this.WISDOM.getLeft() >= clazz.WISDOM.getLeft()
                && this.FAITH.getLeft() >= clazz.FAITH.getLeft();
    }

    public MutablePair<Integer, Double> getCONSTITUTION() {
        return CONSTITUTION;
    }

    public void setCONSTITUTION(MutablePair<Integer, Double> CONSTITUTION) {
        this.CONSTITUTION = CONSTITUTION;
    }

    public MutablePair<Integer, Double> getSTRENGTH() {
        return STRENGTH;
    }

    public void setSTRENGTH(MutablePair<Integer, Double> STRENGTH) {
        this.STRENGTH = STRENGTH;
    }

    public MutablePair<Integer, Double> getDEXTERITY() {
        return DEXTERITY;
    }

    public void setDEXTERITY(MutablePair<Integer, Double> DEXTERITY) {
        this.DEXTERITY = DEXTERITY;
    }

    public MutablePair<Integer, Double> getAGILITY() {
        return AGILITY;
    }

    public void setAGILITY(MutablePair<Integer, Double> AGILITY) {
        this.AGILITY = AGILITY;
    }

    public MutablePair<Integer, Double> getINTELLIGENCE() {
        return INTELLIGENCE;
    }

    public void setINTELLIGENCE(MutablePair<Integer, Double> INTELLIGENCE) {
        this.INTELLIGENCE = INTELLIGENCE;
    }

    public MutablePair<Integer, Double> getWISDOM() {
        return WISDOM;
    }

    public void setWISDOM(MutablePair<Integer, Double> WISDOM) {
        this.WISDOM = WISDOM;
    }

    public MutablePair<Integer, Double> getFAITH() {
        return FAITH;
    }

    public void setFAITH(MutablePair<Integer, Double> FAITH) {
        this.FAITH = FAITH;
    }

}
