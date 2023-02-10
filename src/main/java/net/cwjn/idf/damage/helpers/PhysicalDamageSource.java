package net.cwjn.idf.damage.helpers;

import net.cwjn.idf.damage.IDFDamageSource;

public class PhysicalDamageSource extends IDFDamageSource {

    public PhysicalDamageSource(String msgId, float p, String dc) {
        super(msgId, 0, 0, 0, 0, 0, p, 0, 0, dc);
        this.setIsConversion();
    }

}
