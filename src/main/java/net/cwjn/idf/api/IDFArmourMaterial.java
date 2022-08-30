package net.cwjn.idf.api;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.LazyLoadedValue;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Supplier;

public class IDFArmourMaterial implements ArmorMaterial {

    private static final int[] HEALTH_PER_SLOT = new int[]{13, 15, 16, 11};
    private final String name;
    private final int durabilityMultiplier;
    private final int[] slotProtections;
    private final double[] fire, water, lightning, magic, dark, maxhp, movespeed, luck, evasion, strike, pierce, slash, crush, generic;
    private final int enchantmentValue;
    private final SoundEvent sound;
    private final float toughness;
    private final float knockbackResistance;
    private final LazyLoadedValue<Ingredient> repairIngredient;

    public IDFArmourMaterial(String name, int durabilityMult, int[] armourPerSlot,
                             double[] f, double[] w, double[] l, double[] m, double[] d,
                             double[] hp, double[] ms, double[] luck, double[] evasion,
                             double[] str, double[] prc, double[] sls, double[] crs, double[] gen,
                             int enchantability, SoundEvent equipSound, float toughness, float KBR, Supplier<Ingredient> repairIngredient) {
        this.name = name;
        this.durabilityMultiplier = durabilityMult;
        this.slotProtections = armourPerSlot;
        this.enchantmentValue = enchantability;
        this.sound = equipSound;
        this.toughness = toughness;
        this.knockbackResistance = KBR;
        this.repairIngredient = new LazyLoadedValue<>(repairIngredient);
        fire = f;
        water = w;
        lightning = l;
        magic = m;
        dark = d;
        maxhp = hp;
        movespeed = ms;
        this.luck = luck;
        this.evasion = evasion;
        strike = str;
        pierce = prc;
        slash = sls;
        crush = crs;
        generic = gen;
    }

    public int getDurabilityForSlot(EquipmentSlot slot) {
        return HEALTH_PER_SLOT[slot.getIndex()] * this.durabilityMultiplier;
    }

    public int getDefenseForSlot(EquipmentSlot p_40487_) {
        return this.slotProtections[p_40487_.getIndex()];
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
        return this.toughness;
    }

    public float getKnockbackResistance() {
        return this.knockbackResistance;
    }

    public double getFireResForSlot(EquipmentSlot slot) {
        return this.fire[slot.getIndex()];
    }

    public double getWaterResForSlot(EquipmentSlot slot) {
        return this.water[slot.getIndex()];
    }

    public double getLightningResForSlot(EquipmentSlot slot) {
        return this.lightning[slot.getIndex()];
    }

    public double getMagicResForSlot(EquipmentSlot slot) {
        return this.magic[slot.getIndex()];
    }

    public double getDarkResForSlot(EquipmentSlot slot) {
        return this.dark[slot.getIndex()];
    }

    public double getMaxHPForSlot(EquipmentSlot slot) {
        return this.maxhp[slot.getIndex()];
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

    public double getCrushForSlot(EquipmentSlot slot) {
        return this.crush[slot.getIndex()];
    }

    public double getGenericForSlot(EquipmentSlot slot) {
        return this.generic[slot.getIndex()];
    }

}
