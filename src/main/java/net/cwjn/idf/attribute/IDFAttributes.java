package net.cwjn.idf.attribute;

import net.cwjn.idf.ImprovedDamageFramework;
import net.cwjn.idf.mixin.AccessRangedAttribute;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class IDFAttributes {

    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, ImprovedDamageFramework.MOD_ID);

    //elemental damage types
    public static final RegistryObject<Attribute> FIRE_DAMAGE = register("idf.fire_damage", () -> new RangedAttribute("fire_damage", 0.0D, 0.0D, Double.MAX_VALUE).setSyncable(true));
    public static final RegistryObject<Attribute> WATER_DAMAGE = register("idf.water_damage", () -> new RangedAttribute("water_damage", 0.0D, 0.0D, Double.MAX_VALUE).setSyncable(true));
    public static final RegistryObject<Attribute> LIGHTNING_DAMAGE = register("idf.lightning_damage", () -> new RangedAttribute("lightning_damage", 0.0D, 0.0D, Double.MAX_VALUE).setSyncable(true));
    public static final RegistryObject<Attribute> MAGIC_DAMAGE = register("idf.magic_damage", () -> new RangedAttribute("magic_damage", 0.0D, 0.0D, Double.MAX_VALUE).setSyncable(true));
    public static final RegistryObject<Attribute> DARK_DAMAGE = register("idf.dark_damage", () -> new RangedAttribute("dark_damage", 0.0D, 0.0D, Double.MAX_VALUE).setSyncable(true));
    public static final RegistryObject<Attribute> HOLY_DAMAGE = register("idf.holy_damage", () -> new RangedAttribute("holy_damage", 0.0D, 0.0D, Double.MAX_VALUE).setSyncable(true));
    //auxiliary offensive attributes
    public static final RegistryObject<Attribute> FORCE = register("idf.force", () -> new RangedAttribute("force", 1.0D, -1.0D, 40).setSyncable(true));
    public static final RegistryObject<Attribute> LIFESTEAL = register("idf.lifesteal", () -> new RangedAttribute("lifesteal", 0.0D, 0.0D, 100.D).setSyncable(true));
    public static final RegistryObject<Attribute> PENETRATING = register("idf.penetrating", () -> new RangedAttribute("armour_penetration", 0.0D, 0.0D, 100.0D).setSyncable(true));
    public static final RegistryObject<Attribute> CRIT_CHANCE = register("idf.crit_chance", () -> new RangedAttribute("crit_chance", 0.0D, 0.0D, 100.0D).setSyncable(true));
    //elemental resistance types
    public static final RegistryObject<Attribute> FIRE_RESISTANCE = register("idf.fire_resistance", () -> new RangedAttribute("fire_resistance", 0.0D, -Double.MAX_VALUE, Double.MAX_VALUE).setSyncable(true));
    public static final RegistryObject<Attribute> WATER_RESISTANCE = register("idf.water_resistance", () -> new RangedAttribute("water_resistance", 0.0D, -Double.MAX_VALUE, Double.MAX_VALUE).setSyncable(true));
    public static final RegistryObject<Attribute> LIGHTNING_RESISTANCE = register("idf.lightning_resistance", () -> new RangedAttribute("lightning_resistance", 0.0D, -Double.MAX_VALUE, Double.MAX_VALUE).setSyncable(true));
    public static final RegistryObject<Attribute> MAGIC_RESISTANCE = register("idf.magic_resistance", () -> new RangedAttribute("magic_resistance", 0.0D, -Double.MAX_VALUE, Double.MAX_VALUE).setSyncable(true));
    public static final RegistryObject<Attribute> DARK_RESISTANCE = register("idf.dark_resistance", () -> new RangedAttribute("dark_resistance", 0.0D, -Double.MAX_VALUE, Double.MAX_VALUE).setSyncable(true));
    public static final RegistryObject<Attribute> HOLY_RESISTANCE = register("idf.holy_resistance", () -> new RangedAttribute("holy_resistance", 0.0D, -Double.MAX_VALUE, Double.MAX_VALUE).setSyncable(true));
    //damage class multipliers
    public static final RegistryObject<Attribute> STRIKE_MULT = register("idf.strike_mult", () -> new RangedAttribute("strike_mult", 1.0D, 0.0D, Double.MAX_VALUE).setSyncable(true));
    public static final RegistryObject<Attribute> PIERCE_MULT = register("idf.pierce_mult", () -> new RangedAttribute("pierce_mult", 1.0D, 0.0D, Double.MAX_VALUE).setSyncable(true));
    public static final RegistryObject<Attribute> SLASH_MULT = register("idf.slash_mult", () -> new RangedAttribute("slash_mult", 1.0D, 0.0D, Double.MAX_VALUE).setSyncable(true));
    public static final RegistryObject<Attribute> CRUSH_MULT = register("idf.crush_mult", () -> new RangedAttribute("crush_mult", 1.0D, 0.0D, Double.MAX_VALUE).setSyncable(true));
    public static final RegistryObject<Attribute> GENERIC_MULT = register("idf.generic_mult", () -> new RangedAttribute("generic_mult", 1.0D, 0.0D, Double.MAX_VALUE).setSyncable(true));
    //auxiliary defensive attributes
    public static final RegistryObject<Attribute> EVASION = register("idf.evasion", () -> new RangedAttribute("evasion", 0.0D, 0.0D, 100.0D).setSyncable(true));

    private static <T extends Attribute> RegistryObject<T> register(final String name, final Supplier<T> attribute) {
        return ATTRIBUTES.register(name, attribute);
    }

    //this is to make the vanilla attributes that aren't syncable, syncable. This is needed for the stats screen to display correct information.
    public static void changeDefaultAttributes() {
        Attributes.ATTACK_DAMAGE.setSyncable(true);
        Attributes.ATTACK_KNOCKBACK.setSyncable(true);
        Attributes.KNOCKBACK_RESISTANCE.setSyncable(true);
        AccessRangedAttribute mixinArmour = (AccessRangedAttribute) Attributes.ARMOR;
        AccessRangedAttribute mixinDefense = (AccessRangedAttribute) Attributes.ARMOR_TOUGHNESS;
        AccessRangedAttribute mixinHealth = (AccessRangedAttribute) Attributes.MAX_HEALTH;
        mixinHealth.setMax(Double.MAX_VALUE);
        mixinArmour.setMax(Double.MAX_VALUE);
        mixinArmour.setMin(-Double.MAX_VALUE);
        mixinDefense.setMax(40D);
    }

}
