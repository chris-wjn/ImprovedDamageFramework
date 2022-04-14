package com.cwjn.idf.tetraIntegration;

import com.cwjn.idf.Attributes.AttributeRegistry;
import net.minecraft.world.entity.ai.attributes.Attributes;
import se.mickelus.tetra.gui.stats.bar.GuiStatBar;
import se.mickelus.tetra.gui.stats.bar.GuiStatIndicator;
import se.mickelus.tetra.gui.stats.getter.*;

import javax.annotation.ParametersAreNonnullByDefault;

import static se.mickelus.tetra.gui.stats.GuiStats.attackDamageGetter;
import static se.mickelus.tetra.gui.stats.GuiStats.sharpnessGetter;
import static se.mickelus.tetra.gui.stats.StatsHelper.barLength;
import static se.mickelus.tetra.gui.stats.StatsHelper.sum;

@ParametersAreNonnullByDefault
public class Gui {
    public static final IStatGetter fireDamageGetter = new StatGetterAttribute(AttributeRegistry.FIRE_DAMAGE.get());
    public static final GuiStatBar fireDamage = new GuiStatBar(0, 0, barLength, "idf.stats.fire_damage",
            0, 40, false, fireDamageGetter, LabelGetterBasic.decimalLabel,
            new TooltipGetterDecimal("idf.stats.fire_damage.tooltip", fireDamageGetter));
}
