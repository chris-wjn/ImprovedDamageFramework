package net.cwjn.idf;

import com.mojang.blaze3d.vertex.PoseStack;
import net.cwjn.idf.attribute.IDFAttributes;
import net.cwjn.idf.gui.StatsScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.LiteralContents;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.function.Supplier;

public class Util {

    private Util() {throw new IllegalAccessError("Util class");}
    private static final DecimalFormat attributeFormat = new DecimalFormat("###");

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

    public static void drawCenteredString(Font font, PoseStack matrix, String text, float x, float y, int colour) {
        MutableComponent component = textComponent(text);
        font.draw(matrix, component, (x - (float) font.width(component) / 2), y, colour);
    }

    public static void drawCenteredString(Font font, PoseStack matrix, Component component, float x, float y, int colour) {
        font.draw(matrix, component, (x - (float) font.width(component) / 2), y, colour);
    }

    public static ResourceLocation getEntityRegistryName(EntityType<?> type) {
        return ForgeRegistries.ENTITY_TYPES.getKey(type);
    }

    public static ResourceLocation getItemRegistryName(Item item) {
        return ForgeRegistries.ITEMS.getKey(item);
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

}
