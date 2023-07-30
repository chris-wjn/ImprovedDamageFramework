package net.cwjn.idf.compat;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.cwjn.idf.attribute.IDFAttributes;
import net.cwjn.idf.attribute.IDFElement;
import net.cwjn.idf.data.CommonData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import se.mickelus.tetra.aspect.ItemAspect;
import se.mickelus.tetra.blocks.workbench.gui.WorkbenchStatsGui;
import se.mickelus.tetra.gui.stats.bar.GuiStatBar;
import se.mickelus.tetra.gui.stats.getter.*;
import se.mickelus.tetra.items.modular.IModularItem;
import se.mickelus.tetra.items.modular.ItemPredicateModular;
import se.mickelus.tetra.items.modular.ModularItem;
import se.mickelus.tetra.items.modular.impl.bow.ModularBowItem;
import se.mickelus.tetra.items.modular.impl.crossbow.ModularCrossbowItem;
import se.mickelus.tetra.module.ItemModule;
import se.mickelus.tetra.module.ItemUpgradeRegistry;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Objects;

import static net.cwjn.idf.data.CommonData.WEAPON_TAG;
import static se.mickelus.tetra.gui.stats.StatsHelper.barLength;

public class TetraCompat {

    private static final Gson gson = new Gson();
    private static final ItemPredicateModular isPierce = new ItemPredicateModular(gson.fromJson(new BufferedReader(new InputStreamReader(Objects.requireNonNull(TetraCompat.class.getClassLoader()
            .getResourceAsStream("data/idf/tetra_predicates/pierce.json")))), JsonObject.class));
    private static final ItemPredicateModular isSlash = new ItemPredicateModular(gson.fromJson(new BufferedReader(new InputStreamReader(Objects.requireNonNull(TetraCompat.class.getClassLoader()
            .getResourceAsStream("data/idf/tetra_predicates/slash.json")))), JsonObject.class));
    private static final ItemPredicateModular isStrike = new ItemPredicateModular(gson.fromJson(new BufferedReader(new InputStreamReader(Objects.requireNonNull(TetraCompat.class.getClassLoader()
            .getResourceAsStream("data/idf/tetra_predicates/strike.json")))), JsonObject.class));
    private static final ItemPredicateModular isPierceRight = new ItemPredicateModular(gson.fromJson(new BufferedReader(new InputStreamReader(Objects.requireNonNull(TetraCompat.class.getClassLoader()
            .getResourceAsStream("data/idf/tetra_predicates/pierce_right.json")))), JsonObject.class));
    private static final ItemPredicateModular isSlashRight = new ItemPredicateModular(gson.fromJson(new BufferedReader(new InputStreamReader(Objects.requireNonNull(TetraCompat.class.getClassLoader()
            .getResourceAsStream("data/idf/tetra_predicates/slash_right.json")))), JsonObject.class));
    private static final ItemPredicateModular isStrikeRight = new ItemPredicateModular(gson.fromJson(new BufferedReader(new InputStreamReader(Objects.requireNonNull(TetraCompat.class.getClassLoader()
            .getResourceAsStream("data/idf/tetra_predicates/strike_right.json")))), JsonObject.class));

    public static void register() {
        MinecraftForge.EVENT_BUS.register(TetraCompat.class);
    }

    @SubscribeEvent
    public static void onItemCrafted(PlayerEvent.ItemCraftedEvent event) {
        if (event.getCrafting().getItem() instanceof ModularItem) {
            ItemStack replacement = event.getCrafting();
            CompoundTag tag = event.getCrafting().getTag();
            if (tag == null) return;
            tag.putBoolean(CommonData.EQUIPMENT_TAG, true);
            IModularItem item = (IModularItem) replacement.getItem();
            ItemModule[] modules = item.getMajorModules(replacement);
            String dc = "strike";
            if (replacement.getItem() instanceof ModularBowItem || replacement.getItem() instanceof ModularCrossbowItem) {
                tag.putBoolean(CommonData.RANGED_TAG, true);
                dc = "pierce";
            } else {
                int sls = 0;
                int prc = 0;
                int str = 0;
                for (ItemModule module : modules) {
                    if (module.getAspects(replacement).getLevel(ItemAspect.throwable) > 0) tag.putBoolean(CommonData.THROWN_TAG, true);
                    sls += module.getAspects(replacement).getLevel(ItemAspect.edgedWeapon);
                    str += module.getAspects(replacement).getLevel(ItemAspect.bluntWeapon);
                    prc += module.getAspects(replacement).getLevel(ItemAspect.pointyWeapon);
                }
                int highest = Math.max(sls, Math.max(prc, str));
                if (highest == sls) dc = "slash";
                else if (highest == prc) dc = "pierce";
            }
            tag.putString(WEAPON_TAG, dc);
        }
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
        IStatGetter critChanceGetter = new StatGetterAttribute(IDFAttributes.CRIT_CHANCE.get());
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
        GuiStatBar critChance = new GuiStatBar(0, 0, barLength, "idf.stats.crit_chance",
                0, 100, false, critChanceGetter, LabelGetterBasic.percentageLabel,
                new TooltipGetterPercentage("idf.stats.crit_chance.tooltip", critChanceGetter));
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
        WorkbenchStatsGui.addBar(critChance);
        WorkbenchStatsGui.addBar(lifesteal);
    }

}
