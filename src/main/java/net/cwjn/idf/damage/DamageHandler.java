package net.cwjn.idf.damage;

import net.cwjn.idf.util.Util;
import net.cwjn.idf.attribute.IDFAttributes;
import net.cwjn.idf.config.json.data.SourceCatcherData;
import net.cwjn.idf.api.event.PostMitigationDamageEvent;
import net.cwjn.idf.api.event.PreMitigationDamageEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class DamageHandler {

    private static final double e = 2.718281828459045;

    public static float handleDamage(LivingEntity target, DamageSource source, float amount) {
        //In ATHandler we leave fall damage as purely physical, so we can calculate this here.
        if (source.isFall()) {
            double fallLevel = 0;
            for (ItemStack item : target.getArmorSlots()) {
                fallLevel += item.getEnchantmentLevel(Enchantments.FALL_PROTECTION);
            }
            amount *= (1 - (fallLevel * 0.0625));
        }
        //If the source isn't integrated, integrate it. Maintains isFall, isExplosion, and isProjectile.
        if (!(source instanceof IDFInterface)) source = SourceCatcherData.convert(source);
        //since we integrated the source in the line above, it should be guaranteed that we can cast the source without issue.
        IDFInterface convertedSource = (IDFInterface) source;
        //create variables to hold the damage, damage class, pen, and lifesteal. damage is flat numbers, pen and lifesteal are % values ranging from 0-100.
        //we initialize default values to minimize the possibility of null pointer crashes.
        float fireDamage, waterDamage, lightningDamage, magicDamage, darkDamage, physicalDamage, pen, lifesteal, weight, knockback;
        String damageClass;
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
        //we run the premitigation event first, then put the damage and resistance values into an array here to easily loop through them and apply modifiers.
        //Pen and lifesteal need to be divided by 100 to get a value between 0.0 and 1.0 which we can use in math.
        PreMitigationDamageEvent event = new PreMitigationDamageEvent(target, fireDamage, waterDamage, lightningDamage, magicDamage, darkDamage, physicalDamage, convertedSource.getPen()/100, convertedSource.getLifesteal()/100, convertedSource.getKnockback(), convertedSource.getWeight(),
                (float) armourFormula(target.getAttributeValue(IDFAttributes.FIRE_RESISTANCE.get())), (float) armourFormula(target.getAttributeValue(IDFAttributes.WATER_RESISTANCE.get())),
                (float) armourFormula(target.getAttributeValue(IDFAttributes.LIGHTNING_RESISTANCE.get())), (float) armourFormula(target.getAttributeValue(IDFAttributes.MAGIC_RESISTANCE.get())),
                (float) armourFormula(target.getAttributeValue(IDFAttributes.DARK_RESISTANCE.get())), (float) armourFormula(target.getAttributeValue(Attributes.ARMOR)),
                (float) target.getAttributeValue(Attributes.ARMOR_TOUGHNESS), convertedSource.getDamageClass());
        MinecraftForge.EVENT_BUS.post(event);
        float[] dv = {event.getFireDmg(), event.getWaterDmg(), event.getLightningDmg(), event.getMagicDmg(), event.getDarkDmg(), event.getPhysicalDmg()};
        pen = event.getPen();
        lifesteal = event.getLifesteal();
        damageClass = event.getDamageClass();
        weight = event.getWeight();
        knockback = event.getKnockback();
        float[] rv = {event.getFireRes(), event.getWaterRes(), event.getLightningRes(), event.getMagicRes(), event.getDarkRes(), event.getPhysicalRes()};
        double weightMultiplier = weight == -1 ? 1 : Mth.clamp(Math.sqrt(weight)/Math.sqrt(event.getDef()), 0, 4);
        //now we can knockback the target based on the weightMultiplier. The first code is copied from the
        //vanilla knockback handler to get the direction of the knockback. We add the bonus knockback value from
        //the attack afterwards.
        if (source.getEntity() != null) {
            double d1 = source.getEntity().getX() - target.getX();
            double d0;
            for (d0 = source.getEntity().getZ() - target.getZ(); d1 * d1 + d0 * d0 < 1.0E-4D; d0 = (Math.random() - Math.random()) * 0.01D) {
                d1 = (Math.random() - Math.random()) * 0.01D;
            }
            target.hurtDir = (float)(Mth.atan2(d0, d1) * (double)(180F / (float)Math.PI) - (double)target.getYRot());
            target.knockback(knockback*weightMultiplier, d1, d0);
        }
        //we put the damage class multipliers into a map with the strings as keys. This is to easily apply the correct multiplier
        //to all the damage types. Then iterate through the damage values and apply the weight/defense multiplier followed by
        //the damage class multiplier
        Map<String, Double> mappedMultipliers = new HashMap<>(3);
        mappedMultipliers.put("strike", target.getAttributeValue(IDFAttributes.STRIKE_MULT.get()));
        mappedMultipliers.put("pierce", target.getAttributeValue(IDFAttributes.PIERCE_MULT.get()));
        mappedMultipliers.put("slash", target.getAttributeValue(IDFAttributes.SLASH_MULT.get()));
        mappedMultipliers.put("crush", target.getAttributeValue(IDFAttributes.CRUSH_MULT.get()));
        mappedMultipliers.put("generic", target.getAttributeValue(IDFAttributes.GENERIC_MULT.get()));
        //now multiply each damage value by the correct multiplier.
        for (int i = 0; i < 6; i++) {
            dv[i] *= weightMultiplier;
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
                projectileLevel += item.getEnchantmentLevel(Enchantments.PROJECTILE_PROTECTION);
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
                blastLevel += item.getEnchantmentLevel(Enchantments.BLAST_PROTECTION);
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
                fireLevel += item.getEnchantmentLevel(Enchantments.FIRE_PROTECTION);
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
            protLevel += item.getEnchantmentLevel(Enchantments.ALL_DAMAGE_PROTECTION);
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
        //hurt the player's armour for the sum of the damage now.
        target.hurtArmor(source, sum(dv));
        //now we start calculating the final damage value. We need to post the PostMitigation event,
        //then, create a variable to store the final damage,
        //then we do the damage math for fire, water, lightning, magic, and dark.
        PostMitigationDamageEvent postMitigation = new PostMitigationDamageEvent(target, 0, 0, 0, 0, 0, 0);
        rv[5] = rv[5] * (1.0f - pen); //factor in armour pen
        for (int i = 0; i < 6; i++) {
            if (dv[i] > 0) {
                postMitigation.setDamage(
                        i,
                        damageFormula(dv[i], rv[i])
                );
            }
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
        log.debug("TARGET: " + Util.getEntityRegistryName(target.getType()));
        log.debug("SOURCE: " + source.getMsgId() + "(" + amount + ")");
        log.debug("Source already integrated?: " + (source instanceof IDFInterface));
        log.debug("----------------------------------------");
        if (source.isFall()) {
            log.debug("----------FALL ENCHANT CHECKER----------");
            double fallLevel = 0;
            for (ItemStack item : target.getArmorSlots()) {
                fallLevel += item.getEnchantmentLevel(Enchantments.FALL_PROTECTION);
            }
            log.debug("Total Fall Levels: " + fallLevel);
            amount -= 1 * fallLevel;
            log.debug("Reduced damage to " + amount);
            log.debug("----------------------------------------");
        }
        if (!(source instanceof IDFInterface))  {
            log.debug("----------SOURCE CONVERSION----------");
            log.debug("PRE-CONVERSION:");
            log.debug("isFall?: " + source.isFall());
            log.debug("isExplosion?: " + source.isExplosion());
            log.debug("isProjectile?: " + source.isExplosion());
            log.debug("isBypassInvuln?: " + source.isBypassInvul());
            source = SourceCatcherData.convert(source);
        }
        IDFInterface convertedSource = (IDFInterface) source;
        log.debug("----------CONVERSION INFORMATION----------");
        log.debug("SOURCE: " + convertedSource.getMsgId() + " of class " + convertedSource.getClass().getName());
        log.debug("Is Conversion?: " + convertedSource.isConversion());
        log.debug("DAMAGE CLASS: " + convertedSource.getDamageClass());
        log.debug("WEIGHT: " + convertedSource.getWeight());
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
        log.debug("isProjectile?: " + ((DamageSource) convertedSource).isProjectile());
        log.debug("isTrue?: " + convertedSource.isTrue());
        log.debug("------------------------------------------------");
        float fireDamage, waterDamage, lightningDamage, magicDamage, darkDamage, physicalDamage, pen, lifesteal, knockback, weight;
        String damageClass;
        if (convertedSource.isConversion()) {
            fireDamage = convertedSource.getFire() * amount;
            waterDamage = convertedSource.getWater() * amount;
            lightningDamage = convertedSource.getLightning() * amount;
            magicDamage = convertedSource.getMagic() * amount;
            darkDamage = convertedSource.getDark() * amount;
            physicalDamage = amount - (fireDamage + waterDamage + lightningDamage + magicDamage + darkDamage);
        } else {
            fireDamage = convertedSource.getFire();
            waterDamage = convertedSource.getWater();
            lightningDamage = convertedSource.getLightning();
            magicDamage = convertedSource.getMagic();
            darkDamage = convertedSource.getDark();
            physicalDamage = amount;
        }
        PreMitigationDamageEvent event = new PreMitigationDamageEvent(target, fireDamage, waterDamage, lightningDamage, magicDamage, darkDamage, physicalDamage, convertedSource.getPen()/100, convertedSource.getLifesteal()/100, convertedSource.getKnockback(), convertedSource.getWeight(),
                (float) armourFormula(target.getAttributeValue(IDFAttributes.FIRE_RESISTANCE.get())), (float) armourFormula(target.getAttributeValue(IDFAttributes.WATER_RESISTANCE.get())),
                (float) armourFormula(target.getAttributeValue(IDFAttributes.LIGHTNING_RESISTANCE.get())), (float) armourFormula(target.getAttributeValue(IDFAttributes.MAGIC_RESISTANCE.get())),
                (float) armourFormula(target.getAttributeValue(IDFAttributes.DARK_RESISTANCE.get())), (float) armourFormula(target.getAttributeValue(Attributes.ARMOR)),
                (float) target.getAttributeValue(Attributes.ARMOR_TOUGHNESS), convertedSource.getDamageClass());
        MinecraftForge.EVENT_BUS.post(event);
        float[] dv = {event.getFireDmg(), event.getWaterDmg(), event.getLightningDmg(), event.getMagicDmg(), event.getDarkDmg(), event.getPhysicalDmg()};
        pen = event.getPen();
        lifesteal = event.getLifesteal();
        damageClass = event.getDamageClass();
        weight = event.getWeight();
        knockback = event.getKnockback();
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
        log.debug("WEIGHT: " + weight);
        log.debug("---------------------------------------");
        float[] rv = {event.getFireRes(), event.getWaterRes(), event.getLightningRes(), event.getMagicRes(), event.getDarkRes(), event.getPhysicalRes()};
        double weightMultiplier = weight == -1 ? 1 : Mth.clamp(Math.sqrt(weight)/Math.sqrt(target.getAttributeValue(Attributes.ARMOR_TOUGHNESS)), 0, 4);
        if (source.getEntity() != null) {
            double d1 = source.getEntity().getX() - target.getX();
            double d0;
            for (d0 = source.getEntity().getZ() - target.getZ(); d1 * d1 + d0 * d0 < 1.0E-4D; d0 = (Math.random() - Math.random()) * 0.01D) {
                d1 = (Math.random() - Math.random()) * 0.01D;
            }
            target.knockback(knockback*weightMultiplier, d1, d0);
        }
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
                target.getAttributeValue(Attributes.ARMOR_TOUGHNESS));
        log.debug("----------TARGET DAMAGE CLASS MULTIPLIERS----------");
        log.debug("WEIGHT RATIO = " + weightMultiplier);
        for (Map.Entry<String, Double> entry : mappedMultipliers.entrySet()) {
            log.debug(entry.getKey() + ": " + entry.getValue());
        }
        log.debug("---------------------------------------------------");
        for (int i = 0; i < 6; i++) {
            dv[i] *= weightMultiplier;
            dv[i] *= mappedMultipliers.get(damageClass);
        }
        log.debug("----------POST DAMAGE CLASS/WEIGHT MULTIPLICATION----------");
        log.debug("DAMAGE NUMBERS (fire, water, lightning, magic, dark, physical): " +
                dv[0] + ", " +
                dv[1] + ", " +
                dv[2] + ", " +
                dv[3] + ", " +
                dv[4] + ", " +
                dv[5]);
        log.debug("----------------------------------------------------");
        if (convertedSource.isTrue()) {
            log.debug("!!!!Source is true damage. Returning " + sum(dv));
            return sum(dv);
        }
        if (source.isProjectile()) {
            log.debug("----------PROJECTILE ENCHANT CHECKER----------");
            double projectileLevel = 0;
            for (ItemStack item : target.getArmorSlots()) {
                projectileLevel += item.getEnchantmentLevel(Enchantments.PROJECTILE_PROTECTION);
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
                blastLevel += item.getEnchantmentLevel(Enchantments.BLAST_PROTECTION);
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
                fireLevel += item.getEnchantmentLevel(Enchantments.FIRE_PROTECTION);
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
            protLevel += item.getEnchantmentLevel(Enchantments.ALL_DAMAGE_PROTECTION);
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
                    target.getAttributeValue(Attributes.ARMOR_TOUGHNESS));
        }
        target.hurtArmor(source, sum(dv));
        log.debug("----------FINAL DAMAGE NUMBERS----------");
        PostMitigationDamageEvent postMitigation = new PostMitigationDamageEvent(target, 0, 0, 0, 0, 0, 0);
        rv[5] = rv[5] * (1.0f - pen);
        for (int i = 0; i < 6; i++) {
            if (dv[i] > 0) {
                postMitigation.setDamage(
                        i,
                        damageFormula(dv[i], rv[i])
                );
            }
        }
        MinecraftForge.EVENT_BUS.post(postMitigation);
        for (float f : postMitigation.getDamage()) {
            log.debug(f);
        }
        float returnValue = sum(postMitigation.getDamage());
        log.debug("----------------------------------------");
        log.debug("----------LIFESTEAL CHECK----------");
        if (convertedSource instanceof EntityDamageSource) {
            log.debug("-> source is entity damage!");
            Entity sourceEntity = ((EntityDamageSource) convertedSource).getEntity();
            if (sourceEntity instanceof LivingEntity livingEntity) {
                log.debug("-> the source of the damage was a living entity!");
                if (lifesteal != 0) {
                    log.debug("lifesteal value is: " + lifesteal*100 + "%, healed " + Util.getEntityRegistryName(livingEntity.getType()) + " for " + (returnValue * lifesteal));
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

    public static double armourFormula(double d) {
        if (d == 0) return 0; //if res is 0, then give 0 res 4HEAD
        //the following formula is a reversal of the main armour formula. Used for negative armor values.
        if (d < 0) return -100/(1 + Math.exp((d/10) + 2));
        //the following formula is a function that has a horizontal asymptote at y = 100.
        //this is so the player can never go over 100% damage reduction.
        return 100/(1 + Math.exp((-d/10) + 2));
    }

    public static float damageFormula(float a, float d) {
        if (d == 0) return a; //if res is 0, then we should return true damage.

        //the following formula is a sigmoid function. At 0 res it's true damage, at equal it's 50%.
        // return a - (a/(1 + Math.pow( (e/2.3) , ((a - d)/9) )));

        //the following formula is just a simple % reduction, i.e d = 50 ->  return 0.5a
        return a * (1 - (d/100));
    }

}
