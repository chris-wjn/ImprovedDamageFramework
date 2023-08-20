package net.cwjn.idf.capability;

import com.google.common.collect.Multimap;
import net.cwjn.idf.attribute.IDFElement;
import net.cwjn.idf.data.CommonData;
import net.cwjn.idf.util.Util;
import net.cwjn.idf.attribute.IDFAttributes;
import net.cwjn.idf.capability.data.AuxiliaryData;
import net.cwjn.idf.capability.data.ProjectileHelper;
import net.cwjn.idf.capability.provider.ArrowHelperProvider;
import net.cwjn.idf.capability.provider.AuxiliaryProvider;
import net.cwjn.idf.capability.provider.TridentHelperProvider;
import net.cwjn.idf.config.json.records.EntityData;
import net.cwjn.idf.ImprovedDamageFramework;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static net.cwjn.idf.attribute.IDFElement.*;
import static net.cwjn.idf.util.Util.getAttributeAmount;

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
            EntityData data = CommonData.LOGICAL_ENTITY_MAP.get(Util.getEntityRegistryName(entity.getType())); //get the mob's json data
            if (data != null) {
                event.addCapability(new ResourceLocation(ImprovedDamageFramework.MOD_ID, "auxiliary"), new AuxiliaryProvider());
                event.addCapability(new ResourceLocation(ImprovedDamageFramework.MOD_ID, "arrow_helper"), new ArrowHelperProvider());
                event.addCapability(new ResourceLocation(ImprovedDamageFramework.MOD_ID, "trident_helper"), new TridentHelperProvider());
            } else if (entity instanceof Player) {
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
            LivingEntity entity = event.getEntity();
            entity.getCapability(AuxiliaryProvider.AUXILIARY_DATA).ifPresent(h -> {
                if (CommonData.LOGICAL_ENTITY_MAP.get(Util.getEntityRegistryName(entity.getType())) != null)
                    h.setDamageClass(CommonData.LOGICAL_ENTITY_MAP.get(Util.getEntityRegistryName(entity.getType())).damageClass());
                if ((item.hasTag() && item.getTag().contains("idf.damage_class"))) {
                    h.setDamageClass(item.getTag().getString("idf.damage_class"));
                } else {
                    h.setDamageClass("strike");
                }
            });
        }
    }

    @SubscribeEvent
    public static void onLivingUseItem(LivingEntityUseItemEvent.Stop event) {
        ItemStack item = event.getItem();
        LivingEntity entity = event.getEntity();
        if (item.hasTag() && item.getTag().getBoolean(CommonData.RANGED_TAG)) {
            Multimap<Attribute, AttributeModifier> map = item.getAttributeModifiers(LivingEntity.getEquipmentSlotForItem(item));
            entity.getCapability(ArrowHelperProvider.PROJECTILE_HELPER).ifPresent(h -> {
                h.setFire((float) (getAttributeAmount(map.get(FIRE.damage)) + entity.getAttributeValue(IDFAttributes.FIRE_DAMAGE.get())));
                h.setWater((float) (getAttributeAmount(map.get(WATER.damage)) + entity.getAttributeValue(IDFAttributes.WATER_DAMAGE.get())));
                h.setLightning((float) (getAttributeAmount(map.get(LIGHTNING.damage)) + entity.getAttributeValue(LIGHTNING.damage)));
                h.setMagic((float) (getAttributeAmount(map.get(MAGIC.damage)) + entity.getAttributeValue(MAGIC.damage)));
                h.setDark((float) (getAttributeAmount(map.get(DARK.damage)) + entity.getAttributeValue(DARK.damage)));
                h.setHoly((float) (getAttributeAmount(map.get(HOLY.damage)) + entity.getAttributeValue(HOLY.damage)));
                h.setPhys((float) (getAttributeAmount(map.get(Attributes.ATTACK_DAMAGE)) + entity.getAttributeValue(Attributes.ATTACK_DAMAGE)));
                h.setPen((float) (getAttributeAmount(map.get(IDFAttributes.PENETRATING.get())) + entity.getAttributeValue(IDFAttributes.PENETRATING.get())));
                h.setCrit(((getAttributeAmount(map.get(IDFAttributes.CRIT_CHANCE.get())) + entity.getAttributeValue(IDFAttributes.CRIT_CHANCE.get()))*0.01 > entity.getRandom().nextDouble()));
                h.setCritDmg((float) (getAttributeAmount(map.get(IDFAttributes.CRIT_DAMAGE.get())) + entity.getAttributeValue(IDFAttributes.CRIT_DAMAGE.get())));
                h.setLifesteal((float) (getAttributeAmount(map.get(IDFAttributes.LIFESTEAL.get())) + entity.getAttributeValue(IDFAttributes.LIFESTEAL.get())));
                h.setWeight((float) (getAttributeAmount(map.get(IDFAttributes.FORCE.get())) + entity.getAttributeValue(IDFAttributes.FORCE.get())));
                h.setDamageClass(item.hasTag() ?
                        item.getTag().contains("idf.damage_class") ? item.getTag().getString("idf.damage_class") : "pierce" : "pierce");
            });
        } else if (item.hasTag() && item.getTag().getBoolean(CommonData.THROWN_TAG)) {
            entity.getCapability(TridentHelperProvider.PROJECTILE_HELPER).ifPresent(h -> {
                h.setFire((float) entity.getAttributeValue(IDFAttributes.FIRE_DAMAGE.get()));
                h.setWater((float) entity.getAttributeValue(IDFAttributes.WATER_DAMAGE.get()));
                h.setLightning((float) entity.getAttributeValue(IDFAttributes.LIGHTNING_DAMAGE.get()));
                h.setMagic((float) entity.getAttributeValue(IDFAttributes.MAGIC_DAMAGE.get()));
                h.setDark((float) entity.getAttributeValue(IDFAttributes.DARK_DAMAGE.get()));
                h.setHoly((float) entity.getAttributeValue(IDFElement.HOLY.damage));
                h.setPhys((float) entity.getAttributeValue(Attributes.ATTACK_DAMAGE));
                h.setPen((float) entity.getAttributeValue(IDFAttributes.PENETRATING.get()));
                h.setCrit((entity.getAttributeValue(IDFAttributes.CRIT_CHANCE.get()))*0.01 > entity.getRandom().nextDouble());
                h.setCritDmg((float) (entity.getAttributeValue(IDFAttributes.CRIT_DAMAGE.get())));
                h.setLifesteal((float) entity.getAttributeValue(IDFAttributes.LIFESTEAL.get()));
                h.setWeight((float) entity.getAttributeValue(IDFAttributes.FORCE.get()));
                h.setDamageClass(item.hasTag() ?
                        item.getTag().contains("idf.damage_class") ? item.getTag().getString("idf.damage_class") : "pierce" : "pierce");
            });
        }
    }

}
