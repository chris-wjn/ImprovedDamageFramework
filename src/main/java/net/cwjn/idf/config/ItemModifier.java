package net.cwjn.idf.config;

import net.cwjn.idf.Util;
import net.cwjn.idf.attribute.IDFAttributes;
import net.cwjn.idf.config.json.JSONHandler;
import net.cwjn.idf.config.json.data.DamageData;
import net.cwjn.idf.config.json.data.ResistanceData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.util.thread.SidedThreadGroups;
import static net.cwjn.idf.UUIDs.*;

@Mod.EventBusSubscriber
public class ItemModifier {

    private static ItemStack cachedItem;

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void ItemModifierEvent(ItemAttributeModifierEvent event) {
        ItemStack item = event.getItemStack(); //get the item from the event
        if (LivingEntity.getEquipmentSlotForItem(item) == event.getSlotType()) {
            if (Thread.currentThread().getThreadGroup() == SidedThreadGroups.CLIENT) {
                if (cachedItem == item) {
                    return;
                }
            }
            ResourceLocation loc = Util.getItemRegistryName(item.getItem());
            if (JSONHandler.damageMap.containsKey(loc)) {
                cachedItem = item;
                item.getOrCreateTag().putBoolean("idf.equipment", true);
                item.getTag().putString("idf.damage_class", JSONHandler.damageMap.get(loc).getDamageClass());
                DamageData data = JSONHandler.getDamageData(loc);
                event.addModifier(Attributes.ATTACK_DAMAGE, new AttributeModifier(configAttackDamage, () -> "config_attack_damage", data.getAttackDamage(), AttributeModifier.Operation.ADDITION));
                event.addModifier(IDFAttributes.FIRE_DAMAGE.get(), new AttributeModifier(configFireDamage, () -> "config_fire_damage", data.getFire(), AttributeModifier.Operation.ADDITION));
                event.addModifier(IDFAttributes.WATER_DAMAGE.get(), new Attribut eModifier(configWaterDamage, () -> "config_water_damage", data.getWater(), AttributeModifier.Operation.ADDITION));
                event.addModifier(IDFAttributes.LIGHTNING_DAMAGE.get(), new AttributeModifier(configLightningDamage, () -> "config_lightning_damage", data.getLightning(), AttributeModifier.Operation.ADDITION));
                event.addModifier(IDFAttributes.MAGIC_DAMAGE.get(), new AttributeModifier(configMagicDamage, () -> "config_magic_damage", data.getMagic(), AttributeModifier.Operation.ADDITION));
                event.addModifier(IDFAttributes.DARK_DAMAGE.get(), new AttributeModifier(configDarkDamage, () -> "config_dark_damage", data.getDark(), AttributeModifier.Operation.ADDITION));
                event.addModifier(IDFAttributes.LIFESTEAL.get(), new AttributeModifier(configLifesteal, () -> "config_lifesteal", data.getLifesteal(), AttributeModifier.Operation.ADDITION));
                event.addModifier(IDFAttributes.PENETRATING.get(), new AttributeModifier(configPenetration, () -> "config_penetration", data.getArmourPenetration(), AttributeModifier.Operation.ADDITION));
                event.addModifier(IDFAttributes.WEIGHT.get(), new AttributeModifier(configWeight, () -> "config_weight", data.getWeight(), AttributeModifier.Operation.ADDITION));
                event.addModifier(IDFAttributes.CRIT_CHANCE.get(), new AttributeModifier(configCrit, () -> "config_crit_chance", data.getCritChance(), AttributeModifier.Operation.ADDITION));
            } else if (JSONHandler.resistanceMap.containsKey(loc)) {
                cachedItem = item;
                item.getOrCreateTag().putBoolean("idf.equipment", true);
                ResistanceData data = JSONHandler.getResistanceData(loc);
                if (event.getSlotType() == EquipmentSlot.HEAD) {
                    event.addModifier(Attributes.ARMOR, new AttributeModifier(helmetConfigArmour, () -> "helmet_config_armour", data.getArmour(), AttributeModifier.Operation.ADDITION));
                    event.addModifier(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(helmetConfigArmourToughness, () -> "helmet_config_armour_toughness", data.getArmourToughness(), AttributeModifier.Operation.ADDITION));
                    event.addModifier(IDFAttributes.FIRE_RESISTANCE.get(), new AttributeModifier(helmetConfigFireResistance, () -> "helmet_config_fire_resistance", data.getFire(), AttributeModifier.Operation.ADDITION));
                    event.addModifier(IDFAttributes.WATER_RESISTANCE.get(), new AttributeModifier(helmetConfigWaterResistance, () -> "helmet_config_water_resistance", data.getWater(), AttributeModifier.Operation.ADDITION));
                    event.addModifier(IDFAttributes.LIGHTNING_RESISTANCE.get(), new AttributeModifier(helmetConfigLightningResistance, () -> "helmet_config_lightning_resistance", data.getLightning(), AttributeModifier.Operation.ADDITION));
                    event.addModifier(IDFAttributes.MAGIC_RESISTANCE.get(), new AttributeModifier(helmetConfigMagicResistance, () -> "helmet_config_magic_resistance", data.getMagic(), AttributeModifier.Operation.ADDITION));
                    event.addModifier(IDFAttributes.DARK_RESISTANCE.get(), new AttributeModifier(helmetConfigDarkResistance, () -> "helmet_config_dark_resistance", data.getDark(), AttributeModifier.Operation.ADDITION));
                    event.addModifier(IDFAttributes.EVASION.get(), new AttributeModifier(helmetConfigEvasion, () -> "helmet_config_evasion", data.getEvasion(), AttributeModifier.Operation.ADDITION));
                    event.addModifier(Attributes.MAX_HEALTH, new AttributeModifier(helmetConfigMaxHP, () -> "helmet_config_max_hp", data.getMaxHP(), AttributeModifier.Operation.ADDITION));
                    event.addModifier(Attributes.MOVEMENT_SPEED, new AttributeModifier(helmetConfigMovespeed, () -> "helmet_config_movespeed", data.getMovespeed(), AttributeModifier.Operation.ADDITION));
                    event.addModifier(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(helmetConfigKnockbackResistance, () -> "helmet_config_knockback_resistance", data.getKnockbackRes(), AttributeModifier.Operation.ADDITION));
                    event.addModifier(Attributes.LUCK, new AttributeModifier(helmetConfigLuck, () -> "helmet_config_luck", data.getLuck(), AttributeModifier.Operation.ADDITION));
                    event.addModifier(IDFAttributes.STRIKE_MULT.get(), new AttributeModifier(helmetConfigStrikeMult, () -> "helmet_config_strike", data.getStrikeMult(), AttributeModifier.Operation.ADDITION));
                    event.addModifier(IDFAttributes.PIERCE_MULT.get(), new AttributeModifier(helmetConfigPierceMult, () -> "helmet_config_pierce", data.getPierceMult(), AttributeModifier.Operation.ADDITION));
                    event.addModifier(IDFAttributes.SLASH_MULT.get(), new AttributeModifier(helmetConfigSlashMult, () -> "helmet_config_slash", data.getSlashMult(), AttributeModifier.Operation.ADDITION));
                    event.addModifier(IDFAttributes.CRUSH_MULT.get(), new AttributeModifier(helmetConfigCrushMult, () -> "helmet_config_crush", data.getCrushMult(), AttributeModifier.Operation.ADDITION));
                    event.addModifier(IDFAttributes.GENERIC_MULT.get(), new AttributeModifier(helmetConfigGenericMult, () -> "helmet_config_generic", data.getGenericMult(), AttributeModifier.Operation.ADDITION));
                }
                else if (event.getSlotType() == EquipmentSlot.CHEST) {
                    event.addModifier(Attributes.ARMOR, new AttributeModifier(chestplateConfigArmour, () -> "chestplate_config_armour", data.getArmour(), AttributeModifier.Operation.ADDITION));
                    event.addModifier(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(chestplateConfigArmourToughness, () -> "chestplate_config_armour_toughness", data.getArmourToughness(), AttributeModifier.Operation.ADDITION));
                    event.addModifier(IDFAttributes.FIRE_RESISTANCE.get(), new AttributeModifier(chestplateConfigFireResistance, () -> "chestplate_config_fire_resistance", data.getFire(), AttributeModifier.Operation.ADDITION));
                    event.addModifier(IDFAttributes.WATER_RESISTANCE.get(), new AttributeModifier(chestplateConfigWaterResistance, () -> "chestplate_config_water_resistance", data.getWater(), AttributeModifier.Operation.ADDITION));
                    event.addModifier(IDFAttributes.LIGHTNING_RESISTANCE.get(), new AttributeModifier(chestplateConfigLightningResistance, () -> "chestplate_config_lightning_resistance", data.getLightning(), AttributeModifier.Operation.ADDITION));
                    event.addModifier(IDFAttributes.MAGIC_RESISTANCE.get(), new AttributeModifier(chestplateConfigMagicResistance, () -> "chestplate_config_magic_resistance", data.getMagic(), AttributeModifier.Operation.ADDITION));
                    event.addModifier(IDFAttributes.DARK_RESISTANCE.get(), new AttributeModifier(chestplateConfigDarkResistance, () -> "chestplate_config_dark_resistance", data.getDark(), AttributeModifier.Operation.ADDITION));
                    event.addModifier(IDFAttributes.EVASION.get(), new AttributeModifier(chestplateConfigEvasion, () -> "chestplate_config_evasion", data.getEvasion(), AttributeModifier.Operation.ADDITION));
                    event.addModifier(Attributes.MAX_HEALTH, new AttributeModifier(chestplateConfigMaxHP, () -> "chestplate_config_max_hp", data.getMaxHP(), AttributeModifier.Operation.ADDITION));
                    event.addModifier(Attributes.MOVEMENT_SPEED, new AttributeModifier(chestplateConfigMovespeed, () -> "chestplate_config_movespeed", data.getMovespeed(), AttributeModifier.Operation.ADDITION));
                    event.addModifier(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(chestplateConfigKnockbackResistance, () -> "chestplate_config_knockback_resistance", data.getKnockbackRes(), AttributeModifier.Operation.ADDITION));
                    event.addModifier(Attributes.LUCK, new AttributeModifier(chestplateConfigLuck, () -> "chestplate_config_luck", data.getLuck(), AttributeModifier.Operation.ADDITION));
                    event.addModifier(IDFAttributes.STRIKE_MULT.get(), new AttributeModifier(chestplateConfigStrikeMult, () -> "chestplate_config_strike", data.getStrikeMult(), AttributeModifier.Operation.ADDITION));
                    event.addModifier(IDFAttributes.PIERCE_MULT.get(), new AttributeModifier(chestplateConfigPierceMult, () -> "chestplate_config_pierce", data.getPierceMult(), AttributeModifier.Operation.ADDITION));
                    event.addModifier(IDFAttributes.SLASH_MULT.get(), new AttributeModifier(chestplateConfigSlashMult, () -> "chestplate_config_slash", data.getSlashMult(), AttributeModifier.Operation.ADDITION));
                    event.addModifier(IDFAttributes.CRUSH_MULT.get(), new AttributeModifier(chestplateConfigCrushMult, () -> "chestplate_config_crush", data.getCrushMult(), AttributeModifier.Operation.ADDITION));
                    event.addModifier(IDFAttributes.GENERIC_MULT.get(), new AttributeModifier(chestplateConfigGenericMult, () -> "chestplate_config_generic", data.getGenericMult(), AttributeModifier.Operation.ADDITION));
                }
                else if (event.getSlotType() == EquipmentSlot.LEGS) {
                    event.addModifier(Attributes.ARMOR, new AttributeModifier(leggingsConfigArmour, () -> "leggings_config_armour", data.getArmour(), AttributeModifier.Operation.ADDITION));
                    event.addModifier(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(leggingsConfigArmourToughness, () -> "leggings_config_armour_toughness", data.getArmourToughness(), AttributeModifier.Operation.ADDITION));
                    event.addModifier(IDFAttributes.FIRE_RESISTANCE.get(), new AttributeModifier(leggingsConfigFireResistance, () -> "leggings_config_fire_resistance", data.getFire(), AttributeModifier.Operation.ADDITION));
                    event.addModifier(IDFAttributes.WATER_RESISTANCE.get(), new AttributeModifier(leggingsConfigWaterResistance, () -> "leggings_config_water_resistance", data.getWater(), AttributeModifier.Operation.ADDITION));
                    event.addModifier(IDFAttributes.LIGHTNING_RESISTANCE.get(), new AttributeModifier(leggingsConfigLightningResistance, () -> "leggings_config_lightning_resistance", data.getLightning(), AttributeModifier.Operation.ADDITION));
                    event.addModifier(IDFAttributes.MAGIC_RESISTANCE.get(), new AttributeModifier(leggingsConfigMagicResistance, () -> "leggings_config_magic_resistance", data.getMagic(), AttributeModifier.Operation.ADDITION));
                    event.addModifier(IDFAttributes.DARK_RESISTANCE.get(), new AttributeModifier(leggingsConfigDarkResistance, () -> "leggings_config_dark_resistance", data.getDark(), AttributeModifier.Operation.ADDITION));
                    event.addModifier(IDFAttributes.EVASION.get(), new AttributeModifier(leggingsConfigEvasion, () -> "leggings_config_evasion", data.getEvasion(), AttributeModifier.Operation.ADDITION));
                    event.addModifier(Attributes.MAX_HEALTH, new AttributeModifier(leggingsConfigMaxHP, () -> "leggings_config_max_hp", data.getMaxHP(), AttributeModifier.Operation.ADDITION));
                    event.addModifier(Attributes.MOVEMENT_SPEED, new AttributeModifier(leggingsConfigMovespeed, () -> "leggings_config_movespeed", data.getMovespeed(), AttributeModifier.Operation.ADDITION));
                    event.addModifier(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(leggingsConfigKnockbackResistance, () -> "leggings_config_knockback_resistance", data.getKnockbackRes(), AttributeModifier.Operation.ADDITION));
                    event.addModifier(Attributes.LUCK, new AttributeModifier(leggingsConfigLuck, () -> "leggings_config_luck", data.getLuck(), AttributeModifier.Operation.ADDITION));
                    event.addModifier(IDFAttributes.STRIKE_MULT.get(), new AttributeModifier(leggingsConfigStrikeMult, () -> "leggings_config_strike", data.getStrikeMult(), AttributeModifier.Operation.ADDITION));
                    event.addModifier(IDFAttributes.PIERCE_MULT.get(), new AttributeModifier(leggingsConfigPierceMult, () -> "leggings_config_pierce", data.getPierceMult(), AttributeModifier.Operation.ADDITION));
                    event.addModifier(IDFAttributes.SLASH_MULT.get(), new AttributeModifier(leggingsConfigSlashMult, () -> "leggings_config_slash", data.getSlashMult(), AttributeModifier.Operation.ADDITION));
                    event.addModifier(IDFAttributes.CRUSH_MULT.get(), new AttributeModifier(leggingsConfigCrushMult, () -> "leggings_config_crush", data.getCrushMult(), AttributeModifier.Operation.ADDITION));
                    event.addModifier(IDFAttributes.GENERIC_MULT.get(), new AttributeModifier(leggingsConfigGenericMult, () -> "leggings_config_generic", data.getGenericMult(), AttributeModifier.Operation.ADDITION));
                }
                else if (event.getSlotType() == EquipmentSlot.FEET) {
                    event.addModifier(Attributes.ARMOR, new AttributeModifier(bootsConfigArmour, () -> "boots_config_armour", data.getArmour(), AttributeModifier.Operation.ADDITION));
                    event.addModifier(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(bootsConfigArmourToughness, () -> "boots_config_armour_toughness", data.getArmourToughness(), AttributeModifier.Operation.ADDITION));
                    event.addModifier(IDFAttributes.FIRE_RESISTANCE.get(), new AttributeModifier(bootsConfigFireResistance, () -> "boots_config_fire_resistance", data.getFire(), AttributeModifier.Operation.ADDITION));
                    event.addModifier(IDFAttributes.WATER_RESISTANCE.get(), new AttributeModifier(bootsConfigWaterResistance, () -> "boots_config_water_resistance", data.getWater(), AttributeModifier.Operation.ADDITION));
                    event.addModifier(IDFAttributes.LIGHTNING_RESISTANCE.get(), new AttributeModifier(bootsConfigLightningResistance, () -> "boots_config_lightning_resistance", data.getLightning(), AttributeModifier.Operation.ADDITION));
                    event.addModifier(IDFAttributes.MAGIC_RESISTANCE.get(), new AttributeModifier(bootsConfigMagicResistance, () -> "boots_config_magic_resistance", data.getMagic(), AttributeModifier.Operation.ADDITION));
                    event.addModifier(IDFAttributes.DARK_RESISTANCE.get(), new AttributeModifier(bootsConfigDarkResistance, () -> "boots_config_dark_resistance", data.getDark(), AttributeModifier.Operation.ADDITION));
                    event.addModifier(IDFAttributes.EVASION.get(), new AttributeModifier(bootsConfigEvasion, () -> "boots_config_evasion", data.getEvasion(), AttributeModifier.Operation.ADDITION));
                    event.addModifier(Attributes.MAX_HEALTH, new AttributeModifier(bootsConfigMaxHP, () -> "boots_config_max_hp", data.getMaxHP(), AttributeModifier.Operation.ADDITION));
                    event.addModifier(Attributes.MOVEMENT_SPEED, new AttributeModifier(bootsConfigMovespeed, () -> "boots_config_movespeed", data.getMovespeed(), AttributeModifier.Operation.ADDITION));
                    event.addModifier(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(bootsConfigKnockbackResistance, () -> "boots_config_knockback_resistance", data.getKnockbackRes(), AttributeModifier.Operation.ADDITION));
                    event.addModifier(Attributes.LUCK, new AttributeModifier(bootsConfigLuck, () -> "boots_config_luck", data.getLuck(), AttributeModifier.Operation.ADDITION));
                    event.addModifier(IDFAttributes.STRIKE_MULT.get(), new AttributeModifier(bootsConfigStrikeMult, () -> "boots_config_strike", data.getStrikeMult(), AttributeModifier.Operation.ADDITION));
                    event.addModifier(IDFAttributes.PIERCE_MULT.get(), new AttributeModifier(bootsConfigPierceMult, () -> "boots_config_pierce", data.getPierceMult(), AttributeModifier.Operation.ADDITION));
                    event.addModifier(IDFAttributes.SLASH_MULT.get(), new AttributeModifier(bootsConfigSlashMult, () -> "boots_config_slash", data.getSlashMult(), AttributeModifier.Operation.ADDITION));
                    event.addModifier(IDFAttributes.CRUSH_MULT.get(), new AttributeModifier(bootsConfigCrushMult, () -> "boots_config_crush", data.getCrushMult(), AttributeModifier.Operation.ADDITION));
                    event.addModifier(IDFAttributes.GENERIC_MULT.get(), new AttributeModifier(bootsConfigGenericMult, () -> "boots_config_generic", data.getGenericMult(), AttributeModifier.Operation.ADDITION));
                }
            }
        }
    }

}
