package net.cwjn.idf.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ClientConfig {

    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<Boolean> CHANGE_PLAYER_HEALTH_BAR;
    public static final ForgeConfigSpec.ConfigValue<Integer> MOB_HEALTH_BAR_LIGHT_LEVEL;
    public static final ForgeConfigSpec.ConfigValue<Boolean> REMOVE_ARMOUR_DISPLAY;

    static {

        BUILDER.push("Client Config");

        CHANGE_PLAYER_HEALTH_BAR = BUILDER.comment("Change health display to a bar instead of hearts. Highly recommended. You can disable this if you want to use another mod for HUD, or you want half your screen taken up by little red hearts.")
                .define("Change Health Display", true);
        MOB_HEALTH_BAR_LIGHT_LEVEL = BUILDER.comment("Mob health bar will be displayed if the mob is within the player's detection attribute value, or if the mob is on a block with light level at least this.")
                .define("Min Light Level", 8);
        REMOVE_ARMOUR_DISPLAY = BUILDER.comment("Remove vanilla armour display. With the changes this mod makes, the vanilla armour display is useless. This option is included if another mod you're using needs the armour level display event to function.")
                .define("Remove Armour Display", true);

        BUILDER.pop();
        SPEC = BUILDER.build();

    }

}
