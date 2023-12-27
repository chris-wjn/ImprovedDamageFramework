package net.cwjn.idf.util;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.blaze3d.vertex.PoseStack;
import net.cwjn.idf.ImprovedDamageFramework;
import net.cwjn.idf.api.event.IDFAttackRangeTooltip;
import net.cwjn.idf.attribute.IDFAttributes;
import net.cwjn.idf.config.CommonConfig;
import net.cwjn.idf.data.CommonData;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;
import java.util.function.Predicate;

import static net.cwjn.idf.ImprovedDamageFramework.*;
import static net.cwjn.idf.damage.DamageHandler.DEFAULT_KNOCKBACK;
import static net.cwjn.idf.data.CommonData.*;
import static net.cwjn.idf.util.Color.LIGHTGREEN;
import static net.minecraft.network.chat.Component.literal;
import static net.minecraft.network.chat.Component.translatable;
import static net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADDITION;

public class Util {

    private Util() {
        throw new IllegalAccessError("Util class");
    }

    private static final Style ICON = Style.EMPTY.withFont(FONT_ICONS);
    public static final Style TOOLTIP = Style.EMPTY.withFont(FONT_TOOLTIPS);
    private static final Style SPACER = Style.EMPTY.withFont(FONT_SPACER);
    private static final Style DEFAULT = Style.EMPTY.withFont(Style.DEFAULT_FONT);
    private static final Style DIVIDERS = Style.EMPTY.withFont(FONT_DIVIDERS);
    public static final Predicate<String> offensiveAttribute = name -> (
            (name.contains("damage") || name.contains("crit") || name.contains("attack_knockback") || name.contains("force") || name.contains("lifesteal") || name.contains("pen"))
    );
    private static final Predicate<Attribute> isPercentageAttribute = a -> (
            a.equals(IDFAttributes.EVASION.get()) ||
                    a.equals(IDFAttributes.LIFESTEAL.get()) ||
                    a.equals(IDFAttributes.CRIT_CHANCE.get()) ||
                    a.equals(IDFAttributes.CRIT_DAMAGE.get()) ||
                    a.equals(IDFAttributes.STRIKE_MULT.get()) ||
                    a.equals(IDFAttributes.SLASH_MULT.get()) ||
                    a.equals(IDFAttributes.PIERCE_MULT.get()) ||
                    a.equals(Attributes.KNOCKBACK_RESISTANCE) ||
                    a.equals(Attributes.ATTACK_KNOCKBACK) ||
                    a.equals(IDFAttributes.PENETRATING.get())
    );
    private static final Predicate<Attribute> shouldMult100 = a -> (
            a.equals(IDFAttributes.STRIKE_MULT.get()) ||
                    a.equals(IDFAttributes.SLASH_MULT.get()) ||
                    a.equals(IDFAttributes.PIERCE_MULT.get()) ||
                    a.equals(Attributes.KNOCKBACK_RESISTANCE) ||
                    a.equals(Attributes.ATTACK_KNOCKBACK)
    );
    private static final Predicate<Attribute> isInverseNegative = a -> (
            a.equals(IDFAttributes.STRIKE_MULT.get()) ||
                    a.equals(IDFAttributes.SLASH_MULT.get()) ||
                    a.equals(IDFAttributes.PIERCE_MULT.get())
    );
    public static final int ICON_PIXEL_SPACER = 1;
    public static final DecimalFormat tenths = new DecimalFormat("#.##");
    private static final DecimalFormat hundredFormat = new DecimalFormat("###");
    public static final UUID[] UUID_BASE_STAT_ADDITION = {
            UUID.fromString("55CEEB33-BEFB-41DF-BF9F-0E805BA1B6F7"),
            UUID.fromString("132DB4C0-8CD5-46EE-B7A6-48CCFD11B1F0"),
            UUID.fromString("0D6AB740-41B9-4BDE-ADBA-BAAB28623C6E"),
            UUID.fromString("55CEEB33-BEFB-41DF-BF9F-0E805BA1B6F8"),
            UUID.fromString("0D6AB740-41B9-4BDE-ADBA-BAAB28623C6F"),
            UUID.fromString("BCAF7601-AC93-4705-8F3A-51CA50281AC6")};
    public static final UUID[] UUID_BASE_STAT_MULTIPLY_TOTAL = {
            UUID.fromString("55CEEB33-BEFB-41DF-BF9F-0E805BA1B6F9"),
            UUID.fromString("132DB4C0-8CD5-46EE-B7A6-48CCFD11B1F2"),
            UUID.fromString("0D6AB740-41B9-4BDE-ADBA-BAAB28623C61"),
            UUID.fromString("55CEEB33-BEFB-41DF-BF9F-0E805BA1B6FA"),
            UUID.fromString("0D6AB740-41B9-4BDE-ADBA-BAAB28623C62"),
            UUID.fromString("BCAF7601-AC93-4705-8F3A-51CA50281AC8")};
    public static final UUID[] UUID_STAT_CONVERSION = {
            UUID.fromString("55CEEB33-BEFB-41DF-BF9F-0E805BA1B6FA"),
            UUID.fromString("132DB4C0-8CD5-46EE-B7A6-48CCFD11B1F3"),
            UUID.fromString("0D6AB740-41B9-4BDE-ADBA-BAAB28623C62"),
            UUID.fromString("55CEEB33-BEFB-41DF-BF9F-0E805BA1B6FB"),
            UUID.fromString("0D6AB740-41B9-4BDE-ADBA-BAAB28623C63"),
            UUID.fromString("BCAF7601-AC93-4705-8F3A-51CA50281AC9")};
    public static final UUID[] UUID_IDF_CUSTOM_ITEMS = {
            UUID.fromString("3B3A2AC8-EB86-4B37-ABF1-1DF34F564F29"),
            UUID.fromString("98A3F188-6A2C-4827-90A9-FB49632A135E"),
            UUID.fromString("E57F9B5D-3D47-498A-884F-7E739E98A489"),
            UUID.fromString("4F7F98E3-42D6-4E7D-A50C-BF4D9805AB12"),
            UUID.fromString("A542BAC0-C124-4F4E-9F0D-829F7C1DD8E9"),
            UUID.fromString("7B8ECA81-1B6A-4AC7-9FED-6720EB2E2F4A")};

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

    public static MutableComponent translation(String key) {
        return MutableComponent.create(new TranslatableContents(key));
    }

    public static MutableComponent translation(String main, Object... additions) {
        return translatable(main, additions);
    }

    public static MutableComponent text(String text) {
        return Component.literal(text);
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

    public static ResourceLocation getEntityRegistryName(EntityType<?> type) {
        return ForgeRegistries.ENTITY_TYPES.getKey(type);
    }

    public static EntityType<? extends LivingEntity> getEntityTypeFromRegistryName(ResourceLocation name) {
        return (EntityType<? extends LivingEntity>) ForgeRegistries.ENTITY_TYPES.getValue(name);
    }

    public static ResourceLocation getItemRegistryName(Item item) {
        return ForgeRegistries.ITEMS.getKey(item);
    }

    public static Item getItemFromRegistryName(ResourceLocation loc) {
        return ForgeRegistries.ITEMS.getValue(loc);
    }

    public static double pBPS(double attribute) {
        BigDecimal x = new BigDecimal(attribute * 43.178 - 0.02141);
        x = x.setScale(1, RoundingMode.HALF_UP);
        return x.doubleValue();
    }

    public static double mBPS(double attribute) {
        BigDecimal x = new BigDecimal(attribute * 9.60845544);
        x = x.setScale(1, RoundingMode.HALF_UP);
        return x.doubleValue();
    }

    public static double fastSqrt(double d) {
        return Double.longBitsToDouble(((Double.doubleToRawLongBits(d) >> 32) + 1072632448) << 31);
    }

    public static int getItemBorderType(String dc, Map<String, Double> map) {
        double f = 0, w = 0, l = 0, m = 0, d = 0, h = 0, p = 0;
        for (Map.Entry<String, Double> entry : map.entrySet()) {
            switch (entry.getKey()) {
                case "fire_damage" -> f = entry.getValue();
                case "water_damage" -> w = entry.getValue();
                case "lightning_damage" -> l = entry.getValue();
                case "magic_damage" -> m = entry.getValue();
                case "dark_damage" -> d = entry.getValue();
                case "holy_damage" -> h = entry.getValue();
                case "physical_damage" -> p = entry.getValue();
            }
        }
        double[] dv = new double[]{f, w, l, m, d, h, p};
        int index = 6;
        double highest = 0.0D;
        for (int i = 0; i < 6; ++i) {
            if (dv[i] > highest) {
                index = i;
                highest = dv[i];
            }
        }
        if (index != 6) {
            return index;
        } else {
            return switch (dc) {
                case "strike" -> 6;
                case "pierce" -> 7;
                case "slash" -> 8;
                default -> 10;
            };
        }
    }

    public static String[] sort(Multimap<Attribute, AttributeModifier> map, boolean damage) {
        //get all the requested elements in the list and put them in an array
        ArrayList<String> list = new ArrayList<>();
        ArrayList<String> elements = new ArrayList<>();
        /*for (Attribute a : map.keySet()) {
            if (damage? a.getDescriptionId().equals("attribute.name.generic.attack_damage") : a.getDescriptionId().equals("attribute.name.generic.armor")) {
                list.add(a.getDescriptionId());
            }
            if (!CommonData.ELEMENTS.contains(a)) continue;
            if (damage? a.getDescriptionId().contains("damage") : a.getDescriptionId().contains("defence")) {
                list.add(a.getDescriptionId());
            }
        }*/
        for (Attribute a : map.keySet()) {
            if (damage && CommonData.OFFENSIVE_ATTRIBUTES.contains(a)) {
                if (CommonData.ELEMENTS.contains(a)) elements.add(ForgeRegistries.ATTRIBUTES.getKey(a).toString());
                else list.add(ForgeRegistries.ATTRIBUTES.getKey(a).toString());
            } else if (!damage && CommonData.DEFENSIVE_ATTRIBUTES.contains(a)) {
                if (CommonData.ELEMENTS.contains(a)) elements.add(ForgeRegistries.ATTRIBUTES.getKey(a).toString());
                else list.add(ForgeRegistries.ATTRIBUTES.getKey(a).toString());
            }
        }
        elements.sort(String.CASE_INSENSITIVE_ORDER);
        list.sort(String.CASE_INSENSITIVE_ORDER);
        elements.addAll(list);
        return elements.toArray(new String[list.size()]);
    }

    /*
        Following 2 methods written by mickelus, author of MUtil, Tetra, and Scroll of Harvest. The GOAT, as they say.
     */
    public static String readString(FriendlyByteBuf buffer) {
        String string = "";
        char c = buffer.readChar();

        while (c != '\0') {
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

    public static MutableComponent writeTooltipInteger(int num, boolean withColour) {
        MutableComponent comp = Util.text("").withStyle(TOOLTIP);
        String s = num > 0 ? "+" + num : String.valueOf(num);
        for (int i = 0; i < s.length(); i++) {
            comp.append(String.valueOf(s.charAt(i)));
            if (i != s.length() - 1) {
                if (s.charAt(i + 1) == '.') {
                    comp.append(spacer(-2));
                } else if (s.charAt(i + 1) == '1') {
                    comp.append(spacer(-1));
                }
                comp.append(spacer(-1));
            }
        }
        return withColour ? (num < 0 ? comp.withStyle(ChatFormatting.RED) : comp) : comp;
    }

    public static MutableComponent writeTooltipDouble(double num, boolean withColour, boolean appendX, boolean appendPercentage, boolean invertNegative, Color colour) {
        MutableComponent comp = Component.empty().withStyle(TOOLTIP);
        String number = (num > 0 && !appendX) ? "+" + tenths.format(num) : tenths.format(num);
        if (appendX) {
            comp.append(spacer(-1));
            comp.append("x");
        }
        if (number.charAt(0) == '1') comp.append(spacer(-1));
        for (int i = 0; i < number.length(); i++) {
            comp.append(String.valueOf(number.charAt(i)));
            if (i != number.length() - 1) {
                if (number.charAt(i + 1) == '.') {
                    comp.append(spacer(-2));
                } else if (number.charAt(i + 1) == '1') {
                    comp.append(spacer(-1));
                }
                comp.append(spacer(-1));
            }
        }
        if (appendPercentage) {
            comp.append(spacer(-1));
            comp.append("%");
        }
        comp.append(spacer(1));
        int threshhold = appendX ? 1 : 0;
        if (invertNegative) {
            return withColour ? (num > threshhold ? comp.withStyle(ChatFormatting.RED) : Util.withColor(comp, colour)) : comp;
        } else {
            return withColour ? (num < threshhold ? comp.withStyle(ChatFormatting.RED) : Util.withColor(comp, colour)) : comp;
        }
    }

    public static MutableComponent writeTooltipString(String s) {
        MutableComponent comp = Util.text("").withStyle(TOOLTIP);
        if (s.charAt(0) == '1') comp.append(spacer(-1));
        for (int i = 0; i < s.length(); i++) {
            comp.append(String.valueOf(s.charAt(i)));
            if (i != s.length() - 1) {
                if (s.charAt(i + 1) == '.') {
                    comp.append(spacer(-2));
                } else if (s.charAt(i + 1) == '1' || s.charAt(i + 1) == '/') {
                    comp.append(spacer(-1));
                }
                comp.append(spacer(-1));
            }
        }
        return comp;
    }

    public static MutableComponent writeStaticTooltipComponent(double n, String name, @Nullable Color colour, boolean isPercentage, boolean drawBorders) {
        //first format the number so that percentages have no decimal and other numbers are rounded to the nearest tenth
        String num;
        if (isPercentage) num = String.valueOf((int) n);
        else num = tenths.format(n);
        int FRONT_SPACER = num.charAt(0) == '1' ? -2 : -1;
        int ones = 0;
        int dots = 0;
        for (char c : num.toCharArray()) {
            if (c == '1') ones++;
            else if (c == '.') dots++;
        }
        int leftOnes = 0;
        int rightOnes = 0;
        while (ones != 0) {
            leftOnes++;
            rightOnes++;
            ones--;
        }

        //now lets check how many spaces we need to fill. Each component should have 6 characters between the dividers, so
        //we start with 6, subtract the number of characters we already have, and then add 1 if the percentage symbol will take up a space
        double spaces = 4 - (num.length() + (isPercentage ? 1 : 0));

        //start our component object with a divider if this is not a middle element
        MutableComponent comp = text(drawBorders ? "|" : "").withStyle(DEFAULT);

        //add left padding, plus a half if the total spaces is odd
        comp.append(spacer((int) (8 * (spaces / 2)) + dots * 2 + leftOnes));

        //add the number to the component. Also add a percent if needed
        comp.append(translation("idf.icon." + name).withStyle(ICON)).append(spacer(-1));
        if (colour == null) {
            comp.append(writeTooltipString(num + (isPercentage ? "%" : "")));
        } else {
            comp.append(withColor(writeTooltipString(num + (isPercentage ? "%" : "")), colour));
        }

        //add a half spacer if total spaces is odd, then add the rest of the right padding
        comp.append(spacer((int) (8 * (spaces / 2)) + dots * 2 + rightOnes));

        //finish up with an ending divider if needed
        return drawBorders ? comp.append("|").withStyle(DEFAULT) : comp;
    }

    public static MutableComponent writeStaticInfinityComponent(Color colour, boolean drawBorders) {
        MutableComponent comp = text(drawBorders ? "|" : "").append(withColor(writeIcon("infinity.symbol", true), colour));
        for (int i = 1; i <= ((drawBorders ? 6 : 5) - 1); ++i) {
            comp.append(" ");
        }
        return drawBorders ? comp.append("|") : comp;
    }

    public static MutableComponent writeIcon(String name, boolean includeSpacer) {
        if (I18n.exists("idf.icon." + name)) {
            MutableComponent comp = spacer(ICON_PIXEL_SPACER);
            MutableComponent comp1 = translation("idf.icon." + name).withStyle(ICON);
            return includeSpacer ? comp1.append(comp) : comp1;
        } else return Component.empty();
    }

    public static MutableComponent spacer(int i) {
        return translation("space." + i).withStyle(SPACER);
    }

    public static ResourceLocation[] flipLeftAnim() {
        ResourceLocation[] returnRL = new ResourceLocation[8];
        returnRL[0] = new ResourceLocation(ImprovedDamageFramework.MOD_ID, "textures/gui/book_flip_left/1.png");
        returnRL[1] = new ResourceLocation(ImprovedDamageFramework.MOD_ID, "textures/gui/book_flip_left/2.png");
        returnRL[2] = new ResourceLocation(ImprovedDamageFramework.MOD_ID, "textures/gui/book_flip_left/3.png");
        returnRL[3] = new ResourceLocation(ImprovedDamageFramework.MOD_ID, "textures/gui/book_flip_left/4.png");
        returnRL[4] = new ResourceLocation(ImprovedDamageFramework.MOD_ID, "textures/gui/book_flip_left/5.png");
        returnRL[5] = new ResourceLocation(ImprovedDamageFramework.MOD_ID, "textures/gui/book_flip_left/6.png");
        returnRL[6] = new ResourceLocation(ImprovedDamageFramework.MOD_ID, "textures/gui/book_flip_left/7.png");
        returnRL[7] = new ResourceLocation(ImprovedDamageFramework.MOD_ID, "textures/gui/book_flip_left/8.png");
        return returnRL;
    }

    public static ResourceLocation[] flipRightAnim() {
        ResourceLocation[] returnRL = new ResourceLocation[8];
        returnRL[0] = new ResourceLocation(ImprovedDamageFramework.MOD_ID, "textures/gui/book_flip_right/1.png");
        returnRL[1] = new ResourceLocation(ImprovedDamageFramework.MOD_ID, "textures/gui/book_flip_right/2.png");
        returnRL[2] = new ResourceLocation(ImprovedDamageFramework.MOD_ID, "textures/gui/book_flip_right/3.png");
        returnRL[3] = new ResourceLocation(ImprovedDamageFramework.MOD_ID, "textures/gui/book_flip_right/4.png");
        returnRL[4] = new ResourceLocation(ImprovedDamageFramework.MOD_ID, "textures/gui/book_flip_right/5.png");
        returnRL[5] = new ResourceLocation(ImprovedDamageFramework.MOD_ID, "textures/gui/book_flip_right/6.png");
        returnRL[6] = new ResourceLocation(ImprovedDamageFramework.MOD_ID, "textures/gui/book_flip_right/7.png");
        returnRL[7] = new ResourceLocation(ImprovedDamageFramework.MOD_ID, "textures/gui/book_flip_right/8.png");
        return returnRL;
    }

    public static ResourceLocation[] openAnim() {
        ResourceLocation[] returnRL = new ResourceLocation[5];
        returnRL[0] = new ResourceLocation(ImprovedDamageFramework.MOD_ID, "textures/gui/book_open/1.png");
        returnRL[1] = new ResourceLocation(ImprovedDamageFramework.MOD_ID, "textures/gui/book_open/2.png");
        returnRL[2] = new ResourceLocation(ImprovedDamageFramework.MOD_ID, "textures/gui/book_open/3.png");
        returnRL[3] = new ResourceLocation(ImprovedDamageFramework.MOD_ID, "textures/gui/book_open/4.png");
        returnRL[4] = new ResourceLocation(ImprovedDamageFramework.MOD_ID, "textures/gui/book_open/5.png");
        return returnRL;
    }

    public static ResourceLocation[] contentAppearAnim() {
        ResourceLocation[] returnRL = new ResourceLocation[36];
        for (int i = 1; i <= 36; i++) {
            returnRL[i - 1] = new ResourceLocation(MOD_ID, "textures/gui/book_content_appear/" + i + ".png");
        }
        return returnRL;
    }

    //the ugliest method known to man
    public static Component divider(int length, Color colour) {
        MutableComponent c = Component.empty().withStyle(DIVIDERS);
        int j = Math.max(1, (int) (length * 0.04));
        for (int i = 0; i < j; i++) {
            c.append(translatable("idf.divider1")).append(spacer(-1));
        }
        for (int i = 0; i < j; i++) {
            c.append(translatable("idf.divider2")).append(spacer(-1));
        }
        for (int i = 0; i < j; i++) {
            c.append(translatable("idf.divider3")).append(spacer(-1));
        }
        for (int i = 0; i < j; i++) {
            c.append(translatable("idf.divider4")).append(spacer(-1));
        }
        for (int i = 0; i < j; i++) {
            c.append(translatable("idf.divider5")).append(spacer(-1));
        }
        for (int i = 0; i < j; i++) {
            c.append(translatable("idf.divider6")).append(spacer(-1));
        }
        for (int i = 0; i < length * 0.6; i++) {
            c.append(translatable("idf.divider7")).append(spacer(-1));
        }
        for (int i = 0; i < j; i++) {
            c.append(translatable("idf.divider6")).append(spacer(-1));
        }
        for (int i = 0; i < j; i++) {
            c.append(translatable("idf.divider5")).append(spacer(-1));
        }
        for (int i = 0; i < j; i++) {
            c.append(translatable("idf.divider4")).append(spacer(-1));
        }
        for (int i = 0; i < j; i++) {
            c.append(translatable("idf.divider3")).append(spacer(-1));
        }
        for (int i = 0; i < j; i++) {
            c.append(translatable("idf.divider2")).append(spacer(-1));
        }
        for (int i = 0; i < j; i++) {
            c.append(translatable("idf.divider1")).append(spacer(-1));
        }
        return Util.withColor(c, colour);
    }

    public static void doAttributeTooltip(ItemStack item, Player player, List<Component> list) {
        EquipmentSlot slot = LivingEntity.getEquipmentSlotForItem(item);
        Multimap<Attribute, AttributeModifier> map = HashMultimap.create(item.getAttributeModifiers(slot));
        boolean isWeapon = item.getTag().contains(WEAPON_TAG);
        boolean isRanged = item.getTag().getBoolean(RANGED_TAG);
        boolean writeBorderTag = isWeapon && CommonConfig.LEGENDARY_TOOLTIPS_COMPAT_MODE.get() && !item.getTag().contains(BORDER_TAG);
        Optional<Map<String, Double>> borderHelper = writeBorderTag ? Optional.of(new HashMap<>()) : Optional.empty();
        if (item.isDamageableItem()) {
            int currentDurability = item.getMaxDamage() - item.getDamageValue();
            double percentage = (double) currentDurability / (double) item.getMaxDamage();
            Color colour = new Color((int) (128 + (128 * 0.5 * (1.0 - percentage))), (int) (255 * percentage), 0);
            MutableComponent durabilityComponent = Component.empty();
            list.add(durabilityComponent
                    .append(Component.literal(" ").append(translatable("idf.right_arrow.symbol").append(spacer(2))))
                    .append(Util.writeIcon("durability", true))
                    .append(translatable("idf.tooltip.durability").withStyle(ChatFormatting.GRAY)
                            .append(Util.withColor(writeTooltipString(String.valueOf(currentDurability)), colour))
                    ));
        }
        else {
            MutableComponent durabilityComponent = Component.empty();
            list.add(durabilityComponent
                    .append(Component.literal(" ").append(translatable("idf.right_arrow.symbol").append(spacer(2))))
                    .append(Util.writeIcon("durability", true))
                    .append(translatable("idf.tooltip.durability").withStyle(ChatFormatting.GRAY)
                            .append(withColor(translation("idf.icon.infinity.symbol"), Color.DARKSEAGREEN))
                    ));
        }
        if (isWeapon) {
            if (!isRanged) {
                MutableComponent atkSpeed = Component.empty();
                list.add(atkSpeed
                        .append(Component.literal(" ").append(translatable("idf.right_arrow.symbol").append(spacer(2))))
                        .append(Util.writeIcon("attack_speed", true))
                        .append(Util.withColor(translatable("idf.tooltip.attack_speed"), ChatFormatting.GRAY.getColor()))
                        .append(Util.withColor(writeTooltipString(tenths.format(convertAndRemoveAttribute(map, Attributes.ATTACK_SPEED, player, true))), Color.HOLY_COLOUR))
                );
                if (BETTER_COMBAT_LOADED) {
                    IDFAttackRangeTooltip event = new IDFAttackRangeTooltip(item, player, list, true);
                    MinecraftForge.EVENT_BUS.post(event);
                    list = event.getList();
                    map.removeAll(ForgeMod.ATTACK_RANGE.get());
                }
                else {
                    MutableComponent atkRange = Component.empty();
                    list.add(atkRange
                            .append(Component.literal(" ").append(translatable("idf.right_arrow.symbol").append(spacer(2))))
                            .append(Util.writeIcon("attack_range", true))
                            .append(Util.withColor(translatable("idf.tooltip.attack_range"), ChatFormatting.GRAY.getColor()))
                            .append(Util.withColor(writeTooltipString(tenths.format(convertAndRemoveAttribute(map, ForgeMod.ATTACK_RANGE.get(), player, true))), Color.HOLY_COLOUR))
                    );
                }
            } else {
                MutableComponent accuracy = Component.empty();
                list.add(accuracy
                        .append(Component.literal(" ").append(translatable("idf.right_arrow.symbol").append(spacer(2))))
                        .append(Util.writeIcon("accuracy", true))
                        .append(Util.withColor(translatable("idf.tooltip.accuracy"), ChatFormatting.GRAY.getColor()))
                        .append(Util.withColor(writeTooltipString(tenths.format(convertAndRemoveAttribute(map, IDFAttributes.ACCURACY.get(), player, true))), Color.HOLY_COLOUR))
                );
            }
            MutableComponent force = Component.empty();
            double num = convertAndRemoveAttribute(map, IDFAttributes.FORCE.get(), player, true);
            if (num < 0) {
                list.add(force
                        .append(Component.literal(" ").append(translatable("idf.right_arrow.symbol").append(spacer(2))))
                        .append(Util.writeIcon("force", true))
                        .append(Util.withColor(translatable("idf.tooltip.force"), ChatFormatting.GRAY.getColor()))
                        .append(Util.withColor(Util.writeTooltipString("N/A"), Color.LIGHTGREY))
                );
            } else {
                list.add(force
                        .append(Component.literal(" ").append(translatable("idf.right_arrow.symbol").append(spacer(2))))
                        .append(Util.writeIcon("force", true))
                        .append(Util.withColor(translatable("idf.tooltip.force"), ChatFormatting.GRAY.getColor()))
                        .append(Util.withColor(writeTooltipString(tenths.format(num)), Color.HOLY_COLOUR))
                );
            }
        }
        else {
            MutableComponent weight = Component.empty();
            list.add(weight
                    .append(Component.literal(" ").append(translatable("idf.right_arrow.symbol").append(spacer(2))))
                    .append(Util.writeIcon("weight", true))
                    .append(Util.withColor(translatable("idf.tooltip.weight"), ChatFormatting.GRAY.getColor()))
                    .append(Util.withColor(writeTooltipString(tenths.format(convertAndRemoveAttribute(map, Attributes.ARMOR_TOUGHNESS, player, false))), Color.HOLY_COLOUR))
            );
        }
        for (EquipmentSlot s : EquipmentSlot.values()) {
            boolean isStandardSlot = s == slot;
            Color cl;
            if (isStandardSlot) {
                String[] keys = Util.sort(map, isWeapon);
                for (String key : keys) {
                    Attribute a = ForgeRegistries.ATTRIBUTES.getValue(ResourceLocation.tryParse(key));
                    String name = a.getDescriptionId();
                    cl = Color.ATTRIBUTE_COLOURS.get(a.getDescriptionId());
                    if (cl == null) cl = LIGHTGREEN;
                    Collection<AttributeModifier> mods = map.get(a);
                    final double base = mods.stream().filter((modifier) -> modifier.getOperation().equals(AttributeModifier.Operation.ADDITION)).mapToDouble(AttributeModifier::getAmount).sum();
                    double mult_base = mods.stream().filter((modifier) -> modifier.getOperation().equals(AttributeModifier.Operation.MULTIPLY_BASE)).mapToDouble(AttributeModifier::getAmount).map((amount) -> amount * Math.abs(base)).sum();
                    double mult_total = mods.stream().filter((modifier) -> modifier.getOperation().equals(AttributeModifier.Operation.MULTIPLY_TOTAL)).mapToDouble(AttributeModifier::getAmount).map((amount) -> amount + 1.0).reduce(1, (x, y) -> x * y);
                    MutableComponent c = Component.empty();
                    c.append(Component.literal(" ").append(translatable("idf.right_arrow.symbol").append(spacer(2))));
                    if (ELEMENTS.contains(a)) {
                        c.append(writeIcon(name, true))
                                .append(translatable(name).withStyle(ChatFormatting.GRAY))
                                .append(" ")
                                .append(writeDamageTooltip((player.getAttributeBaseValue(a) + base + mult_base) * mult_total, cl));
                    } else {
                        double val = (base + mult_base) * mult_total;
                        if (shouldMult100.test(a)) val *= 100;
                        c.append(writeIcon(name, true))
                                .append(translatable(name).withStyle(ChatFormatting.GRAY))
                                .append(" ")
                                .append(writeTooltipDouble(val, true, false, isPercentageAttribute.test(a), isInverseNegative.test(a), cl));
                    }
                    if (writeBorderTag) {
                        borderHelper.ifPresent(h -> h.put(key, mult_total * (mult_base + base)));
                    }
                    map.removeAll(a);
                    list.add(c);
                }
                for (Map.Entry<Attribute, AttributeModifier> entry : map.entries()) {
                    Attribute a = entry.getKey();
                    String name = a.getDescriptionId();
                    AttributeModifier.Operation op = entry.getValue().getOperation();
                    Color col;
                    MutableComponent c = Component.empty().withStyle(Style.EMPTY);
                    c.append(Component.literal(" ").append(translatable("idf.right_arrow.symbol").append(spacer(2))));
                    double val = entry.getValue().getAmount();
                    if (val == 0) continue;
                    if (OFFENSIVE_ATTRIBUTES.contains(a)) col = LIGHTGREEN;
                    else if (DEFENSIVE_ATTRIBUTES.contains(a)) col = LIGHTGREEN;
                    else if (AUXILIARY_ATTRIBUTES.contains(a)) col = Color.GOLD;
                    else col = Color.WHITESMOKE;
                    if (a == Attributes.MOVEMENT_SPEED) val = Util.pBPS(val);
                    if (a == Attributes.ATTACK_KNOCKBACK) {
                        BigDecimal numerator = BigDecimal.valueOf(val), denominator = new BigDecimal(DEFAULT_KNOCKBACK);
                        val = numerator.divide(denominator, RoundingMode.CEILING).doubleValue();
                    }
                    if (shouldMult100.test(a) && op == ADDITION) val *= 100;
                    if (op == ADDITION) {
                        c.append(writeIcon(name, true))
                                .append(translatable(name).withStyle(ChatFormatting.GRAY))
                                .append(" ")
                                .append(writeTooltipDouble(val, true, false, isPercentageAttribute.test(a), isInverseNegative.test(a), col));
                    } else {
                        c.append(writeIcon(name, true))
                                .append(translatable(name).withStyle(ChatFormatting.GRAY))
                                .append(" ")
                                .append(writeTooltipDouble(val + 1, true, true, false, isInverseNegative.test(a), col));
                    }
                    list.add(c);
                }
            }
            else {
                if (!item.getAttributeModifiers(s).isEmpty()) {
                    if (!Screen.hasShiftDown()) {
                        MutableComponent c = Component.empty();
                        c.append(Component.translatable("idf.hold_shift_for_tooltips").withStyle(ChatFormatting.GRAY));
                        list.add(c);
                    }
                    else {
                        for (Map.Entry<Attribute, AttributeModifier> entry : item.getAttributeModifiers(s).entries()) {
                            Attribute a = entry.getKey();
                            String name = a.getDescriptionId();
                            AttributeModifier.Operation op = entry.getValue().getOperation();
                            Color col;
                            MutableComponent c = Component.empty().withStyle(Style.EMPTY);
                            c.append(Component.literal(" ").append(translatable("idf.right_arrow.symbol").append(spacer(2))));
                            double val = entry.getValue().getAmount();
                            if (val == 0) continue;
                            if (OFFENSIVE_ATTRIBUTES.contains(a)) col = LIGHTGREEN;
                            else if (DEFENSIVE_ATTRIBUTES.contains(a)) col = Color.LIGHTGREEN;
                            else if (AUXILIARY_ATTRIBUTES.contains(a)) col = Color.GOLD;
                            else col = Color.WHITESMOKE;
                            if (a == Attributes.MOVEMENT_SPEED) val = Util.pBPS(val);
                            else if (a == Attributes.ATTACK_KNOCKBACK) {
                                BigDecimal numerator = BigDecimal.valueOf(val), denominator = new BigDecimal(DEFAULT_KNOCKBACK);
                                val = numerator.divide(denominator, RoundingMode.CEILING).doubleValue();
                            } else if (shouldMult100.test(a) && op == ADDITION) val *= 100;
                            if (op == ADDITION) {
                                c.append(writeIcon(name, true))
                                        .append(translatable(name).withStyle(ChatFormatting.GRAY))
                                        .append(" ")
                                        .append(writeTooltipDouble(val, true, false, isPercentageAttribute.test(a), isInverseNegative.test(a), col));
                            } else {
                                c.append(writeIcon(name, true))
                                        .append(translatable(name).withStyle(ChatFormatting.GRAY))
                                        .append(" ")
                                        .append(writeTooltipDouble(val + 1, true, true, false, isInverseNegative.test(a), col));
                            }
                            c.append(Util.withColor(literal(" (").append(Component.translatable("idf.itemslot." + slot.getName()).append(literal(")"))), Color.GREY));
                            list.add(c);
                        }
                    }
                }
            }
        }
        borderHelper.ifPresent(h -> item.getTag().putInt(BORDER_TAG, Util.getItemBorderType(item.getTag().getString(CommonData.WEAPON_TAG), h)));
    }

    private static double convertAndRemoveAttribute(Multimap<Attribute, AttributeModifier> map, Attribute a, Player player, boolean factorBase) {
        final double value = getCollectedModifiers(map.get(a));
        map.removeAll(a);
        return value + (factorBase? player.getAttributeBaseValue(a) : 0);
    }

    private static MutableComponent writeDamageTooltip(double num, Color colour) {
        MutableComponent comp = Component.empty().withStyle(TOOLTIP);
        String number = tenths.format(num);
        if (number.charAt(0) == '1') comp.append(spacer(-1));
        for(int i = 0; i < number.length() ; i++) {
            comp.append(String.valueOf(number.charAt(i)));
            if (i != number.length()-1) {
                if (number.charAt(i+1) == '.') {
                    comp.append(spacer(-2));
                } else if (number.charAt(i+1) == '1') {
                    comp.append(spacer(-1));
                }
                comp.append(spacer(-1));
            }
        }
        comp.append(spacer(1));
        return (num < 0 ? comp.withStyle(ChatFormatting.RED) : Util.withColor(comp, colour));
    }

    @NotNull
    public static Multimap<Attribute, AttributeModifier> getReworkedAttributeMap(ItemStack item, EquipmentSlot slot) {
        Multimap<Attribute, AttributeModifier> newMap = HashMultimap.create();
        Multimap<Attribute, AttributeModifier> oldMap = item.getAttributeModifiers(slot);
        if (!item.hasTag() || !item.getTag().contains(EQUIPMENT_TAG)) {
            return oldMap;
        }
        if (!item.getTag().contains(WEAPON_TAG)) { //armour case
            for (Map.Entry<Attribute, AttributeModifier> entry : oldMap.entries()) {
                if (DEFENSIVE_ATTRIBUTES.contains(entry.getKey())) {
                    newMap.put(
                            entry.getKey(),
                            new AttributeModifier(
                                    Util.UUID_STAT_CONVERSION[slot.getIndex()],
                                    "conversion",
                                    getCollectedModifiers(oldMap.get(entry.getKey())),
                                    ADDITION));
                } else {
                    newMap.put(entry.getKey(), entry.getValue());
                }
            }
        }
        else { //weapon case
            boolean isRanged = item.getTag().getBoolean(RANGED_TAG);
            for (Map.Entry<Attribute, AttributeModifier> entry : oldMap.entries()) {
                if (OFFENSIVE_ATTRIBUTES.contains(entry.getKey())) {
                    if (isRanged && entry.getKey() != IDFAttributes.ACCURACY.get()) continue;
                    newMap.put(
                            entry.getKey(),
                            new AttributeModifier(Util.UUID_STAT_CONVERSION[slot.getIndex()],
                            "conversion",
                                    getCollectedModifiers(oldMap.get(entry.getKey())),
                                    ADDITION));
                } else {
                    newMap.put(entry.getKey(), entry.getValue());
                }
            }
        }
        return newMap;
    }

    public static double getCollectedModifiers(Collection<AttributeModifier> mods) {
        final double flat = mods.stream().filter((modifier) -> modifier.getOperation().equals(AttributeModifier.Operation.ADDITION)).mapToDouble(AttributeModifier::getAmount).sum();
        double f1 = flat + mods.stream().filter((modifier) -> modifier.getOperation().equals(AttributeModifier.Operation.MULTIPLY_BASE)).mapToDouble(AttributeModifier::getAmount).map((amount) -> amount * Math.abs(flat)).sum();
        double f2 = mods.stream().filter((modifier) -> modifier.getOperation().equals(AttributeModifier.Operation.MULTIPLY_TOTAL)).mapToDouble(AttributeModifier::getAmount).map((amount) -> amount + 1.0).reduce(1.0, (x, y) -> x * y);
        if (f1 < 0.0) {
            return f1 * (1/f2);
        }
        else return f1 * f2;
    }

}
