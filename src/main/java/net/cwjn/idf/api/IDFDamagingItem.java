package net.cwjn.idf.api;

public interface IDFDamagingItem {

    double getFireDamage();
    double getWaterDamage();
    double getLightningDamage();
    double getMagicDamage();
    double getDarkDamage();
    double getCrit();
    double getWeight();
    double getPen();
    double getLifesteal();
    String getDamageClass();

}
