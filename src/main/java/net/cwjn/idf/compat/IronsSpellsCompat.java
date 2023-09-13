package net.cwjn.idf.compat;

import io.redspace.ironsspellbooks.damage.DamageSources;
import net.cwjn.idf.damage.IDFDamageSource;

public class IronsSpellsCompat {

    public static void register() {
        DamageSources.HEARTSTOP = new IDFDamageSource("heartstop", "none").setTrue();
        DamageSources.BLEED_DAMAGE = new IDFDamageSource("bleed_effect", 0, 0, 0, 0, 1, 0, 0, 0, "none").setIsConversion();
        DamageSources.CAULDRON = new IDFDamageSource("cauldron", 0, 0, 0, 0, 1, 0, 0, 0, "none").setIsConversion();
    }

}
