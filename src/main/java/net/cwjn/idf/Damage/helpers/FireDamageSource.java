package net.cwjn.idf.Damage.helpers;

import net.cwjn.idf.Damage.IDFConversionDamageSource;
import net.cwjn.idf.Damage.IDFDamageSource;

public class FireDamageSource extends IDFConversionDamageSource {

    public FireDamageSource(String msgId, float f, float p, String dc) {
        super(msgId, f, 0, 0, 0, 0, p, dc);
    }

}
