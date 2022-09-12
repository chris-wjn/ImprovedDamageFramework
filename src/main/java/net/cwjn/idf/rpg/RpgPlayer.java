package net.cwjn.idf.rpg;

public interface RpgPlayer {

    int getLevel();

    //attribute scaling stats
    int getCons(); //health, defense, knockback resistance
    int getStr(); //physical scaling and requirements, damage, knockback
    int getDex(); //physical scaling and requirements, crit chance, attack speed
    int getAgl(); //physical scaling and requirements, movespeed, evasion chance
    int getIntel(); //elemental scaling and requirements, dark damage
    int getWis(); //elemental scaling and requirements, magic/water damage
    int getFth(); //elemental scaling and requirements, fire/lightning damage

    //gameplay related stats
    int getCrtv(); //CREATIVITY: restrict crafting of certain items?
    int getHarv(); //HARVESTING: block breaking speed, ability to mine in air and underwater


}
