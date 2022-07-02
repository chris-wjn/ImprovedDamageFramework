package net.cwjn.idf.attribute;

import net.cwjn.idf.ImprovedDamageFramework;
import net.cwjn.idf.config.json.Config;
import net.cwjn.idf.config.json.data.EntityData;
import net.cwjn.idf.config.json.JSONHandler;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class AttributeAttachEvent {

    @SubscribeEvent
    public static void attachAttributes(EntityAttributeModificationEvent event) {
        Config.init();
        for (EntityType<? extends LivingEntity> entityType : event.getTypes()) {
            EntityData data = JSONHandler.getEntityData(entityType.getRegistryName());
            if (data != null) {
                event.add(entityType, IDFAttributes.FIRE_DAMAGE.get(), data.getFireDamage());
                event.add(entityType, IDFAttributes.WATER_DAMAGE.get(), data.getWaterDamage());
                event.add(entityType, IDFAttributes.LIGHTNING_DAMAGE.get(), data.getLightningDamage());
                event.add(entityType, IDFAttributes.MAGIC_DAMAGE.get(), data.getMagicDamage());
                event.add(entityType, IDFAttributes.DARK_DAMAGE.get(), data.getDarkDamage());
                //resistances
                event.add(entityType, IDFAttributes.FIRE_RESISTANCE.get(), data.getFireResistance());
                event.add(entityType, IDFAttributes.WATER_RESISTANCE.get(), data.getWaterResistance());
                event.add(entityType, IDFAttributes.LIGHTNING_RESISTANCE.get(), data.getLightningResistance());
                event.add(entityType, IDFAttributes.MAGIC_RESISTANCE.get(), data.getMagicResistance());
                event.add(entityType, IDFAttributes.DARK_RESISTANCE.get(), data.getDarkResistance());
                //DAMAGE CLASS MULTIPLIERS
                event.add(entityType, IDFAttributes.STRIKE_MULT.get(), data.getStrikeMult());
                event.add(entityType, IDFAttributes.PIERCE_MULT.get(), data.getPierceMult());
                event.add(entityType, IDFAttributes.SLASH_MULT.get(), data.getSlashMult());
                event.add(entityType, IDFAttributes.CRUSH_MULT.get(), data.getCrushMult());
                event.add(entityType, IDFAttributes.GENERIC_MULT.get(), data.getGenericMult());
                //AUXILIARY
                event.add(entityType, IDFAttributes.EVASION.get(), data.getEvasion());
                event.add(entityType, IDFAttributes.LIFESTEAL.get(), data.getLifesteal());
                event.add(entityType, IDFAttributes.PENETRATING.get(), data.getArmourPenetration());
                event.add(entityType, IDFAttributes.CRIT_CHANCE.get());
            } else {
                //damage types
                event.add(entityType, IDFAttributes.FIRE_DAMAGE.get());
                event.add(entityType, IDFAttributes.WATER_DAMAGE.get());
                event.add(entityType, IDFAttributes.LIGHTNING_DAMAGE.get());
                event.add(entityType, IDFAttributes.MAGIC_DAMAGE.get());
                event.add(entityType, IDFAttributes.DARK_DAMAGE.get());
                //resistances
                event.add(entityType, IDFAttributes.FIRE_RESISTANCE.get());
                event.add(entityType, IDFAttributes.WATER_RESISTANCE.get());
                event.add(entityType, IDFAttributes.LIGHTNING_RESISTANCE.get());
                event.add(entityType, IDFAttributes.MAGIC_RESISTANCE.get());
                event.add(entityType, IDFAttributes.DARK_RESISTANCE.get());
                //DAMAGE CLASS MULTIPLIERS
                event.add(entityType, IDFAttributes.STRIKE_MULT.get());
                event.add(entityType, IDFAttributes.PIERCE_MULT.get());
                event.add(entityType, IDFAttributes.SLASH_MULT.get());
                event.add(entityType, IDFAttributes.CRUSH_MULT.get());
                event.add(entityType, IDFAttributes.GENERIC_MULT.get());
                //AUXILIARY
                event.add(entityType, IDFAttributes.EVASION.get());
                event.add(entityType, IDFAttributes.LIFESTEAL.get());
                event.add(entityType, IDFAttributes.PENETRATING.get());
                event.add(entityType, IDFAttributes.CRIT_CHANCE.get());
            }
        }
    }

}
