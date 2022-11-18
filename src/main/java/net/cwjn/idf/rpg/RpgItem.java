package net.cwjn.idf.rpg;

public interface RpgItem {

    Tier getTier();
    void setTier(Tier t);

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
        A(0.9),
        B(0.8),
        C(0.7),
        D(0.6),
        E(0.5),
        F(0.4);
        private double scalar;
        Tier(double scalar) {
            this.scalar = scalar;
        }
        public double getScalar() {
            return scalar;
        }
    }

}
