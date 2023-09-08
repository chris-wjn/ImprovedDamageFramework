package net.cwjn.idf.mixin;

import net.cwjn.idf.ImprovedDamageFramework;
import net.cwjn.idf.api.event.OnFoodExhaustionEvent;
import net.cwjn.idf.attribute.IDFAttributes;
import net.cwjn.idf.capability.provider.AuxiliaryProvider;
import net.cwjn.idf.damage.*;
import net.cwjn.idf.event.LogicalEvents;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.cwjn.idf.attribute.IDFElement.HOLY;
import static net.minecraftforge.common.ForgeHooks.getCriticalHit;

@Mixin(Player.class)
public class MixinPlayer {

    /**
     * @author cwJn
     * @reason
     * see MixinLivingEntity
    */
    @Overwrite
    protected void actuallyHurt(DamageSource damageSource, float damageAmount) {
        Player thisPlayer = (Player)(Object) this;
        if (!thisPlayer.isInvulnerableTo(damageSource)) {
            damageAmount = net.minecraftforge.common.ForgeHooks.onLivingHurt(thisPlayer, damageSource, damageAmount);

            //if (damageAmount <= 0) return;
            //MODIFIED CODE STARTS HERE
            if (LogicalEvents.debugMode) damageAmount = DamageHandler.handleDamageWithDebug(thisPlayer, damageSource, damageAmount, ImprovedDamageFramework.LOGGER);
            else damageAmount = DamageHandler.handleDamage(thisPlayer, damageSource, damageAmount);
            if (damageAmount <= 0) return;
            //MODIFIED CODE ENDS HERE

            float f2 = Math.max(damageAmount - thisPlayer.getAbsorptionAmount(), 0.0F);
            thisPlayer.setAbsorptionAmount(thisPlayer.getAbsorptionAmount() - (damageAmount - f2));
            f2 = net.minecraftforge.common.ForgeHooks.onLivingDamage(thisPlayer, damageSource, f2);
            float f = damageAmount - f2;
            if (f > 0.0F && f < 3.4028235E37F) {
                thisPlayer.awardStat(Stats.DAMAGE_ABSORBED, Math.round(f * 10.0F));
            }

            if (f2 != 0.0F) {
                thisPlayer.causeFoodExhaustion(damageSource.getFoodExhaustion());
                float f1 = thisPlayer.getHealth();
                thisPlayer.getCombatTracker().recordDamage(damageSource, f1, f2);
                thisPlayer.setHealth(f1 - f2); // Forge: moved to fix MC-121048
                if (f2 < 3.4028235E37F) {
                    thisPlayer.awardStat(Stats.DAMAGE_TAKEN, Math.round(f2 * 10.0F));
                }

            }
        }
    }

    /**
     * @author cwjn
     * @reason
     * Implement new attributes and features. Too much to change here to reasonably do it without overwrite.
     * Anything that is made incompatible by this should probably stay incompatible.
     */
    //@Overwrite
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
                float hd = (float)thisPlayer.getAttributeValue(HOLY.damage);
                float pen = (float)thisPlayer.getAttributeValue(IDFAttributes.PENETRATING.get());
                float force = (float)thisPlayer.getAttributeValue(IDFAttributes.FORCE.get());
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
                hd *= 0.2F + scalar * scalar * 0.8F;
                force *= 0.2F + scalar * scalar * 0.8F;
                damageBonus *= scalar;
                float lifesteal = scalar > 0.9F ? (float)thisPlayer.getAttributeValue(IDFAttributes.LIFESTEAL.get()) : 0;
                thisPlayer.resetAttackStrengthTicker();

                if (ad > 0.0F || fd > 0.0F || wd > 0.0F || ld > 0.0F || md > 0.0F || dd > 0.0F || hd > 0.0F || damageBonus > 0.0F) { //only run if there's actually damage

                    //check for knockback
                    boolean fullStrength = scalar > 0.9F;
                    boolean sprintAttack = false;
                    float knockback = (float)thisPlayer.getAttributeValue(Attributes.ATTACK_KNOCKBACK); // Forge: Initialize player value to the attack knockback attribute of the player, which is by default 0
                    knockback += EnchantmentHelper.getKnockbackBonus(thisPlayer);
                    if (fullStrength) {
                        if (thisPlayer.isSprinting()) {
                            thisPlayer.level.playSound((Player) null, thisPlayer.getX(), thisPlayer.getY(), thisPlayer.getZ(), SoundEvents.PLAYER_ATTACK_KNOCKBACK, thisPlayer.getSoundSource(), 1.0F, 1.0F);
                            knockback += DamageHandler.DEFAULT_KNOCKBACK;
                            sprintAttack = true;
                        }
                    }

                    boolean isCrit = (thisPlayer.getAttributeValue(IDFAttributes.CRIT_CHANCE.get())*0.01) > thisPlayer.getRandom().nextDouble() && target instanceof LivingEntity;
                    net.minecraftforge.event.entity.player.CriticalHitEvent hitResult = net.minecraftforge.common.ForgeHooks.getCriticalHit(thisPlayer, target, isCrit, isCrit ? (float) (thisPlayer.getAttributeValue(IDFAttributes.CRIT_DAMAGE.get()) * 0.01) : 1.0F);
                    isCrit = hitResult != null;
                    if (isCrit) {
                        ad *= hitResult.getDamageModifier();
                        fd *= hitResult.getDamageModifier();
                        wd *= hitResult.getDamageModifier();
                        ld *= hitResult.getDamageModifier();
                        md *= hitResult.getDamageModifier();
                        dd *= hitResult.getDamageModifier();
                        hd *= hitResult.getDamageModifier();
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
                    //we also check if we have fire aspect, and if we do (any level), we set the target on fire for 1 second?
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
                    boolean targetWasHurt = target.hurt(new IDFEntityDamageSource("player", thisPlayer, fd, wd, ld, md, dd, hd, pen, lifesteal, knockback, force, damageClass), ad);

                    if (targetWasHurt) {
                        //knockback the target and if the player was sprinting, stop their sprint
                        if (knockback > 0) {
                            if (!(target instanceof LivingEntity)) {
                                target.push((double)(-Mth.sin(thisPlayer.getYRot() * ((float)Math.PI / 180F)) * (float)knockback * 0.5F), 0.1D, (double)(Mth.cos(thisPlayer.getYRot() * ((float)Math.PI / 180F)) * (float)knockback * 0.5F));
                            }
                            thisPlayer.setDeltaMovement(thisPlayer.getDeltaMovement().multiply(0.6D, 1.0D, 0.6D));
                            thisPlayer.setSprinting(false);
                        }

                        //if the attack was sweeping, check for entities near the target and attack them
                        if (isSweepAttack) {
                            float ratio = 0.25f + EnchantmentHelper.getSweepingDamageRatio(thisPlayer);
                            float sweepingAD = ratio * ad;
                            float sweepingFD = ratio * fd;
                            float sweepingWD = ratio * wd;
                            float sweepingLD = ratio * ld;
                            float sweepingMD = ratio * md;
                            float sweepingDD = ratio * dd;
                            float sweepingHD = ratio * hd;

                            for(LivingEntity livingentity : thisPlayer.level.getEntitiesOfClass(LivingEntity.class, thisPlayer.getItemInHand(InteractionHand.MAIN_HAND).getSweepHitBox(thisPlayer, target))) {
                                if (livingentity != thisPlayer && livingentity != target && !thisPlayer.isAlliedTo(livingentity) && (!(livingentity instanceof ArmorStand) || !((ArmorStand)livingentity).isMarker()) && thisPlayer.distanceToSqr(livingentity) < 9.0D) {
                                    livingentity.hurt(new IDFEntityDamageSource("player", thisPlayer, sweepingFD, sweepingWD, sweepingLD, sweepingMD, sweepingDD, sweepingHD, pen, lifesteal, knockback, force,
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

                        //if there was a damage bonus from enchantments, use the magic crit effects
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

    private boolean storeHurtVar;
    private float ad, bAd, fd, wd, ld, md, dd, hd, pen, force, scalar, knockback, lifesteal;
    private String damageClass;

    /**
     * Ensure the check for damage by vanilla goes through so that we can check ourselves
     */
    @ModifyConstant(method = "attack", constant = @Constant(floatValue = 0.0f, ordinal = 1))
    private float ifStatementRedirect(float constant) {
        return -Float.MAX_VALUE;
    }

    @Inject(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getAttackStrengthScale(F)F", shift = At.Shift.BEFORE))
    private void storeAttackStrength(Entity pTarget, CallbackInfo ci) {
        this.scalar = ((Player)(Object)this).getAttackStrengthScale(0.5F);
    }

    /**
     * Check for damage using all new damage types
     */
    @Inject(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;getKnockbackBonus(Lnet/minecraft/world/entity/LivingEntity;)I"), cancellable = true)
    private void checkForDamage(Entity pTarget, CallbackInfo ci) {
        Player thisPlayer = (Player)((Object)this);
        this.ad = (float)thisPlayer.getAttributeValue(Attributes.ATTACK_DAMAGE);
        this.fd = (float)thisPlayer.getAttributeValue(IDFAttributes.FIRE_DAMAGE.get());
        this.wd = (float)thisPlayer.getAttributeValue(IDFAttributes.WATER_DAMAGE.get());
        this.ld = (float)thisPlayer.getAttributeValue(IDFAttributes.LIGHTNING_DAMAGE.get());
        this.md = (float)thisPlayer.getAttributeValue(IDFAttributes.MAGIC_DAMAGE.get());
        this.dd = (float)thisPlayer.getAttributeValue(IDFAttributes.DARK_DAMAGE.get());
        this.hd = (float)thisPlayer.getAttributeValue(HOLY.damage);
        if (pTarget instanceof LivingEntity) {
            bAd = EnchantmentHelper.getDamageBonus(thisPlayer.getMainHandItem(), ((LivingEntity)pTarget).getMobType());
        } else {
            bAd = EnchantmentHelper.getDamageBonus(thisPlayer.getMainHandItem(), MobType.UNDEFINED);
        }
        this.knockback = (float)thisPlayer.getAttributeValue(Attributes.ATTACK_KNOCKBACK) + EnchantmentHelper.getKnockbackBonus(thisPlayer);
        if (thisPlayer.isSprinting() && scalar > 0.9F) knockback += DamageHandler.DEFAULT_KNOCKBACK;
        this.pen = (float)thisPlayer.getAttributeValue(IDFAttributes.PENETRATING.get());
        this.force = (float)thisPlayer.getAttributeValue(IDFAttributes.FORCE.get());
        this.lifesteal = scalar > 0.9F ? (float)thisPlayer.getAttributeValue(IDFAttributes.LIFESTEAL.get()) : 0;
        this.damageClass = thisPlayer.getCapability(AuxiliaryProvider.AUXILIARY_DATA).orElseThrow(() -> new RuntimeException("player has no damage class!")).getDamageClass();
        ad *= 0.2F + scalar * scalar * 0.8F;
        fd *= 0.2F + scalar * scalar * 0.8F;
        wd *= 0.2F + scalar * scalar * 0.8F;
        ld *= 0.2F + scalar * scalar * 0.8F;
        md *= 0.2F + scalar * scalar * 0.8F;
        dd *= 0.2F + scalar * scalar * 0.8F;
        hd *= 0.2F + scalar * scalar * 0.8F;
        force *= 0.2F + scalar * scalar * 0.8F;
        bAd *= scalar;
        if (!(ad > 0.0F || fd > 0.0F || wd > 0.0F || ld > 0.0F || md > 0.0F || dd > 0.0F || hd > 0.0F || bAd > 0.0F)) {
            ci.cancel();
        }
    }

    /**
     * Remove default crit check and implement crit chance
     */
    @Redirect(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/common/ForgeHooks;getCriticalHit(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/entity/Entity;ZF)Lnet/minecraftforge/event/entity/player/CriticalHitEvent;"))
    private CriticalHitEvent reworkCriticalHit(Player player, Entity target, boolean vanillaCritical, float damageModifier) {
        boolean isCrit = (player.getAttributeValue(IDFAttributes.CRIT_CHANCE.get())*0.01) > player.getRandom().nextDouble() && target instanceof LivingEntity;
        float critMod = isCrit? (float) (player.getAttributeValue(IDFAttributes.CRIT_DAMAGE.get())*0.01) : 1.0F;
        CriticalHitEvent hitResult = getCriticalHit(player, target, isCrit, critMod);
        isCrit = hitResult != null;
        if (isCrit) {
            ad *= hitResult.getDamageModifier();
            fd *= hitResult.getDamageModifier();
            wd *= hitResult.getDamageModifier();
            ld *= hitResult.getDamageModifier();
            md *= hitResult.getDamageModifier();
            dd *= hitResult.getDamageModifier();
            hd *= hitResult.getDamageModifier();
        }
        ad += bAd;
        return hitResult;
    }

    /**
     * Make a DamageSource with new system
     */
    @Redirect(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    private boolean reworkHurt(Entity instance, DamageSource pSource, float pAmount) {
        Player thisPlayer = (Player)((Object)this);
        System.out.println(knockback);
        return instance.hurt(new IDFEntityDamageSource("player", thisPlayer, fd, wd, ld, md, dd, hd, pen, lifesteal, knockback, force, damageClass), ad);
    }

    @Redirect(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    private boolean reworkSweepHurt(LivingEntity instance, DamageSource source, float amt) {
        Player thisPlayer = (Player)((Object)this);
        float ratio = 0.25f + EnchantmentHelper.getSweepingDamageRatio(thisPlayer);
        return instance.hurt(new IDFEntityDamageSource("player", thisPlayer, ratio*fd, ratio*wd, ratio*ld, ratio*md, ratio*dd, ratio*hd, pen, lifesteal, knockback, force,
                damageClass), ratio*ad);
    }

    /**
     * makes vanilla variable not store knockback from attribute and enchantment. Sprint knockback will still apply.
     * to account for this, we don't include sprint knockback bonus into the Damage Handler
     */
    @Redirect(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getAttributeValue(Lnet/minecraft/world/entity/ai/attributes/Attribute;)D", ordinal = 1))
    private double removeKnockback(Player instance, Attribute attribute) {
        return 0;
    }
    @Redirect(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;getKnockbackBonus(Lnet/minecraft/world/entity/LivingEntity;)I"))
    private int removeKnockback1(LivingEntity pPlayer) {
        return 0;
    }
    /**
     * This makes targets hit by sweep not get knockbacked by default and only handled in the DamageHandler
     */
    @ModifyConstant(method = "attack", constant = @Constant(floatValue = 0.4f))
    private float removeKnockback2(float constant) {
        return 0f;
    }

    @Inject(method = "causeFoodExhaustion",
            at = @At(value = "INVOKE", shift = At.Shift.BEFORE, target = "Lnet/minecraft/world/food/FoodData;addExhaustion(F)V"))
    private void fireFoodExhaustionEvent(float pExhaustion, CallbackInfo callback) {
        OnFoodExhaustionEvent event = new OnFoodExhaustionEvent((LivingEntity)(Object)this, pExhaustion);
        MinecraftForge.EVENT_BUS.post(event);
    }

    @Redirect(method = "hurt",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/damagesource/DamageSource;scalesWithDifficulty()Z"
    ))
    private boolean voidDifficultyDamageScaling(DamageSource instance) {
        return false;
    }

}
