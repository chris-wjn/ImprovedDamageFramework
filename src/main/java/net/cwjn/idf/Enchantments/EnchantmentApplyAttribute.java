package net.cwjn.idf.Enchantments;

import net.cwjn.idf.Attributes.AttributeRegistry;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber
public class EnchantmentApplyAttribute {

    public static UUID blastProtectionPhysical = UUID.randomUUID();
    public static UUID blastProtectionFire = UUID.randomUUID();
    public static UUID fireProtectionFire = UUID.randomUUID();
    public static UUID protectionPhysical = UUID.randomUUID();
    public static UUID protectionFire = UUID.randomUUID();
    public static UUID protectionWater = UUID.randomUUID();
    public static UUID protectionLightning = UUID.randomUUID();
    public static UUID protectionMagic = UUID.randomUUID();
    public static UUID protectionDark = UUID.randomUUID();
    public static UUID flatFireDamage =  UUID.randomUUID();
    public static UUID flatWaterDamage =  UUID.randomUUID();
    public static UUID flatLightningDamage =  UUID.randomUUID();
    public static UUID flatMagicDamage =  UUID.randomUUID();
    public static UUID flatDarkDamage =  UUID.randomUUID();
    public static UUID flatFireResistance =  UUID.randomUUID();
    public static UUID flatWaterResistance =  UUID.randomUUID();
    public static UUID flatLightningResistance =  UUID.randomUUID();
    public static UUID flatMagicResistance =  UUID.randomUUID();
    public static UUID flatDarkResistance =  UUID.randomUUID();
    public static UUID multFireDamage =  UUID.randomUUID();
    public static UUID multWaterDamage =  UUID.randomUUID();
    public static UUID multLightningDamage =  UUID.randomUUID();
    public static UUID multMagicDamage =  UUID.randomUUID();
    public static UUID multDarkDamage =  UUID.randomUUID();
    public static UUID multFireResistance =  UUID.randomUUID();
    public static UUID multWaterResistance =  UUID.randomUUID();
    public static UUID multLightningResistance =  UUID.randomUUID();
    public static UUID multMagicResistance =  UUID.randomUUID();
    public static UUID multDarkResistance =  UUID.randomUUID();


    @SubscribeEvent
    public static void ItemModifierEvent(ItemAttributeModifierEvent event) {
        ItemStack item = event.getItemStack();
        if (LivingEntity.getEquipmentSlotForItem(item) == event.getSlotType()) {
            for (Map.Entry<Enchantment, Integer> enchant : EnchantmentHelper.getEnchantments(item).entrySet()) {
                switch (enchant.getKey().getDescriptionId()) {
                    case "enchantment.minecraft.protection":
                        event.addModifier(Attributes.ARMOR, new AttributeModifier(protectionPhysical, () -> "protection_physical", (double) enchant.getValue() / 3, AttributeModifier.Operation.ADDITION));
                        event.addModifier(AttributeRegistry.FIRE_RESISTANCE.get(), new AttributeModifier(protectionFire, () -> "protection_dark", (double) enchant.getValue(), AttributeModifier.Operation.ADDITION));
                        event.addModifier(AttributeRegistry.WATER_RESISTANCE.get(), new AttributeModifier(protectionWater, () -> "protection_fire", (double) enchant.getValue(), AttributeModifier.Operation.ADDITION));
                        event.addModifier(AttributeRegistry.LIGHTNING_RESISTANCE.get(), new AttributeModifier(protectionLightning, () -> "protection_water", (double) enchant.getValue(), AttributeModifier.Operation.ADDITION));
                        event.addModifier(AttributeRegistry.MAGIC_RESISTANCE.get(), new AttributeModifier(protectionMagic, () -> "protection_lightning", (double) enchant.getValue(), AttributeModifier.Operation.ADDITION));
                        event.addModifier(AttributeRegistry.DARK_RESISTANCE.get(), new AttributeModifier(protectionDark, () -> "protection_magic", (double) enchant.getValue(), AttributeModifier.Operation.ADDITION));
                        break;
                    case "enchantment.minecraft.fire_protection":
                        event.addModifier(AttributeRegistry.FIRE_RESISTANCE.get(), new AttributeModifier(fireProtectionFire, () -> "fire_protection_fire", 1.25D * (double) enchant.getValue(), AttributeModifier.Operation.ADDITION));
                        break;
                    case "enchantment.minecraft.blast_protection":
                        event.addModifier(Attributes.ARMOR, new AttributeModifier(blastProtectionPhysical, () -> "blast_protection_physical", (1D / 6) * (double) enchant.getValue(), AttributeModifier.Operation.ADDITION));
                        event.addModifier(AttributeRegistry.FIRE_RESISTANCE.get(), new AttributeModifier(blastProtectionFire, () -> "blast_protection_fire", 0.5D * (double) enchant.getValue(), AttributeModifier.Operation.ADDITION));
                        break;
                    case "enchantment.idf.fire_damage_flat":
                        event.addModifier(AttributeRegistry.FIRE_DAMAGE.get(), new AttributeModifier(flatFireDamage, () -> "flat_fire_damage", (double) enchant.getValue(), AttributeModifier.Operation.ADDITION));
                        break;
                    case "enchantment.idf.water_damage_flat":
                        event.addModifier(AttributeRegistry.WATER_DAMAGE.get(), new AttributeModifier(flatWaterDamage, () -> "flat_water_damage", (double) enchant.getValue(), AttributeModifier.Operation.ADDITION));
                        break;
                    case "enchantment.idf.lightning_damage_flat":
                        event.addModifier(AttributeRegistry.LIGHTNING_DAMAGE.get(), new AttributeModifier(flatLightningDamage, () -> "flat_lightning_damage", (double) enchant.getValue(), AttributeModifier.Operation.ADDITION));
                        break;
                    case "enchantment.idf.magic_damage_flat":
                        event.addModifier(AttributeRegistry.MAGIC_DAMAGE.get(), new AttributeModifier(flatMagicDamage, () -> "flat_magic_damage", (double) enchant.getValue(), AttributeModifier.Operation.ADDITION));
                        break;
                    case "enchantment.idf.dark_damage_flat":
                        event.addModifier(AttributeRegistry.DARK_DAMAGE.get(), new AttributeModifier(flatDarkDamage, () -> "flat_dark_damage", (double) enchant.getValue(), AttributeModifier.Operation.ADDITION));
                        break;
                    case "enchantment.idf.fire_damage_mult":
                        event.addModifier(AttributeRegistry.FIRE_DAMAGE.get(), new AttributeModifier(multFireDamage, () -> "mult_fire_damage", ((double) enchant.getValue())*0.05, AttributeModifier.Operation.MULTIPLY_TOTAL));
                        break;
                    case "enchantment.idf.water_damage_mult":
                        event.addModifier(AttributeRegistry.WATER_DAMAGE.get(), new AttributeModifier(multWaterDamage, () -> "mult_water_damage", ((double) enchant.getValue()) *0.05, AttributeModifier.Operation.MULTIPLY_TOTAL));
                        break;
                    case "enchantment.idf.lightning_damage_mult":
                        event.addModifier(AttributeRegistry.LIGHTNING_DAMAGE.get(), new AttributeModifier(multLightningDamage, () -> "mult_lightning_damage", ((double) enchant.getValue()) *0.05, AttributeModifier.Operation.MULTIPLY_TOTAL));
                        break;
                    case "enchantment.idf.magic_damage_mult":
                        event.addModifier(AttributeRegistry.MAGIC_DAMAGE.get(), new AttributeModifier(multMagicDamage, () -> "mult_magic_damage", ((double) enchant.getValue()) *0.05, AttributeModifier.Operation.MULTIPLY_TOTAL));
                        break;
                    case "enchantment.idf.dark_damage_mult":
                        event.addModifier(AttributeRegistry.DARK_DAMAGE.get(), new AttributeModifier(multDarkDamage, () -> "mult_dark_damage", ((double) enchant.getValue()) *0.05, AttributeModifier.Operation.MULTIPLY_TOTAL));
                        break;
                    case "enchantment.idf.fire_resist_flat":
                        event.addModifier(AttributeRegistry.FIRE_RESISTANCE.get(), new AttributeModifier(flatFireResistance, () -> "flat_fire_resist", (double) enchant.getValue(), AttributeModifier.Operation.ADDITION));
                        break;
                    case "enchantment.idf.water_resist_flat":
                        event.addModifier(AttributeRegistry.WATER_RESISTANCE.get(), new AttributeModifier(flatWaterResistance, () -> "flat_water_resist", (double) enchant.getValue(), AttributeModifier.Operation.ADDITION));
                        break;
                    case "enchantment.idf.lightning_resist_flat":
                        event.addModifier(AttributeRegistry.LIGHTNING_RESISTANCE.get(), new AttributeModifier(flatLightningResistance, () -> "flat_lightning_resist", (double) enchant.getValue(), AttributeModifier.Operation.ADDITION));
                        break;
                    case "enchantment.idf.magic_resist_flat":
                        event.addModifier(AttributeRegistry.MAGIC_RESISTANCE.get(), new AttributeModifier(flatMagicResistance, () -> "flat_magic_resist", (double) enchant.getValue(), AttributeModifier.Operation.ADDITION));
                        break;
                    case "enchantment.idf.dark_resist_flat":
                        event.addModifier(AttributeRegistry.DARK_RESISTANCE.get(), new AttributeModifier(flatDarkResistance, () -> "flat_dark_resist", (double) enchant.getValue(), AttributeModifier.Operation.ADDITION));
                        break;
                    case "enchantment.idf.fire_resist_mult":
                        event.addModifier(AttributeRegistry.FIRE_RESISTANCE.get(), new AttributeModifier(multFireResistance, () -> "mult_fire_resist", ((double) enchant.getValue()) *0.05, AttributeModifier.Operation.MULTIPLY_TOTAL));
                        break;
                    case "enchantment.idf.water_resist_mult":
                        event.addModifier(AttributeRegistry.WATER_RESISTANCE.get(), new AttributeModifier(multWaterResistance, () -> "mult_water_resist", ((double) enchant.getValue()) *0.05, AttributeModifier.Operation.MULTIPLY_TOTAL));
                        break;
                    case "enchantment.idf.lightning_resist_mult":
                        event.addModifier(AttributeRegistry.LIGHTNING_RESISTANCE.get(), new AttributeModifier(multLightningResistance, () -> "mult_lightning_resist", ((double) enchant.getValue()) *0.05, AttributeModifier.Operation.MULTIPLY_TOTAL));
                        break;
                    case "enchantment.idf.magic_resist_mult":
                        event.addModifier(AttributeRegistry.MAGIC_RESISTANCE.get(), new AttributeModifier(multMagicResistance, () -> "mult_magic_resist", ((double) enchant.getValue()) *0.05, AttributeModifier.Operation.MULTIPLY_TOTAL));
                        break;
                    case "enchantment.idf.dark_resist_mult":
                        event.addModifier(AttributeRegistry.DARK_RESISTANCE.get(), new AttributeModifier(multDarkResistance, () -> "mult_dark_resist", ((double) enchant.getValue()) *0.05, AttributeModifier.Operation.MULTIPLY_TOTAL));
                        break;
                }
            }
        }
    }
}
