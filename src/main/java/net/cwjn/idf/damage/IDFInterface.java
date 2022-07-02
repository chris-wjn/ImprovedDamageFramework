package net.cwjn.idf.damage;

public interface IDFInterface {

    float getFire();
    float getWater();
    float getLightning();
    float getMagic();
    float getDark();
    float getPen();
    float getLifesteal();
    String getDamageClass();
    boolean isTrue();
    boolean isConversion();
    IDFInterface setIsConversion();
    IDFInterface setTrue();
    String getMsgId();

}
