package net.cwjn.idf.rpg.player;

import java.util.List;
import java.util.UUID;

public interface RpgPlayer {

    int getLevel();
    void setLevel(int i);

    int getMaxBonfires();
    void setMaxBonfires(int i);

    List<UUID> getBonfires();
    void addBonfire(UUID id);
    void removeBonfire(UUID id);

    //attribute scaling stats
    int getCons(); //health, defense, knockback resistance
    int getStr(); //physical scaling and requirements, damage, knockback
    int getDex(); //physical scaling and requirements, crit chance, attack speed
    int getAgl(); //physical scaling and requirements, movespeed, evasion chance
    int getInt(); //elemental scaling and requirements, dark damage
    int getWis(); //elemental scaling and requirements, magic/water damage
    int getFth(); //elemental scaling and requirements, fire/lightning damage

    void setCons(int i);
    void setStr(int i);
    void setDex(int i);
    void setAgl(int i);
    void setInt(int i);
    void setWis(int i);
    void setFth(int i);

    void incrementCons(int i);
    void incrementStr(int i);
    void incrementDex(int i);
    void incrementAgl(int i);
    void incrementInt(int i);
    void incrementWis(int i);
    void incrementFth(int i);

}
