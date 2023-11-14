package net.cwjn.idf.config;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.Arrays;
import java.util.List;

public class CommonConfig {

    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> WHITELISTED_SOURCES_REDUCED_INVULN;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> UNDODGABLE_SOURCES;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> WHITELISTED_SOURCES_NO_INVULN;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> BLACKLISTED_ENTITIES;
    public static final ForgeConfigSpec.DoubleValue SCALE_HEALTH;
    public static final ForgeConfigSpec.DoubleValue SCALE_DAMAGE;
    public static final ForgeConfigSpec.DoubleValue MAX_FORCE_WEIGHT_MULTIPLIER;
    public static final ForgeConfigSpec.DoubleValue MIN_FORCE_WEIGHT_MULTIPLIER;
    public static final ForgeConfigSpec.DoubleValue MIN_ATTACK_STRENGTH_THRESHOLD;
    public static final ForgeConfigSpec.DoubleValue WEIGHT_FOOD_EXHAUSTION_MULTIPLIER;
    public static final ForgeConfigSpec.BooleanValue ALLOW_JUMP_CRITS;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> BLACKLIST_MOB_HEAL_ON_SPAWN;
    public static final ForgeConfigSpec.ConfigValue<Boolean> LEGENDARY_TOOLTIPS_COMPAT_MODE;
    private static final String[] defaultNoInvulnList = {"player", "fall"};
    private static final String[] undodgableList = {"inFire", "onFire", "lava", "hotFloor", "inWall",
            "cramming", "drown", "starve", "fall", "flyIntoWall", "outOfWorld", "wither", "dryout", "freeze", "bleed_effect", "blood_cauldron", "heartstop"};
    private static final String[] defaultReducedList = {"mob", "sting"};
    private static final String[] defaultBlacklist = {"minecraft:slime", "minecraft:magma_cube"};

    static {

        BUILDER.push("Tooltips");

        LEGENDARY_TOOLTIPS_COMPAT_MODE = BUILDER.comment("Enable compatibility with Legendary Tooltips. If Legendary Tooltips is not installed,\n" +
                        "this will do nothing but cost a slight bit of performance. This has to be a common config setting because the weapons\n" +
                        "need to have appropriate tags to be recognized by LegendaryTooltips.")
                .define("Tooltips Compat Enabled", true);

        BUILDER.pop();

        BUILDER.push("Damage Source Configuration");

        WHITELISTED_SOURCES_REDUCED_INVULN = BUILDER.comment("Damage Sources that will make the target get half the regular i-frames. \n" +
                        "Takes DamageSource object's msgId field. If you don't know what that is, don't touch this.")
                .defineList("Whitelisted reduced invulnerability sources", Arrays.asList(defaultReducedList), s -> s instanceof String);

        UNDODGABLE_SOURCES = BUILDER.comment("Damage source that are undodgable.")
                .defineList("Undodgable sources", Arrays.asList(undodgableList), s -> s instanceof String);

        WHITELISTED_SOURCES_NO_INVULN = BUILDER.comment("Damage sources that will not give i-frames to the target. Takes DamageSource object's msgId field.")
                .defineList("Whitelisted no invulnerability sources", Arrays.asList(defaultNoInvulnList), s -> s instanceof String);

        BLACKLISTED_ENTITIES = BUILDER.comment("Entities that are blacklisted from the mob damage source. Will not do anything if 'mob' is not included in Whitelisted sources. \n" +
                        "Takes the registry name of a mob.")
                .defineList("Blacklisted mobs", Arrays.asList(defaultBlacklist), s -> s instanceof String);

        BUILDER.pop();

        BUILDER.push("Attribute and Damage Calculation Configuration");

        BLACKLIST_MOB_HEAL_ON_SPAWN = BUILDER.comment("""
                Normally, mobs have their maxhp scaled up to the SCALE_HEALTH value when they join the world. Then they are
                healed up to that amount, otherwise they would spawn with only their normal amount of hp. Mobs included in this
                list will not be healed when they spawn. This is to fix edge cases where another mod may want a mob to spawn
                at a reduced hp value.
                """)
                .defineList("No Heal on Spawn", Arrays.asList(new String[]{"minecraft:example"}), s -> s instanceof String);

        SCALE_HEALTH = BUILDER.comment("""
                        Value to scale HP of mobs and the player by. Happens before config data is added.
                        Living Entities will spawn with whatever is set when first joining a world,
                        so make sure this is correct before a player first joins a world.
                        Must be a value from 0.1 to 100, where 2 = 200%, 5 = 500%, 0.2 = 20% etc...""")
                .defineInRange("Scale HP", 5, 0.1, 100);

        SCALE_DAMAGE = BUILDER.comment("""
                        Value to scale physical damage of mobs and the player by. Will only affect mobs that already
                        had an attack damage attribute, and the scaling happens before config data is added.
                        Living Entities will spawn with whatever is set when first joining a world,
                        so make sure this is correct before a player first joins a world.
                        Must be a value from 0.1 to 100, where 2 = 200%, 5 = 500%, 0.2 = 20% etc...""")
                .defineInRange("Scale Physical Damage", 2, 0.1, 100);

        MIN_FORCE_WEIGHT_MULTIPLIER = BUILDER.comment("""
                        Floor for force/weight calculation. Must be a value from 0.1 to 1, where 0.1 = 10% dmg and 1 = 100% or full dmg.""")
                .defineInRange("Force/Weight Multiplier Floor", 0.5, 0.1, 1);

        MAX_FORCE_WEIGHT_MULTIPLIER = BUILDER.comment("""
                        Ceiling for force/weight calculation. Must be a value from 1 to 10""")
                .defineInRange("Force/Weight Multiplier Ceiling", 2D, 1D, 10D);

        MIN_ATTACK_STRENGTH_THRESHOLD = BUILDER.comment("""
                        Attacks below this threshold will be cancelled. 0.1 = 10% strength, 0.4 = 40%, etc...
                        default = 0.4, min = 0, max = 0.8.""")
                .defineInRange("Attack Strength Threshold", 0.4, 0D, 0.8D);

        WEIGHT_FOOD_EXHAUSTION_MULTIPLIER = BUILDER.comment("""
                        Percentage of weight that multiplies food exhaustion. Default 0.01, or 1% of weight. Min 0, max 1""")
                .defineInRange("Food Exhaustion Weight Multiplier", 0.01D, 0D, 1D);

        ALLOW_JUMP_CRITS = BUILDER.comment("""
                        Allow the player to still crit by attacking while falling.""")
                .define("Allow Jump Crits", false);

        BUILDER.pop();
        SPEC = BUILDER.build();

    }

}
