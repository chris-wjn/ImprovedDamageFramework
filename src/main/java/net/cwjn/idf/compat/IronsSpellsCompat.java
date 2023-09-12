package net.cwjn.idf.compat;

import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.effect.EvasionEffect;
import net.cwjn.idf.damage.IDFDamageSource;
import net.cwjn.idf.event.LogicalEvents;

public class IronsSpellsCompat {

    public static void register() {
        EvasionEffect.excludeDamageSources.stream().map((e) -> e.msgId).forEach((i) -> LogicalEvents.undodgables.add(i));
        DamageSources.HEARTSTOP = new IDFDamageSource("heartstop", "none").setTrue();
        DamageSources.BLEED_DAMAGE = new IDFDamageSource("bleed_effect", 0, 0, 0, 0, 1, 0, 0, 0, "none").setIsConversion();
        DamageSources.CAULDRON = new IDFDamageSource("cauldron", 0, 0, 0, 0, 1, 0, 0, 0, "none").setIsConversion();
    }

}
