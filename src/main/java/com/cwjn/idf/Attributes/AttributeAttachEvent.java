package com.cwjn.idf.Attributes;

import com.cwjn.idf.Config.EntityData;
import com.cwjn.idf.Config.JSONHandler;
import com.cwjn.idf.ImprovedDamageFramework;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class AttributeAttachEvent {

    @SubscribeEvent
    public static void attachAttributes(EntityAttributeModificationEvent event) {
        for (EntityType<? extends LivingEntity> entityType : event.getTypes()) {
            //damage types
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
            //AUXILIARY
            event.add(entityType, AttributeRegistry.EVASION.get());
            event.add(entityType, AttributeRegistry.LIFESTEAL.get());
            event.add(entityType, AttributeRegistry.PENETRATING.get());
            event.add(entityType, AttributeRegistry.SLOWING.get());
        }
    }

}
