package net.cwjn.idf.mixin;

import net.cwjn.idf.ImprovedDamageFramework;
import net.cwjn.idf.attribute.AttributeRegistry;
import net.cwjn.idf.config.json.SourceCatcherData;
import net.cwjn.idf.damage.*;
import net.cwjn.idf.event.ServerEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.damagesource.CombatTracker;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.gameevent.GameEvent;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.HashMap;
import java.util.Map;

@Mixin(LivingEntity.class)
public class MixinLivingEntity {

    /**
     * @author cwjn
     */
    @Overwrite //TODO: implement resistance potion effect
    protected void actuallyHurt(DamageSource damageSource, float damageAmount) {
        LivingEntity thisLivingEntity = (LivingEntity)(Object) this;
        if (!thisLivingEntity.isInvulnerableTo(damageSource)) { //if the target is invulnerable to this damage type, dont bother hurting them
            damageAmount = net.minecraftforge.common.ForgeHooks.onLivingHurt(thisLivingEntity, damageSource, damageAmount); //run the forge LivingHurtEvent hook
            damageAmount = calculateDamage(thisLivingEntity, damageSource, damageAmount);
            if (damageAmount <= 0) return;
            float postAbsorptionDamageAmount = Math.max(damageAmount - thisLivingEntity.getAbsorptionAmount(), 0.0F); //subtract the entity's absorption hearts from the damage amount
            thisLivingEntity.setAbsorptionAmount(thisLivingEntity.getAbsorptionAmount() - (damageAmount - postAbsorptionDamageAmount)); //remove the entity's absorption hearts used
            float amountTankedWithAbsorption = damageAmount - postAbsorptionDamageAmount; //track how much damage the entity tanked with absorption
            if (amountTankedWithAbsorption > 0.0F && amountTankedWithAbsorption < 3.4028235E37F && damageSource.getEntity() instanceof ServerPlayer) { //award stat screen numbers to player
                ((ServerPlayer)damageSource.getEntity()).awardStat(Stats.CUSTOM.get(Stats.DAMAGE_DEALT_ABSORBED), Math.round(amountTankedWithAbsorption * 10.0F));
            }
            postAbsorptionDamageAmount = net.minecraftforge.common.ForgeHooks.onLivingDamage(thisLivingEntity, damageSource, postAbsorptionDamageAmount); //run the living damage event on the final damage amount
            if (postAbsorptionDamageAmount != 0.0F) {
                float health = thisLivingEntity.getHealth(); //get the entity's current health
                thisLivingEntity.getCombatTracker().recordDamage(damageSource, health, postAbsorptionDamageAmount); //record how much damage was taken
                thisLivingEntity.setHealth(health - postAbsorptionDamageAmount); //set the new health value for the entity
                thisLivingEntity.setAbsorptionAmount(thisLivingEntity.getAbsorptionAmount() - postAbsorptionDamageAmount);
                thisLivingEntity.gameEvent(GameEvent.ENTITY_DAMAGED, damageSource.getEntity());
            }
        }
    }

    //take in an IDFInterface implemented damage source, check if it's a conversion source
    public float calculateDamage(LivingEntity entity, DamageSource source, float amount) {
        Logger log = ImprovedDamageFramework.getLog();
        if (ServerEvents.debugMode) {
            log.debug("----------------------");
            log.debug(entity.getName() + "was attacked.");
            log.debug("SOURCE IS: " + source.msgId);
            log.debug("source instance of IDFInterface? " + (source instanceof IDFInterface));
        }
        if (source.isProjectile()) {
            double projectileLevel = 0;
            for (ItemStack item : entity.getArmorSlots()) {
                projectileLevel += EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PROJECTILE_PROTECTION, item);
            }
            amount -= 0.5D*projectileLevel;
        }
        if (source.isFall()) {
            double fallLevel = 0;
            for (ItemStack item : entity.getArmorSlots()) {
                fallLevel += EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FALL_PROTECTION, item);
            }
            amount -= 0.5D*fallLevel;
        }
        if (!(source instanceof IDFInterface)) { //if this is an unchecked source, default to a physical strike
            source = SourceCatcherData.convert(source);
            if (ServerEvents.debugMode) {
                log.debug("Converted source to IDFInterface implemented source.");
            }
        }
        float fireDamage = 0, waterDamage = 0, lightningDamage = 0, magicDamage = 0, darkDamage = 0, physicalDamage = 0, pen = 0;
        String damageClass = "strike";
        IDFInterface newSource = (IDFInterface) source; //at this point, it is guaranteed that we have an implementation of IDFInterface
        if (newSource.isConversion()) { //check if source is a conversion type
            fireDamage = newSource.getFire() * amount;
            waterDamage = newSource.getWater() * amount;
            lightningDamage = newSource.getLightning() * amount;
            magicDamage = newSource.getMagic() * amount;
            darkDamage = newSource.getDark() * amount;
            physicalDamage = amount - (fireDamage + waterDamage + lightningDamage + magicDamage + darkDamage);
            pen = newSource.getPen()/100;
            damageClass = newSource.getDamageClass();
        } else {
            fireDamage = newSource.getFire();
            waterDamage = newSource.getWater();
            lightningDamage = newSource.getLightning();
            magicDamage = newSource.getMagic();
            darkDamage = newSource.getDark();
            physicalDamage = amount;
            pen = newSource.getPen()/100;
            damageClass = newSource.getDamageClass();
        }
        float[] dv = {fireDamage, waterDamage, lightningDamage, magicDamage, darkDamage, physicalDamage};
        Map<String, Double> mappedMultipliers = new HashMap<>(5);
        mappedMultipliers.put("strike", entity.getAttributeValue(AttributeRegistry.STRIKE_MULT.get()));
        mappedMultipliers.put("pierce", entity.getAttributeValue(AttributeRegistry.PIERCE_MULT.get()));
        mappedMultipliers.put("_slash", entity.getAttributeValue(AttributeRegistry.SLASH_MULT.get()));
        mappedMultipliers.put("_crush", entity.getAttributeValue(AttributeRegistry.CRUSH_MULT.get()));
        mappedMultipliers.put("genric", entity.getAttributeValue(AttributeRegistry.GENERIC_MULT.get()));
        if (ServerEvents.debugMode) {
            log.debug("----------------------");
            log.debug("PRE-MODIFIERS DAMAGE: (fire, water, ltng, mag, dark, phys)");
        }
        if (ServerEvents.debugMode) {
            for (int i = 0; i < 6; i++) {
                log.debug(dv[i]);
            }
        }
        if (ServerEvents.debugMode) {
            log.debug("----------------------");
            log.debug("DAMAGE CLASS: " + damageClass);
            log.debug("STRIKE MULTIPLIER: " + entity.getAttributeValue(AttributeRegistry.STRIKE_MULT.get()));
            log.debug("PIERCE MULTIPLIER: " + entity.getAttributeValue(AttributeRegistry.PIERCE_MULT.get()));
            log.debug("SLASH MULTIPLIER: " + entity.getAttributeValue(AttributeRegistry.SLASH_MULT.get()));
            log.debug("CRUSH MULTIPLIER: " + entity.getAttributeValue(AttributeRegistry.CRUSH_MULT.get()));
            log.debug("GENERIC MULTIPLIER: " + entity.getAttributeValue(AttributeRegistry.GENERIC_MULT.get()));
            log.debug("----------------------");
        }
        for (int i = 0; i < 6; i++) {
            dv[i] *= mappedMultipliers.get(damageClass);
        }
        if (newSource.isTrue()) {
            return sum(dv);
        }
        double[] rv = {entity.getAttributeValue(AttributeRegistry.FIRE_RESISTANCE.get())/100, entity.getAttributeValue(AttributeRegistry.WATER_RESISTANCE.get())/100,
                        entity.getAttributeValue(AttributeRegistry.LIGHTNING_RESISTANCE.get())/100, entity.getAttributeValue(AttributeRegistry.MAGIC_RESISTANCE.get())/100,
                        entity.getAttributeValue(AttributeRegistry.DARK_RESISTANCE.get())/100, entity.getAttributeValue(Attributes.ARMOR) * 0.03};
        double defense = entity.getAttributeValue(Attributes.ARMOR_TOUGHNESS);
        if (ServerEvents.debugMode) {
            log.debug("TARGET RESISTANCES: ");
            log.debug(defense);
        }
        if (ServerEvents.debugMode) {
            for (int i = 0; i < 6; i++) {
                log.debug(rv[i]);
            }
        }
        if (entity.hasEffect(MobEffects.DAMAGE_RESISTANCE)) {
            double resistanceLevel = ((double)entity.getEffect(MobEffects.DAMAGE_RESISTANCE).getAmplifier() + 1)/5;
            for (int i = 0; i < 6; i++) {
                rv[i] *= (1 + resistanceLevel);
            }
        }
        if (ServerEvents.debugMode) {
            log.debug("----------------------");
            log.debug("POST-MODIFIERS DAMAGE: (fire, water, ltng, mag, dark, phys)");
        }
        hurtArmor(source, sum(dv));
        float returnValue = 0;
        if (ServerEvents.debugMode) {
            for (int i = 0; i < 5; i++) {
                if (dv[i] > 0) {
                    returnValue += (dv[i] * (1 - rv[i]));
                    log.debug((dv[i] * (1 - rv[i])));
                }
            }
        } else {
            for (int i = 0; i < 5; i++) {
                if (dv[i] > 0) {
                    returnValue += (dv[i] * (1 - rv[i]));
                }
            }
        }
        if (dv[5] > 0) {
            float ad = (float) (dv[5] - defense);
            float physicalRes = (float) (rv[5] * (1 - pen));
            returnValue += (ad * (1 - physicalRes));
            if (ServerEvents.debugMode) log.debug((ad * (1 - physicalRes)));
        }
        if (ServerEvents.debugMode) log.debug("final damage: " + returnValue);
        return returnValue;
    }
    public float sum(float[] a) {
        float returnFloat = 0;
        for (float f : a) {
            returnFloat+=f;
        }
        return returnFloat;
    }

    @Shadow
    protected void hurtArmor(DamageSource p_21122_, float p_21123_) {
        throw new IllegalStateException("Mixin failed to shadow hurtArmor(DamageSource d, float f)");
    }


}
