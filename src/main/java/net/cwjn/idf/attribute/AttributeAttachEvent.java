package net.cwjn.idf.attribute;

import net.cwjn.idf.data.CommonData;
import net.cwjn.idf.util.Util;
import net.cwjn.idf.config.json.Config;
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
            EntityData data = CommonData.getEntityData(Util.getEntityRegistryName(entityType));
            if (data != null) {
                event.add(entityType, FIRE.damage, data.oData().fDmg());
                event.add(entityType, WATER.damage, data.oData().wDmg());
                event.add(entityType, LIGHTNING.damage, data.oData().lDmg());
                event.add(entityType, MAGIC.damage, data.oData().mDmg());
                event.add(entityType, DARK.damage, data.oData().dDmg());
                event.add(entityType, HOLY.damage, data.oData().hDmg());
                //resistances
                event.add(entityType, FIRE.resistance, data.dData().fRes());
                event.add(entityType, WATER.resistance, data.dData().wRes());
                event.add(entityType, LIGHTNING.resistance, data.dData().lRes());
                event.add(entityType, MAGIC.resistance, data.dData().mRes());
                event.add(entityType, DARK.resistance, data.dData().dRes());
                event.add(entityType, HOLY.resistance, data.dData().hRes());
                //DAMAGE CLASS MULTIPLIERS
                event.add(entityType, IDFAttributes.STRIKE_MULT.get(), data.dData().str());
                event.add(entityType, IDFAttributes.PIERCE_MULT.get(), data.dData().prc());
                event.add(entityType, IDFAttributes.SLASH_MULT.get(), data.dData().sls());
                //AUXILIARY
                event.add(entityType, IDFAttributes.EVASION.get(), data.dData().eva());
                event.add(entityType, IDFAttributes.LIFESTEAL.get(), data.oData().ls());
                event.add(entityType, IDFAttributes.PENETRATING.get(), data.oData().pen());
                event.add(entityType, IDFAttributes.FORCE.get(), data.oData().force());
                event.add(entityType, IDFAttributes.CRIT_CHANCE.get(), data.oData().crit());
            } else {
                //damage types
                event.add(entityType, IDFAttributes.FIRE_DAMAGE.get());
                event.add(entityType, IDFAttributes.WATER_DAMAGE.get());
                event.add(entityType, IDFAttributes.LIGHTNING_DAMAGE.get());
                event.add(entityType, IDFAttributes.MAGIC_DAMAGE.get());
                event.add(entityType, IDFAttributes.DARK_DAMAGE.get());
                event.add(entityType, HOLY.damage);
                //resistances
                event.add(entityType, IDFAttributes.FIRE_RESISTANCE.get());
                event.add(entityType, IDFAttributes.WATER_RESISTANCE.get());
                event.add(entityType, IDFAttributes.LIGHTNING_RESISTANCE.get());
                event.add(entityType, IDFAttributes.MAGIC_RESISTANCE.get());
                event.add(entityType, IDFAttributes.DARK_RESISTANCE.get());
                event.add(entityType, HOLY.resistance);
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
