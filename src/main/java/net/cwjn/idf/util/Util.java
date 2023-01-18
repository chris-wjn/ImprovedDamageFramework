package net.cwjn.idf.util;

import com.google.common.collect.Multimap;
import com.google.gson.JsonArray;
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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static net.cwjn.idf.ImprovedDamageFramework.FONT_ICONS;
import static net.cwjn.idf.ImprovedDamageFramework.FONT_INDICATORS;
import static net.cwjn.idf.attribute.IDFAttributes.*;
import static net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.*;
import static net.minecraft.world.entity.ai.attributes.Attributes.*;

public class Util {

    private Util() {throw new IllegalAccessError("Util class");}
    private static final Style ICON = Style.EMPTY.withFont(FONT_ICONS);
    private static final Style INDICATOR = Style.EMPTY.withFont(FONT_INDICATORS);
    private static final DecimalFormat attributeFormat = new DecimalFormat("#.##");
    private static final DecimalFormat hundredFormat = new DecimalFormat("###");
    private static final Map<Attribute, UUID> baseStatUUIDs = new HashMap<>();
    private static final Map<Attribute, UUID> baseMultiplier1UUIDs = new HashMap<>();
    private static final Map<Attribute, UUID> baseMultiplier2UUIDs = new HashMap<>();

    static {
        baseStatUUIDs.put(ATTACK_DAMAGE, UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF")); //BASE_ATTACK_DAMAGE_UUID
        baseStatUUIDs.put(FIRE_DAMAGE.get(), UUID.fromString("f36ff4f2-8cbb-4a46-9657-a77676046872"));
        baseStatUUIDs.put(WATER_DAMAGE.get(), UUID.fromString("a301624b-8186-47e3-9aef-8cf6a2e01635"));
        baseStatUUIDs.put(LIGHTNING_DAMAGE.get(), UUID.fromString("20e62cd2-89a5-4107-a43d-3a6197bca618"));
        baseStatUUIDs.put(MAGIC_DAMAGE.get(), UUID.fromString("8104d749-42d6-40d4-9a41-51846ae62449"));
        baseStatUUIDs.put(DARK_DAMAGE.get(), UUID.fromString("b4a9113d-0049-46dc-83ac-4126475bc563"));
        baseStatUUIDs.put(ATTACK_SPEED, UUID.fromString("cc8c7274-ddbd-4a33-bbf8-0ce398d27b13"));
        baseStatUUIDs.put(ATTACK_KNOCKBACK, UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3")); //BASE_ATTACK_SPEED_SPEED
        baseStatUUIDs.put(FORCE.get(), UUID.fromString("4cd3bb13-f792-4e7e-bc8e-a4e87abd38e7"));
        baseStatUUIDs.put(LIFESTEAL.get(), UUID.fromString("40533165-2509-4ccb-923c-12cafe1b219b"));
        baseStatUUIDs.put(CRIT_CHANCE.get(), UUID.fromString("b980dbd0-a70a-42c3-9118-f40504ad9886"));
        baseStatUUIDs.put(PENETRATING.get(), UUID.fromString("cb4bd5b1-733a-4a10-b9c7-6352bb626b98"));
        baseStatUUIDs.put(ARMOR_TOUGHNESS, UUID.fromString("f354b836-5417-41df-9c9c-341b0fbb3289"));
        baseStatUUIDs.put(ARMOR, UUID.fromString("396cc31e-c4b7-4156-a3b9-97391ff46db8"));
        baseStatUUIDs.put(FIRE_RESISTANCE.get(), UUID.fromString("0bfb4c93-f3f0-430e-a07b-e716aeff2240"));
        baseStatUUIDs.put(WATER_RESISTANCE.get(), UUID.fromString("88bf93fe-247b-4378-ac8a-b8902df47332"));
        baseStatUUIDs.put(LIGHTNING_RESISTANCE.get(), UUID.fromString("f4339705-bde5-476a-bf62-f471332db68a"));
        baseStatUUIDs.put(MAGIC_RESISTANCE.get(), UUID.fromString("c50cadec-c12f-4837-86ce-50ad7563ad93"));
        baseStatUUIDs.put(DARK_RESISTANCE.get(), UUID.fromString("47c49fdf-6649-41db-9bd9-b3ea8026616a"));
        baseStatUUIDs.put(EVASION.get(), UUID.fromString("1f5f7bee-80a8-4e00-ac8f-9e1407ca4353"));
        baseStatUUIDs.put(MAX_HEALTH, UUID.fromString("4eb842bc-615a-469c-b693-c7849b39a8bd"));
        baseStatUUIDs.put(MOVEMENT_SPEED, UUID.fromString("e1b21b9f-8212-4e62-9ecd-67f8d6cb583b"));
        baseStatUUIDs.put(KNOCKBACK_RESISTANCE, UUID.fromString("8857d392-cb02-4d92-998b-56cf768c5334"));
        baseStatUUIDs.put(LUCK, UUID.fromString("a65ee3f5-2655-41e2-b233-3333620dbdd3"));
        baseStatUUIDs.put(STRIKE_MULT.get(), UUID.fromString("9f6ccdac-6035-4af7-a851-0a32086fbbcf"));
        baseStatUUIDs.put(PIERCE_MULT.get(), UUID.fromString("a3bb573c-3553-4f50-97e3-feaacf8ec504"));
        baseStatUUIDs.put(SLASH_MULT.get(), UUID.fromString("055b0d8f-f5ba-4c8f-9d0b-664e31821744"));
        baseStatUUIDs.put(CRUSH_MULT.get(), UUID.fromString("608491b9-6ba1-419a-9707-7ec698c3041f"));
        baseStatUUIDs.put(GENERIC_MULT.get(), UUID.fromString("81660c31-d54b-4dd0-8a3c-9c16a8a186ac"));
    }

    static {
        baseMultiplier1UUIDs.put(ATTACK_DAMAGE, UUID.fromString("f9a8d1e1-6a5f-4799-b5a5-b9f7c8e6acb7"));
        baseMultiplier1UUIDs.put(FIRE_DAMAGE.get(), UUID.fromString("e9b7c6d2-3f4a-4c51-9a6f-c8e5d7f6b9c8"));
        baseMultiplier1UUIDs.put(WATER_DAMAGE.get(), UUID.fromString("d8c6b5e3-2e3b-3d40-8a5e-b7d4c6e5a8b9"));
        baseMultiplier1UUIDs.put(LIGHTNING_DAMAGE.get(), UUID.fromString("c7d5a4f4-1d2c-2c3f-7a4d-a6c3b5d495a8"));
        baseMultiplier1UUIDs.put(MAGIC_DAMAGE.get(), UUID.fromString("b6e493e5-0c1d-1b2e-6a3c-95b2a4c384b7"));
        baseMultiplier1UUIDs.put(DARK_DAMAGE.get(), UUID.fromString("a5f382f6-fb0e-0a1f-5a2b-84a1b3c273b6"));
        baseMultiplier1UUIDs.put(ATTACK_SPEED, UUID.fromString("95e271f7-ea0f-f910-4a1a-73a0c2b162b5"));
        baseMultiplier1UUIDs.put(ATTACK_KNOCKBACK, UUID.fromString("84d160f8-d900-e801-3a09-62a1b1b051b4"));
        baseMultiplier1UUIDs.put(FORCE.get(), UUID.fromString("73c04ff9-c701-d702-2a08-51a0c0a040b3"));
        baseMultiplier1UUIDs.put(LIFESTEAL.get(), UUID.fromString("62b03efa-b602-c603-1a07-40a1b09030b2"));
        baseMultiplier1UUIDs.put(CRIT_CHANCE.get(), UUID.fromString("51a02dfb-a503-b504-0a06-30a2a08020b1"));
        baseMultiplier1UUIDs.put(PENETRATING.get(), UUID.fromString("40a01cfc-9404-a405-fa05-20a3a07030b0"));
        baseMultiplier1UUIDs.put(ARMOR_TOUGHNESS, UUID.fromString("30a00bfd-8305-9306-ea04-10a4a06040af"));
        baseMultiplier1UUIDs.put(ARMOR, UUID.fromString("20a09afe-7206-8207-da03-00a5a05050ae"));
        baseMultiplier1UUIDs.put(FIRE_RESISTANCE.get(), UUID.fromString("10b08afd-6107-7108-ca02-f0b4a04040ad"));
        baseMultiplier1UUIDs.put(WATER_RESISTANCE.get(), UUID.fromString("00c07bee-5008-6109-ba01-e0c3a03030ac"));
        baseMultiplier1UUIDs.put(LIGHTNING_RESISTANCE.get(), UUID.fromString("f0d06afd-3f09-510a-aa00-d0d2a02020ab"));
        baseMultiplier1UUIDs.put(MAGIC_RESISTANCE.get(), UUID.fromString("e0e05bfe-2e0a-400b-9a0f-c0e1a01010aa"));
        baseMultiplier1UUIDs.put(DARK_RESISTANCE.get(), UUID.fromString("d0f04cff-1d0b-300c-8a0e-b0f09f000a9a"));
        baseMultiplier1UUIDs.put(EVASION.get(), UUID.fromString("c0803c00-0c0c-200d-7a0d-a0e09e010a89"));
        baseMultiplier1UUIDs.put(MAX_HEALTH, UUID.fromString("b0902b01-fb0d-100e-6a0c-90d08d020a78"));
        baseMultiplier1UUIDs.put(MOVEMENT_SPEED, UUID.fromString("a1c2d3e4-5f6g-7h8i-9j0k-a1b2c3d4e5f6"));
        baseMultiplier1UUIDs.put(KNOCKBACK_RESISTANCE, UUID.fromString("b2d3e4f5-6g7h-8i9j-0ka1-b2c3d4e5f6g7"));
        baseMultiplier1UUIDs.put(LUCK, UUID.fromString("c3e4f5g6-7h8i-9j0k-a1b2-c3d4e5f6g7h8"));
        baseMultiplier1UUIDs.put(STRIKE_MULT.get(), UUID.fromString("d4f5g6h7-8i9j-0ka1-b2c3-d4e5f6g7h8i9"));
        baseMultiplier1UUIDs.put(PIERCE_MULT.get(), UUID.fromString("e5g6h7i8-9j0k-a1b2-c3d4-e5f6g7h8i9j0"));
        baseMultiplier1UUIDs.put(SLASH_MULT.get(), UUID.fromString("f6h7i8j9-0ka1-b2c3-d4e5-f6g7h8i9j0k1"));
        baseMultiplier1UUIDs.put(CRUSH_MULT.get(), UUID.fromString("g7i8j9k0-a1b2-c3d4-e5f6-g7h8i9j0k1a2"));
        baseMultiplier1UUIDs.put(GENERIC_MULT.get(), UUID.fromString("h8j9k0l1-b2c3-d4e5-f6g7-h8i9j0k1a2b3"));
    }

    static {
        baseMultiplier2UUIDs.put(ATTACK_DAMAGE, UUID.fromString("a1b2c3d4-e5f6-a7b8-c9d0-e1f2a3b4c5d6"));
        baseMultiplier2UUIDs.put(FIRE_DAMAGE.get(), UUID.fromString("b1c2d3e4-f5e7-b6a8-d9c0-f2e1a3b4c5d6"));
        baseMultiplier2UUIDs.put(WATER_DAMAGE.get(), UUID.fromString("c1d2e3f4-e5d7-c5b8-e9b0-e3d2a3b4c5d6"));
        baseMultiplier2UUIDs.put(LIGHTNING_DAMAGE.get(), UUID.fromString("d1e2f3g4-d5c7-d4a8-f9a0-d4c2a3b4c5d6"));
        baseMultiplier2UUIDs.put(MAGIC_DAMAGE.get(), UUID.fromString("e1f2g3h4-c5b7-e3a8-g990-c5b2a3b4c5d6"));
        baseMultiplier2UUIDs.put(DARK_DAMAGE.get(), UUID.fromString("f1g2h3i4-b4a7-f2a8-h980-b4a2a3b4c5d6"));
        baseMultiplier2UUIDs.put(ATTACK_SPEED, UUID.fromString("g1h2i3j4-a3b6-g1a8-i970-a3b1a3b4c5d6"));
        baseMultiplier2UUIDs.put(ATTACK_KNOCKBACK, UUID.fromString("h1i2j3k4-b2c5-h1b8-j960-b2c1a3b4c5d6"));
        baseMultiplier2UUIDs.put(FORCE.get(), UUID.fromString("i1j2k3l4-c1d4-i1c8-k950-c1d1a3b4c5d6"));
        baseMultiplier2UUIDs.put(LIFESTEAL.get(), UUID.fromString("j1k2l3m4-d1e3-j1d8-l940-d1e1a3b4c5d6"));
        baseMultiplier2UUIDs.put(CRIT_CHANCE.get(), UUID.fromString("k1l2m3n4-e1f2-k1e8-m930-e1f1a3b4c5d6"));
        baseMultiplier2UUIDs.put(PENETRATING.get(), UUID.fromString("l1m2n3o4-f1g1-l1n3o4-g1h0-m920-f1g1a3b4c5d6"));
        baseMultiplier2UUIDs.put(ARMOR_TOUGHNESS, UUID.fromString("m1n2o3p4-h1i9-n910-h1i1a3b4c5d6"));
        baseMultiplier2UUIDs.put(ARMOR, UUID.fromString("n1o2p3q4-i8j7-o900-i8j1a3b4c5d6"));
        baseMultiplier2UUIDs.put(FIRE_RESISTANCE.get(), UUID.fromString("o1p2q3r4-j6k5-p8f0-j6k1a3b4c5d6"));
        baseMultiplier2UUIDs.put(WATER_RESISTANCE.get(), UUID.fromString("p1q2r3s4-k4l3-q7e0-k4l1a3b4c5d6"));
        baseMultiplier2UUIDs.put(LIGHTNING_RESISTANCE.get(), UUID.fromString("q1r2s3t4-l2m1-r6d0-l2m1a3b4c5d6"));
        baseMultiplier2UUIDs.put(MAGIC_RESISTANCE.get(), UUID.fromString("r1s2t3u4-m0n9-s5c0-m0n1a3b4c5d6"));
        baseMultiplier2UUIDs.put(DARK_RESISTANCE.get(), UUID.fromString("s1t2u3v4-n8o7-t4b0-n8o1a3b4c5d6"));
        baseMultiplier2UUIDs.put(EVASION.get(), UUID.fromString("d0803c00-0c0c-200d-7a0d-a0e09e010a89"));
        baseMultiplier2UUIDs.put(MAX_HEALTH, UUID.fromString("e0902b01-fb0d-100e-6a0c-90d08d020a78"));
        baseMultiplier2UUIDs.put(MOVEMENT_SPEED, UUID.fromString("f1c2d3e4-5f6g-7h8i-9j0k-a1b2c3d4e5f6"));
        baseMultiplier2UUIDs.put(KNOCKBACK_RESISTANCE, UUID.fromString("g2d3e4f5-6g7h-8i9j-0ka1-b2c3d4e5f6g7"));
        baseMultiplier2UUIDs.put(LUCK, UUID.fromString("h3e4f5g6-7h8i-9j0k-a1b2-c3d4e5f6g7h8"));
        baseMultiplier2UUIDs.put(STRIKE_MULT.get(), UUID.fromString("i4f5g6h7-8i9j-0ka1-b2c3-d4e5f6g7h8i9"));
        baseMultiplier2UUIDs.put(PIERCE_MULT.get(), UUID.fromString("j5g6h7i8-9j0k-a1b2-c3d4-e5f6g7h8i9j0"));
        baseMultiplier2UUIDs.put(SLASH_MULT.get(), UUID.fromString("k6h7i8j9-0ka1-b2c3-d4e5-f6g7h8i9j0k1"));
        baseMultiplier2UUIDs.put(CRUSH_MULT.get(), UUID.fromString("l7i8j9k0-a1b2-c3d4-e5f6-g7h8i9j0k1a2"));
        baseMultiplier2UUIDs.put(GENERIC_MULT.get(), UUID.fromString("m8j9k0l1-b2c3-d4e5-f6g7-h8i9j0k1a2b3"));

    }

    static {
        baseMultiplier1UUIDs.put(ATTACK_DAMAGE, UUID.fromString("b9a8d1e1-6a5f-4799-b5a5-b9f7c8e6acb7"));
        baseMultiplier1UUIDs.put(FIRE_DAMAGE.get(), UUID.fromString("c9b7c6d2-3f4a-4c51-9a6f-c8e5d7f6b9c8"));
        baseMultiplier1UUIDs.put(WATER_DAMAGE.get(), UUID.fromString("d8c6b5e3-2e3b-3d40-8a5e-b7d4c6e5a8b9"));
        baseMultiplier1UUIDs.put(LIGHTNING_DAMAGE.get(), UUID.fromString("e7d5a4f4-1d2c-2c3f-7a4d-a6c3b5d495a8"));
        baseMultiplier1UUIDs.put(MAGIC_DAMAGE.get(), UUID.fromString("f6e493e5-0c1d-1b2e-6a3c-95b2a4c384b7"));
        baseMultiplier1UUIDs.put(DARK_DAMAGE.get(), UUID.fromString("a5f382f6-fb0e-0a1f-5a2b-84a1b3c273b6"));
        baseMultiplier1UUIDs.put(ATTACK_SPEED, UUID.fromString("a5e271f7-ea0f-f910-4a1a-73a0c2b162b5"));
        baseMultiplier1UUIDs.put(ATTACK_KNOCKBACK, UUID.fromString("a4d160f8-d900-e801-3a09-62a1b1b051b4"));
        baseMultiplier1UUIDs.put(FORCE.get(), UUID.fromString("a3c04ff9-c701-d702-2a08-51a0c0a040b3"));
        baseMultiplier1UUIDs.put(LIFESTEAL.get(), UUID.fromString("a2b03efa-b602-c603-1a07-40a1b09030b2"));
        baseMultiplier1UUIDs.put(CRIT_CHANCE.get(), UUID.fromString("a1a02dfb-a503-b504-0a06-30a2a08020b1"));
        baseMultiplier1UUIDs.put(PENETRATING.get(), UUID.fromString("a0a01cfc-9404-a405-fa05-20a3a07030b0"));
        baseMultiplier1UUIDs.put(ARMOR_TOUGHNESS, UUID.fromString("a0a00bfd-8305-9306-ea04-10a4a06040af"));
        baseMultiplier1UUIDs.put(ARMOR, UUID.fromString("b0a09afe-7206-8207-da03-00a5a05050ae"));
        baseMultiplier1UUIDs.put(FIRE_RESISTANCE.get(), UUID.fromString("c0b08afd-6107-7108-ca02-f0b4a04040ad"));
        baseMultiplier1UUIDs.put(WATER_RESISTANCE.get(), UUID.fromString("d0c07bee-5008-6109-ba01-e0c3a03030ac"));
        baseMultiplier1UUIDs.put(LIGHTNING_RESISTANCE.get(), UUID.fromString("e0d06afd-3f09-510a-aa00-d0d2a02020ab"));
        baseMultiplier1UUIDs.put(MAGIC_RESISTANCE.get(), UUID.fromString("f0e05bfe-2e0a-400b-9a0f-c0e1a01010aa"));
        baseMultiplier1UUIDs.put(DARK_RESISTANCE.get(), UUID.fromString("g0f04cff-1d0b-300c-8a0e-b0f0a00020a9"));
    }

    public static UUID getUUID(Attribute a, AttributeModifier.Operation o) {
        if (o == ADDITION) {
            return baseStatUUIDs.get(a);
        } else if (o == MULTIPLY_BASE) {
            return baseMultiplier1UUIDs.get(a);
        } else {
            return baseMultiplier2UUIDs.get(a);
        }
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
