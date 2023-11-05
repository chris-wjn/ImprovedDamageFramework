package net.cwjn.idf.mixin;

import net.cwjn.idf.ImprovedDamageFramework;
import net.cwjn.idf.api.event.OnFoodExhaustionEvent;
import net.cwjn.idf.attribute.IDFAttributes;
import net.cwjn.idf.capability.provider.AuxiliaryProvider;
import net.cwjn.idf.config.CommonConfig;
import net.cwjn.idf.damage.DamageHandler;
import net.cwjn.idf.damage.IDFEntityDamageSource;
import net.cwjn.idf.event.LogicalEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.cwjn.idf.attribute.IDFElement.HOLY;
import static net.minecraftforge.common.ForgeHooks.getCriticalHit;

@Mixin(Player.class)
public class MixinPlayer {

    private Player thisPlayer() {
        return (Player)(Object)this;
    }

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
        if (CommonConfig.ALLOW_JUMP_CRITS.get()) {
            isCrit = isCrit || player.fallDistance > 0.0F && !player.isOnGround() && !player.onClimbable() && !player.isInWater() && !player.hasEffect(MobEffects.BLINDNESS) && !player.isPassenger() && target instanceof LivingEntity;
        }
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
     */
    @Redirect(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getAttributeValue(Lnet/minecraft/world/entity/ai/attributes/Attribute;)D", ordinal = 1))
    private double removeKnockback(Player instance, Attribute attribute) {
        return 0;
    }
    @Redirect(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;getKnockbackBonus(Lnet/minecraft/world/entity/LivingEntity;)I"))
    private int removeKnockback1(LivingEntity pPlayer) {
        return 0;
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
