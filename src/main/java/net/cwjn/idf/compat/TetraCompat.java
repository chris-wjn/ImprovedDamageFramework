package net.cwjn.idf.compat;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.cwjn.idf.attribute.IDFAttributes;
import net.cwjn.idf.config.ItemModifier;
import net.cwjn.idf.capability.provider.ArrowHelperProvider;
import net.cwjn.idf.capability.provider.TridentHelperProvider;
import net.cwjn.idf.enchantment.IDFEnchantments;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import se.mickelus.tetra.blocks.workbench.gui.WorkbenchStatsGui;
import se.mickelus.tetra.gui.stats.bar.GuiStatBar;
import se.mickelus.tetra.gui.stats.getter.*;
import se.mickelus.tetra.items.modular.ItemModularHandheld;
import se.mickelus.tetra.items.modular.ItemPredicateModular;
import se.mickelus.tetra.items.modular.ModularItem;
import se.mickelus.tetra.items.modular.impl.bow.ModularBowItem;
import se.mickelus.tetra.items.modular.impl.crossbow.ModularCrossbowItem;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Objects;

import static se.mickelus.tetra.gui.stats.StatsHelper.barLength;
import static se.mickelus.tetra.gui.stats.StatsHelper.sum;

public class TetraCompat {

    private static final Gson gson = new Gson();
    private static final ItemPredicateModular isPierce = new ItemPredicateModular(gson.fromJson(new BufferedReader(new InputStreamReader(Objects.requireNonNull(ItemModifier.class.getClassLoader()
            .getResourceAsStream("data/idf/predicates/pierce.json")))), JsonObject.class));
    private static final ItemPredicateModular isSlash = new ItemPredicateModular(gson.fromJson(new BufferedReader(new InputStreamReader(Objects.requireNonNull(ItemModifier.class.getClassLoader()
            .getResourceAsStream("data/idf/predicates/slash.json")))), JsonObject.class));
    private static final ItemPredicateModular isStrike = new ItemPredicateModular(gson.fromJson(new BufferedReader(new InputStreamReader(Objects.requireNonNull(ItemModifier.class.getClassLoader()
            .getResourceAsStream("data/idf/predicates/strike.json")))), JsonObject.class));
    private static final ItemPredicateModular isPierceRight = new ItemPredicateModular(gson.fromJson(new BufferedReader(new InputStreamReader(Objects.requireNonNull(ItemModifier.class.getClassLoader()
            .getResourceAsStream("data/idf/predicates/pierce_right.json")))), JsonObject.class));
    private static final ItemPredicateModular isSlashRight = new ItemPredicateModular(gson.fromJson(new BufferedReader(new InputStreamReader(Objects.requireNonNull(ItemModifier.class.getClassLoader()
            .getResourceAsStream("data/idf/predicates/slash_right.json")))), JsonObject.class));
    private static final ItemPredicateModular isStrikeRight = new ItemPredicateModular(gson.fromJson(new BufferedReader(new InputStreamReader(Objects.requireNonNull(ItemModifier.class.getClassLoader()
            .getResourceAsStream("data/idf/predicates/strike_right.json")))), JsonObject.class));

    public static void init() {
        MinecraftForge.EVENT_BUS.register(TetraCompat.class);
    }

    public static void initClient() {
        registerClient();
    }

    @SubscribeEvent
    public static void onLivingEquipmentChange(LivingEquipmentChangeEvent event) {
        if (event.getSlot() == EquipmentSlot.MAINHAND) {
            if (event.getTo() == ItemStack.EMPTY) return;
            Item item = event.getTo().getItem();
            LivingEntity entity = event.getEntityLiving();
            if (item instanceof ModularBowItem || item instanceof ModularCrossbowItem) {
                entity.getCapability(ArrowHelperProvider.PROJECTILE_HELPER).ifPresent(h -> {
                    h.setFire((float) entity.getAttributeValue(IDFAttributes.FIRE_DAMAGE.get()));
                    h.setWater((float) entity.getAttributeValue(IDFAttributes.WATER_DAMAGE.get()));
                    h.setLightning((float) entity.getAttributeValue(IDFAttributes.LIGHTNING_DAMAGE.get()));
                    h.setMagic((float) entity.getAttributeValue(IDFAttributes.MAGIC_DAMAGE.get()));
                    h.setDark((float) entity.getAttributeValue(IDFAttributes.DARK_DAMAGE.get()));
                    h.setPen((float) entity.getAttributeValue(IDFAttributes.PENETRATING.get()));
                    h.setCrit((float) entity.getAttributeValue(IDFAttributes.CRIT_CHANCE.get()));
                    h.setLifesteal((float) entity.getAttributeValue(IDFAttributes.LIFESTEAL.get()));
                    h.setDamageClass(event.getTo().hasTag() ?
                            event.getTo().getTag().contains("idf.damage_class") ? event.getTo().getTag().getString("idf.damage_class") : "strike" : "strike");
                });
            }
            if (item instanceof ItemModularHandheld && Arrays.asList(((ItemModularHandheld) item).getMajorModuleKeys()).contains("trident/trident")) {
                entity.getCapability(TridentHelperProvider.PROJECTILE_HELPER).ifPresent(h -> {
                    h.setFire((float) entity.getAttributeValue(IDFAttributes.FIRE_DAMAGE.get()));
                    h.setWater((float) entity.getAttributeValue(IDFAttributes.WATER_DAMAGE.get()));
                    h.setLightning((float) entity.getAttributeValue(IDFAttributes.LIGHTNING_DAMAGE.get()));
                    h.setMagic((float) entity.getAttributeValue(IDFAttributes.MAGIC_DAMAGE.get()));
                    h.setDark((float) entity.getAttributeValue(IDFAttributes.DARK_DAMAGE.get()));
                    h.setPen((float) entity.getAttributeValue(IDFAttributes.PENETRATING.get()));
                    h.setCrit((float) entity.getAttributeValue(IDFAttributes.CRIT_CHANCE.get()));
                    h.setLifesteal((float) entity.getAttributeValue(IDFAttributes.LIFESTEAL.get()));
                    h.setDamageClass(event.getTo().hasTag() ?
                            event.getTo().getTag().contains("idf.damage_class") ? event.getTo().getTag().getString("idf.damage_class") : "strike" : "strike");
                });
            }
        }
    }

    @SubscribeEvent
    public static void ItemModifierEvent(ItemAttributeModifierEvent event) {
        ItemStack item = event.getItemStack();
        if (item.getItem() instanceof ModularItem) {
            item.getOrCreateTag().putBoolean("idf.equipment", true);
            if (isSlash.matches(item)) {
                item.getOrCreateTag().putString("idf.damage_class", "slash");
            } else if (isPierce.matches(item)) {
                item.getOrCreateTag().putString("idf.damage_class", "pierce");
            } else if (isStrike.matches(item)) {
                item.getOrCreateTag().putString("idf.damage_class", "strike");
            } else if (isSlashRight.matches(item)) {
                item.getOrCreateTag().putString("idf.damage_class", "slash");
            } else if (isPierceRight.matches(item)) {
                item.getOrCreateTag().putString("idf.damage_class", "pierce");
            } else if (isStrikeRight.matches(item)) {
                item.getOrCreateTag().putString("idf.damage_class", "strike");
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    private static void registerClient() {
        IStatGetter fireFlatGetter = new StatGetterEnchantmentLevel(IDFEnchantments.FIRE_DAMAGE_FLAT.get(), 1.0);
        IStatGetter waterFlatGetter = new StatGetterEnchantmentLevel(IDFEnchantments.WATER_DAMAGE_FLAT.get(), 1.0);
        IStatGetter lightningFlatGetter = new StatGetterEnchantmentLevel(IDFEnchantments.LIGHTNING_DAMAGE_FLAT.get(), 1.0);
        IStatGetter magicFlatGetter = new StatGetterEnchantmentLevel(IDFEnchantments.MAGIC_DAMAGE_FLAT.get(), 1.0);
        IStatGetter darkFlatGetter = new StatGetterEnchantmentLevel(IDFEnchantments.DARK_DAMAGE_FLAT.get(), 1.0);
        IStatGetter fireMultGetter = new StatGetterEnchantmentLevel(IDFEnchantments.FIRE_DAMAGE_MULT.get(), 5.0);
        IStatGetter waterMultGetter = new StatGetterEnchantmentLevel(IDFEnchantments.WATER_DAMAGE_MULT.get(), 5.0);
        IStatGetter lightningMultGetter = new StatGetterEnchantmentLevel(IDFEnchantments.LIGHTNING_DAMAGE_MULT.get(), 5.0);
        IStatGetter magicMultGetter = new StatGetterEnchantmentLevel(IDFEnchantments.MAGIC_DAMAGE_MULT.get(), 5.0);
        IStatGetter darkMultGetter = new StatGetterEnchantmentLevel(IDFEnchantments.DARK_DAMAGE_MULT.get(), 5.0);
        IStatGetter fireDamageGetter = sum(new StatGetterAttribute(IDFAttributes.FIRE_DAMAGE.get()), fireFlatGetter);
        IStatGetter waterDamageGetter = sum(new StatGetterAttribute(IDFAttributes.WATER_DAMAGE.get()), waterFlatGetter);
        IStatGetter lightningDamageGetter = sum(new StatGetterAttribute(IDFAttributes.LIGHTNING_DAMAGE.get()), lightningFlatGetter);
        IStatGetter magicDamageGetter = sum(new StatGetterAttribute(IDFAttributes.MAGIC_DAMAGE.get()), magicFlatGetter);
        IStatGetter darkDamageGetter = sum(new StatGetterAttribute(IDFAttributes.DARK_DAMAGE.get()), darkFlatGetter);
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
        WorkbenchStatsGui.addBar(fireMultiplier);
        WorkbenchStatsGui.addBar(waterMultiplier);
        WorkbenchStatsGui.addBar(lightningMultiplier);
        WorkbenchStatsGui.addBar(magicMultiplier);
        WorkbenchStatsGui.addBar(darkMultiplier);
        WorkbenchStatsGui.addBar(pen);
        WorkbenchStatsGui.addBar(critChance);
        WorkbenchStatsGui.addBar(lifesteal);
    }

}
