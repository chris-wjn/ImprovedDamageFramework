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
    public static final RegistryObject<Attribute> FIRE_DAMAGE = register("fire_damage", () -> new RangedAttribute("fire_damage", 0.0D, 0.0D, Double.MAX_VALUE).setSyncable(true));
    public static final RegistryObject<Attribute> WATER_DAMAGE = register("water_damage", () -> new RangedAttribute("water_damage", 0.0D, 0.0D, Double.MAX_VALUE).setSyncable(true));
    public static final RegistryObject<Attribute> LIGHTNING_DAMAGE = register("lightning_damage", () -> new RangedAttribute("lightning_damage", 0.0D, 0.0D, Double.MAX_VALUE).setSyncable(true));
    public static final RegistryObject<Attribute> MAGIC_DAMAGE = register("magic_damage", () -> new RangedAttribute("magic_damage", 0.0D, 0.0D, Double.MAX_VALUE).setSyncable(true));
    public static final RegistryObject<Attribute> DARK_DAMAGE = register("dark_damage", () -> new RangedAttribute("dark_damage", 0.0D, 0.0D, Double.MAX_VALUE).setSyncable(true));
    public static final RegistryObject<Attribute> HOLY_DAMAGE = register("holy_damage", () -> new RangedAttribute("holy_damage", 0.0D, 0.0D, Double.MAX_VALUE).setSyncable(true));
    //auxiliary offensive attributes
    public static final RegistryObject<Attribute> FORCE = register("force", () -> new RangedAttribute("force", 0.0D, -1.0D, Double.MAX_VALUE).setSyncable(true));
    public static final RegistryObject<Attribute> LIFESTEAL = register("lifesteal", () -> new RangedAttribute("lifesteal", 0.0D, 0.0D, 100.D).setSyncable(true));
    public static final RegistryObject<Attribute> PENETRATING = register("penetrating", () -> new RangedAttribute("armour_penetration", 0.0D, 0.0D, 100.0D).setSyncable(true));
    public static final RegistryObject<Attribute> CRIT_CHANCE = register("crit_chance", () -> new RangedAttribute("crit_chance", 0.0D, 0.0D, 100.0D).setSyncable(true));
    public static final RegistryObject<Attribute> CRIT_DAMAGE = register("crit_damage", () -> new RangedAttribute("crit_damage", 150.0D, 0.0D, Double.MAX_VALUE).setSyncable(true));
    public static final RegistryObject<Attribute> ACCURACY = register("accuracy", () -> new RangedAttribute("accuracy", 0.0D, 0.0D, Double.MAX_VALUE).setSyncable(true));
    //elemental resistance types
    public static final RegistryObject<Attribute> FIRE_DEFENCE = register("fire_defence", () -> new RangedAttribute("fire_defence", 0.0D, -Double.MAX_VALUE, Double.MAX_VALUE).setSyncable(true));
    public static final RegistryObject<Attribute> WATER_DEFENCE = register("water_defence", () -> new RangedAttribute("water_defence", 0.0D, -Double.MAX_VALUE, Double.MAX_VALUE).setSyncable(true));
    public static final RegistryObject<Attribute> LIGHTNING_DEFENCE = register("lightning_defence", () -> new RangedAttribute("lightning_defence", 0.0D, -Double.MAX_VALUE, Double.MAX_VALUE).setSyncable(true));
    public static final RegistryObject<Attribute> MAGIC_DEFENCE = register("magic_defence", () -> new RangedAttribute("magic_defence", 0.0D, -Double.MAX_VALUE, Double.MAX_VALUE).setSyncable(true));
    public static final RegistryObject<Attribute> DARK_DEFENCE = register("dark_defence", () -> new RangedAttribute("dark_defence", 0.0D, -Double.MAX_VALUE, Double.MAX_VALUE).setSyncable(true));
    public static final RegistryObject<Attribute> HOLY_DEFENCE = register("holy_defence", () -> new RangedAttribute("holy_defence", 0.0D, -Double.MAX_VALUE, Double.MAX_VALUE).setSyncable(true));
    //damage class multipliers
    public static final RegistryObject<Attribute> STRIKE_MULT = register("strike_mult", () -> new RangedAttribute("strike_mult", 1.0D, 0.0D, Double.MAX_VALUE).setSyncable(true));
    public static final RegistryObject<Attribute> PIERCE_MULT = register("pierce_mult", () -> new RangedAttribute("pierce_mult", 1.0D, 0.0D, Double.MAX_VALUE).setSyncable(true));
    public static final RegistryObject<Attribute> SLASH_MULT = register("slash_mult", () -> new RangedAttribute("slash_mult", 1.0D, 0.0D, Double.MAX_VALUE).setSyncable(true));
    //auxiliary defensive attributes
    public static final RegistryObject<Attribute> EVASION = register("evasion", () -> new RangedAttribute("evasion", 0.0D, 0.0D, 100.0D).setSyncable(true));

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
        mixinDefense.setMax(Double.MAX_VALUE);
    }

}
