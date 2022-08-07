package net.cwjn.idf.mixin;

import net.cwjn.idf.ImprovedDamageFramework;
import net.cwjn.idf.damage.*;
import net.cwjn.idf.event.ServerEvents;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.stats.Stats;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.gameevent.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(LivingEntity.class)
public class MixinLivingEntity {

    /**
     * @author cwJn
     *
     * @reason
     * Implement the reworked damage system, but otherwise keep the method the exact same.
     * Has to be an overwrite and not a bunch of redirects because there is no way to remove (via mixins)
     * the early return if onLivingHurt returns 0. This has to be removed because sources of damage
     * that only have elemental typing will have a damageAmount of 0, since that only represents
     * physical damage.
     */
    @Overwrite
    protected void actuallyHurt(DamageSource damageSource, float damageAmount) {
        LivingEntity thisLivingEntity = (LivingEntity)(Object) this;
        if (!thisLivingEntity.isInvulnerableTo(damageSource)) {
            damageAmount = net.minecraftforge.common.ForgeHooks.onLivingHurt(thisLivingEntity, damageSource, damageAmount);

            //MODIFIED CODE STARTS HERE
            if (ServerEvents.debugMode) damageAmount = DamageHandler.handleDamageWithDebug(thisLivingEntity, damageSource, damageAmount, ImprovedDamageFramework.LOGGER);
            else damageAmount = DamageHandler.handleDamage(thisLivingEntity, damageSource, damageAmount);
            if (damageAmount <= 0) return;
            //MODIFIED CODE ENDS HERE

            float f2 = Math.max(damageAmount - thisLivingEntity.getAbsorptionAmount(), 0.0F);
            thisLivingEntity.setAbsorptionAmount(thisLivingEntity.getAbsorptionAmount() - (damageAmount - f2));
            float f = damageAmount - f2;
            if (f > 0.0F && f < 3.4028235E37F && damageSource.getEntity() instanceof ServerPlayer) {
                ((ServerPlayer)damageSource.getEntity()).awardStat(Stats.CUSTOM.get(Stats.DAMAGE_DEALT_ABSORBED), Math.round(f * 10.0F));
            }

            f2 = net.minecraftforge.common.ForgeHooks.onLivingDamage(thisLivingEntity, damageSource, f2);
            if (f2 != 0.0F) {
                float f1 = thisLivingEntity.getHealth();
                thisLivingEntity.getCombatTracker().recordDamage(damageSource, f1, f2);
                thisLivingEntity.setHealth(f1 - f2); // Forge: moved to fix MC-121048
                thisLivingEntity.setAbsorptionAmount(thisLivingEntity.getAbsorptionAmount() - f2);
                thisLivingEntity.gameEvent(GameEvent.ENTITY_DAMAGE);
            }
        }
    }

    /**
     * @author cwjn
     * @reason 
     * too many changes need to be made to this in terms of knockback in immunity frames
     * to where it's not feasible to do it with redirects and injections
     */
    @Overwrite
    public boolean hurt(DamageSource damageSource, float damageAmount) {
        LivingEntity thisLivingEntity = (LivingEntity)(Object) this;
        if (!net.minecraftforge.common.ForgeHooks.onLivingAttack(thisLivingEntity, damageSource, damageAmount)) return false;
        if (thisLivingEntity.isInvulnerableTo(damageSource) || thisLivingEntity.level.isClientSide || thisLivingEntity.isDeadOrDying() || (damageSource.isFire() && thisLivingEntity.hasEffect(MobEffects.FIRE_RESISTANCE))) {
            return false;
        } else {
            if (thisLivingEntity.isSleeping()) thisLivingEntity.stopSleeping();
            thisLivingEntity.setNoActionTime(0);
            float cringe = damageAmount;
            boolean blockedWithShield = false;
            float damageBlocked = 0.0F;
            if (damageAmount > 0.0F && thisLivingEntity.isDamageSourceBlocked(damageSource)) {
                net.minecraftforge.event.entity.living.ShieldBlockEvent ev = net.minecraftforge.common.ForgeHooks.onShieldBlock(thisLivingEntity, damageSource, damageAmount);
                if(!ev.isCanceled()) {
                    if(ev.shieldTakesDamage()) this.hurtCurrentlyUsedShield(damageAmount);
                    damageBlocked = ev.getBlockedDamage();
                    damageAmount -= ev.getBlockedDamage();
                    if (!damageSource.isProjectile()) {
                        Entity entity = damageSource.getDirectEntity();
                        if (entity instanceof LivingEntity) {
                            this.blockUsingShield((LivingEntity)entity);
                        }
                    }
                    blockedWithShield = true;
                }
            }

            thisLivingEntity.animationSpeed = 1.5F;
            /*
             *   MODIFIED CODE STARTS HERE
            */
            boolean gotHit = true;
            if ((float)thisLivingEntity.invulnerableTime > 0.0F) {
                if (damageSource.getEntity() instanceof Player) {
                    this.lastHurt = damageAmount;
                    int saveInvulnerableTime = thisLivingEntity.invulnerableTime;
                    this.actuallyHurt(damageSource, damageAmount);
                    thisLivingEntity.invulnerableTime = saveInvulnerableTime;
                } else {
                    gotHit = false;
                }
            } else {
                this.lastHurt = damageAmount;
                thisLivingEntity.invulnerableTime = 20;
                this.actuallyHurt(damageSource, damageAmount);
                thisLivingEntity.hurtDuration = 10;
                thisLivingEntity.hurtTime = thisLivingEntity.hurtDuration;
            }
            /*
             *   MODIFIED CODE ENDS HERE
            */

            if (damageSource.isDamageHelmet() && !thisLivingEntity.getItemBySlot(EquipmentSlot.HEAD).isEmpty()) {
                this.hurtHelmet(damageSource, damageAmount);
                damageAmount *= 0.75F;
            }

            thisLivingEntity.hurtDir = 0.0F;
            Entity sourceEntity = damageSource.getEntity();
            if (sourceEntity != null) {
                if (sourceEntity instanceof LivingEntity && !damageSource.isNoAggro()) {
                    thisLivingEntity.setLastHurtByMob((LivingEntity)sourceEntity);
                }
                if (sourceEntity instanceof Player) {
                    this.lastHurtByPlayerTime = 100;
                    this.lastHurtByPlayer = (Player)sourceEntity;
                } else if (sourceEntity instanceof TamableAnimal tamableEntity) {
                    if (tamableEntity.isTame()) {
                        this.lastHurtByPlayerTime = 100;
                        LivingEntity livingentity = tamableEntity.getOwner();
                        if (livingentity != null && livingentity.getType() == EntityType.PLAYER) {
                            this.lastHurtByPlayer = (Player)livingentity;
                        } else {
                            this.lastHurtByPlayer = null;
                        }
                    }
                }
            }

            if (gotHit) {
                if (blockedWithShield) {
                    thisLivingEntity.level.broadcastEntityEvent(thisLivingEntity, (byte)29);
                } else if (damageSource instanceof EntityDamageSource && ((EntityDamageSource)damageSource).isThorns()) {
                    thisLivingEntity.level.broadcastEntityEvent(thisLivingEntity, (byte)33);
                } else {
                    byte b0;
                    if (damageSource == DamageSource.DROWN) {
                        b0 = 36;
                    } else if (damageSource.isFire()) {
                        b0 = 37;
                    } else if (damageSource == DamageSource.SWEET_BERRY_BUSH) {
                        b0 = 44;
                    } else if (damageSource == DamageSource.FREEZE) {
                        b0 = 57;
                    } else {
                        b0 = 2;
                    }

                    thisLivingEntity.level.broadcastEntityEvent(thisLivingEntity, b0);
                }

                if (damageSource != DamageSource.DROWN && (!blockedWithShield || damageAmount > 0.0F)) {
                    thisLivingEntity.markHurt();
                }
                //DELETED THE DEFAULT KNOCKBACK CODE HERE
                if (sourceEntity == null) {
                    thisLivingEntity.hurtDir = (float) ((int) (Math.random() * 2.0D) * 180);
                }
            }

            if (thisLivingEntity.isDeadOrDying()) {
                if (!this.checkTotemDeathProtection(damageSource)) {
                    SoundEvent soundevent = this.getDeathSound();
                    if (gotHit && soundevent != null) {
                        thisLivingEntity.playSound(soundevent, this.getSoundVolume(), thisLivingEntity.getVoicePitch());
                    }

                    thisLivingEntity.die(damageSource);
                }
            } else if (gotHit) {
                this.playHurtSound(damageSource);
            }

            boolean wasHitReal = !blockedWithShield || damageAmount > 0.0F;
            if (wasHitReal) {
                this.lastDamageSource = damageSource;
                this.lastDamageStamp = thisLivingEntity.level.getGameTime();
            }

            if (thisLivingEntity instanceof ServerPlayer) {
                CriteriaTriggers.ENTITY_HURT_PLAYER.trigger((ServerPlayer)thisLivingEntity, damageSource, cringe, damageAmount, blockedWithShield);
                if (damageBlocked > 0.0F && damageBlocked < 3.4028235E37F) {
                    ((ServerPlayer)thisLivingEntity).awardStat(Stats.CUSTOM.get(Stats.DAMAGE_BLOCKED_BY_SHIELD), Math.round(damageBlocked * 10.0F));
                }
            }

            if (sourceEntity instanceof ServerPlayer) {
                CriteriaTriggers.PLAYER_HURT_ENTITY.trigger((ServerPlayer)sourceEntity, thisLivingEntity, damageSource, cringe, damageAmount, blockedWithShield);
            }

            return wasHitReal;
        }
    }

    /**
     * The following code was written by Darkhax as part of his maxhealthfix mod.
     * I've included it here since this mod essentially requires this fix to function
     * properly.
     */
    @Unique
    @Nullable
    private Float actualHealth = null;

    @Inject(method = "readAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V", at = @At("HEAD"))
    private void maxhealthfix$readAdditionalSaveData(CompoundTag tag, CallbackInfo callback) {
        if (tag.contains("Health", Tag.TAG_ANY_NUMERIC)) {
            final float savedHealth = tag.getFloat("Health");
            if (savedHealth > getMaxHealth()*5 && savedHealth > 0) {
                actualHealth = savedHealth;
            }
        }
    }

    @Inject(method = "detectEquipmentUpdates()V", at = @At("RETURN"))
    private void maxhealthfix$detectEquipmentUpdates(CallbackInfo callback) {
        if (actualHealth != null && actualHealth > 0 && actualHealth > this.getHealth()) {
            this.setHealth(actualHealth);
            actualHealth = null;
        }
    }

    //the following methods are to change vanilla sources of damage that are normally static to be %hp
    @Redirect(method = "baseTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    private boolean hurtRedirect(LivingEntity instance, DamageSource source, float amount) {
        return instance.hurt(source, instance.getMaxHealth()*0.1f);
    }
    @Redirect(method = "pushEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    private boolean hurtRedirect2(LivingEntity instance, DamageSource source, float amount) {
        return instance.hurt(source, instance.getMaxHealth()*0.1f);
    }
    @Redirect(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    private boolean hurtRedirect3(LivingEntity instance, DamageSource source, float amount) {
        return instance.hurt(source, instance.getMaxHealth()*0.1f);
    }
    @Inject(method = "calculateFallDamage", at = @At(value = "HEAD"), cancellable = true)
    private void fallDamageInject(float pDistance, float pDamageMultiplier, CallbackInfoReturnable<Integer> callback) {
        if (pDistance <= 4) {
            callback.setReturnValue(0);
            return;
        }
        double damage = Math.exp(pDistance/4.0);
        callback.setReturnValue((int) Math.ceil(damage));
    }
    @Redirect(method = "outOfWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    private boolean hurtRedirect4(LivingEntity instance, DamageSource source, float amt) {
        return instance.hurt(source, instance.getMaxHealth()*0.2f);
    }
    //this is to make shields not have a random delay when blocking.
    @ModifyConstant(method = "isBlocking", constant = @Constant(intValue = 5))
    private int setShieldUseDelay(int constant) {
        return 1;
    }
    @Shadow
    public float getMaxHealth() {
        throw new IllegalStateException("failed to shadow getMaxHealth()");
    }
    @Shadow
    public float getHealth() {
        throw new IllegalStateException("failed to shadow getHealth()");
    }
    @Shadow
    public void setHealth(float newHealth) {
        throw new IllegalStateException("failed to shadow setHealth()");
    }
    @Shadow
    protected void hurtCurrentlyUsedShield(float f) {
        throw new IllegalStateException("failed to shadow hurtCurrentlyUsedShield(float f)");
    }
    @Shadow
    protected void blockUsingShield(LivingEntity l) {
        throw new IllegalStateException("failed to shadow blockUsingShield(LivingEntity l)");
    }
    @Shadow
    protected void hurtHelmet(DamageSource d, float f) {
        throw new IllegalStateException("failed to shadow hurtHelmet(DamageSource d, float f)");
    }
    @Shadow
    private boolean checkTotemDeathProtection(DamageSource d) {
        throw new IllegalStateException("failed to shadow checkTotemDeathProtection");
    }
    @Shadow
    protected SoundEvent getDeathSound() {
        throw new IllegalStateException("failed to shadow getDeathSound()");
    }
    @Shadow
    protected float getSoundVolume(){
        throw new IllegalStateException("failed to shadow getSoundVolume()");
    }
    @Shadow
    protected void playHurtSound(DamageSource d) {
        throw new IllegalStateException("failed to shadow playHurtSound(DamageSource d)");
    }
    @Shadow protected float lastHurt;
    @Shadow protected Player lastHurtByPlayer;
    @Shadow protected int lastHurtByPlayerTime;
    @Shadow private DamageSource lastDamageSource;
    @Shadow private long lastDamageStamp;

}
