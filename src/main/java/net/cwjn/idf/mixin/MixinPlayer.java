package net.cwjn.idf.mixin;

import net.cwjn.idf.ImprovedDamageFramework;
import net.cwjn.idf.attribute.IDFAttributes;
import net.cwjn.idf.capability.provider.AuxiliaryProvider;
import net.cwjn.idf.damage.*;
import net.cwjn.idf.event.hook.ServerEvents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
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
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Redirect;

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
            hurtArmor(source, amount);
            if (ServerEvents.debugMode) amount = DamageHandler.handleDamageWithDebug(thisPlayer, source, amount, ImprovedDamageFramework.LOGGER);
            else amount = DamageHandler.handleDamage(thisPlayer, source, amount);
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
                float fd = (float)thisPlayer.getAttributeValue(IDFAttributes.FIRE_DAMAGE.get());
                float wd = (float)thisPlayer.getAttributeValue(IDFAttributes.WATER_DAMAGE.get());
                float ld = (float)thisPlayer.getAttributeValue(IDFAttributes.LIGHTNING_DAMAGE.get());
                float md = (float)thisPlayer.getAttributeValue(IDFAttributes.MAGIC_DAMAGE.get());
                float dd = (float)thisPlayer.getAttributeValue(IDFAttributes.DARK_DAMAGE.get());
                float pen = (float)thisPlayer.getAttributeValue(IDFAttributes.PENETRATING.get());
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
                float lifesteal = scalar > 0.9F ? (float)thisPlayer.getAttributeValue(IDFAttributes.LIFESTEAL.get()) : 0;
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

                    boolean isCrit = (thisPlayer.getAttributeValue(IDFAttributes.CRIT_CHANCE.get())/100) >= Math.random() && target instanceof LivingEntity;
                    net.minecraftforge.event.entity.player.CriticalHitEvent hitResult = net.minecraftforge.common.ForgeHooks.getCriticalHit(thisPlayer, target, isCrit, isCrit ? 1.5F : 1.0F);
                    isCrit = hitResult != null;
                    if (isCrit) {
                        ad *= hitResult.getDamageModifier();
                        fd *= hitResult.getDamageModifier();
                        wd *= hitResult.getDamageModifier();
                        ld *= hitResult.getDamageModifier();
                        md *= hitResult.getDamageModifier();
                        dd *= hitResult.getDamageModifier();
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
                    boolean targetWasHurt = target.hurt(new IDFEntityDamageSource("player", thisPlayer, fd, wd, ld, md, dd, pen, lifesteal, damageClass), ad);

                    if (targetWasHurt) {
                        //knockback the target and if the player was sprinting, stop their sprint

                        if (knockback > 0 && thisPlayer.getAttackStrengthScale(0.5f) > 0.8) {
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
                                    livingentity.hurt(new IDFEntityDamageSource("player", thisPlayer, sweepingFD, sweepingWD, sweepingLD, sweepingMD, sweepingDD, pen, 0,
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
