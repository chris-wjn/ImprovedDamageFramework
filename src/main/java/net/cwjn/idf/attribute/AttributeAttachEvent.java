package net.cwjn.idf.attribute;

import net.cwjn.idf.config.json.Config;
import net.cwjn.idf.config.json.records.EntityData;
import net.cwjn.idf.data.CommonData;
import net.cwjn.idf.util.Util;
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
            EntityData data = CommonData.LOGICAL_ENTITY_MAP.get(Util.getEntityRegistryName(entityType));
            if (data != null) {
                if (!event.has(entityType, FIRE.damage)) event.add(entityType, FIRE.damage, data.oData().fDmg());
                if (!event.has(entityType, WATER.damage)) event.add(entityType, WATER.damage, data.oData().wDmg());
                if (!event.has(entityType, LIGHTNING.damage)) event.add(entityType, LIGHTNING.damage, data.oData().lDmg());
                if (!event.has(entityType, MAGIC.damage)) event.add(entityType, MAGIC.damage, data.oData().mDmg());
                if (!event.has(entityType, DARK.damage)) event.add(entityType, DARK.damage, data.oData().dDmg());
                if (!event.has(entityType, HOLY.damage)) event.add(entityType, HOLY.damage, data.oData().hDmg());
                //resistances
                if (!event.has(entityType, FIRE.defence)) event.add(entityType, FIRE.defence, data.dData().fDef());
                if (!event.has(entityType, WATER.defence)) event.add(entityType, WATER.defence, data.dData().wDef());
                if (!event.has(entityType, LIGHTNING.defence)) event.add(entityType, LIGHTNING.defence, data.dData().lDef());
                if (!event.has(entityType, MAGIC.defence)) event.add(entityType, MAGIC.defence, data.dData().mDef());
                if (!event.has(entityType, DARK.defence)) event.add(entityType, DARK.defence, data.dData().dDef());
                if (!event.has(entityType, HOLY.defence)) event.add(entityType, HOLY.defence, data.dData().hDef());
                //DAMAGE CLASS MULTIPLIERS
                if (!event.has(entityType, IDFAttributes.STRIKE_MULT.get())) event.add(entityType, IDFAttributes.STRIKE_MULT.get(), data.dData().str());
                if (!event.has(entityType, IDFAttributes.PIERCE_MULT.get())) event.add(entityType, IDFAttributes.PIERCE_MULT.get(), data.dData().prc());
                if (!event.has(entityType, IDFAttributes.SLASH_MULT.get())) event.add(entityType, IDFAttributes.SLASH_MULT.get(), data.dData().sls());
                //AUXILIARY
                if (!event.has(entityType, IDFAttributes.EVASION.get())) event.add(entityType, IDFAttributes.EVASION.get(), data.dData().eva());
                if (!event.has(entityType, IDFAttributes.LIFESTEAL.get())) event.add(entityType, IDFAttributes.LIFESTEAL.get(), data.oData().ls());
                if (!event.has(entityType, IDFAttributes.PENETRATING.get())) event.add(entityType, IDFAttributes.PENETRATING.get(), data.oData().pen());
                if (!event.has(entityType, IDFAttributes.FORCE.get())) event.add(entityType, IDFAttributes.FORCE.get(), data.oData().force());
                if (!event.has(entityType, IDFAttributes.ACCURACY.get())) event.add(entityType, IDFAttributes.ACCURACY.get(), data.oData().accuracy());
                if (!event.has(entityType, IDFAttributes.CRIT_CHANCE.get())) event.add(entityType, IDFAttributes.CRIT_CHANCE.get(), data.oData().crit());
                if (!event.has(entityType, IDFAttributes.CRIT_DAMAGE.get())) event.add(entityType, IDFAttributes.CRIT_DAMAGE.get(), data.oData().critDmg());
            } else {
                //damage types
                event.add(entityType, IDFAttributes.FIRE_DAMAGE.get());
                event.add(entityType, IDFAttributes.WATER_DAMAGE.get());
                event.add(entityType, IDFAttributes.LIGHTNING_DAMAGE.get());
                event.add(entityType, IDFAttributes.MAGIC_DAMAGE.get());
                event.add(entityType, IDFAttributes.DARK_DAMAGE.get());
                event.add(entityType, HOLY.damage);
                //resistances
                event.add(entityType, IDFAttributes.FIRE_DEFENCE.get());
                event.add(entityType, IDFAttributes.WATER_DEFENCE.get());
                event.add(entityType, IDFAttributes.LIGHTNING_DEFENCE.get());
                event.add(entityType, IDFAttributes.MAGIC_DEFENCE.get());
                event.add(entityType, IDFAttributes.DARK_DEFENCE.get());
                event.add(entityType, HOLY.defence);
                //DAMAGE CLASS MULTIPLIERS
                event.add(entityType, IDFAttributes.STRIKE_MULT.get());
                event.add(entityType, IDFAttributes.PIERCE_MULT.get());
                event.add(entityType, IDFAttributes.SLASH_MULT.get());
                //AUXILIARY
                event.add(entityType, IDFAttributes.EVASION.get());
                event.add(entityType, IDFAttributes.LIFESTEAL.get());
                event.add(entityType, IDFAttributes.PENETRATING.get());
                event.add(entityType, IDFAttributes.FORCE.get());
                event.add(entityType, IDFAttributes.ACCURACY.get());
                event.add(entityType, IDFAttributes.CRIT_CHANCE.get());
                event.add(entityType, IDFAttributes.CRIT_DAMAGE.get());
            }
        }
    }

}
