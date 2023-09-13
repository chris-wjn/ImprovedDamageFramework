package net.cwjn.idf.compat;

import com.hollingsworth.arsnouveau.api.event.SpellDamageEvent;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellSchools;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.concurrent.atomic.AtomicInteger;

public class ArsNouveauCompat {

    public static void register() {
        MinecraftForge.EVENT_BUS.register(ArsNouveauCompat.class);
    }

    @SubscribeEvent
    private static void convertSpellDamage(SpellDamageEvent.Pre event) {
        float amount = event.damage;
        SpellContext ctx = event.context;
        AtomicInteger fire = new AtomicInteger();
        AtomicInteger water = new AtomicInteger();
        AtomicInteger air = new AtomicInteger();
        AtomicInteger earth = new AtomicInteger();
        ctx.getSpell().recipe.forEach(spellIngredient -> {
            if (spellIngredient.spellSchools.contains(SpellSchools.ELEMENTAL_FIRE)) fire.getAndIncrement();
            if (spellIngredient.spellSchools.contains(SpellSchools.ELEMENTAL_WATER)) water.getAndIncrement();
            if (spellIngredient.spellSchools.contains(SpellSchools.ELEMENTAL_AIR)) air.getAndIncrement();
            if (spellIngredient.spellSchools.contains(SpellSchools.ELEMENTAL_EARTH)) earth.getAndIncrement();
        });
    }

}
