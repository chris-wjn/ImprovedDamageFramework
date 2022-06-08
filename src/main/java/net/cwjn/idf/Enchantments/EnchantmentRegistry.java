package net.cwjn.idf.Enchantments;

import net.cwjn.idf.ImprovedDamageFramework;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class EnchantmentRegistry {
    public static DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, ImprovedDamageFramework.MOD_ID);

    //FLAT BONUSES
    public static final RegistryObject<Enchantment> FIRE_DAMAGE_FLAT = ENCHANTMENTS.register("fire_damage_flat", () -> new AttributeDamageEnchantment(Enchantment.Rarity.UNCOMMON));
    public static final RegistryObject<Enchantment> WATER_DAMAGE_FLAT = ENCHANTMENTS.register("water_damage_flat", () -> new AttributeDamageEnchantment(Enchantment.Rarity.UNCOMMON));
    public static final RegistryObject<Enchantment> LIGHTNING_DAMAGE_FLAT = ENCHANTMENTS.register("lightning_damage_flat", () -> new AttributeDamageEnchantment(Enchantment.Rarity.UNCOMMON));
    public static final RegistryObject<Enchantment> MAGIC_DAMAGE_FLAT = ENCHANTMENTS.register("magic_damage_flat", () -> new AttributeDamageEnchantment(Enchantment.Rarity.UNCOMMON));
    public static final RegistryObject<Enchantment> DARK_DAMAGE_FLAT = ENCHANTMENTS.register("dark_damage_flat", () -> new AttributeDamageEnchantment(Enchantment.Rarity.UNCOMMON));
    public static final RegistryObject<Enchantment> FIRE_RESIST_FLAT = ENCHANTMENTS.register("fire_resist_flat", () -> new AttributeResistanceEnchantment(Enchantment.Rarity.UNCOMMON));
    public static final RegistryObject<Enchantment> WATER_RESIST_FLAT = ENCHANTMENTS.register("water_resist_flat", () -> new AttributeResistanceEnchantment(Enchantment.Rarity.UNCOMMON));
    public static final RegistryObject<Enchantment> LIGHTNING_RESIST_FLAT = ENCHANTMENTS.register("lightning_resist_flat", () -> new AttributeResistanceEnchantment(Enchantment.Rarity.UNCOMMON));
    public static final RegistryObject<Enchantment> MAGIC_RESIST_FLAT = ENCHANTMENTS.register("magic_resist_flat", () -> new AttributeResistanceEnchantment(Enchantment.Rarity.UNCOMMON));
    public static final RegistryObject<Enchantment> DARK_RESIST_FLAT = ENCHANTMENTS.register("dark_resist_flat", () -> new AttributeResistanceEnchantment(Enchantment.Rarity.UNCOMMON));

    //MULTIPLICATIVE BONUSES
    public static final RegistryObject<Enchantment> FIRE_DAMAGE_MULT = ENCHANTMENTS.register("fire_damage_mult", () -> new AttributeDamageEnchantment(Enchantment.Rarity.UNCOMMON, true));
    public static final RegistryObject<Enchantment> WATER_DAMAGE_MULT = ENCHANTMENTS.register("water_damage_mult", () -> new AttributeDamageEnchantment(Enchantment.Rarity.UNCOMMON, true));
    public static final RegistryObject<Enchantment> LIGHTNING_DAMAGE_MULT = ENCHANTMENTS.register("lightning_damage_mult", () -> new AttributeDamageEnchantment(Enchantment.Rarity.UNCOMMON, true));
    public static final RegistryObject<Enchantment> MAGIC_DAMAGE_MULT = ENCHANTMENTS.register("magic_damage_mult", () -> new AttributeDamageEnchantment(Enchantment.Rarity.UNCOMMON, true));
    public static final RegistryObject<Enchantment> DARK_DAMAGE_MULT = ENCHANTMENTS.register("dark_damage_mult", () -> new AttributeDamageEnchantment(Enchantment.Rarity.UNCOMMON, true));
    public static final RegistryObject<Enchantment> FIRE_RESIST_MULT = ENCHANTMENTS.register("fire_resist_mult", () -> new AttributeResistanceEnchantment(Enchantment.Rarity.UNCOMMON, true));
    public static final RegistryObject<Enchantment> WATER_RESIST_MULT = ENCHANTMENTS.register("water_resist_mult", () -> new AttributeResistanceEnchantment(Enchantment.Rarity.UNCOMMON, true));
    public static final RegistryObject<Enchantment> LIGHTNING_RESIST_MULT = ENCHANTMENTS.register("lightning_resist_mult", () -> new AttributeResistanceEnchantment(Enchantment.Rarity.UNCOMMON, true));
    public static final RegistryObject<Enchantment> MAGIC_RESIST_MULT = ENCHANTMENTS.register("magic_resist_mult", () -> new AttributeResistanceEnchantment(Enchantment.Rarity.UNCOMMON, true));
    public static final RegistryObject<Enchantment> DARK_RESIST_MULT = ENCHANTMENTS.register("dark_resist_mult", () -> new AttributeResistanceEnchantment(Enchantment.Rarity.UNCOMMON, true));

}
