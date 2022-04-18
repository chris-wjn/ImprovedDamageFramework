package com.cwjn.idf.mixin;

import com.cwjn.idf.Attributes.AttributeRegistry;
import com.cwjn.idf.Damage.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.damagesource.CombatTracker;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.IndirectEntityDamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.gameevent.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

@Mixin(LivingEntity.class)
public class MixinLivingEntity {

    /**
     * @author cwjn
     */
    @Overwrite //TODO: implement resistance potion effect and maybe protection enchantments?
    protected void actuallyHurt(DamageSource source, float amount) {
        if (!((LivingEntity)(Object)this).isInvulnerableTo(source)) { //if the target is invulnerable to this damage type, dont bother hurting them
            amount = net.minecraftforge.common.ForgeHooks.onLivingHurt(((LivingEntity)(Object)this), source, amount); //run the forge LivingHurtEvent hook
            if (amount <= 0) return;
            System.out.println("ORIGINAL DAMAGE SOURCE: " + amount + " of " + source.msgId);
            amount = filterDamageType(((LivingEntity)(Object)this), source, amount);
            if (amount <= 0) return;
            float postAbsorptionDamageAmount = Math.max(amount - this.getAbsorptionAmount(), 0.0F); //subtract the entity's absorption hearts from the damage amount
            this.setAbsorptionAmount(this.getAbsorptionAmount() - (amount - postAbsorptionDamageAmount)); //remove the entity's absorption hearts used
            float amountTankedWithAbsorption = amount - postAbsorptionDamageAmount; //track how much damage the entity tanked with absorption
            if (amountTankedWithAbsorption > 0.0F && amountTankedWithAbsorption < 3.4028235E37F && source.getEntity() instanceof ServerPlayer) { //award stat screen numbers to player
                ((ServerPlayer)source.getEntity()).awardStat(Stats.CUSTOM.get(Stats.DAMAGE_DEALT_ABSORBED), Math.round(amountTankedWithAbsorption * 10.0F));
            }

            postAbsorptionDamageAmount = net.minecraftforge.common.ForgeHooks.onLivingDamage(((LivingEntity)(Object)this), source, postAbsorptionDamageAmount); //run the living damage event on the final damage amount
            if (postAbsorptionDamageAmount != 0.0F) {
                float health = this.getHealth(); //get the entity's current health
                this.getCombatTracker().recordDamage(source, health, postAbsorptionDamageAmount); //record how much damage was taken
                this.setHealth(health - postAbsorptionDamageAmount); //set the new health value for the entity
                this.setAbsorptionAmount(this.getAbsorptionAmount() - postAbsorptionDamageAmount);
                ((LivingEntity)(Object)this).gameEvent(GameEvent.ENTITY_DAMAGED, source.getEntity());
            }
        }
    }

    public float filterDamageType(LivingEntity entity, DamageSource source, float amount) {
        //TODO: this
        if (source.isProjectile()) {
            double projectileLevel = 0;
            for (ItemStack item : entity.getArmorSlots()) {
                projectileLevel += EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PROJECTILE_PROTECTION, item);
            }
            amount -= 0.5D*projectileLevel;
        }
        if (source instanceof IndirectEntityDamageSource) {
            System.out.println("IndirectEntityDamageSource");
            return runDamageCalculations(entity, new IDFDamageSource("strike"), amount);
        }
        switch (source.msgId) {
            //PLAYER AND MOB
            case "player":
                System.out.println("NEW DAMAGE SOURCE: " + amount + " physical damage of player");
                if (source instanceof IDFEntityDamageSource) return runDamageCalculations(entity, (IDFEntityDamageSource) source, amount);
                else return runDamageCalculations(entity, new IDFDamageSource("strike"), amount);
            case "mob":
                System.out.println("NEW DAMAGE SOURCE: " + amount + " physical damage of mob");
                if (source instanceof IDFEntityDamageSource) return runDamageCalculations(entity, (IDFEntityDamageSource) source, amount);
                else return runDamageCalculations(entity, new IDFDamageSource("strike"), amount);
            //PHYSICAL SOURCES
            case "starve":
            case "outOfWorld":
            case "badRespawnPoint":
                System.out.println("NEW DAMAGE SOURCE: " + amount + " physical true damage of " + "starve/outOfWorld/badRespawnPoint");
                return runDamageCalculations(entity, new IDFDamageSource("genric").setTrue(), amount);
            case "cactus":
            case "sweetBerryBush":
            case "arrow":
            case "trident":
            case "stalagmite":
            case "fallingStalactite":
            case "sting":
                System.out.println("NEW DAMAGE SOURCE: " + amount + " physical pierce damage of " + "cactus/sweetBerryBush/arrow/trident/stalagmite/fallingStalactite/sting");
                return runDamageCalculations(entity, new IDFDamageSource("pierce"), amount);
            case "fall":
            case "flyIntoWall":
            case "generic":
            case "anvil":
            case "fallingBlock":
            case "thrown":
                System.out.println("NEW DAMAGE SOURCE: " + amount + " physical strike damage of " + "fall/flyIntoWall/generic/anvil/fallingBlock/thrown");
                return runDamageCalculations(entity, new IDFDamageSource("strike"), amount);
            case "inWall":
            case "cramming":
                System.out.println("NEW DAMAGE SOURCE: " + amount + " physical crush damage of " + "inWall/cramming");
                return runDamageCalculations(entity, new IDFDamageSource("_crush"), amount);
            //FIRE SOURCES
            case "inFire":
            case "onFire":
            case "lava":
            case "hotFloor":
            case "dryout":
                System.out.println("NEW DAMAGE SOURCE: " + amount + " fire generic damage of " + "inFire/onFire/lava/hotFloor/dryout");
                return runDamageCalculations(entity, new FireDamageSource(amount, "genric"), 0);
            case "fireball":
                System.out.println("NEW DAMAGE SOURCE: " + amount + " fire strike damage of " + "fireball");
                return runDamageCalculations(entity, new FireDamageSource(amount, "strike"), 0);
            //WATER SOURCES
            case "drown":
            case "freeze":
                System.out.println("NEW DAMAGE SOURCE: " + amount + " water generic damage of " + "drown/freeze");
                return runDamageCalculations(entity, new WaterDamageSource(amount, "genric"), 0);
            //LIGHTNING SOURCES
            case "lightningBolt":
                System.out.println("NEW DAMAGE SOURCE: " + amount + " lightning pierce damage of " + "lightningBolt");
                return runDamageCalculations(entity, new LightningDamageSource(amount, "pierce"), 0);
            //MAGIC SOURCES
            case "magic":
            case "indirectMagic":
                System.out.println("NEW DAMAGE SOURCE: " + amount + " magic generic damage of " + "magic/indirectMagic");
                return runDamageCalculations(entity, new MagicDamageSource(amount, "genric"), 0);
            case "thorns":
                System.out.println("NEW DAMAGE SOURCE: " + amount + " magic pierce damage of " + "thorns");
                return runDamageCalculations(entity, new MagicDamageSource(amount, "pierce"), 0);
            //DARK SOURCES
            case "witherSkull":
                System.out.println("NEW DAMAGE SOURCE: " + amount + " dark strike damage of " + "witherSkull");
                return runDamageCalculations(entity, new DarkDamageSource(amount, "strike"), 0);
            case "wither":
                System.out.println("NEW DAMAGE SOURCE: " + amount + " dark generic damage of " + "wither");
                return runDamageCalculations(entity, new DarkDamageSource(amount, "genric"), 0);
            //MIXED SOURCES
            case "explosion":
            case "explosion.player":
            case "fireworks":
                System.out.println("NEW DAMAGE SOURCE: " + amount + " half physical half fire strike damage of " + "explosion/fireworks");
                return runDamageCalculations(entity, new IDFDamageSource(amount/2, 0, 0, 0, 0, "strike"), amount/2);
            case "dragonBreath":
                System.out.println("NEW DAMAGE SOURCE: " + amount + " half fire half magic generic damage of " + "dragonBreath");
                return runDamageCalculations(entity, new IDFDamageSource(amount/2, 0, 0, amount/2, 0, "genric"), 0);
            default:
                throw new RuntimeException("Unchecked damage type!");
        }
    }

    public float runDamageCalculations(LivingEntity entity, IDFInterface source, float amount) {
        if (!source.isTrue()) {
            double fireRes = entity.getAttributeValue(AttributeRegistry.FIRE_RESISTANCE.get());
            double waterRes = entity.getAttributeValue(AttributeRegistry.WATER_RESISTANCE.get());
            double lightningRes = entity.getAttributeValue(AttributeRegistry.LIGHTNING_RESISTANCE.get());
            double magicRes = entity.getAttributeValue(AttributeRegistry.MAGIC_RESISTANCE.get());
            double darkRes = entity.getAttributeValue(AttributeRegistry.DARK_RESISTANCE.get());
            double physicalRes = entity.getAttributeValue(Attributes.ARMOR) * 0.03;
            double defense = entity.getAttributeValue(Attributes.ARMOR_TOUGHNESS);
            String damageClass = source.getDamageClass();
            Map<String, Double> mappedMultipliers = new HashMap<>(5);
            mappedMultipliers.put("strike", entity.getAttributeValue(AttributeRegistry.STRIKE_MULT.get()));
            mappedMultipliers.put("pierce", entity.getAttributeValue(AttributeRegistry.PIERCE_MULT.get()));
            mappedMultipliers.put("_slash", entity.getAttributeValue(AttributeRegistry.SLASH_MULT.get()));
            mappedMultipliers.put("_crush", entity.getAttributeValue(AttributeRegistry.CRUSH_MULT.get()));
            mappedMultipliers.put("genric", entity.getAttributeValue(AttributeRegistry.GENERIC_MULT.get()));
            float returnAmount = 0;
            float dv[] = {amount, source.getFire(), source.getWater(), source.getLightning(), source.getMagic(), source.getDark()};
            for (int i = 0; i < 6; i++) {
                dv[i] *= mappedMultipliers.get(damageClass);
            }
            hurtArmor(new IDFDamageSource(damageClass), sum(dv));
            if (dv[0] > 0) {
                float physicalDamage = dv[0] - (float)defense;
                if (physicalDamage > 0) {
                    returnAmount += (physicalDamage * (1 - physicalRes));
                }
            }
            if (dv[1] > 0) {
                float fireDamage = dv[1] - (float)defense;
                if (fireDamage > 0) {
                    returnAmount += (fireDamage * (1 - fireRes));
                }
            }
            if (dv[2] > 0) {
                float waterDamage = dv[2] - (float)defense;
                if (waterDamage > 0) {
                    returnAmount += (waterDamage * (1 - waterRes));
                }
            }
            if (dv[3] > 0) {
                float lightningDamage = dv[3] - (float)defense;
                if (lightningDamage > 0) {
                    returnAmount += (lightningDamage * (1 - lightningRes));
                }
            }
            if (dv[4] > 0) {
                float magicDamage = dv[4] - (float)defense;
                if (magicDamage > 0) {
                    returnAmount += (magicDamage * (1 - magicRes));
                }
            }
            if (dv[5] > 0) {
                float darkDamage = dv[5] - (float)defense;
                if (darkDamage > 0) {
                    returnAmount += (darkDamage * (1 - darkRes));
                }
            }
            System.out.println("final damage is: " + returnAmount);
            return returnAmount;
        }
        return amount + source.getFire() + source.getWater() + source.getLightning() + source.getMagic() + source.getDark();
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
    @Shadow
    public float getAbsorptionAmount() {
        throw new IllegalStateException("Mixin failed to shadow getAbsorptionAmount()");
    }
    @Shadow
    public void setAbsorptionAmount(float p_21328_) {
        throw new IllegalStateException("Mixin failed to shadow setAbsorptionAmount()");
    }
    @Shadow
    public float getHealth() {
        throw new IllegalStateException("Mixin failed to shadow getHealth()");
    }
    @Shadow
    public CombatTracker getCombatTracker() {
        throw new IllegalStateException("Mixin failed to shadow getCombatTracker()");
    }
    @Shadow
    public void setHealth(float p_21154_) {
        throw new IllegalStateException("Mixin failed to shadow setHealth(float f)");
    }


}
