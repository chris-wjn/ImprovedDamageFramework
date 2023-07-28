package net.cwjn.idf.attribute;

import net.cwjn.idf.ImprovedDamageFramework;
import net.cwjn.idf.config.json.records.EntityTag;
import net.cwjn.idf.data.CommonData;
import net.cwjn.idf.util.Util;
import net.cwjn.idf.config.json.records.EntityData;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import oshi.util.tuples.Pair;

import java.util.Iterator;

import static net.cwjn.idf.data.CommonData.ENTITY_BONUS;
import static net.minecraftforge.eventbus.api.EventPriority.HIGHEST;

@Mod.EventBusSubscriber
public class AttachAttributesOnJoin {

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
            if (livingEntity.getPersistentData().getBoolean(ENTITY_BONUS)) return;
            AttributeInstance healthInstance = livingEntity.getAttribute(Attributes.MAX_HEALTH);
            AttributeInstance damageInstance = livingEntity.getAttribute(Attributes.ATTACK_DAMAGE);
            if (damageInstance != null) damageInstance.setBaseValue(damageInstance.getBaseValue() * 2);
            healthInstance.setBaseValue(healthInstance.getBaseValue() * 5);

            //THIRD SECTION: attach bonus attributes defined in entity_data.json
            EntityData data = CommonData.LOGICAL_ENTITY_MAP.get(Util.getEntityRegistryName(livingEntity.getType()));
            if (data != null) {
                if (damageInstance != null) damageInstance.setBaseValue(damageInstance.getBaseValue() + data.oData().pDmg());
                livingEntity.getAttribute(Attributes.ARMOR).setBaseValue(livingEntity.getAttributeBaseValue(Attributes.ARMOR) + data.dData().pDef());
                livingEntity.getAttribute(Attributes.ARMOR_TOUGHNESS).setBaseValue(livingEntity.getAttributeBaseValue(Attributes.ARMOR_TOUGHNESS) + data.dData().weight());
                livingEntity.getAttribute(Attributes.ATTACK_KNOCKBACK).setBaseValue(livingEntity.getAttributeBaseValue(Attributes.ATTACK_KNOCKBACK) + data.oData().kb());
                healthInstance.setBaseValue(healthInstance.getBaseValue() + data.aData().hp());
                livingEntity.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(livingEntity.getAttributeBaseValue(Attributes.KNOCKBACK_RESISTANCE) + data.dData().kbr());
                livingEntity.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(livingEntity.getAttributeBaseValue(Attributes.MOVEMENT_SPEED) * data.aData().ms());
                //FOURTH SECTION: give the entity it's attribute modifiers defined by its tags
                for (EntityTag e : data.tags()) {
                    for (Iterator<Pair<Attribute, Double>> it = e.getOffensiveData().getAttributesWithModifier(); it.hasNext(); ) {
                        Pair<Attribute, Double> p = it.next();
                        setNewBaseValue(livingEntity, p.getA(), p.getB());
                    }
                    for (Iterator<Pair<Attribute, Double>> it = e.getDefensiveData().getAttributesWithModifier(); it.hasNext(); ) {
                        Pair<Attribute, Double> p = it.next();
                        setNewBaseValue(livingEntity, p.getA(), p.getB());
                    }
                    for (Iterator<Pair<Attribute, Double>> it = e.getAuxiliaryData().getAttributesWithModifier(); it.hasNext(); ) {
                        Pair<Attribute, Double> p = it.next();
                        if (p.getA() == Attributes.MOVEMENT_SPEED) {
                            setNewBaseValueByMultiplication(livingEntity, p.getA(), p.getB());
                        } else {
                            setNewBaseValue(livingEntity, p.getA(), p.getB());
                        }
                    }
                }
            }

            //FIFTH SECTION: give the entity a tag, so it doesn't get modified again on world reload
            if (livingEntity.getHealth() < livingEntity.getMaxHealth()) livingEntity.heal(livingEntity.getMaxHealth() - livingEntity.getHealth());
            livingEntity.getPersistentData().putBoolean(ENTITY_BONUS, true);
        }
    }

    private static void setNewBaseValue(LivingEntity e, Attribute a, Double val) {
        AttributeInstance instance = e.getAttribute(a);
        if (instance == null) return;
        instance.setBaseValue(val + instance.getBaseValue());
    }

    private static void setNewBaseValueByMultiplication(LivingEntity e, Attribute a, Double val) {
        AttributeInstance instance = e.getAttribute(a);
        if (instance == null) return;
        instance.setBaseValue(val * instance.getBaseValue());
    }

}
