package net.cwjn.idf.config;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.Arrays;
import java.util.List;

public class ClientConfig {

    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<Boolean> CHANGE_PLAYER_HEALTH_BAR;
    public static final ForgeConfigSpec.ConfigValue<Boolean> REMOVE_ARMOUR_DISPLAY;
    public static final ForgeConfigSpec.ConfigValue<Boolean> USE_OLD_TOOLTIPS;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> BLACKLISTED_HEALTHBAR_ENTITIES;
    public static final ForgeConfigSpec.ConfigValue<Boolean> DISPLAY_HEALTHBAR_ONLY_ON_DAMAGE;
    public static final ForgeConfigSpec.ConfigValue<Integer> HEALTHBAR_ON_DAMAGE_DISPLAY_TIME;
    private static final String[] defaultBlacklist = {"minecraft:example"};

    static {

        BUILDER.push("Client Config");

        CHANGE_PLAYER_HEALTH_BAR = BUILDER.comment("Change health display to a bar instead of hearts. Highly recommended. You can disable this if you want to use another mod for HUD, or you want half your screen taken up by little red hearts.")
                .define("Change Health Display", true);
        REMOVE_ARMOUR_DISPLAY = BUILDER.comment("Remove vanilla armour display. With the changes this mod makes, the vanilla armour display is useless. This option is included if another mod you're using needs the armour level display event to function.")
                .define("Remove Armour Display", true);
        USE_OLD_TOOLTIPS = BUILDER.comment("Use the old tooltip style which is more compact but doesn't show attribute names")
                .define("Use Old Style", false);
        BLACKLISTED_HEALTHBAR_ENTITIES = BUILDER.comment("List of entities blacklisted from having their healthbar displayed. Takes" +
                "\nthe registry name of a mob.")
                .defineList("Blacklisted entities", Arrays.asList(defaultBlacklist), s -> s instanceof String);
        DISPLAY_HEALTHBAR_ONLY_ON_DAMAGE = BUILDER.comment("Display mob's health bars only briefly (4 seconds) when they take damage.")
                .define("Display only on damage", false);
        HEALTHBAR_ON_DAMAGE_DISPLAY_TIME = BUILDER.comment("How long to display mob's health bar when hit if the previous option is true.")
                .define("Time in ticks", 80);

        BUILDER.pop();
        SPEC = BUILDER.build();

    }

}
