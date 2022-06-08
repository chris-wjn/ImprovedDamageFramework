package net.cwjn.idf.tetraIntegration;

import net.cwjn.idf.Attributes.AttributeRegistry;
import net.cwjn.idf.Enchantments.EnchantmentRegistry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import se.mickelus.tetra.blocks.workbench.gui.WorkbenchStatsGui;
import se.mickelus.tetra.gui.stats.bar.GuiStatBar;
import se.mickelus.tetra.gui.stats.getter.*;

import javax.annotation.ParametersAreNonnullByDefault;

import static se.mickelus.tetra.gui.stats.StatsHelper.barLength;
import static se.mickelus.tetra.gui.stats.StatsHelper.sum;

@ParametersAreNonnullByDefault
public class Tetra {
    @OnlyIn(Dist.CLIENT)
    public static void registerClient() {
        IStatGetter fireFlatGetter = new StatGetterEnchantmentLevel(EnchantmentRegistry.FIRE_DAMAGE_FLAT.get(), 1.0);
        IStatGetter waterFlatGetter = new StatGetterEnchantmentLevel(EnchantmentRegistry.WATER_DAMAGE_FLAT.get(), 1.0);
        IStatGetter lightningFlatGetter = new StatGetterEnchantmentLevel(EnchantmentRegistry.LIGHTNING_DAMAGE_FLAT.get(), 1.0);
        IStatGetter magicFlatGetter = new StatGetterEnchantmentLevel(EnchantmentRegistry.MAGIC_DAMAGE_FLAT.get(), 1.0);
        IStatGetter darkFlatGetter = new StatGetterEnchantmentLevel(EnchantmentRegistry.DARK_DAMAGE_FLAT.get(), 1.0);
        IStatGetter fireMultGetter = new StatGetterEnchantmentLevel(EnchantmentRegistry.FIRE_DAMAGE_MULT.get(), 5.0);
        IStatGetter waterMultGetter = new StatGetterEnchantmentLevel(EnchantmentRegistry.WATER_DAMAGE_MULT.get(), 5.0);
        IStatGetter lightningMultGetter = new StatGetterEnchantmentLevel(EnchantmentRegistry.LIGHTNING_DAMAGE_MULT.get(), 5.0);
        IStatGetter magicMultGetter = new StatGetterEnchantmentLevel(EnchantmentRegistry.MAGIC_DAMAGE_MULT.get(), 5.0);
        IStatGetter darkMultGetter = new StatGetterEnchantmentLevel(EnchantmentRegistry.DARK_DAMAGE_MULT.get(), 5.0);
        IStatGetter fireDamageGetter = sum(new StatGetterAttribute(AttributeRegistry.FIRE_DAMAGE.get()), fireFlatGetter);
        IStatGetter waterDamageGetter = sum(new StatGetterAttribute(AttributeRegistry.WATER_DAMAGE.get()), waterFlatGetter);
        IStatGetter lightningDamageGetter = sum(new StatGetterAttribute(AttributeRegistry.LIGHTNING_DAMAGE.get()), lightningFlatGetter);
        IStatGetter magicDamageGetter = sum(new StatGetterAttribute(AttributeRegistry.MAGIC_DAMAGE.get()), magicFlatGetter);
        IStatGetter darkDamageGetter = sum(new StatGetterAttribute(AttributeRegistry.DARK_DAMAGE.get()), darkFlatGetter);
        GuiStatBar fireDamage = new GuiStatBar(0, 0, barLength, "idf.stats.fire_damage",
                0, 40, false, fireDamageGetter, LabelGetterBasic.decimalLabel,
                new TooltipGetterDecimal("idf.stats.fire_damage.tooltip", fireDamageGetter));
        GuiStatBar waterDamage = new GuiStatBar(0, 0, barLength, "idf.stats.water_damage",
                0, 40, false, waterDamageGetter, LabelGetterBasic.decimalLabel,
                new TooltipGetterDecimal("idf.stats.water_damage.tooltip", waterDamageGetter));
        GuiStatBar lightningDamage = new GuiStatBar(0, 0, barLength, "idf.stats.lightning_damage",
                0, 40, false, lightningDamageGetter, LabelGetterBasic.decimalLabel,
                new TooltipGetterDecimal("idf.stats.lightning_damage.tooltip", lightningDamageGetter));
        GuiStatBar magicDamage = new GuiStatBar(0, 0, barLength, "idf.stats.magic_damage",
                0, 40, false, magicDamageGetter, LabelGetterBasic.decimalLabel,
                new TooltipGetterDecimal("idf.stats.magic_damage.tooltip", magicDamageGetter));
        GuiStatBar darkDamage = new GuiStatBar(0, 0, barLength, "idf.stats.dark_damage",
                0, 40, false, darkDamageGetter, LabelGetterBasic.decimalLabel,
                new TooltipGetterDecimal("idf.stats.dark_damage.tooltip", darkDamageGetter));
        GuiStatBar fireMultiplier = new GuiStatBar(0, 0, barLength, "idf.stats.fire_damage",
                0, 25, false, fireMultGetter, LabelGetterBasic.percentageLabel,
                new TooltipGetterPercentage("idf.stats.fire_damage.tooltip", fireMultGetter));
        GuiStatBar waterMultiplier = new GuiStatBar(0, 0, barLength, "idf.stats.water_damage",
                0, 25, false, waterMultGetter, LabelGetterBasic.percentageLabel,
                new TooltipGetterPercentage("idf.stats.water_damage.tooltip", waterMultGetter));
        GuiStatBar lightningMultiplier = new GuiStatBar(0, 0, barLength, "idf.stats.lightning_damage",
                0, 25, false, lightningMultGetter, LabelGetterBasic.percentageLabel,
                new TooltipGetterPercentage("idf.stats.lightning_damage.tooltip", lightningMultGetter));
        GuiStatBar magicMultiplier = new GuiStatBar(0, 0, barLength, "idf.stats.magic_damage",
                0, 25, false, magicMultGetter, LabelGetterBasic.percentageLabel,
                new TooltipGetterPercentage("idf.stats.magic_damage.tooltip", magicMultGetter));
        GuiStatBar darkMultiplier = new GuiStatBar(0, 0, barLength, "idf.stats.dark_damage",
                0, 25, false, darkMultGetter, LabelGetterBasic.percentageLabel,
                new TooltipGetterPercentage("idf.stats.dark_damage.tooltip", darkMultGetter));
        WorkbenchStatsGui.addBar(fireDamage);
        WorkbenchStatsGui.addBar(waterDamage);
        WorkbenchStatsGui.addBar(lightningDamage);
        WorkbenchStatsGui.addBar(magicDamage);
        WorkbenchStatsGui.addBar(darkDamage);
        WorkbenchStatsGui.addBar(fireMultiplier);
        WorkbenchStatsGui.addBar(waterMultiplier);
        WorkbenchStatsGui.addBar(lightningMultiplier);
        WorkbenchStatsGui.addBar(magicMultiplier);
        WorkbenchStatsGui.addBar(darkMultiplier);
    }

}
