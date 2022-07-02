package net.cwjn.idf.attribute;

import net.cwjn.idf.ImprovedDamageFramework;
import net.cwjn.idf.config.json.JSONHandler;
import net.cwjn.idf.config.json.data.EntityData;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static net.minecraftforge.eventbus.api.EventPriority.*;

@Mod.EventBusSubscriber
public class BonusBaseAttributes {

    @SubscribeEvent(priority = HIGHEST)
    public static void attachFromConfig(EntityJoinWorldEvent event) {
        if (!event.getWorld().isClientSide() && event.getEntity() instanceof LivingEntity livingEntity) {
            //FIRST SECTION: do not spawn the entity if it somehow dodged the attribute attaching event on startup
            if (livingEntity.getAttribute(IDFAttributes.FIRE_DAMAGE.get()) == null) {
                ImprovedDamageFramework.LOGGER.info("ImprovedDamageFramework blocked spawning of entity " + livingEntity + " because the entity somehow does not have proper attributes!");
                event.setCanceled(true);
            }

            //SECOND SECTION: scale the entity's damage and health up
            if (livingEntity.getPersistentData().getBoolean("idf.bonus_applied")) return;
            AttributeInstance healthInstance = livingEntity.getAttribute(Attributes.MAX_HEALTH);
            AttributeInstance damageInstance = livingEntity.getAttribute(Attributes.ATTACK_DAMAGE);
            if (damageInstance != null) damageInstance.setBaseValue(damageInstance.getBaseValue() * 2);
            healthInstance.setBaseValue(healthInstance.getBaseValue() * 5);

            //THIRD SECTION: attach bonus attributes defined in entity_data.json
            EntityData data = JSONHandler.getEntityData(livingEntity.getType().getRegistryName());
            if (data != null) {
                if (damageInstance != null) damageInstance.setBaseValue(damageInstance.getBaseValue() + data.getAttackDamage());
                livingEntity.getAttribute(Attributes.ARMOR).setBaseValue(livingEntity.getAttributeBaseValue(Attributes.ARMOR) + data.getArmour());
                livingEntity.getAttribute(Attributes.ARMOR_TOUGHNESS).setBaseValue(livingEntity.getAttributeBaseValue(Attributes.ARMOR_TOUGHNESS) + data.getArmourToughness());
                healthInstance.setBaseValue(healthInstance.getBaseValue() + data.getMaxHP());
                livingEntity.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(livingEntity.getAttributeBaseValue(Attributes.KNOCKBACK_RESISTANCE) + data.getKnockbackRes());
                livingEntity.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(livingEntity.getAttributeBaseValue(Attributes.MOVEMENT_SPEED) + data.getMovespeed());
            }
            //FOURTH SECTION: heal the entity to their new max hp and make sure they aren't NaN
            if (livingEntity.getHealth() < livingEntity.getMaxHealth()) livingEntity.heal(livingEntity.getMaxHealth() - livingEntity.getHealth());
            if (Float.isNaN(livingEntity.getHealth())) livingEntity.setHealth(livingEntity.getMaxHealth());

            livingEntity.getPersistentData().putBoolean("idf.bonus_applied", true);
        }
    }

}
