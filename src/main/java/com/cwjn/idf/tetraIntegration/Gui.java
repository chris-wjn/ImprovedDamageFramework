package com.cwjn.idf.tetraIntegration;

import com.cwjn.idf.Attributes.AttributeRegistry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import se.mickelus.tetra.blocks.workbench.gui.WorkbenchStatsGui;
import se.mickelus.tetra.gui.stats.bar.GuiStatBar;
import se.mickelus.tetra.gui.stats.getter.*;

import javax.annotation.ParametersAreNonnullByDefault;

import static se.mickelus.tetra.gui.stats.StatsHelper.barLength;

@ParametersAreNonnullByDefault
public class Gui {
    @OnlyIn(Dist.CLIENT)
    public static void register() {
        IStatGetter fireDamageGetter = new StatGetterAttribute(AttributeRegistry.FIRE_DAMAGE.get(), true, true);
        GuiStatBar fireDamage = new GuiStatBar(0, 0, barLength, "idf.stats.fire_damage",
                0, 40, false, fireDamageGetter, LabelGetterBasic.decimalLabel,
                new TooltipGetterDecimal("idf.stats.fire_damage.tooltip", fireDamageGetter));
        IStatGetter waterDamageGetter = new StatGetterAttribute(AttributeRegistry.WATER_DAMAGE.get(), true, true);
        GuiStatBar waterDamage = new GuiStatBar(0, 0, barLength, "idf.stats.water_damage",
                0, 40, false, waterDamageGetter, LabelGetterBasic.decimalLabel,
                new TooltipGetterDecimal("idf.stats.water_damage.tooltip", waterDamageGetter));
        IStatGetter lightningDamageGetter = new StatGetterAttribute(AttributeRegistry.LIGHTNING_DAMAGE.get(), true, true);
        GuiStatBar lightningDamage = new GuiStatBar(0, 0, barLength, "idf.stats.lightning_damage",
                0, 40, false, lightningDamageGetter, LabelGetterBasic.decimalLabel,
                new TooltipGetterDecimal("idf.stats.lightning_damage.tooltip", lightningDamageGetter));
        IStatGetter magicDamageGetter = new StatGetterAttribute(AttributeRegistry.MAGIC_DAMAGE.get(), true, true);
        GuiStatBar magicDamage = new GuiStatBar(0, 0, barLength, "idf.stats.magic_damage",
                0, 40, false, magicDamageGetter, LabelGetterBasic.decimalLabel,
                new TooltipGetterDecimal("idf.stats.magic_damage.tooltip", magicDamageGetter));
        IStatGetter darkDamageGetter = new StatGetterAttribute(AttributeRegistry.DARK_DAMAGE.get(), true, true);
        GuiStatBar darkDamage = new GuiStatBar(0, 0, barLength, "idf.stats.dark_damage",
                0, 40, false, darkDamageGetter, LabelGetterBasic.decimalLabel,
                new TooltipGetterDecimal("idf.stats.dark_damage.tooltip", darkDamageGetter));
        WorkbenchStatsGui.addBar(fireDamage);
        WorkbenchStatsGui.addBar(waterDamage);
        WorkbenchStatsGui.addBar(lightningDamage);
        WorkbenchStatsGui.addBar(magicDamage);
        WorkbenchStatsGui.addBar(darkDamage);
    }
}
