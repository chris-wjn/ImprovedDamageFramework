package net.cwjn.idf.Damage.helpers;

import net.cwjn.idf.Damage.IDFConversionDamageSource;
import net.cwjn.idf.Damage.IDFDamageSource;

public class DarkDamageSource extends IDFConversionDamageSource {

    public DarkDamageSource(String msgId, float d, float p, String dc) {
        super(msgId, 0, 0, 0, 0, d, p, dc);
    }

}
