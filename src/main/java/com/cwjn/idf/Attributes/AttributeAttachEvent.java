package com.cwjn.idf.Attributes;

import com.cwjn.idf.Config.EntityData;
import com.cwjn.idf.Config.JSONHandler;
import com.cwjn.idf.ImprovedDamageFramework;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class AttributeAttachEvent {

    @SubscribeEvent
    public static void attachAttributes(EntityAttributeModificationEvent event) {
        for (EntityType<? extends LivingEntity> entityType : event.getTypes()) {
            //damage
            //TODO: grab values from json to put here!
            if (entityType == EntityType.PLAYER) {
                event.add(entityType, AttributeRegistry.FIRE_DAMAGE.get());
                event.add(entityType, AttributeRegistry.WATER_DAMAGE.get());
                event.add(entityType, AttributeRegistry.LIGHTNING_DAMAGE.get());
                event.add(entityType, AttributeRegistry.MAGIC_DAMAGE.get());
                event.add(entityType, AttributeRegistry.DARK_DAMAGE.get());
                //resistances
                event.add(entityType, AttributeRegistry.FIRE_RESISTANCE.get());
                event.add(entityType, AttributeRegistry.WATER_RESISTANCE.get());
                event.add(entityType, AttributeRegistry.LIGHTNING_RESISTANCE.get());
                event.add(entityType, AttributeRegistry.MAGIC_RESISTANCE.get());
                event.add(entityType, AttributeRegistry.DARK_RESISTANCE.get());
            }
            EntityData data = JSONHandler.getEntityData(entityType.getRegistryName());
            event.add(entityType, AttributeRegistry.FIRE_DAMAGE.get(), data.getDamageValues()[0]);
            event.add(entityType, AttributeRegistry.WATER_DAMAGE.get(), data.getDamageValues()[1]);
            event.add(entityType, AttributeRegistry.LIGHTNING_DAMAGE.get(), data.getDamageValues()[2]);
            event.add(entityType, AttributeRegistry.MAGIC_DAMAGE.get(), data.getDamageValues()[3]);
            event.add(entityType, AttributeRegistry.DARK_DAMAGE.get(), data.getDamageValues()[4]);
            //resistances
            event.add(entityType, AttributeRegistry.FIRE_RESISTANCE.get(), data.getResistanceValues()[0]);
            event.add(entityType, AttributeRegistry.WATER_RESISTANCE.get(), data.getResistanceValues()[1]);
            event.add(entityType, AttributeRegistry.LIGHTNING_RESISTANCE.get(), data.getResistanceValues()[2]);
            event.add(entityType, AttributeRegistry.MAGIC_RESISTANCE.get(), data.getResistanceValues()[3]);
            event.add(entityType, AttributeRegistry.DARK_RESISTANCE.get(), data.getResistanceValues()[4]);
        }
    }

}
