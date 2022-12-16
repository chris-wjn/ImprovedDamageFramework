package net.cwjn.idf.attribute;

import net.cwjn.idf.ImprovedDamageFramework;
import net.cwjn.idf.util.Util;
import net.cwjn.idf.config.json.JSONHandler;
import net.cwjn.idf.config.json.data.EntityData;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static net.minecraftforge.eventbus.api.EventPriority.HIGHEST;

@Mod.EventBusSubscriber
public class BonusBaseAttributes {

    @SubscribeEvent(priority = HIGHEST)
    public static void grantBonuses(EntityJoinLevelEvent event) {
        if (!event.getLevel().isClientSide() && event.getEntity() instanceof LivingEntity livingEntity) {
            //FIRST SECTION: do not spawn the entity if it somehow dodged the attribute attaching event on startup *cough* evilcraft *cough*
            if (livingEntity.getAttribute(IDFAttributes.FIRE_DAMAGE.get()) == null) {
                ImprovedDamageFramework.LOGGER.info("ImprovedDamageFramework blocked spawning of living entity " + livingEntity + " because it does not have proper attributes!");
                event.setCanceled(true);
                return;
            }

            //SECOND SECTION: scale the entity's damage and health up
            if (livingEntity.getPersistentData().getBoolean("idf.bonus_applied")) return;
            AttributeInstance healthInstance = livingEntity.getAttribute(Attributes.MAX_HEALTH);
            AttributeInstance damageInstance = livingEntity.getAttribute(Attributes.ATTACK_DAMAGE);
            if (damageInstance != null) damageInstance.setBaseValue(damageInstance.getBaseValue() * 2);
            healthInstance.setBaseValue(healthInstance.getBaseValue() * 5);
            //THIRD SECTION: attach bonus attributes defined in entity_data.json
            EntityData data = JSONHandler.getEntityData(Util.getEntityRegistryName(livingEntity.getType()));
            if (data != null) {
                if (damageInstance != null) damageInstance.setBaseValue(damageInstance.getBaseValue() + data.getPhysicalDamage());
                livingEntity.getAttribute(Attributes.ARMOR).setBaseValue(livingEntity.getAttributeBaseValue(Attributes.ARMOR) + data.getPhysicalResistance());
                livingEntity.getAttribute(Attributes.ARMOR_TOUGHNESS).setBaseValue(livingEntity.getAttributeBaseValue(Attributes.ARMOR_TOUGHNESS) + data.getDefense());
                livingEntity.getAttribute(Attributes.ATTACK_KNOCKBACK).setBaseValue(livingEntity.getAttributeBaseValue(Attributes.ATTACK_KNOCKBACK) + data.getKnockback());
                healthInstance.setBaseValue(healthInstance.getBaseValue() + data.getMaxHP());
                livingEntity.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(livingEntity.getAttributeBaseValue(Attributes.KNOCKBACK_RESISTANCE) + data.getKnockbackRes());
                livingEntity.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(livingEntity.getAttributeBaseValue(Attributes.MOVEMENT_SPEED) + data.getMovespeed());
            }
            //FOURTH SECTION: heal the entity to their new max hp
            if (livingEntity.getHealth() < livingEntity.getMaxHealth()) livingEntity.heal(livingEntity.getMaxHealth() - livingEntity.getHealth());
            livingEntity.getPersistentData().putBoolean("idf.bonus_applied", true);
        }
    }

}
