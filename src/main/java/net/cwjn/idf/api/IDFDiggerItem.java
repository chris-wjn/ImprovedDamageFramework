package net.cwjn.idf.api;

import com.google.common.collect.ImmutableMultimap;
import net.cwjn.idf.util.Util;
import net.cwjn.idf.attribute.IDFAttributes;
import net.cwjn.idf.config.json.JSONHandler;
import net.cwjn.idf.config.json.data.DamageData;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.block.Block;

import static net.cwjn.idf.util.UUIDs.*;
import static net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADDITION;

public class IDFDiggerItem extends DiggerItem implements IDFDamagingItem{

    private final String damageClass;
    private final double fireDamage, waterDamage, lightningDamage, magicDamage, darkDamage, crit, weight, pen, lifesteal;

    public IDFDiggerItem(Tier tier, float baseAS,
                         float fire, float water, float lightning, float magic, float dark, int baseAD,
                         float crit, float weight, float pen, float lifesteal, String dc,
                         TagKey<Block> tag, Properties p) {
        super(baseAD, baseAS, tier, tag, p);
        damageClass = dc;
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        DamageData data = JSONHandler.getDamageData(Util.getItemRegistryName(this));
        double speed;
        if (tier instanceof IDFTier modTier) {
            if (data != null) {
                speed = modTier.getSpeed() + data.getSpeed() + baseAS;
                fireDamage = modTier.getFire() + data.getFire() + fire;
                waterDamage = modTier.getWater() + data.getWater() + water;
                lightningDamage = modTier.getLightning() + data.getLightning() + lightning;
                magicDamage = modTier.getMagic() + data.getMagic() + magic;
                darkDamage = modTier.getDark() + data.getDark() + dark;
                this.crit = modTier.getCrit() + data.getCritChance() + crit;
                this.weight = modTier.getWeight() + data.getWeight() + weight;
                this.pen = modTier.getPen() + data.getArmourPenetration() + pen;
                this.lifesteal = modTier.getLifesteal() + data.getLifesteal() +  lifesteal;
            } else {
                speed = modTier.getSpeed() + baseAS;
                fireDamage = modTier.getFire() + fire;
                waterDamage = modTier.getWater() + water;
                lightningDamage = modTier.getLightning() + lightning;
                magicDamage = modTier.getMagic() + magic;
                darkDamage = modTier.getDark() + dark;
                this.crit = modTier.getCrit() + crit;
                this.weight = modTier.getWeight() + weight;
                this.pen = modTier.getPen() + pen;
                this.lifesteal = modTier.getLifesteal() + lifesteal;
            }
        } else  {
            if (data != null) {
                speed = data.getSpeed() + baseAS;
                fireDamage = data.getFire() + fire;
                waterDamage = data.getWater() + water;
                lightningDamage = data.getLightning() + lightning;
                magicDamage = data.getMagic() + magic;
                darkDamage = data.getDark() + dark;
                this.crit = data.getCritChance() + crit;
                this.weight = data.getWeight() + weight;
                this.pen = data.getArmourPenetration() + pen;
                this.lifesteal = data.getLifesteal() + lifesteal;
            } else {
                speed = baseAS;
                fireDamage = fire;
                waterDamage = water;
                lightningDamage = lightning;
                magicDamage = magic;
                darkDamage = dark;
                this.crit = crit;
                this.weight = weight;
                this.pen = pen;
                this.lifesteal = lifesteal;
            }
        }
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", this.getAttackDamage(), AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", speed, AttributeModifier.Operation.ADDITION));
        builder.put(IDFAttributes.FIRE_DAMAGE.get(), new AttributeModifier(baseFireDamage, "Weapon modifier", fireDamage, ADDITION));
        builder.put(IDFAttributes.WATER_DAMAGE.get(), new AttributeModifier(baseWaterDamage, "Weapon modifier", waterDamage, ADDITION));
        builder.put(IDFAttributes.LIGHTNING_DAMAGE.get(), new AttributeModifier(baseLightningDamage, "Weapon modifier", lightningDamage, ADDITION));
        builder.put(IDFAttributes.MAGIC_DAMAGE.get(), new AttributeModifier(baseMagicDamage, "Weapon modifier", magicDamage, ADDITION));
        builder.put(IDFAttributes.DARK_DAMAGE.get(), new AttributeModifier(baseDarkDamage, "Weapon modifier", darkDamage, ADDITION));
        builder.put(IDFAttributes.CRIT_CHANCE.get(), new AttributeModifier(baseCrit, "Weapon modifier", this.crit, ADDITION));
        builder.put(IDFAttributes.WEIGHT.get(), new AttributeModifier(baseWeight, "Weapon modifier", this.weight, ADDITION));
        builder.put(IDFAttributes.PENETRATING.get(), new AttributeModifier(basePen, "Weapon modifier", this.pen, ADDITION));
        builder.put(IDFAttributes.LIFESTEAL.get(), new AttributeModifier(baseLifesteal, "Weapon modifier", this.lifesteal, ADDITION));
        this.defaultModifiers = builder.build();
    }

    public String getDamageClass() {
        return damageClass;
    }

    @Override
    public float getAttackDamage() {
        return (float) (this.attackDamageBaseline + fireDamage + waterDamage + lightningDamage + magicDamage + darkDamage);
    }

    public double getFireDamage() {
        return fireDamage;
    }

    public double getWaterDamage() {
        return waterDamage;
    }

    public double getLightningDamage() {
        return lightningDamage;
    }

    public double getMagicDamage() {
        return magicDamage;
    }

    public double getDarkDamage() {
        return darkDamage;
    }

    public double getCrit() {
        return crit;
    }

    public double getWeight() {
        return weight;
    }

    public double getPen() {
        return pen;
    }

    public double getLifesteal() {
        return lifesteal;
    }

}
