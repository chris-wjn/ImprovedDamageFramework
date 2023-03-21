package net.cwjn.idf.util;

import com.google.common.collect.Multimap;
import com.mojang.blaze3d.vertex.PoseStack;
import net.cwjn.idf.attribute.IDFAttributes;
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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;

import static net.cwjn.idf.ImprovedDamageFramework.*;
import static net.cwjn.idf.attribute.IDFAttributes.*;
import static net.cwjn.idf.event.ClientEventsForgeBus.ICON_PIXEL_SPACER;
import static net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.*;
import static net.minecraft.world.entity.ai.attributes.Attributes.*;

public class Util {

    private Util() {throw new IllegalAccessError("Util class");}
    private static final Style ICON = Style.EMPTY.withFont(FONT_ICONS);
    private static final Style INDICATOR = Style.EMPTY.withFont(FONT_INDICATORS);
    private static final Style TOOLTIP = Style.EMPTY.withFont(FONT_TOOLTIPS);
    private static final Style SPACER = Style.EMPTY.withFont(FONT_SPACER);
    private static final Style DEFAULT = Style.EMPTY.withFont(Style.DEFAULT_FONT);
    private static final DecimalFormat tenths = new DecimalFormat("#.#");
    private static final DecimalFormat hundredths = new DecimalFormat("#.##");
    private static final DecimalFormat hundredFormat = new DecimalFormat("###");
    public static final List<Component> damageTooltip = new ArrayList<>();
    public static final List<Component> resistanceTooltip = new ArrayList<>();
    public static final UUID[] UUID_BASE_STAT_ADDITION = {
            UUID.fromString("55CEEB33-BEFB-41DF-BF9F-0E805BA1B6F7"),
            UUID.fromString("132DB4C0-8CD5-46EE-B7A6-48CCFD11B1F0"),
            UUID.fromString("0D6AB740-41B9-4BDE-ADBA-BAAB28623C6E"),
            UUID.fromString("55CEEB33-BEFB-41DF-BF9F-0E805BA1B6F8"),
            UUID.fromString("0D6AB740-41B9-4BDE-ADBA-BAAB28623C6F"),
            UUID.fromString("BCAF7601-AC93-4705-8F3A-51CA50281AC6")};
    public static final UUID[] UUID_BASE_STAT_MULTIPLY_BASE = {
            UUID.fromString("55CEEB33-BEFB-41DF-BF9F-0E805BA1B6F8"),
            UUID.fromString("132DB4C0-8CD5-46EE-B7A6-48CCFD11B1F1"),
            UUID.fromString("0D6AB740-41B9-4BDE-ADBA-BAAB28623C6F"),
            UUID.fromString("55CEEB33-BEFB-41DF-BF9F-0E805BA1B6F9"),
            UUID.fromString("0D6AB740-41B9-4BDE-ADBA-BAAB28623C61"),
            UUID.fromString("BCAF7601-AC93-4705-8F3A-51CA50281AC7")};
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

    static {
        MutableComponent damageComponent = Util.text(" ");
        damageComponent.append(Util.translation("idf.icon.damage").withStyle(ICON));
        damageComponent.append(Util.translation("idf.damage.tooltip"));
        damageTooltip.add(damageComponent);
        MutableComponent resistanceComponent = Util.text(" ");
        damageComponent.append(Util.translation("idf.icon.resistance").withStyle(ICON));
        damageComponent.append(Util.translation("idf.resistance.tooltip"));
        resistanceTooltip.add(resistanceComponent);
    }

    public static List<Component> getNewDamageTooltip() {
        return List.copyOf(damageTooltip);
    }

    public static List<Component> getNewResistanceTooltip() {
        return List.copyOf(resistanceTooltip);
    }

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
        return Component.translatable(main, additions);
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
            if (entry.getKey() == FIRE_DAMAGE.get()) {
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
            } else if (entry.getKey() == ATTACK_DAMAGE) {
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
        Following 2 methods written by mickelus, author of MUtil, Tetra, and Scroll of Harvest. The GOAT, as they say.
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

    public static List<Component> format(List<Component> list) {
        List<Component> returnList = new ArrayList<>();
        for (int i = 0; i < list.size(); ++i) {
            MutableComponent c = (MutableComponent) list.get(i);
            if (i < list.size()-1) {
                c.append(Util.withColor(Util.text(" | "), Color.LIGHTGOLDENRODYELLOW));
            }
            returnList.add(c);
        }
        return returnList;
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
        MutableComponent returnComponent = text("");
        boolean hasAddition = addition != 0, hasBaseMult = base != 0, hasTotalMult = total != 0;
        boolean hasNothing = true;
        //if (hasAddition || hasTotalMult || hasBaseMult) returnComponent.append(translationComponent("idf.icon." + a.getDescriptionId()).withStyle(ICON));
        if (hasAddition) {
            hasNothing = false;
            if (addition < 0) {
                returnComponent.append(withColor(text(hundredths.format(addition)), Color.RED).withStyle(INDICATOR));
            } else {
                returnComponent.append(withColor(text("+" + hundredths.format(addition)), Color.LIGHTGREEN).withStyle(INDICATOR));
            }
            if (hasBaseMult || hasTotalMult) returnComponent.append(", ");
        }
        if (hasBaseMult) {
            hasNothing = false;
            if (base < 0) {
                returnComponent.append(withColor(translation("idf.base_multiplication"), Color.RED));
                returnComponent.append(withColor(text(hundredths.format(base)), Color.RED).withStyle(INDICATOR));
                returnComponent.append(withColor(text(")"), Color.RED));
            } else {
                returnComponent.append(withColor(translation("idf.base_multiplication"), Color.LIGHTGREEN));
                returnComponent.append(withColor(text(hundredths.format(base)), Color.LIGHTGREEN).withStyle(INDICATOR));
                returnComponent.append(withColor(text(")"), Color.LIGHTGREEN));
            }
            if (hasTotalMult) returnComponent.append(", ");
        }
        if (hasTotalMult) {
            hasNothing = false;
            if (total < 0) {
                returnComponent.append(withColor(translation("idf.total_multiplication"), Color.RED));
                returnComponent.append(withColor(text(hundredths.format(total)), Color.RED).withStyle(INDICATOR));
                returnComponent.append(withColor(text(")"), Color.RED));
            } else {
                returnComponent.append(withColor(translation("idf.total_multiplication"), Color.LIGHTGREEN));
                returnComponent.append(withColor(text(hundredths.format(total)), Color.LIGHTGREEN).withStyle(INDICATOR));
                returnComponent.append(withColor(text(")"), Color.LIGHTGREEN));
            }
        }
        if (hasNothing) {
            returnComponent.append(withColor(text("0"), Color.WHITESMOKE).withStyle(INDICATOR));
        }
        return returnComponent;
    }

    public static String threeDigit(int in) {
        StringBuilder number = new StringBuilder(String.valueOf(in));
        int i = number.length();
        int c = number.toString().contains(".") ? 4 : 3;
        if (c == 3 && i > 3) return "999";
        if (c == 3 && i != 3) number.append(".");
        if (i <= c) {
            number.append("\uF803");
            number.append("0".repeat(Math.max(0, c - i)));
        } else {
            number = new StringBuilder(number.substring(0, 3));
        }
        return number.toString();
    }

    public static MutableComponent writeTooltipInteger(int num) {
        MutableComponent comp = Util.text("").withStyle(TOOLTIP);
        String s = String.valueOf(num);
        for(int i = 0; i < s.length() ; i++) {
            comp.append(String.valueOf(s.charAt(i)));
            if (i != s.length()-1) {
                comp.append(translation("space.-1").withStyle(SPACER));
            }
        }
        return comp;
    }

    public static MutableComponent writeTooltipDouble(double num) {
        MutableComponent comp = Util.text("(").withStyle(TOOLTIP);
        String s = tenths.format(num);
        for(int i = 0; i < s.length() ; i++) {
            comp.append(String.valueOf(s.charAt(i)));
            if (i != s.length()-1) {
                comp.append(translation("space.-1").withStyle(SPACER));
            }
        }
        return comp.append(")");
    }

    public static MutableComponent writeStaticTooltipComponent(double n, String name, @Nullable Color colour, boolean isPercentage, boolean drawBorders) {
        String num = String.valueOf(n);
        MutableComponent comp = text(drawBorders? "|" : "").withStyle(DEFAULT).append(writeIcon(name));
        if (colour == null) {
            comp.append(text(num));
            if (isPercentage) comp.append("%");
        }
        else {
            comp.append(withColor(text(num), colour));
            if (isPercentage) comp.append("%");
        }
        for (int i = 1; i <= ((drawBorders? 6 : 5 + (isPercentage? 0 : 1))-num.length()); ++i) {
            comp.append(" ");
        }
        return drawBorders? comp.append("|") : comp;
    }

    public static MutableComponent writeStaticInfinityComponent(Color colour, boolean drawBorders) {
        MutableComponent comp = text(drawBorders? "|" : "").append(withColor(writeIcon("infinity.symbol"), colour));
        for (int i = 1; i <= ((drawBorders? 6 : 5)-1); ++i) {
            comp.append(" ");
        }
        return drawBorders? comp.append("|") : comp;
    }

    public static MutableComponent writeIcon(String name) {
        MutableComponent comp = translation("space." + ICON_PIXEL_SPACER).withStyle(SPACER);
        MutableComponent comp1 = translation("idf.icon." + name).withStyle(ICON);
        return comp.append(comp1);
    }

}
