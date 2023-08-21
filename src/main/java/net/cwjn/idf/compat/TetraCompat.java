package net.cwjn.idf.compat;

import net.cwjn.idf.attribute.IDFAttributes;
import net.cwjn.idf.attribute.IDFElement;
import net.cwjn.idf.data.CommonData;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import se.mickelus.tetra.blocks.workbench.gui.WorkbenchStatsGui;
import se.mickelus.tetra.effect.ItemEffect;
import se.mickelus.tetra.gui.stats.bar.GuiStatBar;
import se.mickelus.tetra.gui.stats.getter.*;
import se.mickelus.tetra.items.modular.IModularItem;
import se.mickelus.tetra.items.modular.ModularItem;

import static se.mickelus.tetra.gui.stats.StatsHelper.barLength;

public class TetraCompat {

    public static void register() {
        MinecraftForge.EVENT_BUS.register(TetraCompat.class);
    }

    public static void registerClient() {
        initClient();
    }

    @OnlyIn(Dist.CLIENT)
    private static void initClient() {
        IStatGetter fireDamageGetter = new StatGetterAttribute(IDFAttributes.FIRE_DAMAGE.get());
        IStatGetter waterDamageGetter = new StatGetterAttribute(IDFAttributes.WATER_DAMAGE.get());
        IStatGetter lightningDamageGetter = new StatGetterAttribute(IDFAttributes.LIGHTNING_DAMAGE.get());
        IStatGetter magicDamageGetter = new StatGetterAttribute(IDFAttributes.MAGIC_DAMAGE.get());
        IStatGetter darkDamageGetter = new StatGetterAttribute(IDFAttributes.DARK_DAMAGE.get());
        IStatGetter holyDamageGetter = new StatGetterAttribute(IDFElement.HOLY.damage);
        IStatGetter accuracyGetter = new StatGetterAttribute(IDFAttributes.ACCURACY.get());
        IStatGetter forceGetter = new StatGetterAttribute(IDFAttributes.FORCE.get());
        IStatGetter penGetter = new StatGetterAttribute(IDFAttributes.PENETRATING.get());
        IStatGetter lifestealGetter = new StatGetterAttribute(IDFAttributes.LIFESTEAL.get());
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
                new TooltipGetterDecimal("idf.stats.holy_damage.tooltip", darkDamageGetter));
        GuiStatBar holyDamage = new GuiStatBar(0, 0, barLength, "idf.stats.dark_damage",
                0, 40, false, holyDamageGetter, LabelGetterBasic.decimalLabel,
                new TooltipGetterDecimal("idf.stats.holy_damage.tooltip", holyDamageGetter));
        GuiStatBar accuracy = new GuiStatBar(0, 0, barLength, "idf.stats.accuracy",
                0, 100, false, accuracyGetter, LabelGetterBasic.decimalLabel,
                new TooltipGetterDecimal("idf.stats.accuracy.tooltip", accuracyGetter));
        GuiStatBar force = new GuiStatBar(0, 0, barLength, "idf.stats.force",
                0, 40, false, forceGetter, LabelGetterBasic.decimalLabel,
                new TooltipGetterDecimal("idf.stats.force.tooltip", forceGetter));
        GuiStatBar pen = new GuiStatBar(0, 0, barLength, "idf.stats.pen",
                0, 100, false, penGetter, LabelGetterBasic.percentageLabel,
                new TooltipGetterPercentage("idf.stats.pen.tooltip", penGetter));
        GuiStatBar lifesteal = new GuiStatBar(0, 0, barLength, "idf.stats.lifesteal",
                0, 100, false, lifestealGetter, LabelGetterBasic.percentageLabel,
                new TooltipGetterPercentage("idf.stats.lifesteal.tooltip", lifestealGetter));
        WorkbenchStatsGui.addBar(fireDamage);
        WorkbenchStatsGui.addBar(waterDamage);
        WorkbenchStatsGui.addBar(lightningDamage);
        WorkbenchStatsGui.addBar(magicDamage);
        WorkbenchStatsGui.addBar(darkDamage);
        WorkbenchStatsGui.addBar(holyDamage);
        WorkbenchStatsGui.addBar(accuracy);
        WorkbenchStatsGui.addBar(force);
        WorkbenchStatsGui.addBar(pen);
        WorkbenchStatsGui.addBar(lifesteal);
    }

    @SubscribeEvent
    public static void onAttributeEvent(ItemAttributeModifierEvent event) {
        ItemStack itemStack = event.getItemStack();
        if (itemStack.getItem() instanceof IModularItem) {
            if (itemStack.getTag().getBoolean(CommonData.RANGED_TAG)) return;
            IModularItem item = (IModularItem) itemStack.getItem();
            int critChance = item.getEffectLevel(itemStack, ItemEffect.criticalStrike);
            float critDmg = item.getEffectEfficiency(itemStack, ItemEffect.criticalStrike)*100 - 150;
            if (critChance > 0) event.addModifier(IDFAttributes.CRIT_CHANCE.get(), new AttributeModifier("tetra_crit_chance", critChance, AttributeModifier.Operation.ADDITION));
            if (critDmg > 0) event.addModifier(IDFAttributes.CRIT_DAMAGE.get(), new AttributeModifier("tetra_crit_damage", critDmg, AttributeModifier.Operation.ADDITION));
        }
    }

}
