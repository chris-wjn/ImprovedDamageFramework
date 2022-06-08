package net.cwjn.idf.mixin;

import net.cwjn.idf.Attributes.AttributeRegistry;
import net.cwjn.idf.Damage.*;
import net.cwjn.idf.Damage.helpers.*;
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
import net.minecraft.world.damagesource.IndirectEntityDamageSource;
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
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.HashMap;
import java.util.Map;

@Mixin(Player.class)
public class MixinPlayer {

    @Shadow
    public boolean isInvulnerableTo(DamageSource p_36249_) {
        throw new IllegalStateException("Mixin failed to shadow isInvulnerableTo(DamageSource d)");
    }

    @Shadow
    public void setAbsorptionAmount(float p_36396_) {
        throw new IllegalStateException("Mixin failed to shadow setAbsorptionAmount(float f)");
    }

    @Shadow
    public float getAbsorptionAmount() {
        throw new IllegalStateException("Mixin failed to shadow getAbsorptionAmount(float f)");
    }

    /**
     * @author cwJn
    */
    @Overwrite
    protected void actuallyHurt(DamageSource source, float amount) {
        if (!this.isInvulnerableTo(source)) {
            amount = net.minecraftforge.common.ForgeHooks.onLivingHurt((Player)(Object)this, source, amount);
            if (amount <= 0) return;
            System.out.println("ORIGINAL DAMAGE SOURCE: " + amount + " of " + source.msgId);
            amount = filterDamageType(((LivingEntity)(Object)this), source, amount);
            if (amount <= 0) return;
            System.out.println("player was hurt for " + amount);
            float postAbsorptionDamageAmount = Math.max(amount - this.getAbsorptionAmount(), 0.0F); //subtract the entity's absorption hearts from the damage amount
            this.setAbsorptionAmount(this.getAbsorptionAmount() - (amount - postAbsorptionDamageAmount));
            postAbsorptionDamageAmount = net.minecraftforge.common.ForgeHooks.onLivingDamage((Player)(Object)this, source, postAbsorptionDamageAmount);
            float amountTankedWithAbsorption = amount - postAbsorptionDamageAmount;
            if (amountTankedWithAbsorption > 0.0F && amountTankedWithAbsorption < 3.4028235E37F) {
                this.awardStat(Stats.DAMAGE_ABSORBED, Math.round(amountTankedWithAbsorption * 10.0F));
            }
            if (postAbsorptionDamageAmount != 0.0F) {
                this.causeFoodExhaustion(source.getFoodExhaustion());
                float health = ((Player)(Object)this).getHealth();
                ((Player)(Object)this).getCombatTracker().recordDamage(source, health, postAbsorptionDamageAmount);
                ((Player)(Object)this).setHealth(health - postAbsorptionDamageAmount); // Forge: moved to fix MC-121048
                if (postAbsorptionDamageAmount < 3.4028235E37F) {
                    this.awardStat(Stats.DAMAGE_TAKEN, Math.round(postAbsorptionDamageAmount * 10.0F));
                }
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
            double pen = source.getPen()/100;
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
                    returnAmount += (physicalDamage * (1 - (physicalRes * (1 - pen))));
                }
            }
            if (dv[1] > 0) {
                float fireDamage = dv[1] - (float)defense;
                if (fireDamage > 0) {
                    returnAmount += (fireDamage * (1 - fireRes/100));
                }
            }
            if (dv[2] > 0) {
                float waterDamage = dv[2] - (float)defense;
                if (waterDamage > 0) {
                    returnAmount += (waterDamage * (1 - waterRes/100));
                }
            }
            if (dv[3] > 0) {
                float lightningDamage = dv[3] - (float)defense;
                if (lightningDamage > 0) {
                    returnAmount += (lightningDamage * (1 - lightningRes/100));
                }
            }
            if (dv[4] > 0) {
                float magicDamage = dv[4] - (float)defense;
                if (magicDamage > 0) {
                    returnAmount += (magicDamage * (1 - magicRes/100));
                }
            }
            if (dv[5] > 0) {
                float darkDamage = dv[5] - (float)defense;
                if (darkDamage > 0) {
                    returnAmount += (darkDamage * (1 - darkRes/100));
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
                float scalar = this.getAttackStrengthScale(0.5F);
                ad *= 0.2F + scalar * scalar * 0.8F;
                fd *= 0.2F + scalar * scalar * 0.8F;
                wd *= 0.2F + scalar * scalar * 0.8F;
                ld *= 0.2F + scalar * scalar * 0.8F;
                md *= 0.2F + scalar * scalar * 0.8F;
                dd *= 0.2F + scalar * scalar * 0.8F;
                damageBonus *= scalar;
                this.resetAttackStrengthTicker();

                if (ad > 0.0F || damageBonus > 0.0F) { //only run if there's actually damage

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

                    //check if the attack is a crit - TODO: change way crits work?
                    //boolean isCrit = fullStrength && thisPlayer.fallDistance > 0.0F && !thisPlayer.isOnGround() && !thisPlayer.onClimbable() && !thisPlayer.isInWater() && !thisPlayer.hasEffect(MobEffects.BLINDNESS) && !thisPlayer.isPassenger() && target instanceof LivingEntity;
                    boolean isCrit = (thisPlayer.getAttributeValue(AttributeRegistry.CRIT_CHANCE.get())/100) >= Math.random();
                    System.out.println("Crit chance was " + thisPlayer.getAttributeValue(AttributeRegistry.CRIT_CHANCE.get()) + "%" + ", and the crit result was " + isCrit);
                    //isCrit = isCrit && !thisPlayer.isSprinting();
                    //we still need to run this event, but we can change the conditions for a crit with the lines above.
                    net.minecraftforge.event.entity.player.CriticalHitEvent hitResult = net.minecraftforge.common.ForgeHooks.getCriticalHit(thisPlayer, target, isCrit, isCrit ? 1.5F : 1.0F);
                    isCrit = hitResult != null;
                    if (isCrit) {
                        ad *= hitResult.getDamageModifier();
                    }

                    //add damage bonus to physical damage, and check if the attack is a sweepAttack
                    ad += damageBonus;
                    boolean isSweepAttack = false;
                    double d0 = (double)(thisPlayer.walkDist - thisPlayer.walkDistO);
                    if (fullStrength && !isCrit && !sprintAttack && thisPlayer.isOnGround() && d0 < (double)thisPlayer.getSpeed()) {
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
                    boolean targetWasHurt;
                    if (thisPlayer.getItemInHand(thisPlayer.getUsedItemHand()) != null) {
                        ItemStack item = thisPlayer.getItemInHand(thisPlayer.getUsedItemHand());
                        if (item.getOrCreateTag().contains("idf.damage_class")) {
                            targetWasHurt = target.hurt(new IDFEntityDamageSource("player", thisPlayer, fd, wd, ld, md, dd, item.getTag().getString("idf.damage_class"), pen), ad);
                        } else {
                            targetWasHurt = target.hurt(new IDFEntityDamageSource("player", thisPlayer, fd, wd, ld, md, dd, "strike", pen), ad);
                        }
                    } else targetWasHurt = target.hurt(new IDFEntityDamageSource("player", thisPlayer, fd, wd, ld, md, dd, "strike", pen), ad);

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
                                    livingentity.hurt(new IDFEntityDamageSource("player", thisPlayer, sweepingFD, sweepingWD, sweepingLD, sweepingMD, sweepingDD, "strike", pen), sweepingAD);
                                }
                            }

                            thisPlayer.level.playSound((Player)null, thisPlayer.getX(), thisPlayer.getY(), thisPlayer.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, thisPlayer.getSoundSource(), 1.0F, 1.0F);
                            this.sweepAttack();
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
                            this.crit(target);
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
                            this.magicCrit(target);
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
                            this.awardStat(Stats.DAMAGE_DEALT, Math.round(newTargetHealth * 10.0F));
                            if (fireAspectLevel > 0) {
                                target.setSecondsOnFire(fireAspectLevel * 4);
                            }

                            if (thisPlayer.level instanceof ServerLevel && newTargetHealth > 2.0F) {
                                int k = (int)((double)newTargetHealth * 0.5D);
                                ((ServerLevel)thisPlayer.level).sendParticles(ParticleTypes.DAMAGE_INDICATOR, target.getX(), target.getY(0.5D), target.getZ(), k, 0.1D, 0.0D, 0.1D, 0.2D);
                            }
                        }

                        this.causeFoodExhaustion(0.1F);
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
    public float getAttackStrengthScale(float p_36404_) {
        throw new IllegalStateException("Mixin failed to shadow getAttackStrengthScale(float f)");
    }

    @Shadow
    public void resetAttackStrengthTicker() {
        throw new IllegalStateException("Mixin failed to shadow resetAttackStrengthTicker()");
    }

    @Shadow
    public void sweepAttack() {
        throw new IllegalStateException("Mixin failed to shadow sweepAttack()");
    }

    @Shadow
    public void crit(Entity p_36156_) {
        throw new IllegalStateException("Mixin failed to shadow crit(Entity e)");
    }

    @Shadow
    public void magicCrit(Entity p_36253_) {
        throw new IllegalStateException("Mixin failed to shadow magiCrit(Entity e)");
    }

    @Shadow
    public void awardStat(ResourceLocation r, int p_36146_) {
        throw new IllegalStateException("Mixin failed to shadow awardStat(ResourceLocation r, int i");
    }

    @Shadow
    public void causeFoodExhaustion(float p_36400_) {
        throw new IllegalStateException("Mixin failed to shadow causeFoodExhaustion(float f)");
    }

    @Shadow
    protected void hurtArmor(DamageSource p_21122_, float p_21123_) {
        throw new IllegalStateException("Mixin failed to shadow hurtArmor(DamageSource d, float f)");
    }

}
