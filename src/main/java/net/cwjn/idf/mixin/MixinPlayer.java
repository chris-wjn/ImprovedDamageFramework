package net.cwjn.idf.mixin;

import net.cwjn.idf.ImprovedDamageFramework;
import net.cwjn.idf.attribute.AttributeRegistry;
import net.cwjn.idf.capability.AuxiliaryProvider;
import net.cwjn.idf.config.json.SourceCatcherData;
import net.cwjn.idf.damage.*;
import net.cwjn.idf.event.ServerEvents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.jmx.Server;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.HashMap;
import java.util.Map;

@Mixin(Player.class)
public class MixinPlayer {
    /**
     * @author cwJn
    */
    @Overwrite
    protected void actuallyHurt(DamageSource source, float amount) {
        Player thisPlayer = (Player)(Object) this;
        if (!thisPlayer.isInvulnerableTo(source)) {
            amount = net.minecraftforge.common.ForgeHooks.onLivingHurt(thisPlayer, source, amount);
            amount = calculateDamage(thisPlayer, source, amount);
            if (amount <= 0) return;
            float postAbsorptionDamageAmount = Math.max(amount - thisPlayer.getAbsorptionAmount(), 0.0F); //subtract the entity's absorption hearts from the damage amount
            thisPlayer.setAbsorptionAmount(thisPlayer.getAbsorptionAmount() - (amount - postAbsorptionDamageAmount));
            postAbsorptionDamageAmount = net.minecraftforge.common.ForgeHooks.onLivingDamage(thisPlayer, source, postAbsorptionDamageAmount);
            float amountTankedWithAbsorption = amount - postAbsorptionDamageAmount;
            if (amountTankedWithAbsorption > 0.0F && amountTankedWithAbsorption < 3.4028235E37F) {
                thisPlayer.awardStat(Stats.DAMAGE_ABSORBED, Math.round(amountTankedWithAbsorption * 10.0F));
            }
            if (postAbsorptionDamageAmount != 0.0F) {
                thisPlayer.causeFoodExhaustion(source.getFoodExhaustion());
                float health = thisPlayer.getHealth();
                (thisPlayer).getCombatTracker().recordDamage(source, health, postAbsorptionDamageAmount);
                (thisPlayer).setHealth(health - postAbsorptionDamageAmount); // Forge: moved to fix MC-121048
                if (postAbsorptionDamageAmount < 3.4028235E37F) {
                    thisPlayer.awardStat(Stats.DAMAGE_TAKEN, Math.round(postAbsorptionDamageAmount * 10.0F));
                }
            }
        }
    }

    public float calculateDamage(Player entity, DamageSource source, float amount) {
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
        Map<String, Double> mappedMultipliers = new HashMap<>(3);
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
        double defense = entity.getAttributeValue(Attributes.ARMOR_TOUGHNESS)/3;
        if (ServerEvents.debugMode) {
            log.debug("TARGET RESISTANCES: (def, fire, water, ltng, mag, dark, phys");
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

    /**
     * @author cwjn
     */
    @Overwrite
    public void attack(Entity target) {
        Player thisPlayer = (Player)((Object)this);
        if (!net.minecraftforge.common.ForgeHooks.onPlayerAttackTarget(thisPlayer, target)) return;
        if (target.isAttackable()) { //check that target is currently able to be hit
            if (!target.skipAttackInteraction(thisPlayer)) { //check that the target is not supposed to skip player interactions atm

                //get the values for the players attack, including any enchantment effects like smite, sharpness, etc...
                float ad = (float)thisPlayer.getAttributeValue(Attributes.ATTACK_DAMAGE);
                float fd = (float)thisPlayer.getAttributeValue(AttributeRegistry.FIRE_DAMAGE.get());
                float wd = (float)thisPlayer.getAttributeValue(AttributeRegistry.WATER_DAMAGE.get());
                float ld = (float)thisPlayer.getAttributeValue(AttributeRegistry.LIGHTNING_DAMAGE.get());
                float md = (float)thisPlayer.getAttributeValue(AttributeRegistry.MAGIC_DAMAGE.get());
                float dd = (float)thisPlayer.getAttributeValue(AttributeRegistry.DARK_DAMAGE.get());
                float pen = (float)thisPlayer.getAttributeValue(AttributeRegistry.PENETRATING.get());
                float damageBonus;
                if (target instanceof LivingEntity) {
                    damageBonus = EnchantmentHelper.getDamageBonus(thisPlayer.getMainHandItem(), ((LivingEntity)target).getMobType());
                } else {
                    damageBonus = EnchantmentHelper.getDamageBonus(thisPlayer.getMainHandItem(), MobType.UNDEFINED);
                }

                //factor in the player's attack strength scalar (the little sword icon under crosshair)
                float scalar = thisPlayer.getAttackStrengthScale(0.5F);
                ad *= 0.2F + scalar * scalar * 0.8F;
                fd *= 0.2F + scalar * scalar * 0.8F;
                wd *= 0.2F + scalar * scalar * 0.8F;
                ld *= 0.2F + scalar * scalar * 0.8F;
                md *= 0.2F + scalar * scalar * 0.8F;
                dd *= 0.2F + scalar * scalar * 0.8F;
                damageBonus *= scalar;
                thisPlayer.resetAttackStrengthTicker();

                if (ad > 0.0F || fd > 0.0F || wd > 0.0F || ld > 0.0F || md > 0.0F || dd > 0.0F || damageBonus > 0.0F) { //only run if there's actually damage

                    //check for knockback
                    boolean fullStrength = scalar > 0.9F;
                    boolean sprintAttack = false;
                    float knockback = (float)thisPlayer.getAttributeValue(Attributes.ATTACK_KNOCKBACK); // Forge: Initialize player value to the attack knockback attribute of the player, which is by default 0
                    knockback += EnchantmentHelper.getKnockbackBonus(thisPlayer);
                    if (thisPlayer.isSprinting() && fullStrength) {
                        thisPlayer.level.playSound((Player)null, thisPlayer.getX(), thisPlayer.getY(), thisPlayer.getZ(), SoundEvents.PLAYER_ATTACK_KNOCKBACK, thisPlayer.getSoundSource(), 1.0F, 1.0F);
                        ++knockback;
                        sprintAttack = true;
                    }

                    boolean isCrit = (thisPlayer.getAttributeValue(AttributeRegistry.CRIT_CHANCE.get())/100) >= Math.random() && target instanceof LivingEntity;
                    net.minecraftforge.event.entity.player.CriticalHitEvent hitResult = net.minecraftforge.common.ForgeHooks.getCriticalHit(thisPlayer, target, isCrit, isCrit ? 1.5F : 1.0F);
                    isCrit = hitResult != null;
                    if (isCrit) {
                        ad *= hitResult.getDamageModifier();
                    }

                    //add damage bonus to physical damage, and check if the attack is a sweepAttack
                    ad += damageBonus;
                    boolean isSweepAttack = false;
                    double d0 = (double)(thisPlayer.walkDist - thisPlayer.walkDistO);
                    if (fullStrength && !sprintAttack && thisPlayer.isOnGround() && d0 < (double)thisPlayer.getSpeed()) {
                        ItemStack itemstack = thisPlayer.getItemInHand(InteractionHand.MAIN_HAND);
                        isSweepAttack = itemstack.canPerformAction(net.minecraftforge.common.ToolActions.SWORD_SWEEP);
                    }


                    //for some reason we start tracking the target's health here, even though we don't use it yet
                    //we also check if we have fire aspect, and if we do (any level), we set the target on fire for 1 tick?
                    float targetHealth = 0.0F;
                    boolean targetOnFire = false;
                    int fireAspectLevel = EnchantmentHelper.getFireAspect(thisPlayer);
                    if (target instanceof LivingEntity) {
                        targetHealth = ((LivingEntity)target).getHealth();
                        if (fireAspectLevel > 0 && !target.isOnFire()) {
                            targetOnFire = true;
                            target.setSecondsOnFire(1);
                        }
                    }

                    //this is where we actually set the target to be hurt, and check to see if the hurt event went through
                    Vec3 direction = target.getDeltaMovement();
                    String damageClass = thisPlayer.getCapability(AuxiliaryProvider.AUXILIARY_DATA).orElseThrow(() -> new RuntimeException("player has no damage class!")).getDamageClass();
                    boolean targetWasHurt = target.hurt(new IDFEntityDamageSource("player", thisPlayer, fd, wd, ld, md, dd, pen, damageClass), ad);

                    if (targetWasHurt) {
                        //knockback the target and if the player was sprinting, stop their sprint

                        float lifesteal = (float) thisPlayer.getAttributeValue(AttributeRegistry.LIFESTEAL.get());
                        if (lifesteal > 0) {
                            thisPlayer.heal(lifesteal);
                        }

                        if (knockback > 0) {
                            if (target instanceof LivingEntity) {
                                ((LivingEntity)target).knockback((double)((float)knockback * 0.5F), (double) Mth.sin(thisPlayer.getYRot() * ((float)Math.PI / 180F)), (double)(-Mth.cos(thisPlayer.getYRot() * ((float)Math.PI / 180F))));
                            } else {
                                target.push((double)(-Mth.sin(thisPlayer.getYRot() * ((float)Math.PI / 180F)) * (float)knockback * 0.5F), 0.1D, (double)(Mth.cos(thisPlayer.getYRot() * ((float)Math.PI / 180F)) * (float)knockback * 0.5F));
                            }

                            thisPlayer.setDeltaMovement(thisPlayer.getDeltaMovement().multiply(0.6D, 1.0D, 0.6D));
                            thisPlayer.setSprinting(false);
                        }

                        //if the attack was sweeping, check for entities near the target and attack them
                        if (isSweepAttack) {
                            float sweepingAD = 1.0F + EnchantmentHelper.getSweepingDamageRatio(thisPlayer) * ad;
                            float sweepingFD = EnchantmentHelper.getSweepingDamageRatio(thisPlayer) * fd;
                            float sweepingWD = EnchantmentHelper.getSweepingDamageRatio(thisPlayer) * wd;
                            float sweepingLD = EnchantmentHelper.getSweepingDamageRatio(thisPlayer) * ld;
                            float sweepingMD = EnchantmentHelper.getSweepingDamageRatio(thisPlayer) * md;
                            float sweepingDD = EnchantmentHelper.getSweepingDamageRatio(thisPlayer) * dd;

                            for(LivingEntity livingentity : thisPlayer.level.getEntitiesOfClass(LivingEntity.class, thisPlayer.getItemInHand(InteractionHand.MAIN_HAND).getSweepHitBox(thisPlayer, target))) {
                                if (livingentity != thisPlayer && livingentity != target && !thisPlayer.isAlliedTo(livingentity) && (!(livingentity instanceof ArmorStand) || !((ArmorStand)livingentity).isMarker()) && thisPlayer.distanceToSqr(livingentity) < 9.0D) {
                                    livingentity.knockback((double)0.4F, (double)Mth.sin(thisPlayer.getYRot() * ((float)Math.PI / 180F)), (double)(-Mth.cos(thisPlayer.getYRot() * ((float)Math.PI / 180F))));
                                    livingentity.hurt(new IDFEntityDamageSource("player", thisPlayer, sweepingFD, sweepingWD, sweepingLD, sweepingMD, sweepingDD, pen,
                                            thisPlayer.getCapability(AuxiliaryProvider.AUXILIARY_DATA).orElseThrow(() -> new RuntimeException("player has no damage class!")).getDamageClass()), sweepingAD);
                                }
                            }

                            thisPlayer.level.playSound((Player)null, thisPlayer.getX(), thisPlayer.getY(), thisPlayer.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, thisPlayer.getSoundSource(), 1.0F, 1.0F);
                            thisPlayer.sweepAttack();
                        }

                        //update relevant clients
                        if (target instanceof ServerPlayer && target.hurtMarked) {
                            ((ServerPlayer)target).connection.send(new ClientboundSetEntityMotionPacket(target));
                            target.hurtMarked = false;
                            target.setDeltaMovement(direction);
                        }

                        //if the attack was a crit play the crit sound
                        if (isCrit) {
                            thisPlayer.level.playSound((Player)null, thisPlayer.getX(), thisPlayer.getY(), thisPlayer.getZ(), SoundEvents.PLAYER_ATTACK_CRIT, thisPlayer.getSoundSource(), 1.0F, 1.0F);
                            thisPlayer.crit(target);
                        }

                        //if the attack wasn't a crit or sweeping, play a sound based on whether the attack was full strength or not
                        if (!isCrit && !isSweepAttack) {
                            if (fullStrength) {
                                thisPlayer.level.playSound((Player)null, thisPlayer.getX(), thisPlayer.getY(), thisPlayer.getZ(), SoundEvents.PLAYER_ATTACK_STRONG, thisPlayer.getSoundSource(), 1.0F, 1.0F);
                            } else {
                                thisPlayer.level.playSound((Player)null, thisPlayer.getX(), thisPlayer.getY(), thisPlayer.getZ(), SoundEvents.PLAYER_ATTACK_WEAK, thisPlayer.getSoundSource(), 1.0F, 1.0F);
                            }
                        }

                        //if there is was a damage bonus from enchantments, use the magic crit effects
                        if (damageBonus > 0.0F) {
                            thisPlayer.magicCrit(target);
                        }

                        //update the player's last hurt mob, and do some post hurt effects or something
                        thisPlayer.setLastHurtMob(target);
                        if (target instanceof LivingEntity) {
                            EnchantmentHelper.doPostHurtEffects((LivingEntity)target, thisPlayer);
                        }

                        //do post damage effects to the target, if the target is a multipart entity, get the parent entity
                        EnchantmentHelper.doPostDamageEffects(thisPlayer, target);
                        ItemStack playerMainHandItem = thisPlayer.getMainHandItem();
                        Entity multipartTarget = target;
                        if (target instanceof net.minecraftforge.entity.PartEntity) {
                            multipartTarget = ((net.minecraftforge.entity.PartEntity<?>) target).getParent();
                        }

                        //idk
                        if (!thisPlayer.level.isClientSide && !playerMainHandItem.isEmpty() && multipartTarget instanceof LivingEntity) {
                            ItemStack copy = playerMainHandItem.copy();
                            playerMainHandItem.hurtEnemy((LivingEntity)multipartTarget, thisPlayer);
                            if (playerMainHandItem.isEmpty()) {
                                net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(thisPlayer, copy, InteractionHand.MAIN_HAND);
                                thisPlayer.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
                            }
                        }

                        //now we set the target on fire again ?XD
                        if (target instanceof LivingEntity) {
                            float newTargetHealth = targetHealth - ((LivingEntity)target).getHealth();
                            thisPlayer.awardStat(Stats.DAMAGE_DEALT, Math.round(newTargetHealth * 10.0F));
                            if (fireAspectLevel > 0) {
                                target.setSecondsOnFire(fireAspectLevel * 4);
                            }

                            if (thisPlayer.level instanceof ServerLevel && newTargetHealth > 2.0F) {
                                int k = (int)((double)newTargetHealth * 0.5D);
                                ((ServerLevel)thisPlayer.level).sendParticles(ParticleTypes.DAMAGE_INDICATOR, target.getX(), target.getY(0.5D), target.getZ(), k, 0.1D, 0.0D, 0.1D, 0.2D);
                            }
                        }

                        thisPlayer.causeFoodExhaustion(0.1F);
                    } else {
                        thisPlayer.level.playSound((Player)null, thisPlayer.getX(), thisPlayer.getY(), thisPlayer.getZ(), SoundEvents.PLAYER_ATTACK_NODAMAGE, thisPlayer.getSoundSource(), 1.0F, 1.0F);
                        if (targetOnFire) {
                            target.clearFire();
                        }
                    }
                }
            }
        }
    }

    @Shadow
    protected void hurtArmor(DamageSource p_21122_, float p_21123_) {
        throw new IllegalStateException("Mixin failed to shadow hurtArmor(DamageSource d, float f)");
    }


}
