package net.cwjn.idf.enchantment;

import net.cwjn.idf.ImprovedDamageFramework;
import net.cwjn.idf.util.UUIDs;
import net.cwjn.idf.attribute.IDFAttributes;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.UUID;

public class IDFEnchantments {
    public static DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, ImprovedDamageFramework.MOD_ID);

    //FLAT BONUSES
    public static final RegistryObject<Enchantment> FIRE_DAMAGE_FLAT = ENCHANTMENTS.register("fire_damage_flat", () -> new AttributeDamageEnchantment(Enchantment.Rarity.RARE, UUID.randomUUID(), IDFAttributes.FIRE_DAMAGE.get(), "fire"));
    public static final RegistryObject<Enchantment> WATER_DAMAGE_FLAT = ENCHANTMENTS.register("water_damage_flat", () -> new AttributeDamageEnchantment(Enchantment.Rarity.RARE, UUID.randomUUID(), IDFAttributes.WATER_DAMAGE.get(), "water"));
    public static final RegistryObject<Enchantment> LIGHTNING_DAMAGE_FLAT = ENCHANTMENTS.register("lightning_damage_flat", () -> new AttributeDamageEnchantment(Enchantment.Rarity.RARE, UUID.randomUUID(), IDFAttributes.LIGHTNING_DAMAGE.get(), "lightning"));
    public static final RegistryObject<Enchantment> MAGIC_DAMAGE_FLAT = ENCHANTMENTS.register("magic_damage_flat", () -> new AttributeDamageEnchantment(Enchantment.Rarity.RARE, UUID.randomUUID(), IDFAttributes.MAGIC_DAMAGE.get(), "magic"));
    public static final RegistryObject<Enchantment> DARK_DAMAGE_FLAT = ENCHANTMENTS.register("dark_damage_flat", () -> new AttributeDamageEnchantment(Enchantment.Rarity.RARE, UUID.randomUUID(), IDFAttributes.DARK_DAMAGE.get(), "dark"));
    public static final RegistryObject<Enchantment> FIRE_RESIST_FLAT = ENCHANTMENTS.register("fire_resist_flat", () -> new AttributeResistanceEnchantment(Enchantment.Rarity.RARE, UUIDs.createUUIDArray(8), IDFAttributes.FIRE_RESISTANCE.get(), "fire"));
    public static final RegistryObject<Enchantment> WATER_RESIST_FLAT = ENCHANTMENTS.register("water_resist_flat", () -> new AttributeResistanceEnchantment(Enchantment.Rarity.RARE, UUIDs.createUUIDArray(8), IDFAttributes.WATER_RESISTANCE.get(), "water"));
    public static final RegistryObject<Enchantment> LIGHTNING_RESIST_FLAT = ENCHANTMENTS.register("lightning_resist_flat", () -> new AttributeResistanceEnchantment(Enchantment.Rarity.RARE, UUIDs.createUUIDArray(8), IDFAttributes.LIGHTNING_RESISTANCE.get(), "lightning"));
    public static final RegistryObject<Enchantment> MAGIC_RESIST_FLAT = ENCHANTMENTS.register("magic_resist_flat", () -> new AttributeResistanceEnchantment(Enchantment.Rarity.RARE, UUIDs.createUUIDArray(8), IDFAttributes.MAGIC_RESISTANCE.get(), "magic"));
    public static final RegistryObject<Enchantment> DARK_RESIST_FLAT = ENCHANTMENTS.register("dark_resist_flat", () -> new AttributeResistanceEnchantment(Enchantment.Rarity.RARE, UUIDs.createUUIDArray(8), IDFAttributes.DARK_RESISTANCE.get(), "dark"));

    //MULTIPLICATIVE BONUSES
    public static final RegistryObject<Enchantment> FIRE_DAMAGE_MULT = ENCHANTMENTS.register("fire_damage_mult", () -> new AttributeDamageEnchantment(Enchantment.Rarity.RARE, true, UUID.randomUUID(), IDFAttributes.FIRE_DAMAGE.get(), "fire"));
    public static final RegistryObject<Enchantment> WATER_DAMAGE_MULT = ENCHANTMENTS.register("water_damage_mult", () -> new AttributeDamageEnchantment(Enchantment.Rarity.RARE, true, UUID.randomUUID(), IDFAttributes.WATER_DAMAGE.get(), "water"));
    public static final RegistryObject<Enchantment> LIGHTNING_DAMAGE_MULT = ENCHANTMENTS.register("lightning_damage_mult", () -> new AttributeDamageEnchantment(Enchantment.Rarity.RARE, true, UUID.randomUUID(), IDFAttributes.LIGHTNING_DAMAGE.get(), "lightning"));
    public static final RegistryObject<Enchantment> MAGIC_DAMAGE_MULT = ENCHANTMENTS.register("magic_damage_mult", () -> new AttributeDamageEnchantment(Enchantment.Rarity.RARE, true, UUID.randomUUID(), IDFAttributes.MAGIC_DAMAGE.get(), "magic"));
    public static final RegistryObject<Enchantment> DARK_DAMAGE_MULT = ENCHANTMENTS.register("dark_damage_mult", () -> new AttributeDamageEnchantment(Enchantment.Rarity.RARE, true, UUID.randomUUID(), IDFAttributes.DARK_DAMAGE.get(), "dark"));
    public static final RegistryObject<Enchantment> FIRE_RESIST_MULT = ENCHANTMENTS.register("fire_resist_mult", () -> new AttributeResistanceEnchantment(Enchantment.Rarity.RARE, true, UUIDs.createUUIDArray(8), IDFAttributes.FIRE_RESISTANCE.get(), "fire"));
    public static final RegistryObject<Enchantment> WATER_RESIST_MULT = ENCHANTMENTS.register("water_resist_mult", () -> new AttributeResistanceEnchantment(Enchantment.Rarity.RARE, true, UUIDs.createUUIDArray(8), IDFAttributes.WATER_RESISTANCE.get(), "water"));
    public static final RegistryObject<Enchantment> LIGHTNING_RESIST_MULT = ENCHANTMENTS.register("lightning_resist_mult", () -> new AttributeResistanceEnchantment(Enchantment.Rarity.RARE, true, UUIDs.createUUIDArray(8), IDFAttributes.LIGHTNING_RESISTANCE.get(), "lightning"));
    public static final RegistryObject<Enchantment> MAGIC_RESIST_MULT = ENCHANTMENTS.register("magic_resist_mult", () -> new AttributeResistanceEnchantment(Enchantment.Rarity.RARE, true, UUIDs.createUUIDArray(8), IDFAttributes.MAGIC_RESISTANCE.get(), "magic"));
    public static final RegistryObject<Enchantment> DARK_RESIST_MULT = ENCHANTMENTS.register("dark_resist_mult", () -> new AttributeResistanceEnchantment(Enchantment.Rarity.RARE, true, UUIDs.createUUIDArray(8), IDFAttributes.DARK_RESISTANCE.get(), "dark"));

    //OTHER
    public static final RegistryObject<Enchantment> ADAPTIVE = ENCHANTMENTS.register("adaptive", () -> new AdaptiveEnchantment(Enchantment.Rarity.VERY_RARE));

}
