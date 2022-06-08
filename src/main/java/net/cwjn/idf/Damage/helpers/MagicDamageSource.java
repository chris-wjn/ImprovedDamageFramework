package net.cwjn.idf.Damage.helpers;

import net.cwjn.idf.Damage.IDFConversionDamageSource;
import net.cwjn.idf.Damage.IDFDamageSource;

public class MagicDamageSource extends IDFConversionDamageSource {

    public MagicDamageSource(String msgId, float m, float p, String dc) {
        super(msgId, 0, 0, 0, m, 0, p, dc);
    }

}
