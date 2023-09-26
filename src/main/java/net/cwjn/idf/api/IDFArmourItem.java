package net.cwjn.idf.api;

import com.google.common.collect.ImmutableMultimap;
import net.cwjn.idf.attribute.IDFAttributes;
import net.cwjn.idf.attribute.IDFElement;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import oshi.util.tuples.Pair;

import java.util.Map;

import static net.cwjn.idf.util.Util.UUID_BASE_STAT_ADDITION;
import static net.cwjn.idf.util.Util.UUID_IDF_CUSTOM_ITEMS;

public class IDFArmourItem extends ArmorItem implements IDFCustomEquipment {

    @SafeVarargs
    public IDFArmourItem(IDFArmourMaterial material, EquipmentSlot slot,
                         double physicalDefence, double weight, double KBR,
                         double strike, double pierce, double slash,
                         Properties properties, Pair<Attribute, AttributeModifier>... bonusAttributes) {
        super(material, slot, properties);
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ARMOR, new AttributeModifier(UUID_IDF_CUSTOM_ITEMS[slot.getIndex()], "base_physical_defence", physicalDefence+material.getPhysicalDefenceForSlot(slot), AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(UUID_IDF_CUSTOM_ITEMS[slot.getIndex()], "base_weight", weight+material.getWeightForSlot(slot), AttributeModifier.Operation.ADDITION));
        if (this.knockbackResistance > 0) {
            builder.put(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(UUID_IDF_CUSTOM_ITEMS[slot.getIndex()], "base_knockback_resistance", KBR+material.getKnockbackResistance(), AttributeModifier.Operation.ADDITION));
        }
        builder.put(IDFAttributes.STRIKE_MULT.get(), new AttributeModifier(UUID_IDF_CUSTOM_ITEMS[slot.getIndex()], "base_strike", strike+material.getStrikeForSlot(slot), AttributeModifier.Operation.ADDITION));
        builder.put(IDFAttributes.PIERCE_MULT.get(), new AttributeModifier(UUID_IDF_CUSTOM_ITEMS[slot.getIndex()], "base_pierce", pierce+material.getPierceForSlot(slot), AttributeModifier.Operation.ADDITION));
        builder.put(IDFAttributes.SLASH_MULT.get(), new AttributeModifier(UUID_IDF_CUSTOM_ITEMS[slot.getIndex()], "base_slash", slash+material.getSlashForSlot(slot), AttributeModifier.Operation.ADDITION));
        for (Pair<Attribute, AttributeModifier> pair : bonusAttributes) {
            builder.put(pair.getA(), pair.getB());
        }
        for (Map.Entry<Attribute, AttributeModifier> entry : material.getBonusAttributes().entrySet()) {
            builder.put(entry.getKey(), entry.getValue());
        }
        this.defaultModifiers = builder.build();
    }

    @Override
    public int getDefense() {
        float returnDefence = 0;
        returnDefence += this.defaultModifiers.get(Attributes.ARMOR).stream().mapToDouble(AttributeModifier::getAmount).sum();
        returnDefence += this.defaultModifiers.get(IDFElement.FIRE.defence).stream().mapToDouble(AttributeModifier::getAmount).sum();
        returnDefence += this.defaultModifiers.get(IDFElement.WATER.defence).stream().mapToDouble(AttributeModifier::getAmount).sum();
        returnDefence += this.defaultModifiers.get(IDFElement.LIGHTNING.defence).stream().mapToDouble(AttributeModifier::getAmount).sum();
        returnDefence += this.defaultModifiers.get(IDFElement.MAGIC.defence).stream().mapToDouble(AttributeModifier::getAmount).sum();
        returnDefence += this.defaultModifiers.get(IDFElement.DARK.defence).stream().mapToDouble(AttributeModifier::getAmount).sum();
        returnDefence += this.defaultModifiers.get(IDFElement.HOLY.defence).stream().mapToDouble(AttributeModifier::getAmount).sum();
        return (int) returnDefence;
    }

    @Deprecated
    /*
    Use getWeight instead.
     */
    @Override
    public float getToughness() {
        return 0f;
    }

    public double getWeight() {
        return this.defaultModifiers.get(Attributes.ARMOR_TOUGHNESS).stream().mapToDouble(AttributeModifier::getAmount).sum();
    }

}
