package net.cwjn.idf.Damage.helpers;

import net.cwjn.idf.Damage.IDFConversionDamageSource;
import net.cwjn.idf.Damage.IDFDamageSource;

public class WaterDamageSource extends IDFConversionDamageSource {

    public WaterDamageSource(String msgId, float w, float p, String dc) {
        super(msgId, 0, w, 0, 0, 0, p, dc);
    }

}
