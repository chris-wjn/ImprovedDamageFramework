package net.cwjn.idf.compat;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.cwjn.idf.api.event.OnItemStackCreatedEvent;
import net.cwjn.idf.attribute.IDFAttributes;
import net.cwjn.idf.capability.provider.ArrowHelperProvider;
import net.cwjn.idf.capability.provider.TridentHelperProvider;
import net.cwjn.idf.data.CommonData;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.EventBus;
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

public class TetraCompat implements CompatClass {

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

    public void register() {
        MinecraftForge.EVENT_BUS.register(TetraCompat.class);
    }

    public void registerClient() {
        initClient();
    }

    @SubscribeEvent
    public static void onLivingUseItemEvent(LivingEntityUseItemEvent.Start event) {
        ItemStack itemStack = event.getItem();
        Item item = itemStack.getItem();
        LivingEntity entity = event.getEntity();
        if (item instanceof ModularBowItem || item instanceof ModularCrossbowItem) {
            entity.getCapability(ArrowHelperProvider.PROJECTILE_HELPER).ifPresent(h -> {
                h.setFire((float) entity.getAttributeValue(IDFAttributes.FIRE_DAMAGE.get()));
                h.setWater((float) entity.getAttributeValue(IDFAttributes.WATER_DAMAGE.get()));
                h.setLightning((float) entity.getAttributeValue(IDFAttributes.LIGHTNING_DAMAGE.get()));
                h.setMagic((float) entity.getAttributeValue(IDFAttributes.MAGIC_DAMAGE.get()));
                h.setDark((float) entity.getAttributeValue(IDFAttributes.DARK_DAMAGE.get()));
                h.setPhys((float) entity.getAttributeValue(Attributes.ATTACK_DAMAGE));
                h.setPen((float) entity.getAttributeValue(IDFAttributes.PENETRATING.get()));
                h.setCrit((float) entity.getAttributeValue(IDFAttributes.CRIT_CHANCE.get()));
                h.setLifesteal((float) entity.getAttributeValue(IDFAttributes.LIFESTEAL.get()));
                h.setWeight((float) entity.getAttributeValue(IDFAttributes.FORCE.get()));
                h.setDamageClass(itemStack.hasTag() ?
                        itemStack.getTag().contains("idf.damage_class") ? itemStack.getTag().getString("idf.damage_class") : "pierce" : "pierce");
            });
        } else if (item instanceof ItemModularHandheld && Arrays.asList(((ItemModularHandheld) item).getMajorModuleKeys()).contains("trident/trident")) {
            entity.getCapability(TridentHelperProvider.PROJECTILE_HELPER).ifPresent(h -> {
                h.setFire((float) entity.getAttributeValue(IDFAttributes.FIRE_DAMAGE.get()));
                h.setWater((float) entity.getAttributeValue(IDFAttributes.WATER_DAMAGE.get()));
                h.setLightning((float) entity.getAttributeValue(IDFAttributes.LIGHTNING_DAMAGE.get()));
                h.setMagic((float) entity.getAttributeValue(IDFAttributes.MAGIC_DAMAGE.get()));
                h.setDark((float) entity.getAttributeValue(IDFAttributes.DARK_DAMAGE.get()));
                h.setPhys((float) entity.getAttributeValue(Attributes.ATTACK_DAMAGE));
                h.setPen((float) entity.getAttributeValue(IDFAttributes.PENETRATING.get()));
                h.setCrit((float) entity.getAttributeValue(IDFAttributes.CRIT_CHANCE.get()));
                h.setLifesteal((float) entity.getAttributeValue(IDFAttributes.LIFESTEAL.get()));
                h.setWeight((float) entity.getAttributeValue(IDFAttributes.FORCE.get()));
                h.setDamageClass(itemStack.hasTag() ?
                        itemStack.getTag().contains("idf.damage_class") ? itemStack.getTag().getString("idf.damage_class") : "pierce" : "pierce");
            });
        }
    }

    @SubscribeEvent
    public static void ItemModifierEvent(OnItemStackCreatedEvent event) {
        ItemStack item = event.getItemStack();
        if (item.getItem() instanceof ModularItem) {
            item.getOrCreateTag().putBoolean(CommonData.EQUIPMENT_TAG, true);
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
    private static void initClient() {
        IStatGetter fireDamageGetter = new StatGetterAttribute(IDFAttributes.FIRE_DAMAGE.get());
        IStatGetter waterDamageGetter = new StatGetterAttribute(IDFAttributes.WATER_DAMAGE.get());
        IStatGetter lightningDamageGetter = new StatGetterAttribute(IDFAttributes.LIGHTNING_DAMAGE.get());
        IStatGetter magicDamageGetter = new StatGetterAttribute(IDFAttributes.MAGIC_DAMAGE.get());
        IStatGetter darkDamageGetter = new StatGetterAttribute(IDFAttributes.DARK_DAMAGE.get());
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
        WorkbenchStatsGui.addBar(pen);
        WorkbenchStatsGui.addBar(critChance);
        WorkbenchStatsGui.addBar(lifesteal);
    }

}
