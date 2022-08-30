package net.cwjn.idf.util;

import com.google.common.primitives.UnsignedInts;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;
//written by SilentChaos512 as part of silentutils
public class Color {
    private static final Map<String, Color> NAMED_MAP = new HashMap();
    private static final Pattern PATTERN_LEADING_JUNK = Pattern.compile("(#|0x)", 2);
    private static final Pattern PATTERN_HEX_CODE = Pattern.compile("(#|0x)?[0-9a-f]{1,8}", 2);
    public static final int VALUE_WHITE = 16777215;
    public static final Color ALICEBLUE = named("AliceBlue", 15792383);
    public static final Color ANTIQUEWHITE = named("AntiqueWhite", 16444375);
    public static final Color AQUA = named("Aqua", 65535);
    public static final Color AQUAMARINE = named("Aquamarine", 8388564);
    public static final Color AZURE = named("Azure", 15794175);
    public static final Color BEIGE = named("Beige", 16119260);
    public static final Color BISQUE = named("Bisque", 16770244);
    public static final Color BLACK = named("Black", 0);
    public static final Color BLANCHEDALMOND = named("BlanchedAlmond", 16772045);
    public static final Color BLUE = named("Blue", 255);
    public static final Color BLUEVIOLET = named("BlueViolet", 9055202);
    public static final Color BROWN = named("Brown", 10824234);
    public static final Color BURLYWOOD = named("BurlyWood", 14596231);
    public static final Color CADETBLUE = named("CadetBlue", 6266528);
    public static final Color CHARTREUSE = named("Chartreuse", 8388352);
    public static final Color CHOCOLATE = named("Chocolate", 13789470);
    public static final Color CORAL = named("Coral", 16744272);
    public static final Color CORNFLOWERBLUE = named("CornflowerBlue", 6591981);
    public static final Color CORNSILK = named("Cornsilk", 16775388);
    public static final Color CRIMSON = named("Crimson", 14423100);
    public static final Color CYAN = named("Cyan", 65535);
    public static final Color DARKBLUE = named("DarkBlue", 139);
    public static final Color DARKCYAN = named("DarkCyan", 35723);
    public static final Color DARKGOLDENROD = named("DarkGoldenRod", 12092939);
    public static final Color DARKGRAY = named("DarkGray", 11119017);
    public static final Color DARKGREY = named("DarkGrey", 11119017);
    public static final Color DARKGREEN = named("DarkGreen", 25600);
    public static final Color DARKKHAKI = named("DarkKhaki", 12433259);
    public static final Color DARKMAGENTA = named("DarkMagenta", 9109643);
    public static final Color DARKOLIVEGREEN = named("DarkOliveGreen", 5597999);
    public static final Color DARKORANGE = named("DarkOrange", 16747520);
    public static final Color DARKORCHID = named("DarkOrchid", 10040012);
    public static final Color DARKRED = named("DarkRed", 9109504);
    public static final Color DARKSALMON = named("DarkSalmon", 15308410);
    public static final Color DARKSEAGREEN = named("DarkSeaGreen", 9419919);
    public static final Color DARKSLATEBLUE = named("DarkSlateBlue", 4734347);
    public static final Color DARKSLATEGRAY = named("DarkSlateGray", 3100495);
    public static final Color DARKSLATEGREY = named("DarkSlateGrey", 3100495);
    public static final Color DARKTURQUOISE = named("DarkTurquoise", 52945);
    public static final Color DARKVIOLET = named("DarkViolet", 9699539);
    public static final Color DEEPPINK = named("DeepPink", 16716947);
    public static final Color DEEPSKYBLUE = named("DeepSkyBlue", 49151);
    public static final Color DIMGRAY = named("DimGray", 6908265);
    public static final Color DIMGREY = named("DimGrey", 6908265);
    public static final Color DODGERBLUE = named("DodgerBlue", 2003199);
    public static final Color FIREBRICK = named("FireBrick", 11674146);
    public static final Color FLORALWHITE = named("FloralWhite", 16775920);
    public static final Color FORESTGREEN = named("ForestGreen", 2263842);
    public static final Color FUCHSIA = named("Fuchsia", 16711935);
    public static final Color GAINSBORO = named("Gainsboro", 14474460);
    public static final Color GHOSTWHITE = named("GhostWhite", 16316671);
    public static final Color GOLD = named("Gold", 16766720);
    public static final Color GOLDENROD = named("GoldenRod", 14329120);
    public static final Color GRAY = named("Gray", 8421504);
    public static final Color GREY = named("Grey", 8421504);
    public static final Color GREEN = named("Green", 32768);
    public static final Color GREENYELLOW = named("GreenYellow", 11403055);
    public static final Color HONEYDEW = named("HoneyDew", 15794160);
    public static final Color HOTPINK = named("HotPink", 16738740);
    public static final Color INDIANRED = named("IndianRed", 13458524);
    public static final Color INDIGO = named("Indigo", 4915330);
    public static final Color IVORY = named("Ivory", 16777200);
    public static final Color KHAKI = named("Khaki", 15787660);
    public static final Color LAVENDER = named("Lavender", 15132410);
    public static final Color LAVENDERBLUSH = named("LavenderBlush", 16773365);
    public static final Color LAWNGREEN = named("LawnGreen", 8190976);
    public static final Color LEMONCHIFFON = named("LemonChiffon", 16775885);
    public static final Color LIGHTBLUE = named("LightBlue", 11393254);
    public static final Color LIGHTCORAL = named("LightCoral", 15761536);
    public static final Color LIGHTCYAN = named("LightCyan", 14745599);
    public static final Color LIGHTGOLDENRODYELLOW = named("LightGoldenRodYellow", 16448210);
    public static final Color LIGHTGRAY = named("LightGray", 13882323);
    public static final Color LIGHTGREY = named("LightGrey", 13882323);
    public static final Color LIGHTGREEN = named("LightGreen", 9498256);
    public static final Color LIGHTPINK = named("LightPink", 16758465);
    public static final Color LIGHTSALMON = named("LightSalmon", 16752762);
    public static final Color LIGHTSEAGREEN = named("LightSeaGreen", 2142890);
    public static final Color LIGHTSKYBLUE = named("LightSkyBlue", 8900346);
    public static final Color LIGHTSLATEGRAY = named("LightSlateGray", 7833753);
    public static final Color LIGHTSLATEGREY = named("LightSlateGrey", 7833753);
    public static final Color LIGHTSTEELBLUE = named("LightSteelBlue", 11584734);
    public static final Color LIGHTYELLOW = named("LightYellow", 16777184);
    public static final Color LIME = named("Lime", 65280);
    public static final Color LIMEGREEN = named("LimeGreen", 3329330);
    public static final Color LINEN = named("Linen", 16445670);
    public static final Color MAGENTA = named("Magenta", 16711935);
    public static final Color MAROON = named("Maroon", 8388608);
    public static final Color MEDIUMAQUAMARINE = named("MediumAquaMarine", 6737322);
    public static final Color MEDIUMBLUE = named("MediumBlue", 205);
    public static final Color MEDIUMORCHID = named("MediumOrchid", 12211667);
    public static final Color MEDIUMPURPLE = named("MediumPurple", 9662683);
    public static final Color MEDIUMSEAGREEN = named("MediumSeaGreen", 3978097);
    public static final Color MEDIUMSLATEBLUE = named("MediumSlateBlue", 8087790);
    public static final Color MEDIUMSPRINGGREEN = named("MediumSpringGreen", 64154);
    public static final Color MEDIUMTURQUOISE = named("MediumTurquoise", 4772300);
    public static final Color MEDIUMVIOLETRED = named("MediumVioletRed", 13047173);
    public static final Color MIDNIGHTBLUE = named("MidnightBlue", 1644912);
    public static final Color MINTCREAM = named("MintCream", 16121850);
    public static final Color MISTYROSE = named("MistyRose", 16770273);
    public static final Color MOCCASIN = named("Moccasin", 16770229);
    public static final Color NAVAJOWHITE = named("NavajoWhite", 16768685);
    public static final Color NAVY = named("Navy", 128);
    public static final Color OLDLACE = named("OldLace", 16643558);
    public static final Color OLIVE = named("Olive", 8421376);
    public static final Color OLIVEDRAB = named("OliveDrab", 7048739);
    public static final Color ORANGE = named("Orange", 16753920);
    public static final Color ORANGERED = named("OrangeRed", 16729344);
    public static final Color ORCHID = named("Orchid", 14315734);
    public static final Color PALEGOLDENROD = named("PaleGoldenRod", 15657130);
    public static final Color PALEGREEN = named("PaleGreen", 10025880);
    public static final Color PALETURQUOISE = named("PaleTurquoise", 11529966);
    public static final Color PALEVIOLETRED = named("PaleVioletRed", 14381203);
    public static final Color PAPAYAWHIP = named("PapayaWhip", 16773077);
    public static final Color PEACHPUFF = named("PeachPuff", 16767673);
    public static final Color PERU = named("Peru", 13468991);
    public static final Color PINK = named("Pink", 16761035);
    public static final Color PLUM = named("Plum", 14524637);
    public static final Color POWDERBLUE = named("PowderBlue", 11591910);
    public static final Color PURPLE = named("Purple", 8388736);
    public static final Color DARKPURPLE = named("DarkPurple", 3152180);
    public static final Color REBECCAPURPLE = named("RebeccaPurple", 6697881);
    public static final Color RED = named("Red", 16711680);
    public static final Color ROSYBROWN = named("RosyBrown", 12357519);
    public static final Color ROYALBLUE = named("RoyalBlue", 4286945);
    public static final Color SADDLEBROWN = named("SaddleBrown", 9127187);
    public static final Color SALMON = named("Salmon", 16416882);
    public static final Color SANDYBROWN = named("SandyBrown", 16032864);
    public static final Color SEAGREEN = named("SeaGreen", 3050327);
    public static final Color SEASHELL = named("SeaShell", 16774638);
    public static final Color SIENNA = named("Sienna", 10506797);
    public static final Color SILVER = named("Silver", 12632256);
    public static final Color SKYBLUE = named("SkyBlue", 8900331);
    public static final Color SLATEBLUE = named("SlateBlue", 6970061);
    public static final Color SLATEGRAY = named("SlateGray", 7372944);
    public static final Color SLATEGREY = named("SlateGrey", 7372944);
    public static final Color SNOW = named("Snow", 16775930);
    public static final Color SPRINGGREEN = named("SpringGreen", 65407);
    public static final Color STEELBLUE = named("SteelBlue", 4620980);
    public static final Color TAN = named("Tan", 13808780);
    public static final Color TEAL = named("Teal", 32896);
    public static final Color THISTLE = named("Thistle", 14204888);
    public static final Color TOMATO = named("Tomato", 16737095);
    public static final Color TURQUOISE = named("Turquoise", 4251856);
    public static final Color VIOLET = named("Violet", 15631086);
    public static final Color WHEAT = named("Wheat", 16113331);
    public static final Color WHITE = named("White", 16777215);
    public static final Color WHITESMOKE = named("WhiteSmoke", 16119285);
    public static final Color YELLOW = named("Yellow", 16776960);
    public static final Color YELLOWGREEN = named("YellowGreen", 10145074);
    public static final Color ENCHANTINGGRAY = named("EnchantingGray", 7368816);
    public static final Color MAGICBLUE = named("MagicBlue", 2477509);
    public static final Color WEAKPURPLE = named("WeakPurple", 8015744);
    public static final Color FULLPURPLE = named("FullPurple", 6684788);
    public static final Color PHYSICAL_COLOUR = named("PhysicalColour", 11181194);
    public static final Color FIRE_COLOUR = named("FireColour", 16753920);
    public static final Color WATER_COLOUR = named("WaterColour", 3044334);
    public static final Color LIGHTNING_COLOUR = named("LightningColour", 16769397);
    public static final Color MAGIC_COLOUR = named("MagicColour", 11796455);
    public static final Color DARK_COLOUR = named("DarkColour", 5908062);
    private final int color;
    private final int red;
    private final int green;
    private final int blue;
    private final int alpha;

    public Color(int color) {
        this.red = color >> 16 & 255;
        this.green = color >> 8 & 255;
        this.blue = color & 255;
        int a = color >> 24 & 255;
        this.alpha = a > 0 ? a : 255;
        this.color = this.alpha << 24 | color & 16777215;
    }

    public Color(int red, int green, int blue) {
        this(red, green, blue, 255);
    }

    public Color(int red, int green, int blue, int alpha) {
        this.alpha = alpha > 0 ? alpha : 255;
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.color = this.alpha << 24 | this.red << 16 | this.green << 8 | this.blue;
    }

    public Color(float red, float green, float blue) {
        this(red, green, blue, 1.0F);
    }

    public Color(float red, float green, float blue, float alpha) {
        this((int)(red * 255.0F), (int)(green * 255.0F), (int)(blue * 255.0F), (int)(alpha * 255.0F));
    }

    private static Color named(String name, int color) {
        Color c = new Color(color);
        NAMED_MAP.put(name.toLowerCase(Locale.ROOT), c);
        return c;
    }

    public static boolean validate(String str) {
        return NAMED_MAP.containsKey(str.toLowerCase(Locale.ROOT)) || PATTERN_HEX_CODE.matcher(str).matches();
    }

    public static String format(int color) {
        return String.format(color > 16777215 ? "#%08X" : "#%06X", color);
    }

    public static Color parse(String str) {
        return new Color(parseInt(str));
    }

    public static Color tryParse(String str, int defaultValue) {
        return !validate(str) ? new Color(defaultValue) : parse(str);
    }

    public static int parseInt(String str) {
        str = str.toLowerCase(Locale.ROOT);
        if (NAMED_MAP.containsKey(str)) {
            return ((Color)NAMED_MAP.get(str)).getColor();
        } else {
            str = PATTERN_LEADING_JUNK.matcher(str).replaceFirst("");
            return UnsignedInts.parseUnsignedInt(str, 16);
        }
    }

    public static Color from(JsonObject json, String propertyName, int defaultValue) {
        String defaultStr = Integer.toHexString(defaultValue);
        JsonElement element = json.get(propertyName);
        return element != null && element.isJsonPrimitive() ? parse(element.getAsString()) : parse(defaultStr);
    }

    public int getColor() {
        return this.color;
    }

    public float getRed() {
        return (float)this.red / 255.0F;
    }

    public float getGreen() {
        return (float)this.green / 255.0F;
    }

    public float getBlue() {
        return (float)this.blue / 255.0F;
    }

    public float getAlpha() {
        return (float)this.alpha / 255.0F;
    }

    public int getRedInt() {
        return this.red;
    }

    public int getGreenInt() {
        return this.green;
    }

    public int getBlueInt() {
        return this.blue;
    }

    public int getAlphaInt() {
        return this.alpha;
    }
}
