package net.cwjn.idf.mixin;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.cwjn.idf.ImprovedDamageFramework;
import net.cwjn.idf.config.CommonConfig;
import net.cwjn.idf.damage.*;
import net.cwjn.idf.event.LogicalEvents;
import net.cwjn.idf.util.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.gameevent.GameEvent;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;

import static net.cwjn.idf.data.CommonData.RANGED_TAG;
import static net.cwjn.idf.data.CommonData.WEAPON_TAG;
import static net.cwjn.idf.util.Util.offensiveAttribute;
import static net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.*;

@Mixin(LivingEntity.class)
public class MixinLivingEntity {

    @Shadow @Final public int invulnerableDuration;
    private final LivingEntity thisLivingEntity = (LivingEntity)(Object) this;

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
            if (LogicalEvents.debugMode) damageAmount = DamageHandler.handleDamageWithDebug(thisLivingEntity, damageSource, damageAmount, ImprovedDamageFramework.LOGGER);
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


    /*
    In vanilla, there are three kinds of attribute modifiers: Addition, Multiply Base, and Multiply Total.
    Multiply Base works very strangely and doesn't really make sense in the context of this mod. So,
    this method will take the damage, attackspeed, and force of melee weapons and convert them all to an
    addition modifier.
    Furthermore, we do not want bows and crossbows to transfer their damage stats over to the player,
    as they would then be able to use them as melee weapons.
     */
    @Redirect(method = "collectEquipmentChanges", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/item/ItemStack;getAttributeModifiers(Lnet/minecraft/world/entity/EquipmentSlot;)Lcom/google/common/collect/Multimap;"))
    private Multimap<Attribute, AttributeModifier> changeMainhandAttributeLogic(ItemStack item, EquipmentSlot slot) {
        //first lets instantiate two maps, one to be modified and returned and one to get the original modifiers
        Multimap<Attribute, AttributeModifier> newMap = HashMultimap.create();
        Multimap<Attribute, AttributeModifier> oldMap = item.getAttributeModifiers(slot);
        //weapon case
        if (item.hasTag()) {
            if (item.getTag().contains(WEAPON_TAG)) {
                boolean isRanged = item.getTag().getBoolean(RANGED_TAG);
                for (Map.Entry<Attribute, AttributeModifier> entry : oldMap.entries()) {
                    String name = entry.getKey().getDescriptionId().toLowerCase();
                    if (offensiveAttribute.test(name)) {
                        if (isRanged) continue;
                        if (entry.getKey() == Attributes.ATTACK_SPEED && thisLivingEntity instanceof Player) {
                            Collection<AttributeModifier> mods = oldMap.get(entry.getKey());
                            final double flat = mods.stream().filter((modifier) -> modifier.getOperation().equals(AttributeModifier.Operation.ADDITION)).mapToDouble(AttributeModifier::getAmount).sum();
                            double f1 = flat + mods.stream().filter((modifier) -> modifier.getOperation().equals(AttributeModifier.Operation.MULTIPLY_BASE)).mapToDouble(AttributeModifier::getAmount).map((amount) -> amount * Math.abs(flat)).sum();
                            double f2 = mods.stream().filter((modifier) -> modifier.getOperation().equals(AttributeModifier.Operation.MULTIPLY_TOTAL)).mapToDouble(AttributeModifier::getAmount).map((amount) -> amount + 1.0).reduce(1.0, (x, y) -> x * y);
                            newMap.put(entry.getKey(), new AttributeModifier(Util.UUID_STAT_CONVERSION[slot.getIndex()], "conversion", (f2 * (f1 + thisLivingEntity.getAttributeBaseValue(Attributes.ATTACK_SPEED)))-thisLivingEntity.getAttributeBaseValue(Attributes.ATTACK_SPEED), ADDITION));
                            continue;
                        }
                        Collection<AttributeModifier> mods = oldMap.get(entry.getKey());
                        final double flat = mods.stream().filter((modifier) -> modifier.getOperation().equals(AttributeModifier.Operation.ADDITION)).mapToDouble(AttributeModifier::getAmount).sum();
                        double f1 = flat + mods.stream().filter((modifier) -> modifier.getOperation().equals(AttributeModifier.Operation.MULTIPLY_BASE)).mapToDouble(AttributeModifier::getAmount).map((amount) -> amount * Math.abs(flat)).sum();
                        double f2 = mods.stream().filter((modifier) -> modifier.getOperation().equals(AttributeModifier.Operation.MULTIPLY_TOTAL)).mapToDouble(AttributeModifier::getAmount).map((amount) -> amount + 1.0).reduce(1.0, (x, y) -> x * y);
                        newMap.put(entry.getKey(), new AttributeModifier(Util.UUID_STAT_CONVERSION[slot.getIndex()], "conversion", f1 * f2, ADDITION));
                    } else {
                        newMap.put(entry.getKey(), entry.getValue());
                    }
                }
            } else {
                return oldMap;
            }
        }
        else {
            return oldMap;
        }
        return newMap;
    }

    /**
    All the following methods were written by me to change the vanilla way iFrames and knockback is handled.
    By default, any instance of damage will knockback the entity slightly. This is fkn stupid, and makes no sense.
    Knockback is now handled in idf.damage.DamageHandler. For iFrames, it will no longer hurt the entity with the difference
    in damage if the new attack is higher. If the target is in iFrames, they are immune to anything except damage sources
    predefined in the config.
     */

    @ModifyConstant(method = "hurt", constant = @Constant(floatValue = 10.0F, ordinal = 0))
    private float inject(float constant) {
        return -1F;
    }

    @Redirect(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;actuallyHurt(Lnet/minecraft/world/damagesource/DamageSource;F)V", ordinal = 0))
    protected void voidActuallyHurt(LivingEntity instance, DamageSource f2, float f) {
    }

    @Redirect(method = "hurt", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/LivingEntity;lastHurt:F", opcode = Opcodes.GETFIELD, ordinal = 0))
    private float voidIfStatement(LivingEntity instance) {
        return -Float.MAX_VALUE;
    }

    @Redirect(method = "hurt", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/LivingEntity;lastHurt:F", opcode = Opcodes.PUTFIELD, ordinal = 0))
    private void voidLastHurt(LivingEntity instance, float value) {
    }

    @Redirect(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;knockback(DDD)V"))
    private void voidKnockback(LivingEntity instance, double strength, double x, double z) {
    }

    @Inject(method = "hurt", at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/world/entity/LivingEntity;actuallyHurt(Lnet/minecraft/world/damagesource/DamageSource;F)V", ordinal = 0), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
    private void injectOverrideLogic(DamageSource source, float val, CallbackInfoReturnable<Boolean> callback, float f, boolean flag, float f1, boolean flag1) {
        String msgID = source.getMsgId();
        boolean hasDamage = source instanceof IDFInterface? ((IDFInterface) source).hasDamage() || val > 0 : val > 0;
        if (source.getEntity() instanceof Player) {
            this.actuallyHurt(source, val);
            gotHit(source, hasDamage, flag);
        }
        else if (thisLivingEntity.invulnerableTime > 0) {
            if (CommonConfig.WHITELISTED_SOURCES_NO_INVULN.get().contains(msgID) || (thisLivingEntity.invulnerableTime < 11 && CommonConfig.WHITELISTED_SOURCES_REDUCED_INVULN.get().contains(msgID))) {
                this.actuallyHurt(source, val);
                gotHit(source, hasDamage, flag);
            }
            else {
                callback.setReturnValue(false);
            }
        }
        else {
            thisLivingEntity.invulnerableTime = 20;
            this.actuallyHurt(source, val);
            gotHit(source, hasDamage, flag);
            thisLivingEntity.hurtDuration = 10;
            thisLivingEntity.hurtTime = thisLivingEntity.hurtDuration;
        }
    }

    private void gotHit(DamageSource source, boolean hasDamage, boolean flag) {
        if (flag) {
            thisLivingEntity.level.broadcastEntityEvent(thisLivingEntity, (byte)29);
        } else if (source instanceof EntityDamageSource && ((EntityDamageSource)source).isThorns()) {
            thisLivingEntity.level.broadcastEntityEvent(thisLivingEntity, (byte)33);
        } else {
            byte b0;
            if (source == DamageSource.DROWN) {
                b0 = 36;
            } else if (source.isFire()) {
                b0 = 37;
            } else if (source == DamageSource.SWEET_BERRY_BUSH) {
                b0 = 44;
            } else if (source == DamageSource.FREEZE) {
                b0 = 57;
            } else {
                b0 = 2;
            }
            thisLivingEntity.level.broadcastEntityEvent(thisLivingEntity, b0);
        }
        if (source != DamageSource.DROWN && (!flag || hasDamage)) {
            thisLivingEntity.markHurt();
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
    public void setHealth(float newHealth){
            throw new IllegalStateException("failed to shadow setHealth()");
    }
}
