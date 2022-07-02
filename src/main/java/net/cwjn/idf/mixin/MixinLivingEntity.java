package net.cwjn.idf.mixin;

import net.cwjn.idf.ImprovedDamageFramework;
import net.cwjn.idf.damage.*;
import net.cwjn.idf.event.hook.ServerEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.gameevent.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

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
            hurtArmor(damageSource, damageAmount);
            if (ServerEvents.debugMode) damageAmount = DamageHandler.handleDamageWithDebug(thisLivingEntity, damageSource, damageAmount, ImprovedDamageFramework.LOGGER);
            else damageAmount = DamageHandler.handleDamage(thisLivingEntity, damageSource, damageAmount);
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

    @Shadow
    protected void hurtArmor(DamageSource p_21122_, float p_21123_) {
        throw new IllegalStateException("Mixin failed to shadow hurtArmor(DamageSource d, float f)");
    }

}
