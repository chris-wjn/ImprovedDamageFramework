package net.cwjn.idf.capability;

import net.cwjn.idf.attribute.IDFAttributes;
import net.cwjn.idf.capability.data.AuxiliaryData;
import net.cwjn.idf.capability.data.ProjectileHelper;
import net.cwjn.idf.capability.provider.ArrowHelperProvider;
import net.cwjn.idf.capability.provider.AuxiliaryProvider;
import net.cwjn.idf.capability.provider.TridentHelperProvider;
import net.cwjn.idf.config.json.data.EntityData;
import net.cwjn.idf.config.json.JSONHandler;
import net.cwjn.idf.ImprovedDamageFramework;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ImprovedDamageFramework.MOD_ID)
public class CapabilityEvents {

    @SubscribeEvent
    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.register(AuxiliaryData.class);
        event.register(ProjectileHelper.class);
    }

    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof LivingEntity entity) {
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
    public static void onLivingEquipmentChange(LivingEquipmentChangeEvent event) {
        if (event.getSlot() == EquipmentSlot.MAINHAND) {
            ItemStack item = event.getTo();
            LivingEntity entity = event.getEntityLiving();
            entity.getCapability(AuxiliaryProvider.AUXILIARY_DATA).ifPresent(h -> {
                if (JSONHandler.getEntityData(entity.getType().getRegistryName()) != null)
                    h.setDamageClass(JSONHandler.getEntityData(entity.getType().getRegistryName()).getDamageClass());
                if (item.hasTag() && item.getTag().contains("idf.damage_class")) {
                    h.setDamageClass(item.getTag().getString("idf.damage_class"));
                } else {
                    h.setDamageClass("strike");
                }
            });
            if (item == ItemStack.EMPTY) return;
            if (item.getItem() instanceof BowItem || item.getItem() instanceof CrossbowItem) {
                entity.getCapability(ArrowHelperProvider.PROJECTILE_HELPER).ifPresent(h -> {
                    h.setFire((float) entity.getAttributeValue(IDFAttributes.FIRE_DAMAGE.get()));
                    h.setWater((float) entity.getAttributeValue(IDFAttributes.WATER_DAMAGE.get()));
                    h.setLightning((float) entity.getAttributeValue(IDFAttributes.LIGHTNING_DAMAGE.get()));
                    h.setMagic((float) entity.getAttributeValue(IDFAttributes.MAGIC_DAMAGE.get()));
                    h.setDark((float) entity.getAttributeValue(IDFAttributes.DARK_DAMAGE.get()));
                    h.setPen((float) entity.getAttributeValue(IDFAttributes.PENETRATING.get()));
                    h.setCrit((float) entity.getAttributeValue(IDFAttributes.CRIT_CHANCE.get()));
                    h.setLifesteal((float) entity.getAttributeValue(IDFAttributes.LIFESTEAL.get()));
                    h.setDamageClass(item.hasTag() ?
                            item.getTag().contains("idf.damage_class") ? item.getTag().getString("idf.damage_class") : "strike" : "strike");
                });
            }
            if (item.getItem() instanceof TridentItem) {
                entity.getCapability(TridentHelperProvider.PROJECTILE_HELPER).ifPresent(h -> {
                    h.setFire((float) entity.getAttributeValue(IDFAttributes.FIRE_DAMAGE.get()));
                    h.setWater((float) entity.getAttributeValue(IDFAttributes.WATER_DAMAGE.get()));
                    h.setLightning((float) entity.getAttributeValue(IDFAttributes.LIGHTNING_DAMAGE.get()));
                    h.setMagic((float) entity.getAttributeValue(IDFAttributes.MAGIC_DAMAGE.get()));
                    h.setDark((float) entity.getAttributeValue(IDFAttributes.DARK_DAMAGE.get()));
                    h.setPen((float) entity.getAttributeValue(IDFAttributes.PENETRATING.get()));
                    h.setCrit((float) entity.getAttributeValue(IDFAttributes.CRIT_CHANCE.get()));
                    h.setLifesteal((float) entity.getAttributeValue(IDFAttributes.LIFESTEAL.get()));
                    h.setDamageClass(item.hasTag() ?
                            item.getTag().contains("idf.damage_class") ? item.getTag().getString("idf.damage_class") : "strike" : "strike");
                });
            }
        }
    }

}
