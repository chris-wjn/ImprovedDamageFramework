package net.cwjn.idf.damage;

public interface IDFInterface {

    float getFire();
    float getWater();
    float getLightning();
    float getMagic();
    float getDark();
    float getHoly();
    float getPen();
    float getLifesteal();
    float getForce();
    float getKnockback();
    String getDamageClass();
    boolean isTrue();
    boolean isConversion();
    IDFInterface setIsConversion();
    IDFInterface setTrue();
    String getName();
    boolean hasDamage();

}
