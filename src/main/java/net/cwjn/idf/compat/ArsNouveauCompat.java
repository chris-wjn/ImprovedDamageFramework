package net.cwjn.idf.compat;

import com.hollingsworth.arsnouveau.api.event.SpellDamageEvent;
import com.hollingsworth.arsnouveau.api.spell.AbstractCastMethod;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellSchools;
import com.hollingsworth.arsnouveau.common.spell.method.MethodOrbit;
import com.hollingsworth.arsnouveau.common.spell.method.MethodProjectile;
import com.hollingsworth.arsnouveau.common.spell.method.MethodTouch;
import net.cwjn.idf.damage.IDFIndirectEntityDamageSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.concurrent.atomic.AtomicInteger;

public class ArsNouveauCompat {

    public static void register() {
        MinecraftForge.EVENT_BUS.register(ArsNouveauCompat.class);
    }

    @SubscribeEvent
    public static void convertSpellDamage(SpellDamageEvent.Pre event) {
        DamageSource source = event.damageSource;
        SpellContext ctx = event.context;
        int[] schools = {0, 0, 0, 0};
        final String[][] damageClass = {{"none"}};
        ctx.getSpell().recipe.forEach(spellIngredient -> {
            if (spellIngredient.spellSchools.contains(SpellSchools.ELEMENTAL_FIRE)) schools[0]++;
            if (spellIngredient.spellSchools.contains(SpellSchools.ELEMENTAL_WATER)) schools[1]++;
            if (spellIngredient.spellSchools.contains(SpellSchools.ELEMENTAL_AIR)) schools[2]++;
            if (spellIngredient.spellSchools.contains(SpellSchools.ELEMENTAL_EARTH)) schools[3]++;
            if (spellIngredient instanceof AbstractCastMethod castMethod) {
                if (castMethod instanceof MethodTouch || castMethod instanceof MethodOrbit) damageClass[0][0] = "strike";
                else if (castMethod instanceof MethodProjectile) damageClass[0][0] = "pierce";
            }
        });
        int highest = 0;
        for (int i = 0; i < 4; ++i) {
            if (schools[i] > schools[highest]) highest = i;
        }
        switch (highest) {
            case 0: event.damageSource = new IDFIndirectEntityDamageSource(source.msgId, source.getDirectEntity(), event.caster,
                    1, 0, 0, 0, 0, 0, 0, 0, damageClass[0][0]);
            case 1: event.damageSource = new IDFIndirectEntityDamageSource(source.msgId, source.getDirectEntity(), event.caster,
                    0, 1, 0, 0, 0, 0, 0, 0, damageClass[0][0]);
            default:
                event.damageSource = new IDFIndirectEntityDamageSource(source.msgId, source.getDirectEntity(), event.caster,
                    0, 0, 0, 0, 0, 0, 0, 0, damageClass[0][0]);
        }
    }

}
