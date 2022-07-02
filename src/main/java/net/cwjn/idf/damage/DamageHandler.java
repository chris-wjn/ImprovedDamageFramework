package net.cwjn.idf.damage;

import net.cwjn.idf.attribute.IDFAttributes;
import net.cwjn.idf.config.json.data.SourceCatcherData;
import net.cwjn.idf.event.post.PostMitigationDamageEvent;
import net.cwjn.idf.event.post.PreMitigationDamageEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class DamageHandler {

    public static float handleDamage(LivingEntity target, DamageSource source, float amount) {
        //In ATHandler we leave fall damage as purely physical, so we can calculate this here.
        if (source.isFall()) {
            double fallLevel = 0;
            for (ItemStack item : target.getArmorSlots()) {
                fallLevel += EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FALL_PROTECTION, item);
            }
            amount -= 1 * fallLevel;
        }
        //If the source isn't integrated, integrate it. Maintains isFall, isExplosion, and isProjectile.
        if (!(source instanceof IDFInterface)) source = SourceCatcherData.convert(source);
        //since we integrated the source in the line above, it should be guaranteed that we can cast the source without issue.
        IDFInterface convertedSource = (IDFInterface) source;
        //create variables to hold the damage, damage class, pen, and lifesteal. damage is flat numbers, pen and lifesteal are % values ranging from 0-100.
        //we initialize default values to minimize the possibility of null pointer crashes.
        float fireDamage = 0, waterDamage = 0, lightningDamage = 0, magicDamage = 0, darkDamage = 0, physicalDamage = 0, pen = 0, lifesteal = 0;
        String damageClass = "strike";
        if (convertedSource.isConversion()) {
            //if the source is a conversion from a vanilla source, we take the physical amount provided and spread it across all damage types
            //according the ratios given. This means the values of all the damage types should be < 1.0, and any leftovers should remain as
            //physical damage.
            fireDamage = convertedSource.getFire() * amount;
            waterDamage = convertedSource.getWater() * amount;
            lightningDamage = convertedSource.getLightning() * amount;
            magicDamage = convertedSource.getMagic() * amount;
            darkDamage = convertedSource.getDark() * amount;
            physicalDamage = amount - (fireDamage + waterDamage + lightningDamage + magicDamage + darkDamage);
        } else {
            //otherwise, the source is a regular instance of IDFDamage, so we don't have to convert anything. The values given here
            //should be flat values we want to use directly. Amount just corresponds to the physical damage.
            fireDamage = convertedSource.getFire();
            waterDamage = convertedSource.getWater();
            lightningDamage = convertedSource.getLightning();
            magicDamage = convertedSource.getMagic();
            darkDamage = convertedSource.getDark();
            physicalDamage = amount;
        }
        //pen and lifesteal work the same way regardless of if the source is a conversion or not.
        pen = convertedSource.getPen()/100; //we divide this by 100 here to get a number btwn 0.0 and 1.0 so we can use it in damage calculations.
        lifesteal = convertedSource.getLifesteal()/100; //same concept here.
        damageClass = convertedSource.getDamageClass();
        //we run the premitigation event first, then put the damage and resistance values into an array here to easily loop through them and apply damage calculation math.
        //resistances need to be divided by 100 to convert them to usable numbers in math. Armour is multiplied by 0.03 because every
        //point of armour should be worth 3% physical damage reduction.
        //defense is just 1/3rd of armour toughness. Subject to change.
        PreMitigationDamageEvent event = new PreMitigationDamageEvent(target, fireDamage, waterDamage, lightningDamage, magicDamage, darkDamage, physicalDamage, pen, lifesteal, damageClass);
        MinecraftForge.EVENT_BUS.post(event);
        float[] dv = {event.getFire(), event.getWater(), event.getLightning(), event.getMagic(), event.getDark(), event.getPhysical()};
        pen = event.getPen();
        lifesteal = event.getLifesteal();
        damageClass = event.getDamageClass();
        double[] rv = {target.getAttributeValue(IDFAttributes.FIRE_RESISTANCE.get())/100, target.getAttributeValue(IDFAttributes.WATER_RESISTANCE.get())/100,
                target.getAttributeValue(IDFAttributes.LIGHTNING_RESISTANCE.get())/100, target.getAttributeValue(IDFAttributes.MAGIC_RESISTANCE.get())/100,
                target.getAttributeValue(IDFAttributes.DARK_RESISTANCE.get())/100, target.getAttributeValue(Attributes.ARMOR) * 0.03};
        double defense = target.getAttributeValue(Attributes.ARMOR_TOUGHNESS)/3;
        //we put the damage class multipliers into a map with the strings as keys. This is to easily apply the correct multiplier
        //to all the damage types.
        Map<String, Double> mappedMultipliers = new HashMap<>(3);
        mappedMultipliers.put("strike", target.getAttributeValue(IDFAttributes.STRIKE_MULT.get()));
        mappedMultipliers.put("pierce", target.getAttributeValue(IDFAttributes.PIERCE_MULT.get()));
        mappedMultipliers.put("slash", target.getAttributeValue(IDFAttributes.SLASH_MULT.get()));
        mappedMultipliers.put("crush", target.getAttributeValue(IDFAttributes.CRUSH_MULT.get()));
        mappedMultipliers.put("generic", target.getAttributeValue(IDFAttributes.GENERIC_MULT.get()));
        //now multiply each damage value by the correct multiplier.
        for (int i = 0; i < 6; i++) {
            dv[i] *= mappedMultipliers.get(damageClass);
        }
        //now that we have the final pre-mitigation values, check if the source is true damage. If it is,
        //there's no need to do the rest of the method.
        if (convertedSource.isTrue()) return sum(dv);
        //now we can factor in blast protection, fire protection and projectile protection. Reduce each damage type by 6.25% per
        //level of blast and projectile respectively. Assuming the highest any player could get is 16 (prot 4 on each armour piece),
        //this means having maxed out of either makes you take 100% reduced damage from these sources.
        if (source.isProjectile()) {
            double projectileLevel = 0;
            for (ItemStack item : target.getArmorSlots()) {
                projectileLevel += EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PROJECTILE_PROTECTION, item);
            }
            if (projectileLevel > 0) {
                for (int i = 0; i < 6; i++) {
                    dv[i] *= (1 - (projectileLevel * 0.0625));
                }
            }
        }
        if (source.isExplosion()) {
            double blastLevel = 0;
            for (ItemStack item : target.getArmorSlots()) {
                blastLevel += EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BLAST_PROTECTION, item);
            }
            if (blastLevel > 0) {
                for (int i = 0; i < 6; i++) {
                    dv[i] *= (1 - (blastLevel * 0.0625));
                }
            }
        }
        if (source.isFire()) {
            double fireLevel = 0;
            for (ItemStack item : target.getArmorSlots()) {
                fireLevel += EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FIRE_PROTECTION, item);
            }
            if (fireLevel > 0) {
                for (int i = 0; i < 6; i++) {
                    dv[i] *= (1 - (fireLevel * 0.0625));
                }
            }
        }
        //now we calculate for general protection enchantment. We want this to be weaker than specific protection enchants,
        //so lets make it reduce damage taken by 1.5% per level, capping out at 24% at full prot 4 armour.
        double protLevel = 0;
        for (ItemStack item: target.getArmorSlots()) {
            protLevel += EnchantmentHelper.getItemEnchantmentLevel(Enchantments.ALL_DAMAGE_PROTECTION, item);
        }
        if (protLevel > 0) {
            for (int i = 0; i < 6; i++) {
                dv[i] *= (1 - (protLevel * 0.015));
            }
        }
        //now we factor in the resistance effect. Increases all resistances by 20% per level.
        if (target.hasEffect(MobEffects.DAMAGE_RESISTANCE)) {
            double resistanceLevel = ((double)target.getEffect(MobEffects.DAMAGE_RESISTANCE).getAmplifier() + 1)/5;
            for (int i = 0; i < 6; i++) {
                rv[i] *= (1 + resistanceLevel);
            }
        }
        //now we start calculating the final damage value. We need to post the PostMitigation event,
        //then, create a variable to store the final damage,
        //then we do the damage math for fire, water, lightning, magic, and dark.
        //After, do the math for physical damage, factoring in defense as a flat reduction.
        PostMitigationDamageEvent postMitigation = new PostMitigationDamageEvent(target, 0, 0, 0, 0, 0, 0);
        for (int i = 0; i < 5; i++) {
            if (dv[i] > 0) {
                postMitigation.setDamage(i, (float) (dv[i] * (1 - rv[i])));
            }
        }
        if (dv[5] > 0) {
            float ad = (float) (dv[5] - defense);
            float physicalRes = (float) (rv[5] * (1 - pen));
            postMitigation.setDamage(5, (ad * (1 - physicalRes)));
        }
        MinecraftForge.EVENT_BUS.post(postMitigation);
        float returnValue = sum(postMitigation.getDamage());
        //final thing we do is give lifesteal to the attacker, if it was a source of entity damage that was done.
        if (convertedSource instanceof EntityDamageSource) {
            Entity sourceEntity = ((EntityDamageSource) convertedSource).getEntity();
            if (sourceEntity instanceof LivingEntity livingEntity) {
                if (lifesteal != 0) {
                    livingEntity.heal(returnValue * lifesteal);
                }
            }
        }
        return returnValue;
    }

    public static float handleDamageWithDebug(LivingEntity target, DamageSource source, float amount, Logger log) {
        log.debug("----------DAMAGE HANDLER DEBUG----------");
        log.debug("TARGET: " + target.getType().getRegistryName());
        log.debug("SOURCE: " + source.getMsgId() + "(" + amount + ")");
        log.debug("Source already integrated?: " + (source instanceof IDFInterface));
        log.debug("----------------------------------------");
        //In ATHandler we leave fall damage as purely physical, so we can calculate this here.
        if (source.isFall()) {
            log.debug("----------FALL ENCHANT CHECKER----------");
            double fallLevel = 0;
            for (ItemStack item : target.getArmorSlots()) {
                fallLevel += EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FALL_PROTECTION, item);
            }
            log.debug("Total Fall Levels: " + fallLevel);
            amount -= 1 * fallLevel;
            log.debug("Reduced damage to " + amount);
            log.debug("----------------------------------------");
        }
        //If the source isn't integrated, integrate it. Maintains isFall, isExplosion, bypassInvuln, and isProjectile.
        if (!(source instanceof IDFInterface))  {
            log.debug("----------SOURCE CONVERSION----------");
            log.debug("PRE-CONVERSION:");
            log.debug("isFall?: " + source.isFall());
            log.debug("isExplosion?: " + source.isExplosion());
            log.debug("isProjectile?: " + source.isExplosion());
            log.debug("isBypassInvuln?: " + source.isBypassInvul());
            source = SourceCatcherData.convert(source);
        }
        //since we integrated the source in the line above, it should be guaranteed that we can cast the source without issue.
        IDFInterface convertedSource = (IDFInterface) source;
        log.debug("----------CONVERTED SOURCE INFORMATION----------");
        log.debug("SOURCE: " + convertedSource.getMsgId() + " of class " + convertedSource.getClass().getName());
        log.debug("Is Conversion?: " + convertedSource.isConversion());
        log.debug("DAMAGE CLASS: " + convertedSource.getDamageClass());
        log.debug("DAMAGE NUMBERS (fire, water, lightning, magic, dark, physical): " +
                convertedSource.getFire() + ", " +
                convertedSource.getWater() + ", " +
                convertedSource.getLightning() + ", " +
                convertedSource.getMagic() + ", " +
                convertedSource.getDark() + ", " +
                amount);
        log.debug("AUXILIARY INFO (pen, lifesteal): " +
                convertedSource.getPen() + "%, " +
                convertedSource.getLifesteal() + "%, ");
        log.debug("OTHER INFO: ");
        log.debug("isFall?: " + ((DamageSource) convertedSource).isFall());
        log.debug("isExplosion?: " + ((DamageSource) convertedSource).isExplosion());
        log.debug("isProjectile?:" + ((DamageSource) convertedSource).isProjectile());
        log.debug("isTrue?:" + convertedSource.isTrue());
        log.debug("------------------------------------------------");
        //create variables to hold the damage, damage class, pen, and lifesteal. damage is flat numbers, pen and lifesteal are % values ranging from 0-100.
        //we initialize default values to minimize the possibility of null pointer crashes.
        float fireDamage = 0, waterDamage = 0, lightningDamage = 0, magicDamage = 0, darkDamage = 0, physicalDamage = 0, pen = 0, lifesteal = 0;
        String damageClass = "strike";
        if (convertedSource.isConversion()) {
            //if the source is a conversion from a vanilla source, we take the physical amount provided and spread it across all damage types
            //according the ratios given. This means the values of all the damage types should be < 1.0, and any leftovers should remain as
            //physical damage.
            fireDamage = convertedSource.getFire() * amount;
            waterDamage = convertedSource.getWater() * amount;
            lightningDamage = convertedSource.getLightning() * amount;
            magicDamage = convertedSource.getMagic() * amount;
            darkDamage = convertedSource.getDark() * amount;
            physicalDamage = amount - (fireDamage + waterDamage + lightningDamage + magicDamage + darkDamage);
        } else {
            //otherwise, the source is a regular instance of IDFDamage, so we don't have to convert anything. The values given here
            //should be flat values we want to use directly. Amount just corresponds to the physical damage.
            fireDamage = convertedSource.getFire();
            waterDamage = convertedSource.getWater();
            lightningDamage = convertedSource.getLightning();
            magicDamage = convertedSource.getMagic();
            darkDamage = convertedSource.getDark();
            physicalDamage = amount;
        }
        //pen and lifesteal work the same way regardless of it the source is a conversion or not.
        pen = convertedSource.getPen()/100; //we divide this by 100 here to get a number btwn 0.0 and 1.0 so we can use it in damage calculations.
        lifesteal = convertedSource.getLifesteal()/100; //same concept here.
        damageClass = convertedSource.getDamageClass();
        //we run the premitigation event first, then put the damage and resistance values into an array here to easily loop through them and apply damage calculation math.
        //resistances need to be divided by 100 to convert them to usable numbers in math. Armour is multiplied by 0.03 because every
        //point of armour should be worth 3% physical damage reduction.
        //defense is just 1/3rd of armour toughness. Subject to change.
        PreMitigationDamageEvent event = new PreMitigationDamageEvent(target, fireDamage, waterDamage, lightningDamage, magicDamage, darkDamage, physicalDamage, pen, lifesteal, damageClass);
        MinecraftForge.EVENT_BUS.post(event);
        float[] dv = {event.getFire(), event.getWater(), event.getLightning(), event.getMagic(), event.getDark(), event.getPhysical()};
        pen = event.getPen();
        lifesteal = event.getLifesteal();
        damageClass = event.getDamageClass();
        log.debug("----------POST-INITIALIZATION----------");
        log.debug("DAMAGE NUMBERS (fire, water, lightning, magic, dark, physical): " +
                dv[0] + ", " +
                dv[1] + ", " +
                dv[2] + ", " +
                dv[3] + ", " +
                dv[4] + ", " +
                dv[5]);
        log.debug("AUXILIARY INFO (pen, lifesteal): " +
                pen + ", " +
                lifesteal + ", ");
        log.debug("DAMAGE CLASS: " + damageClass);
        log.debug("---------------------------------------");
        double[] rv = {target.getAttributeValue(IDFAttributes.FIRE_RESISTANCE.get())/100, target.getAttributeValue(IDFAttributes.WATER_RESISTANCE.get())/100,
                target.getAttributeValue(IDFAttributes.LIGHTNING_RESISTANCE.get())/100, target.getAttributeValue(IDFAttributes.MAGIC_RESISTANCE.get())/100,
                target.getAttributeValue(IDFAttributes.DARK_RESISTANCE.get())/100, target.getAttributeValue(Attributes.ARMOR) * 0.03};
        double defense = target.getAttributeValue(Attributes.ARMOR_TOUGHNESS)/3;
        //we put the damage class multipliers into a map with the strings as keys. This is to easily apply the correct multiplier
        //to all the damage types.
        Map<String, Double> mappedMultipliers = new HashMap<>(3);
        mappedMultipliers.put("strike", target.getAttributeValue(IDFAttributes.STRIKE_MULT.get()));
        mappedMultipliers.put("pierce", target.getAttributeValue(IDFAttributes.PIERCE_MULT.get()));
        mappedMultipliers.put("slash", target.getAttributeValue(IDFAttributes.SLASH_MULT.get()));
        mappedMultipliers.put("crush", target.getAttributeValue(IDFAttributes.CRUSH_MULT.get()));
        mappedMultipliers.put("generic", target.getAttributeValue(IDFAttributes.GENERIC_MULT.get()));
        log.debug("----------TARGET RESISTANCE INFORMATION----------");
        log.debug("TARGET HEALTH: " + target.getHealth());
        log.debug("RESISTANCE NUMBERS (fire, water, lightning, magic, dark, physical, defense): " +
                rv[0] + ", " +
                rv[1] + ", " +
                rv[2] + ", " +
                rv[3] + ", " +
                rv[4] + ", " +
                rv[5] + ", " +
                defense);
        log.debug("----------TARGET DAMAGE CLASS MULTIPLIERS----------");
        for (Map.Entry<String, Double> entry : mappedMultipliers.entrySet()) {
            log.debug(entry.getKey() + ": " + entry.getValue());
        }
        log.debug("---------------------------------------------------");
        //now multiply each damage value by the correct multiplier.
        for (int i = 0; i < 6; i++) {
            dv[i] *= mappedMultipliers.get(damageClass);
        }
        log.debug("----------POST DAMAGE CLASS MULTIPLICATION----------");
        log.debug("DAMAGE NUMBERS (fire, water, lightning, magic, dark, physical): " +
                dv[0] + ", " +
                dv[1] + ", " +
                dv[2] + ", " +
                dv[3] + ", " +
                dv[4] + ", " +
                dv[5]);
        log.debug("----------------------------------------------------");
        //now that we have the final pre-mitigation values, check if the source is true damage. If it is,
        //there's no need to do the rest of the method.
        if (convertedSource.isTrue()) {
            log.debug("!!!!Source is true damage. Returning " + sum(dv));
            return sum(dv);
        }
        //now we can factor in blast protection and projectile protection. Reduce each damage type by 6.25% per
        //level of blast and projectile respectively. Assuming the highest any player could get is 16 (prot 4 on each armour piece),
        //this means having maxed out of either makes you take 100% reduced damage from these sources.
        if (source.isProjectile()) {
            log.debug("----------PROJECTILE ENCHANT CHECKER----------");
            double projectileLevel = 0;
            for (ItemStack item : target.getArmorSlots()) {
                projectileLevel += EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PROJECTILE_PROTECTION, item);
            }
            log.debug("Total Projectile Levels: " + projectileLevel);
            if (projectileLevel > 0) {
                for (int i = 0; i < 6; i++) {
                    dv[i] *= (1 - (projectileLevel * 0.0625));
                }
                log.debug("NEW DAMAGE NUMBERS (fire, water, lightning, magic, dark, physical): " +
                        dv[0] + ", " +
                        dv[1] + ", " +
                        dv[2] + ", " +
                        dv[3] + ", " +
                        dv[4] + ", " +
                        dv[5]);
            }
            log.debug("----------------------------------------------");
        }
        if (source.isExplosion()) {
            log.debug("----------BLAST ENCHANT CHECKER----------");
            double blastLevel = 0;
            for (ItemStack item : target.getArmorSlots()) {
                blastLevel += EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BLAST_PROTECTION, item);
            }
            log.debug("Total Blast Levels: " + blastLevel);
            if (blastLevel > 0) {
                for (int i = 0; i < 6; i++) {
                    dv[i] *= (1 - (blastLevel * 0.0625));
                }
                log.debug("NEW DAMAGE NUMBERS (fire, water, lightning, magic, dark, physical): " +
                        dv[0] + ", " +
                        dv[1] + ", " +
                        dv[2] + ", " +
                        dv[3] + ", " +
                        dv[4] + ", " +
                        dv[5]);
            }
            log.debug("-----------------------------------------");
        }
        if (source.isFire()) {
            log.debug("----------FIRE PROTECT ENCHANT CHECKER----------");
            double fireLevel = 0;
            for (ItemStack item : target.getArmorSlots()) {
                fireLevel += EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FIRE_PROTECTION, item);
            }
            log.debug("Total Fire Levels: " + fireLevel);
            if (fireLevel > 0) {
                for (int i = 0; i < 6; i++) {
                    dv[i] *= (1 - (fireLevel * 0.0625));
                }
                log.debug("NEW DAMAGE NUMBERS (fire, water, lightning, magic, dark, physical): " +
                        dv[0] + ", " +
                        dv[1] + ", " +
                        dv[2] + ", " +
                        dv[3] + ", " +
                        dv[4] + ", " +
                        dv[5]);
            }
            log.debug("-----------------------------------------");
        }
        double protLevel = 0;
        for (ItemStack item: target.getArmorSlots()) {
            protLevel += EnchantmentHelper.getItemEnchantmentLevel(Enchantments.ALL_DAMAGE_PROTECTION, item);
        }
        if (protLevel > 0) {
            log.debug("----------ALL PROTECTION ENCHANT CHECKER----------");
            log.debug("Total Protection Levels: " + protLevel);
            for (int i = 0; i < 6; i++) {
                dv[i] *= (1 - (protLevel * 0.015));
            }
            log.debug("NEW DAMAGE NUMBERS (fire, water, lightning, magic, dark, physical): " +
                    dv[0] + ", " +
                    dv[1] + ", " +
                    dv[2] + ", " +
                    dv[3] + ", " +
                    dv[4] + ", " +
                    dv[5]);
            log.debug("--------------------------------------------------");
        }
        //now we factor in the resistance effect. Increases all resistances by 20% per level.
        if (target.hasEffect(MobEffects.DAMAGE_RESISTANCE)) {
            log.debug("----------RESISTANCE EFFECT CHECKER----------");
            double resistanceLevel = ((double)target.getEffect(MobEffects.DAMAGE_RESISTANCE).getAmplifier() + 1)/5;
            log.debug("Total Resistance Levels: " + resistanceLevel);
            for (int i = 0; i < 6; i++) {
                rv[i] *= (1 + resistanceLevel);
            }
            log.debug("NEW RESISTANCE NUMBERS (fire, water, lightning, magic, dark, physical, defense): " +
                    rv[0] + ", " +
                    rv[1] + ", " +
                    rv[2] + ", " +
                    rv[3] + ", " +
                    rv[4] + ", " +
                    rv[5] + ", " +
                    defense);
        }
        //now we start calculating the final damage value. First create a variable to store the final damage,
        //then we do the damage math for fire, water, lightning, magic, and dark.
        //After, do the math for physical damage, factoring in defense as a flat reduction.
        log.debug("----------FINAL DAMAGE NUMBERS----------");
        PostMitigationDamageEvent postMitigation = new PostMitigationDamageEvent(target, 0, 0, 0, 0, 0, 0);
        for (int i = 0; i < 5; i++) {
            if (dv[i] > 0) {
                postMitigation.setDamage(i, (float) (dv[i] * (1 - rv[i])));
            }
        }
        if (dv[5] > 0) {
            float ad = (float) (dv[5] - defense);
            float physicalRes = (float) (rv[5] * (1 - pen));
            postMitigation.setDamage(5, (ad * (1 - physicalRes)));
        }
        MinecraftForge.EVENT_BUS.post(postMitigation);
        for (float f : postMitigation.getDamage()) {
            log.debug(f);
        }
        float returnValue = sum(postMitigation.getDamage());
        log.debug("----------------------------------------");
        //final thing we do is give lifesteal to the attacker, if it was a source of entity damage that was done.\
        log.debug("----------LIFESTEAL CHECK----------");
        if (convertedSource instanceof EntityDamageSource) {
            log.debug("-> source is entity damage!");
            Entity sourceEntity = ((EntityDamageSource) convertedSource).getEntity();
            if (sourceEntity != null && sourceEntity instanceof LivingEntity livingEntity) {
                log.debug("-> the source of the damage was a living entity!");
                if (lifesteal != 0) {
                    log.debug("lifesteal value is: " + lifesteal*100 + "%, healed " + livingEntity.getType().getRegistryName() + " for " + (returnValue * lifesteal));
                    livingEntity.heal(returnValue * lifesteal);
                }
            }
        }
        log.debug("-----------------------------------");
        return returnValue;
    }

    private static float sum(float[] a) {
        float returnFloat = 0;
        for (float f : a) {
            returnFloat+=f;
        }
        return returnFloat;
    }
}
