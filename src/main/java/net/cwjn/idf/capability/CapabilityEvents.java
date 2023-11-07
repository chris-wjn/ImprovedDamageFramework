package net.cwjn.idf.capability;

import com.google.common.collect.Multimap;
import net.cwjn.idf.ImprovedDamageFramework;
import net.cwjn.idf.attribute.IDFAttributes;
import net.cwjn.idf.attribute.IDFElement;
import net.cwjn.idf.capability.data.AuxiliaryData;
import net.cwjn.idf.capability.data.ProjectileHelper;
import net.cwjn.idf.capability.provider.ArrowHelperProvider;
import net.cwjn.idf.capability.provider.AuxiliaryProvider;
import net.cwjn.idf.capability.provider.TridentHelperProvider;
import net.cwjn.idf.config.json.records.EntityData;
import net.cwjn.idf.data.CommonData;
import net.cwjn.idf.util.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
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
    public static void setPlayerRangedHelpers(LivingEntityUseItemEvent.Stop event) {
        ItemStack item = event.getItem();
        LivingEntity entity = event.getEntity();
        boolean isMainHandUse = entity.getUsedItemHand().equals(InteractionHand.MAIN_HAND);
        if (item.hasTag() && item.getTag().getBoolean(CommonData.RANGED_TAG)) {
            Multimap<Attribute, AttributeModifier> map = item.getAttributeModifiers(LivingEntity.getEquipmentSlotForItem(item));
            entity.getCapability(ArrowHelperProvider.PROJECTILE_HELPER).ifPresent(h -> {
                h.setFire((float) (getAttributeAmount(map.get(FIRE.damage)) + (isMainHandUse? entity.getAttributeValue(FIRE.damage):0)));
                h.setWater((float) (getAttributeAmount(map.get(WATER.damage)) + (isMainHandUse? entity.getAttributeValue(WATER.damage):0)));
                h.setLightning((float) (getAttributeAmount(map.get(LIGHTNING.damage)) + (isMainHandUse? entity.getAttributeValue(LIGHTNING.damage):0)));
                h.setMagic((float) (getAttributeAmount(map.get(MAGIC.damage)) + (isMainHandUse? entity.getAttributeValue(MAGIC.damage):0)));
                h.setDark((float) (getAttributeAmount(map.get(DARK.damage)) + (isMainHandUse? entity.getAttributeValue(DARK.damage):0)));
                h.setHoly((float) (getAttributeAmount(map.get(HOLY.damage)) + (isMainHandUse? entity.getAttributeValue(HOLY.damage):0)));
                h.setPhys((float) (getAttributeAmount(map.get(Attributes.ATTACK_DAMAGE)) + (isMainHandUse? entity.getAttributeValue(Attributes.ATTACK_DAMAGE):0)));
                h.setPen((float) (getAttributeAmount(map.get(IDFAttributes.PENETRATING.get())) + (isMainHandUse? entity.getAttributeValue(IDFAttributes.PENETRATING.get()):0)));
                h.setCrit(((getAttributeAmount(map.get(IDFAttributes.CRIT_CHANCE.get())) + (isMainHandUse? entity.getAttributeValue(IDFAttributes.CRIT_CHANCE.get()):0))*0.01 > entity.getRandom().nextDouble()) && entity instanceof Player);
                h.setCritDmg((float) (getAttributeAmount(map.get(IDFAttributes.CRIT_DAMAGE.get())) + (isMainHandUse? entity.getAttributeValue(IDFAttributes.CRIT_DAMAGE.get()):0)));
                h.setLifesteal((float) (getAttributeAmount(map.get(IDFAttributes.LIFESTEAL.get())) + (isMainHandUse? entity.getAttributeValue(IDFAttributes.LIFESTEAL.get()):0)));
                h.setWeight((float) (getAttributeAmount(map.get(IDFAttributes.FORCE.get())) + (isMainHandUse? entity.getAttributeValue(IDFAttributes.FORCE.get()):0)));
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
                h.setCrit((entity.getAttributeValue(IDFAttributes.CRIT_CHANCE.get()))*0.01 > entity.getRandom().nextDouble() && entity instanceof Player);
                h.setCritDmg((float) (entity.getAttributeValue(IDFAttributes.CRIT_DAMAGE.get())));
                h.setLifesteal((float) entity.getAttributeValue(IDFAttributes.LIFESTEAL.get()));
                h.setWeight((float) entity.getAttributeValue(IDFAttributes.FORCE.get()));
                h.setDamageClass(item.hasTag() ?
                        item.getTag().contains("idf.damage_class") ? item.getTag().getString("idf.damage_class") : "pierce" : "pierce");
            });
        }
    }

    @SubscribeEvent
    public static void setMobRangedHelpers(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity instanceof Player) return;
        if (entity.tickCount % 50 == 0) {
            ItemStack item = entity.getMainHandItem();
            if (item.hasTag()) {
                if (item.getTag().getBoolean(CommonData.RANGED_TAG)) {
                    Multimap<Attribute, AttributeModifier> map = item.getAttributeModifiers(LivingEntity.getEquipmentSlotForItem(item));
                    entity.getCapability(ArrowHelperProvider.PROJECTILE_HELPER).ifPresent(h -> {
                        h.setFire((float) (getAttributeAmount(map.get(FIRE.damage)) + entity.getAttributeValue(FIRE.damage)));
                        h.setWater((float) (getAttributeAmount(map.get(WATER.damage)) + entity.getAttributeValue(WATER.damage)));
                        h.setLightning((float) (getAttributeAmount(map.get(LIGHTNING.damage)) + entity.getAttributeValue(LIGHTNING.damage)));
                        h.setMagic((float) (getAttributeAmount(map.get(MAGIC.damage)) + entity.getAttributeValue(MAGIC.damage)));
                        h.setDark((float) (getAttributeAmount(map.get(DARK.damage)) + entity.getAttributeValue(DARK.damage)));
                        h.setHoly((float) (getAttributeAmount(map.get(HOLY.damage)) + entity.getAttributeValue(HOLY.damage)));
                        h.setPhys((float) (getAttributeAmount(map.get(Attributes.ATTACK_DAMAGE)) + entity.getAttributeValue(Attributes.ATTACK_DAMAGE)));
                        h.setPen((float) (getAttributeAmount(map.get(IDFAttributes.PENETRATING.get())) + entity.getAttributeValue(IDFAttributes.PENETRATING.get())));
                        h.setCrit(false);
                        h.setCritDmg((float) (getAttributeAmount(map.get(IDFAttributes.CRIT_DAMAGE.get())) + entity.getAttributeValue(IDFAttributes.CRIT_DAMAGE.get())));
                        h.setLifesteal((float) (getAttributeAmount(map.get(IDFAttributes.LIFESTEAL.get())) + entity.getAttributeValue(IDFAttributes.LIFESTEAL.get())));
                        h.setWeight((float) (getAttributeAmount(map.get(IDFAttributes.FORCE.get())) + entity.getAttributeValue(IDFAttributes.FORCE.get())));
                        h.setDamageClass(item.hasTag() ?
                                item.getTag().contains("idf.damage_class") ? item.getTag().getString("idf.damage_class") : "pierce" : "pierce");
                    });
                } else if (item.getTag().getBoolean(CommonData.THROWN_TAG)) {
                    entity.getCapability(TridentHelperProvider.PROJECTILE_HELPER).ifPresent(h -> {
                        h.setFire((float) entity.getAttributeValue(IDFAttributes.FIRE_DAMAGE.get()));
                        h.setWater((float) entity.getAttributeValue(IDFAttributes.WATER_DAMAGE.get()));
                        h.setLightning((float) entity.getAttributeValue(IDFAttributes.LIGHTNING_DAMAGE.get()));
                        h.setMagic((float) entity.getAttributeValue(IDFAttributes.MAGIC_DAMAGE.get()));
                        h.setDark((float) entity.getAttributeValue(IDFAttributes.DARK_DAMAGE.get()));
                        h.setHoly((float) entity.getAttributeValue(IDFElement.HOLY.damage));
                        h.setPhys((float) entity.getAttributeValue(Attributes.ATTACK_DAMAGE));
                        h.setPen((float) entity.getAttributeValue(IDFAttributes.PENETRATING.get()));
                        h.setCrit(false);
                        h.setCritDmg((float) (entity.getAttributeValue(IDFAttributes.CRIT_DAMAGE.get())));
                        h.setLifesteal((float) entity.getAttributeValue(IDFAttributes.LIFESTEAL.get()));
                        h.setWeight((float) entity.getAttributeValue(IDFAttributes.FORCE.get()));
                        h.setDamageClass(item.hasTag() ?
                                item.getTag().contains("idf.damage_class") ? item.getTag().getString("idf.damage_class") : "pierce" : "pierce");
                    });
                }
            }
        }
    }

}
