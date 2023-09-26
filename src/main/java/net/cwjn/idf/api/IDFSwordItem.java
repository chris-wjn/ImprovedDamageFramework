package net.cwjn.idf.api;

import com.google.common.collect.ImmutableMultimap;
import net.cwjn.idf.attribute.IDFAttributes;
import net.cwjn.idf.attribute.IDFElement;
import net.cwjn.idf.util.ItemInterface;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import oshi.util.tuples.Pair;

import java.util.Map;

import static net.cwjn.idf.util.Util.UUID_IDF_CUSTOM_ITEMS;
import static net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADDITION;

public class IDFSwordItem extends SwordItem implements IDFCustomEquipment {

    @SafeVarargs
    public IDFSwordItem(Tier tier, String damageClass, double physicalDamage, double force, double speed, Properties p, Pair<Attribute, AttributeModifier>... bonusAttributes) {
        super(tier, (int) physicalDamage, (float) speed, p);
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(UUID_IDF_CUSTOM_ITEMS[0], "base_physical_damage", physicalDamage+tier.getAttackDamageBonus(), ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(UUID_IDF_CUSTOM_ITEMS[0], "base_attack_speed", speed+tier.getSpeed(), ADDITION));
        builder.put(IDFAttributes.FORCE.get(), new AttributeModifier(UUID_IDF_CUSTOM_ITEMS[0], "base_force", force, ADDITION));
        ((ItemInterface) this).setDamageClass(damageClass);
        for (Pair<Attribute, AttributeModifier> pair : bonusAttributes) {
            builder.put(pair.getA(), pair.getB());
        }
        if (tier instanceof IDFTier idfTier) {
            for (Map.Entry<Attribute, AttributeModifier> entry : idfTier.getBonusAttributes().entrySet()) {
                builder.put(entry.getKey(), entry.getValue());
            }
        }
        this.defaultModifiers = builder.build();
    }

    @Override
    public float getDamage() {
        float returnDamage = 0;
        returnDamage += this.defaultModifiers.get(Attributes.ATTACK_DAMAGE).stream().mapToDouble(AttributeModifier::getAmount).sum();
        returnDamage += this.defaultModifiers.get(IDFElement.FIRE.damage).stream().mapToDouble(AttributeModifier::getAmount).sum();
        returnDamage += this.defaultModifiers.get(IDFElement.WATER.damage).stream().mapToDouble(AttributeModifier::getAmount).sum();
        returnDamage += this.defaultModifiers.get(IDFElement.LIGHTNING.damage).stream().mapToDouble(AttributeModifier::getAmount).sum();
        returnDamage += this.defaultModifiers.get(IDFElement.MAGIC.damage).stream().mapToDouble(AttributeModifier::getAmount).sum();
        returnDamage += this.defaultModifiers.get(IDFElement.DARK.damage).stream().mapToDouble(AttributeModifier::getAmount).sum();
        returnDamage += this.defaultModifiers.get(IDFElement.HOLY.damage).stream().mapToDouble(AttributeModifier::getAmount).sum();
        return returnDamage;
    }

}
