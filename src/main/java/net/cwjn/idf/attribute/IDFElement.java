package net.cwjn.idf.attribute;

import net.minecraft.world.entity.ai.attributes.Attribute;

public enum IDFElement {

    FIRE(IDFAttributes.FIRE_DAMAGE.get(), IDFAttributes.FIRE_DEFENCE.get()),
    WATER(IDFAttributes.WATER_DAMAGE.get(), IDFAttributes.WATER_DEFENCE.get()),
    LIGHTNING(IDFAttributes.LIGHTNING_DAMAGE.get(), IDFAttributes.LIGHTNING_DEFENCE.get()),
    MAGIC(IDFAttributes.MAGIC_DAMAGE.get(), IDFAttributes.MAGIC_DEFENCE.get()),
    DARK(IDFAttributes.DARK_DAMAGE.get(), IDFAttributes.DARK_DEFENCE.get()),
    HOLY(IDFAttributes.HOLY_DAMAGE.get(), IDFAttributes.HOLY_DEFENCE.get());

    IDFElement(Attribute dmg, Attribute def) {
        damage = dmg;
        defence = def;
    }

    public final Attribute damage, defence;

}
