package net.cwjn.idf.attribute;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.cwjn.idf.config.json.DamageData;
import net.cwjn.idf.config.json.JSONHandler;
import net.cwjn.idf.config.json.ResistanceData;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.util.thread.SidedThreadGroups;
import se.mickelus.tetra.items.modular.ItemPredicateModular;
import se.mickelus.tetra.items.modular.ModularItem;

import java.io.*;
import java.util.Objects;
import java.util.UUID;

@Mod.EventBusSubscriber
public class ItemModifier {

    public static final UUID baseFireDamage = UUID.randomUUID();
    public static final UUID baseWaterDamage = UUID.randomUUID();
    public static final UUID baseLightningDamage = UUID.randomUUID();
    public static final UUID baseMagicDamage = UUID.randomUUID();
    public static final UUID baseDarkDamage = UUID.randomUUID();
    public static final UUID baseLifesteal = UUID.randomUUID();
    public static final UUID basePenetration = UUID.randomUUID();
    public static final UUID baseCrit = UUID.randomUUID();
    public static final UUID baseFireResistance = UUID.randomUUID();
    public static final UUID baseWaterResistance = UUID.randomUUID();
    public static final UUID baseLightningResistance = UUID.randomUUID();
    public static final UUID baseMagicResistance = UUID.randomUUID();
    public static final UUID baseDarkResistance = UUID.randomUUID();
    public static final UUID baseEvasion = UUID.randomUUID();
    public static final UUID baseMaxHP = UUID.randomUUID();
    public static final UUID baseMovespeed = UUID.randomUUID();
    public static final UUID baseKnockbackResistance = UUID.randomUUID();
    public static final UUID baseLuck = UUID.randomUUID();
    public static final UUID baseStrikeMult = UUID.randomUUID();
    public static final UUID basePierceMult = UUID.randomUUID();
    public static final UUID baseCrushMult = UUID.randomUUID();
    public static final UUID baseSlashMult = UUID.randomUUID();
    public static final UUID baseGenericMult = UUID.randomUUID();
    private static final Gson gson = new Gson();
    private static ItemStack cachedItem;
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


    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void ItemModifierEvent(ItemAttributeModifierEvent event) {
        ItemStack item = event.getItemStack(); //get the item from the event
        if (LivingEntity.getEquipmentSlotForItem(item) == event.getSlotType()) {
            if (Thread.currentThread().getThreadGroup() == SidedThreadGroups.CLIENT) {
                if (cachedItem == item) {
                    return;
                }
            }
            if (item.getItem() instanceof ModularItem) {
                cachedItem = item;
                item.getOrCreateTag().putBoolean("idf.equipment", true);
                item.getOrCreateTag().putBoolean("idf.has_damage", true);
                if (isSlash.matches(item)) {
                    item.getOrCreateTag().putString("idf.damage_class", "_slash");
                } else if (isPierce.matches(item)) {
                    item.getOrCreateTag().putString("idf.damage_class", "pierce");
                } else if (isStrike.matches(item)) {
                    item.getOrCreateTag().putString("idf.damage_class", "strike");
                } else if (isSlashRight.matches(item)) {
                    item.getOrCreateTag().putString("idf.damage_class", "_slash");
                } else if (isPierceRight.matches(item)) {
                    item.getOrCreateTag().putString("idf.damage_class", "pierce");
                } else if (isStrikeRight.matches(item)) {
                    item.getOrCreateTag().putString("idf.damage_class", "strike");
                }
            }
            if (JSONHandler.damageMap.containsKey(item.getItem().getRegistryName())) {
                cachedItem = item;
                item.getOrCreateTag().putBoolean("idf.equipment", true);
                item.getOrCreateTag().putBoolean("idf.has_damage", true);
                item.getOrCreateTag().putString("idf.damage_class", JSONHandler.damageMap.get(item.getItem().getRegistryName()).getDamageClass());
                DamageData data = JSONHandler.getDamageData(item.getItem().getRegistryName());
                event.addModifier(AttributeRegistry.FIRE_DAMAGE.get(), new AttributeModifier(baseFireDamage, () -> "base_fire_damage", data.getDamageValues()[0], AttributeModifier.Operation.ADDITION));
                event.addModifier(AttributeRegistry.WATER_DAMAGE.get(), new AttributeModifier(baseWaterDamage, () -> "base_water_damage", data.getDamageValues()[1], AttributeModifier.Operation.ADDITION));
                event.addModifier(AttributeRegistry.LIGHTNING_DAMAGE.get(), new AttributeModifier(baseLightningDamage, () -> "base_lightning_damage", data.getDamageValues()[2], AttributeModifier.Operation.ADDITION));
                event.addModifier(AttributeRegistry.MAGIC_DAMAGE.get(), new AttributeModifier(baseMagicDamage, () -> "base_magic_damage", data.getDamageValues()[3], AttributeModifier.Operation.ADDITION));
                event.addModifier(AttributeRegistry.DARK_DAMAGE.get(), new AttributeModifier(baseDarkDamage, () -> "base_dark_damage", data.getDamageValues()[4], AttributeModifier.Operation.ADDITION));
                event.addModifier(AttributeRegistry.LIFESTEAL.get(), new AttributeModifier(baseLifesteal, () -> "base_lifesteal", data.getAuxiliary()[0], AttributeModifier.Operation.ADDITION));
                event.addModifier(AttributeRegistry.PENETRATING.get(), new AttributeModifier(basePenetration, () -> "base_penetration", data.getAuxiliary()[1], AttributeModifier.Operation.ADDITION));
                event.addModifier(AttributeRegistry.CRIT_CHANCE.get(), new AttributeModifier(baseCrit, () -> "base_crit_chance", data.getAuxiliary()[2], AttributeModifier.Operation.ADDITION));
            }
            if (JSONHandler.resistanceMap.containsKey(item.getItem().getRegistryName())) {
                cachedItem = item;
                item.getOrCreateTag().putBoolean("idf.equipment", true);
                item.getOrCreateTag().putBoolean("idf.has_resistance", true);
                ResistanceData data = JSONHandler.getResistanceData(item.getItem().getRegistryName());
                event.addModifier(AttributeRegistry.FIRE_RESISTANCE.get(), new AttributeModifier(baseFireResistance, () -> "base_fire_resistance", data.getResistanceValues()[0], AttributeModifier.Operation.ADDITION));
                event.addModifier(AttributeRegistry.WATER_RESISTANCE.get(), new AttributeModifier(baseWaterResistance, () -> "base_water_resistance", data.getResistanceValues()[1], AttributeModifier.Operation.ADDITION));
                event.addModifier(AttributeRegistry.LIGHTNING_RESISTANCE.get(), new AttributeModifier(baseLightningResistance, () -> "base_lightning_resistance", data.getResistanceValues()[2], AttributeModifier.Operation.ADDITION));
                event.addModifier(AttributeRegistry.MAGIC_RESISTANCE.get(), new AttributeModifier(baseMagicResistance, () -> "base_magic_resistance", data.getResistanceValues()[3], AttributeModifier.Operation.ADDITION));
                event.addModifier(AttributeRegistry.DARK_RESISTANCE.get(), new AttributeModifier(baseDarkResistance, () -> "base_dark_resistance", data.getResistanceValues()[4], AttributeModifier.Operation.ADDITION));
                event.addModifier(AttributeRegistry.EVASION.get(), new AttributeModifier(baseEvasion, () -> "base_evasion", data.getAuxiliary()[0], AttributeModifier.Operation.ADDITION));
                event.addModifier(Attributes.MAX_HEALTH, new AttributeModifier(baseMaxHP, () -> "base_max_hp", data.getAuxiliary()[1], AttributeModifier.Operation.ADDITION));
                event.addModifier(Attributes.MOVEMENT_SPEED, new AttributeModifier(baseMovespeed, () -> "base_movespeed", data.getAuxiliary()[2], AttributeModifier.Operation.ADDITION));
                event.addModifier(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(baseKnockbackResistance, () -> "base_knockback_resistance", data.getAuxiliary()[3], AttributeModifier.Operation.ADDITION));
                event.addModifier(Attributes.LUCK, new AttributeModifier(baseLuck, () -> "base_luck", data.getAuxiliary()[4], AttributeModifier.Operation.ADDITION));
                event.addModifier(AttributeRegistry.STRIKE_MULT.get(), new AttributeModifier(baseStrikeMult, () -> "base_strike", data.getMultipliers()[0], AttributeModifier.Operation.ADDITION));
                event.addModifier(AttributeRegistry.PIERCE_MULT.get(), new AttributeModifier(basePierceMult, () -> "base_pierce", data.getMultipliers()[1], AttributeModifier.Operation.ADDITION));
                event.addModifier(AttributeRegistry.CRUSH_MULT.get(), new AttributeModifier(baseCrushMult, () -> "base_crush", data.getMultipliers()[2], AttributeModifier.Operation.ADDITION));
                event.addModifier(AttributeRegistry.SLASH_MULT.get(), new AttributeModifier(baseSlashMult, () -> "base_slash", data.getMultipliers()[3], AttributeModifier.Operation.ADDITION));
                event.addModifier(AttributeRegistry.GENERIC_MULT.get(), new AttributeModifier(baseGenericMult, () -> "base_generic", data.getMultipliers()[4], AttributeModifier.Operation.ADDITION));
            }
        }
    }

}
