package net.cwjn.idf.attribute;

import net.minecraft.world.entity.ai.attributes.Attribute;

public enum IDFElement {

    FIRE(IDFAttributes.FIRE_DAMAGE.get(), IDFAttributes.FIRE_RESISTANCE.get()),
    WATER(IDFAttributes.WATER_DAMAGE.get(), IDFAttributes.WATER_RESISTANCE.get()),
    LIGHTNING(IDFAttributes.LIGHTNING_DAMAGE.get(), IDFAttributes.LIGHTNING_RESISTANCE.get()),
    MAGIC(IDFAttributes.MAGIC_DAMAGE.get(), IDFAttributes.MAGIC_RESISTANCE.get()),
    DARK(IDFAttributes.DARK_DAMAGE.get(), IDFAttributes.DARK_RESISTANCE.get()),
    HOLY(IDFAttributes.HOLY_DAMAGE.get(), IDFAttributes.HOLY_RESISTANCE.get());

    IDFElement(Attribute dmg, Attribute res) {
        damage = dmg;
        resistance = res;
    }

    public final Attribute damage, resistance;

}
