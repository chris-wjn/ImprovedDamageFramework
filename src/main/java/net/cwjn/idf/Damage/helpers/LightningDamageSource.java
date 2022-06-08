package net.cwjn.idf.Damage.helpers;

import net.cwjn.idf.Damage.IDFConversionDamageSource;
import net.cwjn.idf.Damage.IDFDamageSource;

public class LightningDamageSource extends IDFConversionDamageSource {

    public LightningDamageSource(String msgId, float l, float p, String dc) {
        super(msgId, 0, 0, l, 0, 0, p, dc);
    }

}
