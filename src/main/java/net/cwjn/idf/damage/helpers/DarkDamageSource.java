package net.cwjn.idf.damage.helpers;

import net.cwjn.idf.damage.IDFDamageSource;

public class DarkDamageSource extends IDFDamageSource {

    public DarkDamageSource(String msgId, String dc) {
        super(msgId, 0, 0, 0, 0, 1, 0, 0, 0, dc);
        this.setIsConversion();
    }

}
