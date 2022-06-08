package net.cwjn.idf.Damage.helpers;

import net.cwjn.idf.Damage.IDFConversionDamageSource;

public class PhysicalDamageSource extends IDFConversionDamageSource {

    public PhysicalDamageSource(String msgId, float p, String dc) {
        super(msgId, 0, 0, 0, 0, 0, p, dc);
    }

}
