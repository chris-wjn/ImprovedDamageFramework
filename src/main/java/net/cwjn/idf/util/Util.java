package net.cwjn.idf.util;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.mojang.blaze3d.vertex.PoseStack;
import net.cwjn.idf.ImprovedDamageFramework;
import net.cwjn.idf.attribute.IDFAttributes;
import net.cwjn.idf.rpg.bonfire.entity.BonfireBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.LiteralContents;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.registries.ForgeRegistries;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.UUID;

import static net.cwjn.idf.util.UUIDs.*;
import static net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADDITION;

public class Util {

    private Util() {throw new IllegalAccessError("Util class");}
    private static final DecimalFormat attributeFormat = new DecimalFormat("###");
    private static final UUID BASE_ATTACK_DAMAGE_UUID = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
    private static final UUID BASE_ATTACK_SPEED_UUID = UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3");
    private static final UUID[] ARMOR_MODIFIER_UUID_PER_SLOT = new UUID[]{UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"), UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"), UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"), UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150")};

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
        if (val > 99) return MutableComponent.create(new LiteralContents(attributeFormat.format(val)));
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

    public static Multimap<Attribute, AttributeModifier> buildDefaultAttributesWeapon(double speed, double fire, double water, double lightning, double magic, double dark, double phys, double crit, double weight, double pen, double lifesteal) {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        putIfExists(builder, Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", phys, AttributeModifier.Operation.ADDITION));
        putIfExists(builder, Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", speed, AttributeModifier.Operation.ADDITION));
        putIfExists(builder, IDFAttributes.FIRE_DAMAGE.get(), new AttributeModifier(baseFireDamage, "Weapon modifier", fire, ADDITION));
        putIfExists(builder, IDFAttributes.WATER_DAMAGE.get(), new AttributeModifier(baseWaterDamage, "Weapon modifier", water, ADDITION));
        putIfExists(builder, IDFAttributes.LIGHTNING_DAMAGE.get(), new AttributeModifier(baseLightningDamage, "Weapon modifier", lightning, ADDITION));
        putIfExists(builder, IDFAttributes.MAGIC_DAMAGE.get(), new AttributeModifier(baseMagicDamage, "Weapon modifier", magic, ADDITION));
        putIfExists(builder, IDFAttributes.DARK_DAMAGE.get(), new AttributeModifier(baseDarkDamage, "Weapon modifier", dark, ADDITION));
        putIfExists(builder, IDFAttributes.CRIT_CHANCE.get(), new AttributeModifier(baseCrit, "Weapon modifier", crit, ADDITION));
        putIfExists(builder, IDFAttributes.WEIGHT.get(), new AttributeModifier(baseWeight, "Weapon modifier", weight, ADDITION));
        putIfExists(builder, IDFAttributes.PENETRATING.get(), new AttributeModifier(basePen, "Weapon modifier", pen, ADDITION));
        putIfExists(builder, IDFAttributes.LIFESTEAL.get(), new AttributeModifier(baseLifesteal, "Weapon modifier", lifesteal, ADDITION));
        return builder.build();
    }

    public static Multimap<Attribute, AttributeModifier> buildDefaultAttributesArmor(EquipmentSlot slot,
                                                                                     double armour, double toughness,
                                                                                     double fire, double water, double lightning, double magic, double dark,
                                                                                     double knockbackres, double maxhp, double movespeed, double luck, double evasion,
                                                                                     double strike, double pierce, double slash, double crush, double generic) {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        UUID uuid = ARMOR_MODIFIER_UUID_PER_SLOT[slot.getIndex()];
        putIfExists(builder, Attributes.ARMOR, new AttributeModifier(uuid, "Armor modifier", armour, ADDITION));
        putIfExists(builder, Attributes.ARMOR_TOUGHNESS, new AttributeModifier(uuid, "Armor toughness", toughness, ADDITION));
        putIfExists(builder, IDFAttributes.FIRE_RESISTANCE.get(), new AttributeModifier(uuid, "Base fire resistance", fire, ADDITION));
        putIfExists(builder, IDFAttributes.WATER_RESISTANCE.get(), new AttributeModifier(uuid, "Base water resistance", water, ADDITION));
        putIfExists(builder, IDFAttributes.LIGHTNING_RESISTANCE.get(), new AttributeModifier(uuid, "Base lightning resistance", lightning, ADDITION));
        putIfExists(builder, IDFAttributes.MAGIC_RESISTANCE.get(), new AttributeModifier(uuid, "Base magic resistance", magic, ADDITION));
        putIfExists(builder, IDFAttributes.DARK_RESISTANCE.get(), new AttributeModifier(uuid, "Base dark resistance", dark, ADDITION));
        putIfExists(builder, Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(uuid, "Armor knockback resistance", knockbackres, ADDITION));
        putIfExists(builder, Attributes.MAX_HEALTH, new AttributeModifier(uuid, "Base max health", maxhp, ADDITION));
        putIfExists(builder, Attributes.MOVEMENT_SPEED, new AttributeModifier(uuid, "Base movespeed", movespeed, ADDITION));
        putIfExists(builder, Attributes.LUCK, new AttributeModifier(uuid, "Base luck", luck, ADDITION));
        putIfExists(builder, IDFAttributes.EVASION.get(), new AttributeModifier(uuid, "Base evasion", evasion, ADDITION));
        putIfExists(builder, IDFAttributes.STRIKE_MULT.get(), new AttributeModifier(uuid, "Base strike multiplier", strike, ADDITION));
        putIfExists(builder, IDFAttributes.PIERCE_MULT.get(), new AttributeModifier(uuid, "Base pierce multiplier", pierce, ADDITION));
        putIfExists(builder, IDFAttributes.SLASH_MULT.get(), new AttributeModifier(uuid, "Base slash multiplier", slash, ADDITION));
        putIfExists(builder, IDFAttributes.CRUSH_MULT.get(), new AttributeModifier(uuid, "Base crush multiplier", crush, ADDITION));
        putIfExists(builder, IDFAttributes.GENERIC_MULT.get(), new AttributeModifier(uuid, "Base generic multiplier", generic, ADDITION));
        return builder.build();
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

    public static int getItemBorderType(ItemStack item, String dc, double f, double w, double l, double m, double d, double p) {
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

    public static boolean lookingAt(float partialTicks, BonfireBlockEntity be) {
        HitResult ray = Minecraft.getInstance().player.pick(20, partialTicks, false);
        return (((BlockHitResult)ray).getBlockPos().equals(be.getBlockPos()));
    }

    public static ResourceLocation[] openBookAnim() {
        ResourceLocation[] returnRL = new ResourceLocation[8];
        returnRL[0] = new ResourceLocation(ImprovedDamageFramework.MOD_ID, "textures/gui/book_open/0.png");
        returnRL[1] = new ResourceLocation(ImprovedDamageFramework.MOD_ID, "textures/gui/book_open/1.png");
        returnRL[2] = new ResourceLocation(ImprovedDamageFramework.MOD_ID, "textures/gui/book_open/2.png");
        returnRL[3] = new ResourceLocation(ImprovedDamageFramework.MOD_ID, "textures/gui/book_open/3.png");
        returnRL[4] = new ResourceLocation(ImprovedDamageFramework.MOD_ID, "textures/gui/book_open/4.png");
        returnRL[5] = new ResourceLocation(ImprovedDamageFramework.MOD_ID, "textures/gui/book_open/5.png");
        returnRL[6] = new ResourceLocation(ImprovedDamageFramework.MOD_ID, "textures/gui/book_open/6.png");
        returnRL[7] = new ResourceLocation(ImprovedDamageFramework.MOD_ID, "textures/gui/book_open/7.png");
        return returnRL;
    }

    public static ResourceLocation[] closeBookAnim() {
        ResourceLocation[] returnRL = new ResourceLocation[8];
        returnRL[0] = new ResourceLocation(ImprovedDamageFramework.MOD_ID, "textures/gui/book_close/0.png");
        returnRL[1] = new ResourceLocation(ImprovedDamageFramework.MOD_ID, "textures/gui/book_close/1.png");
        returnRL[2] = new ResourceLocation(ImprovedDamageFramework.MOD_ID, "textures/gui/book_close/2.png");
        returnRL[3] = new ResourceLocation(ImprovedDamageFramework.MOD_ID, "textures/gui/book_close/3.png");
        returnRL[4] = new ResourceLocation(ImprovedDamageFramework.MOD_ID, "textures/gui/book_close/4.png");
        returnRL[5] = new ResourceLocation(ImprovedDamageFramework.MOD_ID, "textures/gui/book_close/5.png");
        returnRL[6] = new ResourceLocation(ImprovedDamageFramework.MOD_ID, "textures/gui/book_close/6.png");
        returnRL[7] = new ResourceLocation(ImprovedDamageFramework.MOD_ID, "textures/gui/book_close/7.png");
        return returnRL;
    }
}
