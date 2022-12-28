package net.cwjn.idf.util;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.JsonArray;
import com.mojang.blaze3d.vertex.PoseStack;
import net.cwjn.idf.attribute.IDFAttributes;
import net.cwjn.idf.config.json.data.ArmourData;
import net.cwjn.idf.config.json.data.ItemData;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.contents.LiteralContents;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.UUID;

import static net.cwjn.idf.ImprovedDamageFramework.FONT_ICONS;
import static net.cwjn.idf.ImprovedDamageFramework.FONT_INDICATORS;
import static net.cwjn.idf.util.UUIDs.*;
import static net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.*;

public class Util {

    private Util() {throw new IllegalAccessError("Util class");}
    private static final Style ICON = Style.EMPTY.withFont(FONT_ICONS);
    private static final Style INDICATOR = Style.EMPTY.withFont(FONT_INDICATORS);
    private static final DecimalFormat attributeFormat = new DecimalFormat("#.##");
    private static final DecimalFormat hundredFormat = new DecimalFormat("###");
    private static final UUID[] ARMOR_MODIFIER_UUID_PER_SLOT = new UUID[]{UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"), UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"), UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"), UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150")};
    private static final UUID[] ARMOR_MODIFIER_UUID_PER_SLOT_OP1 = new UUID[]{UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()};
    private static final UUID[] ARMOR_MODIFIER_UUID_PER_SLOT_OP2 = new UUID[]{UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()};

    public static MutableComponent withColor(MutableComponent text, int color) {
        return text.withStyle(text.getStyle().withColor(net.minecraft.network.chat.TextColor.fromRgb(color & 0xFFFFFF)));
    }

    public static MutableComponent withColor(MutableComponent text, Color color) {
        return withColor(text, color.getColor());
    }

    public static MutableComponent withColor(MutableComponent text, ChatFormatting color) {
        int colorCode = color.getColor() != null ? color.getColor() : Color.VALUE_WHITE;
        return withColor(text, colorCode);
    }

    public static MutableComponent translationComponent(String key) {
        return MutableComponent.create(new TranslatableContents(key));
    }

    public static MutableComponent translationComponent(String main, Object... additions) {
        return MutableComponent.create(new TranslatableContents(main, additions));
    }

    public static MutableComponent textComponent(String text) {
        return MutableComponent.create(new LiteralContents(text));
    }

    public static MutableComponent numericalAttributeComponent(double val) {
        if (val > 99) return MutableComponent.create(new LiteralContents(hundredFormat.format(val)));
        BigDecimal d = new BigDecimal(val);
        int integralDigits = d.toBigInteger().toString().length();
        d = d.setScale(2 - integralDigits, RoundingMode.HALF_EVEN);
        return MutableComponent.create(new LiteralContents(d.toString()));
    }

    public static void drawCenteredString(Font font, PoseStack matrix, Component component, float x, float y, int colour) {
        font.draw(matrix, component, (x - (float) font.width(component) / 2), y, colour);
    }

    public static void drawCenteredPercentageString(Font font, PoseStack matrix, MutableComponent component, float x, float y, int colour) {
        float textX = (x - (float) font.width(component) / 2);
        MutableComponent newComp = component.append("%");
        font.draw(matrix, newComp, textX, y, colour);
    }

    public static ResourceLocation getEntityRegistryName(EntityType<?> type) {
        return ForgeRegistries.ENTITY_TYPES.getKey(type);
    }

    public static ResourceLocation getItemRegistryName(Item item) {
        return ForgeRegistries.ITEMS.getKey(item);
    }

    public static Item getItemFromRegistryName(ResourceLocation loc) {
        return ForgeRegistries.ITEMS.getValue(loc);
    }

    public static void buildWeaponAttributesOp2(ImmutableMultimap.Builder<Attribute, AttributeModifier> builder, ItemData data) {
        if (data == null) return;
        putIfExists(builder, Attributes.ATTACK_DAMAGE, new AttributeModifier(TOTAL_MULTIPLY_ATTACK_DAMAGE_UUID, "Weapon modifier", data.physicalDamage(), MULTIPLY_TOTAL));
        putIfExists(builder, Attributes.ATTACK_SPEED, new AttributeModifier(TOTAL_MULTIPLY_ATTACK_SPEED_UUID, "Weapon modifier", data.attackSpeed(), MULTIPLY_TOTAL));
        putIfExists(builder, IDFAttributes.FIRE_DAMAGE.get(), new AttributeModifier(TOTAL_MULTIPLY_FIRE_DAMAGE_UUID, "Weapon modifier", data.fireDamage(), MULTIPLY_TOTAL));
        putIfExists(builder, IDFAttributes.WATER_DAMAGE.get(), new AttributeModifier(TOTAL_MULTIPLY_WATER_DAMAGE_UUID, "Weapon modifier", data.waterDamage(), MULTIPLY_TOTAL));
        putIfExists(builder, IDFAttributes.LIGHTNING_DAMAGE.get(), new AttributeModifier(TOTAL_MULTIPLY_LIGHTNING_DAMAGE_UUID, "Weapon modifier", data.lightningDamage(), MULTIPLY_TOTAL));
        putIfExists(builder, IDFAttributes.MAGIC_DAMAGE.get(), new AttributeModifier(TOTAL_MULTIPLY_MAGIC_DAMAGE_UUID, "Weapon modifier", data.magicDamage(), MULTIPLY_TOTAL));
        putIfExists(builder, IDFAttributes.DARK_DAMAGE.get(), new AttributeModifier(TOTAL_MULTIPLY_DARK_DAMAGE_UUID, "Weapon modifier", data.darkDamage(), MULTIPLY_TOTAL));
        putIfExists(builder, IDFAttributes.CRIT_CHANCE.get(), new AttributeModifier(TOTAL_MULTIPLY_CRITICAL_CHANCE_UUID, "Weapon modifier", data.criticalChance(), MULTIPLY_TOTAL));
        putIfExists(builder, IDFAttributes.FORCE.get(), new AttributeModifier(TOTAL_MULTIPLY_FORCE_UUID, "Weapon modifier", data.force(), MULTIPLY_TOTAL));
        putIfExists(builder, IDFAttributes.PENETRATING.get(), new AttributeModifier(TOTAL_MULTIPLY_ARMOUR_PENETRATION_UUID, "Weapon modifier", data.armourPenetration(), MULTIPLY_TOTAL));
        putIfExists(builder, IDFAttributes.LIFESTEAL.get(), new AttributeModifier(TOTAL_MULTIPLY_LIFESTEAL_UUID, "Weapon modifier", data.lifesteal(), MULTIPLY_TOTAL));
        putIfExists(builder, Attributes.ATTACK_KNOCKBACK, new AttributeModifier(TOTAL_MULTIPLY_KNOCKBACK_UUID, "Weapon modifier", data.knockback(), MULTIPLY_TOTAL));
        putIfExists(builder, Attributes.ARMOR_TOUGHNESS, new AttributeModifier(TOTAL_MULTIPLY_DEFENSE_UUID, "Weapon modifier", data.defense(), MULTIPLY_TOTAL));
        putIfExists(builder, Attributes.ARMOR, new AttributeModifier(TOTAL_MULTIPLY_PHYSICAL_RESISTANCE_UUID, "Weapon modifier", data.physicalResistance(), MULTIPLY_TOTAL));
        putIfExists(builder, IDFAttributes.FIRE_RESISTANCE.get(), new AttributeModifier(TOTAL_MULTIPLY_FIRE_RESISTANCE_UUID, "Weapon modifier", data.fireResistance(), MULTIPLY_TOTAL));
        putIfExists(builder, IDFAttributes.WATER_RESISTANCE.get(), new AttributeModifier(TOTAL_MULTIPLY_WATER_RESISTANCE_UUID, "Weapon modifier", data.waterResistance(), MULTIPLY_TOTAL));
        putIfExists(builder, IDFAttributes.LIGHTNING_RESISTANCE.get(), new AttributeModifier(TOTAL_MULTIPLY_LIGHTNING_RESISTANCE_UUID, "Weapon modifier", data.lightningResistance(), MULTIPLY_TOTAL));
        putIfExists(builder, IDFAttributes.MAGIC_RESISTANCE.get(), new AttributeModifier(TOTAL_MULTIPLY_MAGIC_RESISTANCE_UUID, "Weapon modifier", data.magicResistance(), MULTIPLY_TOTAL));
        putIfExists(builder, IDFAttributes.DARK_RESISTANCE.get(), new AttributeModifier(TOTAL_MULTIPLY_DARK_RESISTANCE_UUID, "Weapon modifier", data.darkResistance(), MULTIPLY_TOTAL));
        putIfExists(builder, IDFAttributes.EVASION.get(), new AttributeModifier(TOTAL_MULTIPLY_EVASION_UUID, "Weapon modifier", data.evasion(), MULTIPLY_TOTAL));
        putIfExists(builder, Attributes.MAX_HEALTH, new AttributeModifier(TOTAL_MULTIPLY_MAXHP_UUID, "Weapon modifier", data.maxHP(), MULTIPLY_TOTAL));
        putIfExists(builder, Attributes.MOVEMENT_SPEED, new AttributeModifier(TOTAL_MULTIPLY_MOVESPEED_UUID, "Weapon modifier", data.movespeed(), MULTIPLY_TOTAL));
        putIfExists(builder, Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(TOTAL_MULTIPLY_KNOCKBACK_RESISTANCE_UUID, "Weapon modifier", data.knockbackResistance(), MULTIPLY_TOTAL));
        putIfExists(builder, Attributes.LUCK, new AttributeModifier(TOTAL_MULTIPLY_LUCK_UUID, "Weapon modifier", data.luck(), MULTIPLY_TOTAL));
        putIfExists(builder, IDFAttributes.STRIKE_MULT.get(), new AttributeModifier(TOTAL_MULTIPLY_STRIKE_MULTIPLIER_UUID, "Weapon modifier", data.strikeMultiplier(), MULTIPLY_TOTAL));
        putIfExists(builder, IDFAttributes.PIERCE_MULT.get(), new AttributeModifier(TOTAL_MULTIPLY_PIERCE_MULTIPLIER_UUID, "Weapon modifier", data.pierceMultiplier(), MULTIPLY_TOTAL));
        putIfExists(builder, IDFAttributes.SLASH_MULT.get(), new AttributeModifier(TOTAL_MULTIPLY_SLASH_MULTIPLIER_UUID, "Weapon modifier", data.slashMultiplier(), MULTIPLY_TOTAL));
        putIfExists(builder, IDFAttributes.CRUSH_MULT.get(), new AttributeModifier(TOTAL_MULTIPLY_CRUSH_MULTIPLIER_UUID, "Weapon modifier", data.crushMultiplier(), MULTIPLY_TOTAL));
        putIfExists(builder, IDFAttributes.GENERIC_MULT.get(), new AttributeModifier(TOTAL_MULTIPLY_GENERIC_MULTIPLIER_UUID, "Weapon modifier", data.genericMultiplier(), MULTIPLY_TOTAL));
    }

    public static void buildArmourAttributesOp0(ImmutableMultimap.Builder<Attribute, AttributeModifier> builder, EquipmentSlot slot, ArmourData data,
                                                double defaultArmour, double defaultToughness, double defaultKBR) {
        UUID uuid = ARMOR_MODIFIER_UUID_PER_SLOT[slot.getIndex()];
        putIfExists(builder, Attributes.ATTACK_DAMAGE, new AttributeModifier(uuid, "Base physical damage", data.physicalDamage(), ADDITION));
        putIfExists(builder, IDFAttributes.FIRE_DAMAGE.get(), new AttributeModifier(uuid, "Base fire damage", data.fireDamage(), ADDITION));
        putIfExists(builder, IDFAttributes.WATER_DAMAGE.get(), new AttributeModifier(uuid, "Base water damage", data.waterDamage(), ADDITION));
        putIfExists(builder, IDFAttributes.LIGHTNING_DAMAGE.get(), new AttributeModifier(uuid, "Base lightning damage", data.lightningDamage(), ADDITION));
        putIfExists(builder, IDFAttributes.MAGIC_DAMAGE.get(), new AttributeModifier(uuid, "Base magic damage", data.magicDamage(), ADDITION));
        putIfExists(builder, IDFAttributes.DARK_DAMAGE.get(), new AttributeModifier(uuid, "Base dark damage", data.darkDamage(), ADDITION));
        putIfExists(builder, IDFAttributes.LIFESTEAL.get(), new AttributeModifier(uuid, "Base lifesteal", data.lifesteal(), ADDITION));
        putIfExists(builder, IDFAttributes.PENETRATING.get(), new AttributeModifier(uuid, "Base armour penetration", data.armourPenetration(), ADDITION));
        putIfExists(builder, IDFAttributes.CRIT_CHANCE.get(), new AttributeModifier(uuid, "Base critical chance", data.criticalChance(), ADDITION));
        putIfExists(builder, IDFAttributes.FORCE.get(), new AttributeModifier(uuid, "Base force", data.force(), ADDITION));
        putIfExists(builder, Attributes.ATTACK_KNOCKBACK, new AttributeModifier(uuid, "Base knockback", data.knockback(), ADDITION));
        putIfExists(builder, Attributes.ATTACK_SPEED, new AttributeModifier(uuid, "Base attack speed", data.attackSpeed(), ADDITION));
        putIfExists(builder, Attributes.ARMOR, new AttributeModifier(uuid, "Armor modifier", defaultArmour + data.physicalResistance(), ADDITION));
        putIfExists(builder, Attributes.ARMOR_TOUGHNESS, new AttributeModifier(uuid, "Armor toughness", defaultToughness + data.defense(), ADDITION));
        putIfExists(builder, IDFAttributes.FIRE_RESISTANCE.get(), new AttributeModifier(uuid, "Base fire resistance", data.fireResistance(), ADDITION));
        putIfExists(builder, IDFAttributes.WATER_RESISTANCE.get(), new AttributeModifier(uuid, "Base water resistance", data.waterResistance(), ADDITION));
        putIfExists(builder, IDFAttributes.LIGHTNING_RESISTANCE.get(), new AttributeModifier(uuid, "Base lightning resistance", data.lightningResistance(), ADDITION));
        putIfExists(builder, IDFAttributes.MAGIC_RESISTANCE.get(), new AttributeModifier(uuid, "Base magic resistance", data.magicResistance(), ADDITION));
        putIfExists(builder, IDFAttributes.DARK_RESISTANCE.get(), new AttributeModifier(uuid, "Base dark resistance", data.darkResistance(), ADDITION));
        putIfExists(builder, Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(uuid, "Armor knockback resistance", defaultKBR + data.knockbackResistance(), ADDITION));
        putIfExists(builder, Attributes.MAX_HEALTH, new AttributeModifier(uuid, "Base max health", data.maxHP(), ADDITION));
        putIfExists(builder, Attributes.MOVEMENT_SPEED, new AttributeModifier(uuid, "Base movespeed", data.movespeed(), ADDITION));
        putIfExists(builder, Attributes.LUCK, new AttributeModifier(uuid, "Base luck", data.luck(), ADDITION));
        putIfExists(builder, IDFAttributes.EVASION.get(), new AttributeModifier(uuid, "Base evasion", data.evasion(), ADDITION));
        putIfExists(builder, IDFAttributes.STRIKE_MULT.get(), new AttributeModifier(uuid, "Base strike multiplier", data.strikeMultiplier(), ADDITION));
        putIfExists(builder, IDFAttributes.PIERCE_MULT.get(), new AttributeModifier(uuid, "Base pierce multiplier", data.pierceMultiplier(), ADDITION));
        putIfExists(builder, IDFAttributes.SLASH_MULT.get(), new AttributeModifier(uuid, "Base slash multiplier", data.slashMultiplier(), ADDITION));
        putIfExists(builder, IDFAttributes.CRUSH_MULT.get(), new AttributeModifier(uuid, "Base crush multiplier", data.crushMultiplier(), ADDITION));
        putIfExists(builder, IDFAttributes.GENERIC_MULT.get(), new AttributeModifier(uuid, "Base generic multiplier", data.genericMultiplier(), ADDITION));
    }
    public static void buildArmourAttributesOp1(ImmutableMultimap.Builder<Attribute, AttributeModifier> builder, EquipmentSlot slot, ItemData data) {
        UUID uuid = ARMOR_MODIFIER_UUID_PER_SLOT_OP1[slot.getIndex()];
        putIfExists(builder, Attributes.ATTACK_DAMAGE, new AttributeModifier(uuid, "Base multiply physical damage", data.physicalDamage(), MULTIPLY_BASE));
        putIfExists(builder, IDFAttributes.FIRE_DAMAGE.get(), new AttributeModifier(uuid, "Base multiply fire damage", data.fireDamage(), MULTIPLY_BASE));
        putIfExists(builder, IDFAttributes.WATER_DAMAGE.get(), new AttributeModifier(uuid, "Base multiply water damage", data.waterDamage(), MULTIPLY_BASE));
        putIfExists(builder, IDFAttributes.LIGHTNING_DAMAGE.get(), new AttributeModifier(uuid, "Base multiply lightning damage", data.lightningDamage(), MULTIPLY_BASE));
        putIfExists(builder, IDFAttributes.MAGIC_DAMAGE.get(), new AttributeModifier(uuid, "Base multiply magic damage", data.magicDamage(), MULTIPLY_BASE));
        putIfExists(builder, IDFAttributes.DARK_DAMAGE.get(), new AttributeModifier(uuid, "Base multiply dark damage", data.darkDamage(), MULTIPLY_BASE));
        putIfExists(builder, IDFAttributes.LIFESTEAL.get(), new AttributeModifier(uuid, "Base multiply lifesteal", data.lifesteal(), MULTIPLY_BASE));
        putIfExists(builder, IDFAttributes.PENETRATING.get(), new AttributeModifier(uuid, "Base multiply armour penetration", data.armourPenetration(), MULTIPLY_BASE));
        putIfExists(builder, IDFAttributes.CRIT_CHANCE.get(), new AttributeModifier(uuid, "Base multiply critical chance", data.criticalChance(), MULTIPLY_BASE));
        putIfExists(builder, IDFAttributes.FORCE.get(), new AttributeModifier(uuid, "Base multiply force", data.force(), MULTIPLY_BASE));
        putIfExists(builder, Attributes.ATTACK_KNOCKBACK, new AttributeModifier(uuid, "Base multiply knockback", data.knockback(), MULTIPLY_BASE));
        putIfExists(builder, Attributes.ATTACK_SPEED, new AttributeModifier(uuid, "Base multiply attack speed", data.attackSpeed(), MULTIPLY_BASE));
        putIfExists(builder, Attributes.ARMOR, new AttributeModifier(uuid, "Armor modifier base multiply", data.physicalResistance(), MULTIPLY_BASE));
        putIfExists(builder, Attributes.ARMOR_TOUGHNESS, new AttributeModifier(uuid, "Armor toughness base multiply", data.defense(), MULTIPLY_BASE));
        putIfExists(builder, IDFAttributes.FIRE_RESISTANCE.get(), new AttributeModifier(uuid, "Base multiply fire resistance", data.fireResistance(), MULTIPLY_BASE));
        putIfExists(builder, IDFAttributes.WATER_RESISTANCE.get(), new AttributeModifier(uuid, "Base multiply water resistance", data.waterResistance(), MULTIPLY_BASE));
        putIfExists(builder, IDFAttributes.LIGHTNING_RESISTANCE.get(), new AttributeModifier(uuid, "Base multiply lightning resistance", data.lightningResistance(), MULTIPLY_BASE));
        putIfExists(builder, IDFAttributes.MAGIC_RESISTANCE.get(), new AttributeModifier(uuid, "Base multiply magic resistance", data.magicResistance(), MULTIPLY_BASE));
        putIfExists(builder, IDFAttributes.DARK_RESISTANCE.get(), new AttributeModifier(uuid, "Base multiply dark resistance", data.darkResistance(), MULTIPLY_BASE));
        putIfExists(builder, Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(uuid, "Armor knockback resistance", data.knockbackResistance(), MULTIPLY_BASE));
        putIfExists(builder, Attributes.MAX_HEALTH, new AttributeModifier(uuid, "Base multiply max health", data.maxHP(), MULTIPLY_BASE));
        putIfExists(builder, Attributes.MOVEMENT_SPEED, new AttributeModifier(uuid, "Base multiply movespeed", data.movespeed(), MULTIPLY_BASE));
        putIfExists(builder, Attributes.LUCK, new AttributeModifier(uuid, "Base multiply luck", data.luck(), MULTIPLY_BASE));
        putIfExists(builder, IDFAttributes.EVASION.get(), new AttributeModifier(uuid, "Base multiply evasion", data.evasion(), MULTIPLY_BASE));
        putIfExists(builder, IDFAttributes.STRIKE_MULT.get(), new AttributeModifier(uuid, "Base multiply strike multiplier", data.strikeMultiplier(), MULTIPLY_BASE));
        putIfExists(builder, IDFAttributes.PIERCE_MULT.get(), new AttributeModifier(uuid, "Base multiply pierce multiplier", data.pierceMultiplier(), MULTIPLY_BASE));
        putIfExists(builder, IDFAttributes.SLASH_MULT.get(), new AttributeModifier(uuid, "Base multiply slash multiplier", data.slashMultiplier(), MULTIPLY_BASE));
        putIfExists(builder, IDFAttributes.CRUSH_MULT.get(), new AttributeModifier(uuid, "Base multiply crush multiplier", data.crushMultiplier(), MULTIPLY_BASE));
        putIfExists(builder, IDFAttributes.GENERIC_MULT.get(), new AttributeModifier(uuid, "Base multiply generic multiplier", data.genericMultiplier(), MULTIPLY_BASE));
    }

    public static void buildArmourAttributesOp2(ImmutableMultimap.Builder<Attribute, AttributeModifier> builder, EquipmentSlot slot, ItemData data) {
        UUID uuid = ARMOR_MODIFIER_UUID_PER_SLOT_OP2[slot.getIndex()];
        putIfExists(builder, Attributes.ATTACK_DAMAGE, new AttributeModifier(uuid, "Total multiply physical damage", data.physicalDamage(), MULTIPLY_TOTAL));
        putIfExists(builder, IDFAttributes.FIRE_DAMAGE.get(), new AttributeModifier(uuid, "Total multiply fire damage", data.fireDamage(), MULTIPLY_TOTAL));
        putIfExists(builder, IDFAttributes.WATER_DAMAGE.get(), new AttributeModifier(uuid, "Total multiply water damage", data.waterDamage(), MULTIPLY_TOTAL));
        putIfExists(builder, IDFAttributes.LIGHTNING_DAMAGE.get(), new AttributeModifier(uuid, "Total multiply lightning damage", data.lightningDamage(), MULTIPLY_TOTAL));
        putIfExists(builder, IDFAttributes.MAGIC_DAMAGE.get(), new AttributeModifier(uuid, "Total multiply magic damage", data.magicDamage(), MULTIPLY_TOTAL));
        putIfExists(builder, IDFAttributes.DARK_DAMAGE.get(), new AttributeModifier(uuid, "Total multiply dark damage", data.darkDamage(), MULTIPLY_TOTAL));
        putIfExists(builder, IDFAttributes.LIFESTEAL.get(), new AttributeModifier(uuid, "Total multiply lifesteal", data.lifesteal(), MULTIPLY_TOTAL));
        putIfExists(builder, IDFAttributes.PENETRATING.get(), new AttributeModifier(uuid, "Total multiply armour penetration", data.armourPenetration(), MULTIPLY_TOTAL));
        putIfExists(builder, IDFAttributes.CRIT_CHANCE.get(), new AttributeModifier(uuid, "Total multiply critical chance", data.criticalChance(), MULTIPLY_TOTAL));
        putIfExists(builder, IDFAttributes.FORCE.get(), new AttributeModifier(uuid, "Total multiply force", data.force(), MULTIPLY_TOTAL));
        putIfExists(builder, Attributes.ATTACK_KNOCKBACK, new AttributeModifier(uuid, "Total multiply knockback", data.knockback(), MULTIPLY_TOTAL));
        putIfExists(builder, Attributes.ATTACK_SPEED, new AttributeModifier(uuid, "Total multiply attack speed", data.attackSpeed(), MULTIPLY_TOTAL));
        putIfExists(builder, Attributes.ARMOR, new AttributeModifier(uuid, "Armor modifier total multiply", data.physicalResistance(), MULTIPLY_TOTAL));
        putIfExists(builder, Attributes.ARMOR_TOUGHNESS, new AttributeModifier(uuid, "Armor toughness total multiply", data.defense(), MULTIPLY_TOTAL));
        putIfExists(builder, IDFAttributes.FIRE_RESISTANCE.get(), new AttributeModifier(uuid, "Total multiply fire resistance", data.fireResistance(), MULTIPLY_TOTAL));
        putIfExists(builder, IDFAttributes.WATER_RESISTANCE.get(), new AttributeModifier(uuid, "Total multiply water resistance", data.waterResistance(), MULTIPLY_TOTAL));
        putIfExists(builder, IDFAttributes.LIGHTNING_RESISTANCE.get(), new AttributeModifier(uuid, "Total multiply lightning resistance", data.lightningResistance(), MULTIPLY_TOTAL));
        putIfExists(builder, IDFAttributes.MAGIC_RESISTANCE.get(), new AttributeModifier(uuid, "Total multiply magic resistance", data.magicResistance(), MULTIPLY_TOTAL));
        putIfExists(builder, IDFAttributes.DARK_RESISTANCE.get(), new AttributeModifier(uuid, "Total multiply dark resistance", data.darkResistance(), MULTIPLY_TOTAL));
        putIfExists(builder, Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(uuid, "Armor knockback resistance", data.knockbackResistance(), MULTIPLY_TOTAL));
        putIfExists(builder, Attributes.MAX_HEALTH, new AttributeModifier(uuid, "Total multiply max health", data.maxHP(), MULTIPLY_TOTAL));
        putIfExists(builder, Attributes.MOVEMENT_SPEED, new AttributeModifier(uuid, "Total multiply movespeed", data.movespeed(), MULTIPLY_TOTAL));
        putIfExists(builder, Attributes.LUCK, new AttributeModifier(uuid, "Total multiply luck", data.luck(), MULTIPLY_TOTAL));
        putIfExists(builder, IDFAttributes.EVASION.get(), new AttributeModifier(uuid, "Total multiply evasion", data.evasion(), MULTIPLY_TOTAL));
        putIfExists(builder, IDFAttributes.STRIKE_MULT.get(), new AttributeModifier(uuid, "Total multiply strike multiplier", data.strikeMultiplier(), MULTIPLY_TOTAL));
        putIfExists(builder, IDFAttributes.PIERCE_MULT.get(), new AttributeModifier(uuid, "Total multiply pierce multiplier", data.pierceMultiplier(), MULTIPLY_TOTAL));
        putIfExists(builder, IDFAttributes.SLASH_MULT.get(), new AttributeModifier(uuid, "Total multiply slash multiplier", data.slashMultiplier(), MULTIPLY_TOTAL));
        putIfExists(builder, IDFAttributes.CRUSH_MULT.get(), new AttributeModifier(uuid, "Total multiply crush multiplier", data.crushMultiplier(), MULTIPLY_TOTAL));
        putIfExists(builder, IDFAttributes.GENERIC_MULT.get(), new AttributeModifier(uuid, "Total multiply generic multiplier", data.genericMultiplier(), MULTIPLY_TOTAL));
    }
    
    private static void putIfExists(ImmutableMultimap.Builder<Attribute, AttributeModifier> builder, Attribute a, AttributeModifier am) {
        if (am.getAmount() != 0) {
            builder.put(a, am);
        }
    }

    public static float pBPS(double attribute) {
        BigDecimal x = new BigDecimal(attribute * 43.178 - 0.02141);
        x = x.setScale(1, RoundingMode.HALF_UP);
        return x.floatValue();
    }

    public static float mBPS(double attribute) {
        BigDecimal x = new BigDecimal(attribute * 9.60845544);
        x = x.setScale(1, RoundingMode.HALF_UP);
        return x.floatValue();
    }

    public static int getItemBorderType(String dc, Multimap<Attribute, AttributeModifier> map) {
        double f = 0, w = 0, l = 0, m = 0, d = 0, p = 2;
        for (Map.Entry<Attribute, AttributeModifier> entry : map.entries()) {
            if (entry.getKey() == IDFAttributes.FIRE_DAMAGE.get()) {
                if (entry.getValue().getOperation() == ADDITION) {
                    f += entry.getValue().getAmount();
                } else {
                    f += entry.getValue().getAmount() * 10;
                }
            } else if (entry.getKey() == IDFAttributes.WATER_DAMAGE.get()) {
                if (entry.getValue().getOperation() == ADDITION) {
                    w += entry.getValue().getAmount();
                } else {
                    w += entry.getValue().getAmount() * 10;
                }
            } else if (entry.getKey() == IDFAttributes.LIGHTNING_DAMAGE.get()) {
                if (entry.getValue().getOperation() == ADDITION) {
                    l += entry.getValue().getAmount();
                } else {
                    l += entry.getValue().getAmount() * 10;
                }
            } else if (entry.getKey() == IDFAttributes.MAGIC_DAMAGE.get()) {
                if (entry.getValue().getOperation() == ADDITION) {
                    m += entry.getValue().getAmount();
                } else {
                    m += entry.getValue().getAmount() * 10;
                }
            } else if (entry.getKey() == IDFAttributes.DARK_DAMAGE.get()) {
                if (entry.getValue().getOperation() == ADDITION) {
                    d += entry.getValue().getAmount();
                } else {
                    d += entry.getValue().getAmount() * 10;
                }
            } else if (entry.getKey() == Attributes.ATTACK_DAMAGE) {
                if (entry.getValue().getOperation() == ADDITION) {
                    p += entry.getValue().getAmount();
                } else {
                    p += entry.getValue().getAmount() * 10;
                }
            }
        }
        double[] dv = new double[] {f, w, l, m, d, p};
        int index = 5;
        double highest = 0.0D;
        for (int i = 0; i < 5; ++i) {
            if (dv[i] > highest) {
                index = i;
                highest = dv[i];
            }
        }
        if (index != 5) {
            return index;
        } else {
            return switch (dc) {
                case "strike" -> 5;
                case "pierce" -> 6;
                case "slash" -> 7;
                case "crush" -> 8;
                case "generic" -> 9;
                default -> 10;
            };
        }
    }

    public static int findHighest(float[] arr) {
        int index = 0;
        float highest = arr[0];
        for (int i = 0; i < arr.length; ++i) {
            if (arr[i] > highest) {
                index = i;
                highest = arr[i];
            }
        }
        return index;
    }

    /*
        Below methods written by mickelus, author of MUtil, Tetra, and Scroll of Harvest. The GOAT, as they say.
     */
    public static String readString(FriendlyByteBuf buffer) {
        String string = "";
        char c = buffer.readChar();

        while(c != '\0') {
            string += c;
            c = buffer.readChar();
        }

        return string;
    }

    public static void writeString(String string, FriendlyByteBuf buffer) {
        for (int i = 0; i < string.length(); i++) {
            buffer.writeChar(string.charAt(i));
        }
        buffer.writeChar('\0');
    }

    public static MutableComponent getComponentFromAttribute (ItemStack item, Attribute a) {
        double addition = 0;
        double base = 0;
        double total = 0;
        for (Map.Entry<Attribute, AttributeModifier> entry : item.getAttributeModifiers(LivingEntity.getEquipmentSlotForItem(item)).entries()) {
            if (entry.getKey() == a) {
                double val = entry.getValue().getAmount();
                AttributeModifier.Operation op = entry.getValue().getOperation();
                if (op == ADDITION) {
                    addition += val;
                } else if (op == MULTIPLY_BASE) {
                    base += val;
                } else {
                    total += val;
                }
            }
        }
        MutableComponent returnComponent = textComponent("");
        boolean hasAddition = addition != 0, hasBaseMult = base != 0, hasTotalMult = total != 0;
        boolean hasNothing = true;
        //if (hasAddition || hasTotalMult || hasBaseMult) returnComponent.append(translationComponent("idf.icon." + a.getDescriptionId()).withStyle(ICON));
        if (hasAddition) {
            hasNothing = false;
            if (addition < 0) {
                returnComponent.append(withColor(textComponent(attributeFormat.format(addition)), Color.RED).withStyle(INDICATOR));
            } else {
                returnComponent.append(withColor(textComponent("+" + attributeFormat.format(addition)), Color.LIGHTGREEN).withStyle(INDICATOR));
            }
            if (hasBaseMult || hasTotalMult) returnComponent.append(", ");
        }
        if (hasBaseMult) {
            hasNothing = false;
            if (base < 0) {
                returnComponent.append(withColor(translationComponent("idf.base_multiplication"), Color.RED));
                returnComponent.append(withColor(textComponent(attributeFormat.format(base)), Color.RED).withStyle(INDICATOR));
                returnComponent.append(withColor(textComponent(")"), Color.RED));
            } else {
                returnComponent.append(withColor(translationComponent("idf.base_multiplication"), Color.LIGHTGREEN));
                returnComponent.append(withColor(textComponent(attributeFormat.format(base)), Color.LIGHTGREEN).withStyle(INDICATOR));
                returnComponent.append(withColor(textComponent(")"), Color.LIGHTGREEN));
            }
            if (hasTotalMult) returnComponent.append(", ");
        }
        if (hasTotalMult) {
            hasNothing = false;
            if (total < 0) {
                returnComponent.append(withColor(translationComponent("idf.total_multiplication"), Color.RED));
                returnComponent.append(withColor(textComponent(attributeFormat.format(total)), Color.RED).withStyle(INDICATOR));
                returnComponent.append(withColor(textComponent(")"), Color.RED));
            } else {
                returnComponent.append(withColor(translationComponent("idf.total_multiplication"), Color.LIGHTGREEN));
                returnComponent.append(withColor(textComponent(attributeFormat.format(total)), Color.LIGHTGREEN).withStyle(INDICATOR));
                returnComponent.append(withColor(textComponent(")"), Color.LIGHTGREEN));
            }
        }
        if (hasNothing) {
            returnComponent.append(withColor(textComponent("0"), Color.WHITESMOKE).withStyle(INDICATOR));
        }
        return returnComponent;
    }

    public static void addAllToJsonArray(JsonArray array, double... d) {
        for (double v : d) {
            array.add(v);
        }
    }

}
