package net.cwjn.idf.capability;

import net.cwjn.idf.attribute.AttributeRegistry;
import net.cwjn.idf.config.json.Config;
import net.cwjn.idf.config.json.EntityData;
import net.cwjn.idf.config.json.JSONHandler;
import net.cwjn.idf.ImprovedDamageFramework;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import se.mickelus.tetra.items.modular.IModularItem;
import se.mickelus.tetra.items.modular.ItemModularHandheld;
import se.mickelus.tetra.items.modular.ModularItem;
import se.mickelus.tetra.items.modular.impl.bow.ModularBowItem;
import se.mickelus.tetra.items.modular.impl.crossbow.ModularCrossbowItem;

import java.util.Arrays;

@Mod.EventBusSubscriber(modid = ImprovedDamageFramework.MOD_ID)
public class CapabilityEvents {

    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof LivingEntity) {
            LivingEntity entity = (LivingEntity) event.getObject();
            EntityData data = JSONHandler.getEntityData(entity.getType().getRegistryName()); //get the mob's json data
            if (data != null) { //check that we actually have json data for the mob. This should also stop the player from being affected by this
                event.addCapability(new ResourceLocation(ImprovedDamageFramework.MOD_ID, "auxiliary"), new AuxiliaryProvider());
                event.addCapability(new ResourceLocation(ImprovedDamageFramework.MOD_ID, "arrow_helper"), new ArrowHelperProvider());
                event.addCapability(new ResourceLocation(ImprovedDamageFramework.MOD_ID, "trident_helper"), new TridentHelperProvider());
            }
            if (entity instanceof Player) {
                event.addCapability(new ResourceLocation(ImprovedDamageFramework.MOD_ID, "arrow_helper"), new ArrowHelperProvider());
                event.addCapability(new ResourceLocation(ImprovedDamageFramework.MOD_ID, "trident_helper"), new TridentHelperProvider());
                event.addCapability(new ResourceLocation(ImprovedDamageFramework.MOD_ID, "auxiliary"), new AuxiliaryProvider());
            }
        }
    }

    @SubscribeEvent
    public static void livingTickEvent(LivingEvent.LivingUpdateEvent event) {
        LivingEntity entity = event.getEntityLiving();
        entity.getCapability(AuxiliaryProvider.AUXILIARY_DATA).ifPresent(h -> {
            if (JSONHandler.getEntityData(entity.getType().getRegistryName()) != null) {
                h.setDamageClass(JSONHandler.getEntityData(entity.getType().getRegistryName()).getDamageClass());
            }
            if (entity.getMainHandItem() != ItemStack.EMPTY) {
                ItemStack item = entity.getMainHandItem();
                if (item.hasTag() && item.getTag().contains("idf.damage_class")) {
                    h.setDamageClass(entity.getMainHandItem().getTag().getString("idf.damage_class"));
                }
            } else if (entity.getOffhandItem() != ItemStack.EMPTY) {
                ItemStack item = entity.getOffhandItem();
                if (item.hasTag() && item.getTag().contains("idf.damage_class")) {
                    h.setDamageClass(entity.getOffhandItem().getTag().getString("idf.damage_class"));
                }
            }
        });
        entity.getCapability(ArrowHelperProvider.PROJECTILE_HELPER).ifPresent(h -> {
            if (entity.getMainHandItem() != ItemStack.EMPTY) {
                Item item = entity.getMainHandItem().getItem();
                if (item instanceof BowItem || item instanceof CrossbowItem || item instanceof ModularBowItem || item instanceof ModularCrossbowItem) {
                    h.setFire((float) entity.getAttributeValue(AttributeRegistry.FIRE_DAMAGE.get()));
                    h.setWater((float) entity.getAttributeValue(AttributeRegistry.WATER_DAMAGE.get()));
                    h.setLightning((float) entity.getAttributeValue(AttributeRegistry.LIGHTNING_DAMAGE.get()));
                    h.setMagic((float) entity.getAttributeValue(AttributeRegistry.MAGIC_DAMAGE.get()));
                    h.setDark((float) entity.getAttributeValue(AttributeRegistry.DARK_DAMAGE.get()));
                    h.setPen((float) entity.getAttributeValue(AttributeRegistry.PENETRATING.get()));
                    h.setCrit((float) entity.getAttributeValue(AttributeRegistry.CRIT_CHANCE.get()));
                    h.setLifesteal((float) entity.getAttributeValue(AttributeRegistry.LIFESTEAL.get()));
                    h.setDamageClass(entity.getCapability(AuxiliaryProvider.AUXILIARY_DATA).orElseThrow(() -> new RuntimeException("entity has no damage class!")).getDamageClass());
                }
            }
        });
        entity.getCapability(TridentHelperProvider.PROJECTILE_HELPER).ifPresent(h -> {
            if (entity.getMainHandItem() != ItemStack.EMPTY) {
                Item item = entity.getMainHandItem().getItem();
                if (item instanceof TridentItem || item instanceof ItemModularHandheld && Arrays.asList(((ItemModularHandheld) item).getMajorModuleKeys()).contains("trident/trident")) {
                    h.setFire((float) entity.getAttributeValue(AttributeRegistry.FIRE_DAMAGE.get()));
                    h.setWater((float) entity.getAttributeValue(AttributeRegistry.WATER_DAMAGE.get()));
                    h.setLightning((float) entity.getAttributeValue(AttributeRegistry.LIGHTNING_DAMAGE.get()));
                    h.setMagic((float) entity.getAttributeValue(AttributeRegistry.MAGIC_DAMAGE.get()));
                    h.setDark((float) entity.getAttributeValue(AttributeRegistry.DARK_DAMAGE.get()));
                    h.setPen((float) entity.getAttributeValue(AttributeRegistry.PENETRATING.get()));
                    h.setCrit((float) entity.getAttributeValue(AttributeRegistry.CRIT_CHANCE.get()));
                    h.setLifesteal((float) entity.getAttributeValue(AttributeRegistry.LIFESTEAL.get()));
                    h.setDamageClass(entity.getCapability(AuxiliaryProvider.AUXILIARY_DATA).orElseThrow(() -> new RuntimeException("entity has no damage class!")).getDamageClass());
                }
            }
        });
    }

    @SubscribeEvent
    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.register(AuxiliaryData.class);
        event.register(ProjectileHelper.class);
    }

}
