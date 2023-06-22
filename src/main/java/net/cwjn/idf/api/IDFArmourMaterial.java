package net.cwjn.idf.api;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.LazyLoadedValue;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Map;
import java.util.function.Supplier;

public class IDFArmourMaterial implements ArmorMaterial {

    private static final int[] HEALTH_PER_SLOT = new int[]{13, 15, 16, 11};
    private final String name;
    private final int durabilityMultiplier;
    private final double[] weight, physicalDefence, strike, pierce, slash;
    private final Map<Attribute, AttributeModifier> bonusAttributes;
    private final int enchantmentValue;
    private final SoundEvent sound;
    private final float knockbackResistance;
    private final LazyLoadedValue<Ingredient> repairIngredient;

    public IDFArmourMaterial(String name, int durabilityMult, double[] weight, double[] physicalDefence, float KBR, double[] str, double[] prc, double[] sls,
                             int enchantability, SoundEvent equipSound, Supplier<Ingredient> repairIngredient,  Map<Attribute, AttributeModifier> bonusAttributes) {
        this.name = name;
        this.durabilityMultiplier = durabilityMult;
        this.weight = weight;
        this.physicalDefence = physicalDefence;
        this.bonusAttributes = bonusAttributes;
        this.enchantmentValue = enchantability;
        this.sound = equipSound;
        this.knockbackResistance = KBR;
        this.repairIngredient = new LazyLoadedValue<>(repairIngredient);
        strike = str;
        pierce = prc;
        slash = sls;
    }

    public int getDurabilityForSlot(EquipmentSlot slot) {
        return HEALTH_PER_SLOT[slot.getIndex()] * this.durabilityMultiplier;
    }

    @Deprecated
    /*
    Use getPhysicalDefenceForSlot instead.
     */
    public int getDefenseForSlot(EquipmentSlot slot) {
        throw new IllegalStateException("IDFArmourMaterial USED FOR NON-IDFArmourItem CLASS!");
    }

    public double getPhysicalDefenceForSlot(EquipmentSlot slot) {
        return this.physicalDefence[slot.getIndex()];
    }

    public double getWeightForSlot(EquipmentSlot slot) {
        return this.weight[slot.getIndex()];
    }

    public int getEnchantmentValue() {
        return this.enchantmentValue;
    }

    public SoundEvent getEquipSound() {
        return this.sound;
    }

    public Ingredient getRepairIngredient() {
        return this.repairIngredient.get();
    }

    public String getName() {
        return this.name;
    }

    @Deprecated
    public float getToughness() {
        throw new IllegalStateException("IDFArmourMaterial USED FOR NON-IDFArmourItem CLASS!");
    }

    public float getKnockbackResistance() {
        return this.knockbackResistance;
    }

    public double getStrikeForSlot(EquipmentSlot slot) {
        return this.strike[slot.getIndex()];
    }

    public double getPierceForSlot(EquipmentSlot slot) {
        return this.pierce[slot.getIndex()];
    }

    public double getSlashForSlot(EquipmentSlot slot) {
        return this.slash[slot.getIndex()];
    }

    public Map<Attribute, AttributeModifier> getBonusAttributes() {
        return bonusAttributes;
    }
}
