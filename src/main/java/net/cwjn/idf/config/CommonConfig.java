package net.cwjn.idf.config;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.Arrays;
import java.util.List;

public class CommonConfig {

    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> WHITELISTED_DAMAGE_SOURCES_NO_INVULN;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> BLACKLISTED_ENTITIES;
    private static final String[] defaultStringList = {"player", "mob", "sting", "fall"};
    private static final String[] defaultBlacklist = {"minecraft:slime", "minecraft:magma_cube"};

    static {

        BUILDER.push("Configs");

        WHITELISTED_DAMAGE_SOURCES_NO_INVULN = BUILDER.comment("Damage Sources that will not make the target gain invulnerability frames. Takes DamageSource object's msgId field. If you don't know what that is, don't touch this.")
                        .defineList("Whitelisted sources", Arrays.asList(defaultStringList), s -> s instanceof String);

        BLACKLISTED_ENTITIES = BUILDER.comment("Entities that are blacklisted from the mob damage source. Will not do anything if 'mob' is not included in Whitelisted sources. Takes the registry name of a mob.")
                        .defineList("Blacklisted mobs", Arrays.asList(defaultBlacklist), s -> s instanceof String);

        BUILDER.pop();
        SPEC = BUILDER.build();

    }

}
