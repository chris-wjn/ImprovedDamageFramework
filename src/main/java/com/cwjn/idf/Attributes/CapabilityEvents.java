package com.cwjn.idf.Attributes;

import com.cwjn.idf.Config.EntityData;
import com.cwjn.idf.Config.JSONHandler;
import com.cwjn.idf.ImprovedDamageFramework;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ImprovedDamageFramework.MOD_ID)
public class CapabilityEvents {

    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof LivingEntity) {
            LivingEntity entity = (LivingEntity) event.getObject();
            EntityData data = JSONHandler.getEntityData(entity.getType().getRegistryName()); //get the mob's json data
            if (data != null) { //check that we actually have json data for the mob. This should also stop the player from being affected by this
                if (!entity.getCapability(CapabilityProvider.AUXILIARY_DATA).isPresent()) {
                    event.addCapability(new ResourceLocation(ImprovedDamageFramework.MOD_ID, "auxiliary"), new CapabilityProvider());
                }
                entity.getCapability(CapabilityProvider.AUXILIARY_DATA).ifPresent(h -> h.setDamageClass(data.getDamageClass()));
            }
        }
    }

    @SubscribeEvent
    public static void onJoinWorld(EntityJoinWorldEvent event) {
        if (event.getEntity() instanceof LivingEntity) {
            LivingEntity entity = (LivingEntity) event.getEntity();
            EntityData data = JSONHandler.getEntityData(entity.getType().getRegistryName()); //get the mob's json data
            if (data != null) { //check that we actually have json data for the mob. This should also stop the player from being affected by this
                entity.getAttribute(AttributeRegistry.FIRE_DAMAGE.get()).setBaseValue(data.getDamageValues()[0]);
                entity.getAttribute(AttributeRegistry.WATER_DAMAGE.get()).setBaseValue(data.getDamageValues()[1]);
                entity.getAttribute(AttributeRegistry.LIGHTNING_DAMAGE.get()).setBaseValue(data.getDamageValues()[2]);
                entity.getAttribute(AttributeRegistry.MAGIC_DAMAGE.get()).setBaseValue(data.getDamageValues()[3]);
                entity.getAttribute(AttributeRegistry.DARK_DAMAGE.get()).setBaseValue(data.getDamageValues()[4]);
                entity.getAttribute(AttributeRegistry.FIRE_RESISTANCE.get()).setBaseValue(data.getResistanceValues()[0]);
                entity.getAttribute(AttributeRegistry.WATER_RESISTANCE.get()).setBaseValue(data.getResistanceValues()[1]);
                entity.getAttribute(AttributeRegistry.LIGHTNING_RESISTANCE.get()).setBaseValue(data.getResistanceValues()[2]);
                entity.getAttribute(AttributeRegistry.MAGIC_RESISTANCE.get()).setBaseValue(data.getResistanceValues()[3]);
                entity.getAttribute(AttributeRegistry.DARK_RESISTANCE.get()).setBaseValue(data.getResistanceValues()[4]);
                entity.getAttribute(AttributeRegistry.STRIKE_MULT.get()).setBaseValue(data.getDamageClassMult()[0]);
                entity.getAttribute(AttributeRegistry.PIERCE_MULT.get()).setBaseValue(data.getDamageClassMult()[1]);
                entity.getAttribute(AttributeRegistry.SLASH_MULT.get()).setBaseValue(data.getDamageClassMult()[2]);
                entity.getAttribute(AttributeRegistry.CRUSH_MULT.get()).setBaseValue(data.getDamageClassMult()[3]);
                entity.getAttribute(AttributeRegistry.GENERIC_MULT.get()).setBaseValue(data.getDamageClassMult()[4]);
            }
        }
    }

    @SubscribeEvent
    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.register(AuxiliaryData.class);
    }

}
