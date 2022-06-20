package net.cwjn.idf.capability;

import net.cwjn.idf.attribute.AttributeRegistry;
import net.cwjn.idf.config.json.EntityData;
import net.cwjn.idf.config.json.JSONHandler;
import net.cwjn.idf.ImprovedDamageFramework;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
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
                if (!entity.getCapability(AuxiliaryProvider.AUXILIARY_DATA).isPresent()) {
                    event.addCapability(new ResourceLocation(ImprovedDamageFramework.MOD_ID, "auxiliary"), new AuxiliaryProvider());
                }
                event.addCapability(new ResourceLocation(ImprovedDamageFramework.MOD_ID, "arrow_helper"), new ArrowHelperProvider());
                event.addCapability(new ResourceLocation(ImprovedDamageFramework.MOD_ID, "trident_helper"), new TridentHelperProvider());
                entity.getCapability(AuxiliaryProvider.AUXILIARY_DATA).ifPresent(h -> h.setDamageClass(data.getDamageClass()));
                if (entity.getAttribute(AttributeRegistry.FIRE_DAMAGE.get()) == null) throw new IllegalStateException(entity.getType().getRegistryName() + " DOESN'T HAVE ATTRIBUTE INSTANCES!");
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
            if (entity instanceof Player) {
                event.addCapability(new ResourceLocation(ImprovedDamageFramework.MOD_ID, "arrow_helper"), new ArrowHelperProvider());
                event.addCapability(new ResourceLocation(ImprovedDamageFramework.MOD_ID, "trident_helper"), new TridentHelperProvider());
                event.addCapability(new ResourceLocation(ImprovedDamageFramework.MOD_ID, "auxiliary"), new AuxiliaryProvider());
            }
        }
    }

    @SubscribeEvent
    public static void playerTickEvent(TickEvent.PlayerTickEvent event) {
        Player player = event.player;
        player.getCapability(AuxiliaryProvider.AUXILIARY_DATA).ifPresent(h -> {
            if (player.getMainHandItem() != ItemStack.EMPTY) {
                ItemStack item = player.getMainHandItem();
                if (item.getOrCreateTag().contains("idf.damage_class")) {
                    h.setDamageClass(player.getMainHandItem().getTag().getString("idf.damage_class"));
                }
            } else if (player.getOffhandItem() != ItemStack.EMPTY) {
                ItemStack item = player.getOffhandItem();
                if (item.getOrCreateTag().contains("idf.damage_class")) {
                    h.setDamageClass(player.getOffhandItem().getTag().getString("idf.damage_class"));
                }
            }
        });
        player.getCapability(ArrowHelperProvider.PROJECTILE_HELPER).ifPresent(h -> {
            if (player.getMainHandItem() != ItemStack.EMPTY) {
                Item item = player.getMainHandItem().getItem();
                if (item instanceof BowItem || item instanceof ModularBowItem || item instanceof ModularCrossbowItem) {
                    h.setFire((float) player.getAttributeValue(AttributeRegistry.FIRE_DAMAGE.get()));
                    h.setWater((float) player.getAttributeValue(AttributeRegistry.WATER_DAMAGE.get()));
                    h.setLightning((float) player.getAttributeValue(AttributeRegistry.LIGHTNING_DAMAGE.get()));
                    h.setMagic((float) player.getAttributeValue(AttributeRegistry.MAGIC_DAMAGE.get()));
                    h.setDark((float) player.getAttributeValue(AttributeRegistry.DARK_DAMAGE.get()));
                    h.setPen((float) player.getAttributeValue(AttributeRegistry.PENETRATING.get()));
                    h.setCrit((float) player.getAttributeValue(AttributeRegistry.CRIT_CHANCE.get()));
                    h.setLifesteal((float) player.getAttributeValue(AttributeRegistry.LIFESTEAL.get()));
                    h.setDamageClass(player.getCapability(AuxiliaryProvider.AUXILIARY_DATA).orElseThrow(() -> new RuntimeException("player has no damage class!")).getDamageClass());
                }
            }
        });
        player.getCapability(TridentHelperProvider.PROJECTILE_HELPER).ifPresent(h -> {
            if (player.getMainHandItem() != ItemStack.EMPTY) {
                Item item = player.getMainHandItem().getItem();
                if (item instanceof TridentItem || item instanceof ItemModularHandheld && Arrays.asList(((ItemModularHandheld) item).getMajorModuleKeys()).contains("trident/trident")) {
                    h.setFire((float) player.getAttributeValue(AttributeRegistry.FIRE_DAMAGE.get()));
                    h.setWater((float) player.getAttributeValue(AttributeRegistry.WATER_DAMAGE.get()));
                    h.setLightning((float) player.getAttributeValue(AttributeRegistry.LIGHTNING_DAMAGE.get()));
                    h.setMagic((float) player.getAttributeValue(AttributeRegistry.MAGIC_DAMAGE.get()));
                    h.setDark((float) player.getAttributeValue(AttributeRegistry.DARK_DAMAGE.get()));
                    h.setPen((float) player.getAttributeValue(AttributeRegistry.PENETRATING.get()));
                    h.setCrit((float) player.getAttributeValue(AttributeRegistry.CRIT_CHANCE.get()));
                    h.setLifesteal((float) player.getAttributeValue(AttributeRegistry.LIFESTEAL.get()));
                    h.setDamageClass(player.getCapability(AuxiliaryProvider.AUXILIARY_DATA).orElseThrow(() -> new RuntimeException("player has no damage class!")).getDamageClass());
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
