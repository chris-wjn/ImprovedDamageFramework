package com.cwjn.idf.mixin;

import com.cwjn.idf.Attributes.AttributeRegistry;
import com.cwjn.idf.Damage.IDFEntityDamageSource;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(Player.class)
public abstract class MixinPlayer {
    
    /**
     * @author cwjn
     */
    @Overwrite
    public void attack(Entity target) {
        Player player = (Player)((Object)this);
        if (!net.minecraftforge.common.ForgeHooks.onPlayerAttackTarget(player, target)) return;
        if (target.isAttackable()) { //check that target is currently able to be hit
            if (!target.skipAttackInteraction(player)) { //check that the target is not supposed to skip player interactions atm

                //get the values for the players attack, including any enchantment effects like smite, sharpness, etc...
                float ad = (float)player.getAttributeValue(Attributes.ATTACK_DAMAGE);
                float fd = (float)player.getAttributeValue(AttributeRegistry.FIRE_DAMAGE.get());
                float wd = (float)player.getAttributeValue(AttributeRegistry.WATER_DAMAGE.get());
                float ld = (float)player.getAttributeValue(AttributeRegistry.LIGHTNING_DAMAGE.get());
                float md = (float)player.getAttributeValue(AttributeRegistry.MAGIC_DAMAGE.get());
                float dd = (float)player.getAttributeValue(AttributeRegistry.DARK_DAMAGE.get());
                float damageBonus;
                if (target instanceof LivingEntity) {
                    damageBonus = EnchantmentHelper.getDamageBonus(player.getMainHandItem(), ((LivingEntity)target).getMobType());
                } else {
                    damageBonus = EnchantmentHelper.getDamageBonus(player.getMainHandItem(), MobType.UNDEFINED);
                }

                //factor in the player's attack strength scalar (the little sword icon under crosshair)
                float scalar = player.getAttackStrengthScale(0.5F);
                ad *= 0.2F + scalar * scalar * 0.8F;
                fd *= 0.2F + scalar * scalar * 0.8F;
                wd *= 0.2F + scalar * scalar * 0.8F;
                ld *= 0.2F + scalar * scalar * 0.8F;
                md *= 0.2F + scalar * scalar * 0.8F;
                dd *= 0.2F + scalar * scalar * 0.8F;
                damageBonus *= scalar;
                player.resetAttackStrengthTicker();

                if (ad > 0.0F || damageBonus > 0.0F) { //only run if there's actually damage

                    //check for knockback
                    boolean fullStrength = scalar > 0.9F;
                    boolean sprintAttack = false;
                    float knockback = (float)player.getAttributeValue(Attributes.ATTACK_KNOCKBACK); // Forge: Initialize player value to the attack knockback attribute of the player, which is by default 0
                    knockback += EnchantmentHelper.getKnockbackBonus(player);
                    if (player.isSprinting() && fullStrength) {
                        player.level.playSound((Player)null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_KNOCKBACK, player.getSoundSource(), 1.0F, 1.0F);
                        ++knockback;
                        sprintAttack = true;
                    }

                    //check if the attack is a crit - TODO: change way crits work?
                    boolean isCrit = fullStrength && player.fallDistance > 0.0F && !player.isOnGround() && !player.onClimbable() && !player.isInWater() && !player.hasEffect(MobEffects.BLINDNESS) && !player.isPassenger() && target instanceof LivingEntity;
                    isCrit = isCrit && !player.isSprinting();
                    //we still need to run this event, but we can change the conditions for a crit with the lines above.
                    net.minecraftforge.event.entity.player.CriticalHitEvent hitResult = net.minecraftforge.common.ForgeHooks.getCriticalHit(player, target, isCrit, isCrit ? 1.5F : 1.0F);
                    isCrit = hitResult != null;
                    if (isCrit) {
                        ad *= hitResult.getDamageModifier();
                    }

                    //add damage bonus to physical damage, and check if the attack is a sweepAttack
                    ad += damageBonus;
                    boolean isSweepAttack = false;
                    double d0 = (double)(player.walkDist - player.walkDistO);
                    if (fullStrength && !isCrit && !sprintAttack && player.isOnGround() && d0 < (double)player.getSpeed()) {
                        ItemStack itemstack = player.getItemInHand(InteractionHand.MAIN_HAND);
                        isSweepAttack = itemstack.canPerformAction(net.minecraftforge.common.ToolActions.SWORD_SWEEP);
                    }


                    //for some reason we start tracking the target's health here, even though we don't use it yet
                    //we also check if we have fire aspect, and if we do (any level), we set the target on fire for 1 tick?
                    float targetHealth = 0.0F;
                    boolean targetOnFire = false;
                    int fireAspectLevel = EnchantmentHelper.getFireAspect(player);
                    if (target instanceof LivingEntity) {
                        targetHealth = ((LivingEntity)target).getHealth();
                        if (fireAspectLevel > 0 && !target.isOnFire()) {
                            targetOnFire = true;
                            target.setSecondsOnFire(1);
                        }
                    }

                    //this is where we actually set the target to be hurt, and check to see if the hurt event went through
                    Vec3 direction = target.getDeltaMovement();
                    //TODO: set damageClass based on weapon
                    boolean targetWasHurt = target.hurt(new IDFEntityDamageSource("player", player, fd, wd, ld, md, dd, "strike"), ad);
                    if (targetWasHurt) {

                        //knockback the target and if the player was sprinting, stop their sprint
                        if (knockback > 0) {
                            if (target instanceof LivingEntity) {
                                ((LivingEntity)target).knockback((double)((float)knockback * 0.5F), (double) Mth.sin(player.getYRot() * ((float)Math.PI / 180F)), (double)(-Mth.cos(player.getYRot() * ((float)Math.PI / 180F))));
                            } else {
                                target.push((double)(-Mth.sin(player.getYRot() * ((float)Math.PI / 180F)) * (float)knockback * 0.5F), 0.1D, (double)(Mth.cos(player.getYRot() * ((float)Math.PI / 180F)) * (float)knockback * 0.5F));
                            }

                            player.setDeltaMovement(player.getDeltaMovement().multiply(0.6D, 1.0D, 0.6D));
                            player.setSprinting(false);
                        }

                        //if the attack was sweeping, check for entities near the target and attack them
                        if (isSweepAttack) {
                            float sweepingAD = 1.0F + EnchantmentHelper.getSweepingDamageRatio(player) * ad;
                            float sweepingFD = EnchantmentHelper.getSweepingDamageRatio(player) * fd;
                            float sweepingWD = EnchantmentHelper.getSweepingDamageRatio(player) * wd;
                            float sweepingLD = EnchantmentHelper.getSweepingDamageRatio(player) * ld;
                            float sweepingMD = EnchantmentHelper.getSweepingDamageRatio(player) * md;
                            float sweepingDD = EnchantmentHelper.getSweepingDamageRatio(player) * dd;

                            for(LivingEntity livingentity : player.level.getEntitiesOfClass(LivingEntity.class, player.getItemInHand(InteractionHand.MAIN_HAND).getSweepHitBox(player, target))) {
                                if (livingentity != player && livingentity != target && !player.isAlliedTo(livingentity) && (!(livingentity instanceof ArmorStand) || !((ArmorStand)livingentity).isMarker()) && player.distanceToSqr(livingentity) < 9.0D) {
                                    livingentity.knockback((double)0.4F, (double)Mth.sin(player.getYRot() * ((float)Math.PI / 180F)), (double)(-Mth.cos(player.getYRot() * ((float)Math.PI / 180F))));
                                    livingentity.hurt(new IDFEntityDamageSource("player", player, sweepingFD, sweepingWD, sweepingLD, sweepingMD, sweepingDD, "strike"), sweepingAD);
                                }
                            }

                            player.level.playSound((Player)null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, player.getSoundSource(), 1.0F, 1.0F);
                            player.sweepAttack();
                        }

                        //update relevant clients
                        if (target instanceof ServerPlayer && target.hurtMarked) {
                            ((ServerPlayer)target).connection.send(new ClientboundSetEntityMotionPacket(target));
                            target.hurtMarked = false;
                            target.setDeltaMovement(direction);
                        }

                        //if the attack was a crit play the crit sound
                        if (isCrit) {
                            player.level.playSound((Player)null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_CRIT, player.getSoundSource(), 1.0F, 1.0F);
                            player.crit(target);
                        }

                        //if the attack wasn't a crit or sweeping, play a sound based on whether the attack was full strength or not
                        if (!isCrit && !isSweepAttack) {
                            if (fullStrength) {
                                player.level.playSound((Player)null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_STRONG, player.getSoundSource(), 1.0F, 1.0F);
                            } else {
                                player.level.playSound((Player)null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_WEAK, player.getSoundSource(), 1.0F, 1.0F);
                            }
                        }

                        //if there is was a damage bonus from enchantments, use the magic crit effects
                        if (damageBonus > 0.0F) {
                            player.magicCrit(target);
                        }

                        //update the player's last hurt mob, and do some post hurt effects or something
                        player.setLastHurtMob(target);
                        if (target instanceof LivingEntity) {
                            EnchantmentHelper.doPostHurtEffects((LivingEntity)target, player);
                        }

                        //do post damage effects to the target, if the target is a multipart entity, get the parent entity
                        EnchantmentHelper.doPostDamageEffects(player, target);
                        ItemStack playerMainHandItem = player.getMainHandItem();
                        Entity multipartTarget = target;
                        if (target instanceof net.minecraftforge.entity.PartEntity) {
                            multipartTarget = ((net.minecraftforge.entity.PartEntity<?>) target).getParent();
                        }

                        //idk
                        if (!player.level.isClientSide && !playerMainHandItem.isEmpty() && multipartTarget instanceof LivingEntity) {
                            ItemStack copy = playerMainHandItem.copy();
                            playerMainHandItem.hurtEnemy((LivingEntity)multipartTarget, player);
                            if (playerMainHandItem.isEmpty()) {
                                net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(player, copy, InteractionHand.MAIN_HAND);
                                player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
                            }
                        }

                        //now we set the target on fire again ?XD
                        if (target instanceof LivingEntity) {
                            float newTargetHealth = targetHealth - ((LivingEntity)target).getHealth();
                            player.awardStat(Stats.DAMAGE_DEALT, Math.round(newTargetHealth * 10.0F));
                            if (fireAspectLevel > 0) {
                                target.setSecondsOnFire(fireAspectLevel * 4);
                            }

                            if (player.level instanceof ServerLevel && newTargetHealth > 2.0F) {
                                int k = (int)((double)newTargetHealth * 0.5D);
                                ((ServerLevel)player.level).sendParticles(ParticleTypes.DAMAGE_INDICATOR, target.getX(), target.getY(0.5D), target.getZ(), k, 0.1D, 0.0D, 0.1D, 0.2D);
                            }
                        }

                        player.causeFoodExhaustion(0.1F);
                    } else {
                        player.level.playSound((Player)null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_NODAMAGE, player.getSoundSource(), 1.0F, 1.0F);
                        if (targetOnFire) {
                            target.clearFire();
                        }
                    }
                }

            }
        }
    } 
    
}
