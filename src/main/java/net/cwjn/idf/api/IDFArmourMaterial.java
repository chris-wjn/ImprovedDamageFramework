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
    private final double[] physicalDamage, fireDamage, waterDamage, lightningDamage, magicDamage, darkDamage, lifesteal,
            armourPenetration, knockback, criticalChance, force, attackSpeed, defense,
            physicalResistance, fireResistance, waterResistance, lightningResistance, magicResistance, darkResistance,
            maxHP, movespeed, luck, evasion,
            strike, pierce, slash;
    private final Map<Attribute, AttributeModifier> bonusAttributes;
    private final int enchantmentValue;
    private final SoundEvent sound;
    private final float knockbackResistance;
    private final LazyLoadedValue<Ingredient> repairIngredient;

    public IDFArmourMaterial(String name, int durabilityMult, double[] physicalDamage, double[] fireDamage, double[] waterDamage, double[] lightningDamage,
                             double[] magicDamage, double[] darkDamage, double[] lifesteal, double[] armourPenetration, double[] knockback, double[] criticalChance,
                             double[] force, double[] attackSpeed, double[] defense, double[] physicalResistance,
                             double[] f, double[] w, double[] l, double[] m, double[] d,
                             double[] hp, double[] ms, double[] luck, double[] evasion,
                             double[] str, double[] prc, double[] sls,
                             Map<Attribute, AttributeModifier> bonusAttributes, int enchantability, SoundEvent equipSound, float KBR, Supplier<Ingredient> repairIngredient) {
        this.name = name;
        this.durabilityMultiplier = durabilityMult;
        this.physicalDamage = physicalDamage;
        this.fireDamage = fireDamage;
        this.waterDamage = waterDamage;
        this.lightningDamage = lightningDamage;
        this.magicDamage = magicDamage;
        this.darkDamage = darkDamage;
        this.lifesteal = lifesteal;
        this.armourPenetration = armourPenetration;
        this.knockback = knockback;
        this.criticalChance = criticalChance;
        this.force = force;
        this.attackSpeed = attackSpeed;
        this.defense = defense;
        this.physicalResistance = physicalResistance;
        this.bonusAttributes = bonusAttributes;
        this.enchantmentValue = enchantability;
        this.sound = equipSound;
        this.knockbackResistance = KBR;
        this.repairIngredient = new LazyLoadedValue<>(repairIngredient);
        fireResistance = f;
        waterResistance = w;
        lightningResistance = l;
        magicResistance = m;
        darkResistance = d;
        maxHP = hp;
        movespeed = ms;
        this.luck = luck;
        this.evasion = evasion;
        strike = str;
        pierce = prc;
        slash = sls;
    }

    public int getDurabilityForSlot(EquipmentSlot slot) {
        return HEALTH_PER_SLOT[slot.getIndex()] * this.durabilityMultiplier;
    }

    /** @deprecated
     * use getPhysicalResistanceForSlot and getRealDefenseForSlot*/
    @Deprecated
    public int getDefenseForSlot(EquipmentSlot slot) {
        return (int)this.physicalResistance[slot.getIndex()];
    }

    public double getPhysicalResistanceForSlot(EquipmentSlot slot) {
        return this.physicalResistance[slot.getIndex()];
    }

    public double getRealDefenseForSlot(EquipmentSlot slot) {
        return this.defense[slot.getIndex()];
    }

    public double getPhysicalDamage(EquipmentSlot slot) {
        return physicalDamage[slot.getIndex()];
    }

    public double getFireDamage(EquipmentSlot slot) {
        return fireDamage[slot.getIndex()];
    }

    public double getWaterDamage(EquipmentSlot slot) {
        return waterDamage[slot.getIndex()];
    }

    public double getLightningDamage(EquipmentSlot slot) {
        return lightningDamage[slot.getIndex()];
    }

    public double getMagicDamage(EquipmentSlot slot) {
        return magicDamage[slot.getIndex()];
    }

    public double getDarkDamage(EquipmentSlot slot) {
        return darkDamage[slot.getIndex()];
    }

    public double getLifesteal(EquipmentSlot slot) {
        return lifesteal[slot.getIndex()];
    }

    public double getArmourPenetration(EquipmentSlot slot) {
        return armourPenetration[slot.getIndex()];
    }

    public double getKnockback(EquipmentSlot slot) {
        return knockback[slot.getIndex()];
    }

    public double getCriticalChance(EquipmentSlot slot) {
        return criticalChance[slot.getIndex()];
    }

    public double getForce(EquipmentSlot slot) {
        return force[slot.getIndex()];
    }

    public double getAttackSpeed(EquipmentSlot slot) {
        return attackSpeed[slot.getIndex()];
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

    public float getToughness() {
        return (float) defense[0];
    }

    public float getKnockbackResistance() {
        return this.knockbackResistance;
    }

    public double getFireResForSlot(EquipmentSlot slot) {
        return this.fireResistance[slot.getIndex()];
    }

    public double getWaterResForSlot(EquipmentSlot slot) {
        return this.waterResistance[slot.getIndex()];
    }

    public double getLightningResForSlot(EquipmentSlot slot) {
        return this.lightningResistance[slot.getIndex()];
    }

    public double getMagicResForSlot(EquipmentSlot slot) {
        return this.magicResistance[slot.getIndex()];
    }

    public double getDarkResForSlot(EquipmentSlot slot) {
        return this.darkResistance[slot.getIndex()];
    }

    public double getMaxHPForSlot(EquipmentSlot slot) {
        return this.maxHP[slot.getIndex()];
    }

    public double getMovespeedForSlot(EquipmentSlot slot) {
        return this.movespeed[slot.getIndex()];
    }

    public double getLuckForSlot(EquipmentSlot slot) {
        return this.luck[slot.getIndex()];
    }

    public double getEvasionForSlot(EquipmentSlot slot) {
        return this.evasion[slot.getIndex()];
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
