package net.cwjn.idf.rpg;

public interface RpgItem {

    int getConsReq(); //health, defense, knockback resistance
    int getStrReq(); //physical scaling and requirements, damage, knockback
    int getDexReq(); //physical scaling and requirements, crit chance, attack speed
    int getAglReq(); //physical scaling and requirements, movespeed, evasion chance
    int getIntReq(); //elemental scaling and requirements, dark damage
    int getWisReq(); //elemental scaling and requirements, magic/water damage
    int getFthReq(); //elemental scaling and requirements, fire/lightning damage

    void setConsReq(int i);
    void setStrReq(int i);
    void setDexReq(int i);
    void setAglReq(int i);
    void setIntReq(int i);
    void setWisReq(int i);
    void setFthReq(int i);

    enum Tier {
        S(1.0),
        A(0.8),
        B(0.6),
        C(0.4),
        D(0.2);
        private double scalar;
        Tier(double scalar) {
            this.scalar = scalar;
        }
        public double getScalar() {
            return scalar;
        }
    }

}
