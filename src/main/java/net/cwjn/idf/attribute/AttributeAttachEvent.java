package net.cwjn.idf.attribute;

import net.cwjn.idf.util.Util;
import net.cwjn.idf.config.json.Config;
import net.cwjn.idf.config.json.JSONHandler;
import net.cwjn.idf.config.json.data.EntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import static net.cwjn.idf.attribute.IDFElement.*;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class AttributeAttachEvent {

    @SubscribeEvent
    public static void attachAttributes(EntityAttributeModificationEvent event) {
        Config.init();
        for (EntityType<? extends LivingEntity> entityType : event.getTypes()) {
            EntityData data = JSONHandler.getEntityData(Util.getEntityRegistryName(entityType));
            if (data != null) {
                event.add(entityType, FIRE.damage, data.fireDamage());
                event.add(entityType, IDFAttributes.WATER_DAMAGE.get(), data.waterDamage());
                event.add(entityType, IDFAttributes.LIGHTNING_DAMAGE.get(), data.lightningDamage());
                event.add(entityType, IDFAttributes.MAGIC_DAMAGE.get(), data.magicDamage());
                event.add(entityType, IDFAttributes.DARK_DAMAGE.get(), data.darkDamage());
                //resistances
                event.add(entityType, IDFAttributes.FIRE_RESISTANCE.get(), data.fireResistance());
                event.add(entityType, IDFAttributes.WATER_RESISTANCE.get(), data.waterResistance());
                event.add(entityType, IDFAttributes.LIGHTNING_RESISTANCE.get(), data.lightningResistance());
                event.add(entityType, IDFAttributes.MAGIC_RESISTANCE.get(), data.magicResistance());
                event.add(entityType, IDFAttributes.DARK_RESISTANCE.get(), data.darkResistance());
                //DAMAGE CLASS MULTIPLIERS
                event.add(entityType, IDFAttributes.STRIKE_MULT.get(), data.strikeMultiplier());
                event.add(entityType, IDFAttributes.PIERCE_MULT.get(), data.pierceMultiplier());
                event.add(entityType, IDFAttributes.SLASH_MULT.get(), data.slashMultiplier());
                //AUXILIARY
                event.add(entityType, IDFAttributes.EVASION.get(), data.evasion());
                event.add(entityType, IDFAttributes.LIFESTEAL.get(), data.lifesteal());
                event.add(entityType, IDFAttributes.PENETRATING.get(), data.armourPenetration());
                event.add(entityType, IDFAttributes.FORCE.get(), data.force());
                event.add(entityType, IDFAttributes.CRIT_CHANCE.get(), data.criticalChance());
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
                //AUXILIARY
                event.add(entityType, IDFAttributes.EVASION.get());
                event.add(entityType, IDFAttributes.LIFESTEAL.get());
                event.add(entityType, IDFAttributes.PENETRATING.get());
                event.add(entityType, IDFAttributes.FORCE.get());
                event.add(entityType, IDFAttributes.CRIT_CHANCE.get());
            }
        }
    }

}
