package net.cwjn.idf.attribute;

import net.cwjn.idf.ImprovedDamageFramework;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class AttributeRegistry {

    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, ImprovedDamageFramework.MOD_ID);

    public static final RegistryObject<Attribute> FIRE_DAMAGE = register("idf.fire_damage", () -> new RangedAttribute("idf.attribute.fire_damage", 0.0D, 0.0D, Double.MAX_VALUE).setSyncable(true));
    public static final RegistryObject<Attribute> WATER_DAMAGE = register("idf.water_damage", () -> new RangedAttribute("idf.attribute.water_damage", 0.0D, 0.0D, Double.MAX_VALUE).setSyncable(true));
    public static final RegistryObject<Attribute> LIGHTNING_DAMAGE = register("idf.lightning_damage", () -> new RangedAttribute("idf.attribute.lightning_damage", 0.0D, 0.0D, Double.MAX_VALUE).setSyncable(true));
    public static final RegistryObject<Attribute> MAGIC_DAMAGE = register("idf.magic_damage", () -> new RangedAttribute("idf.attribute.magic_damage", 0.0D, 0.0D, Double.MAX_VALUE).setSyncable(true));
    public static final RegistryObject<Attribute> DARK_DAMAGE = register("idf.dark_damage", () -> new RangedAttribute("idf.attribute.dark_damage", 0.0D, 0.0D, Double.MAX_VALUE).setSyncable(true));

    public static final RegistryObject<Attribute> LIFESTEAL = register("idf.lifesteal", () -> new RangedAttribute("idf.attribute.lifesteal", 0.0D, 0.0D, 100.D).setSyncable(true));
    public static final RegistryObject<Attribute> PENETRATING = register("idf.penetrating", () -> new RangedAttribute("idf.attribute.penetrating", 0.0D, 0.0D, 100.0D).setSyncable(true));
    public static final RegistryObject<Attribute> CRIT_CHANCE = register("idf.crit_chance", () -> new RangedAttribute("idf.attribute.crit_chance", 0.0D, 0.0D, 100.0D).setSyncable(true));

    public static final RegistryObject<Attribute> FIRE_RESISTANCE = register("idf.fire_resistance", () -> new RangedAttribute("idf.attribute.fire_resistance", 0.0D, 0.0D, Double.MAX_VALUE).setSyncable(true));
    public static final RegistryObject<Attribute> WATER_RESISTANCE = register("idf.water_resistance", () -> new RangedAttribute("idf.attribute.water_resistance", 0.0D, 0.0D, Double.MAX_VALUE).setSyncable(true));
    public static final RegistryObject<Attribute> LIGHTNING_RESISTANCE = register("idf.lightning_resistance", () -> new RangedAttribute("idf.attribute.lightning_resistance", 0.0D, 0.0D, Double.MAX_VALUE).setSyncable(true));
    public static final RegistryObject<Attribute> MAGIC_RESISTANCE = register("idf.magic_resistance", () -> new RangedAttribute("idf.attribute.magic_resistance", 0.0D, 0.0D, Double.MAX_VALUE).setSyncable(true));
    public static final RegistryObject<Attribute> DARK_RESISTANCE = register("idf.dark_resistance", () -> new RangedAttribute("idf.attribute.dark_resistance", 0.0D, 0.0D, Double.MAX_VALUE).setSyncable(true));

    public static final RegistryObject<Attribute> STRIKE_MULT = register("idf.strike_mult", () -> new RangedAttribute("idf.attribute.strike_mult", 1.0D, 0.0D, Double.MAX_VALUE).setSyncable(true));
    public static final RegistryObject<Attribute> PIERCE_MULT = register("idf.pierce_mult", () -> new RangedAttribute("idf.attribute.pierce_mult", 1.0D, 0.0D, Double.MAX_VALUE).setSyncable(true));
    public static final RegistryObject<Attribute> SLASH_MULT = register("idf.slash_mult", () -> new RangedAttribute("idf.attribute.slash_mult", 1.0D, 0.0D, Double.MAX_VALUE).setSyncable(true));
    public static final RegistryObject<Attribute> CRUSH_MULT = register("idf.crush_mult", () -> new RangedAttribute("idf.attribute.crush_mult", 1.0D, 0.0D, Double.MAX_VALUE).setSyncable(true));
    public static final RegistryObject<Attribute> GENERIC_MULT = register("idf.generic_mult", () -> new RangedAttribute("idf.attribute.generic_mult", 1.0D, 0.0D, Double.MAX_VALUE).setSyncable(true));

    public static final RegistryObject<Attribute> EVASION = register("idf.evasion", () -> new RangedAttribute("idf.attribute.evasion", 0.0D, 0.0D, 100.0D).setSyncable(true));
    //public static final RegistryObject<Attribute> ADAPTING = register("idf.adapting", () -> new RangedAttribute("idf.attribute.adapting", 0.0D, 0.0D, Double.MAX_VALUE).setSyncable(true));
    //public static final RegistryObject<Attribute> REGENERATING = register("idf.regenerating", () -> new RangedAttribute("idf.attribute.regenerating", 0.0D, 0.0D, Double.MAX_VALUE).setSyncable(true));
    // ^ basically Warframe sentients

    private static <T extends Attribute> RegistryObject<T> register(final String name, final Supplier<T> attribute) {
        return ATTRIBUTES.register(name, attribute);
    }

    public static void setSyncables() {
        Attributes.ATTACK_DAMAGE.setSyncable(true);
        Attributes.ATTACK_KNOCKBACK.setSyncable(true);
        Attributes.KNOCKBACK_RESISTANCE.setSyncable(true);
    }

}
