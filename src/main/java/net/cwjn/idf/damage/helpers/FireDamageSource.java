package net.cwjn.idf.damage.helpers;

import net.cwjn.idf.damage.IDFDamageSource;

public class FireDamageSource extends IDFDamageSource {

    public FireDamageSource(String msgId, String dc) {
        super(msgId, 1, 0, 0, 0, 0, 0, dc);
        this.setIsConversion();
    }

}
