package net.cwjn.idf.api;

import com.google.common.collect.ImmutableMultimap;
import net.cwjn.idf.config.json.JSONHandler;
import net.cwjn.idf.config.json.data.ResistanceData;
import net.cwjn.idf.util.Util;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;

public class IDFArmourItem extends ArmorItem {

    private final double armour, defense, knockbackRes, fireRes, waterRes, lightningRes, magicRes, darkRes, maxHp, movespeed, luck, evasion, strike, pierce, slash, crush, generic;

    public IDFArmourItem(ArmorMaterial material, EquipmentSlot slot, Properties p, double fire, double water, double lightning, double magic, double dark, double phys, double def,
                         double kbr, double hp, double ms, double lk, double eva, double str, double prc, double sls, double crs, double gen) {
        super(material, slot, p);
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        ResistanceData data = JSONHandler.getResistanceData(Util.getItemRegistryName(this));
        if (material instanceof IDFArmourMaterial modMaterial) {
            if (data != null) {
                armour = phys + this.getDefense() + data.getArmour();
                defense = def + this.getToughness() + data.getArmourToughness();
                knockbackRes = kbr + this.knockbackResistance + data.getKnockbackRes();
                fireRes = fire + data.getFire() + modMaterial.getFireResForSlot(slot);
                waterRes = water + data.getWater() + modMaterial.getWaterResForSlot(slot);
                lightningRes = lightning + data.getLightning() + modMaterial.getLightningResForSlot(slot);
                magicRes = magic + data.getMagic() + modMaterial.getMagicResForSlot(slot);
                darkRes = dark + data.getDark() + modMaterial.getDarkResForSlot(slot);
                maxHp = hp + data.getMaxHP() + modMaterial.getMaxHPForSlot(slot);
                movespeed = ms + data.getMovespeed() + modMaterial.getMovespeedForSlot(slot);
                luck = lk + data.getLuck() + modMaterial.getLuckForSlot(slot);
                evasion = eva + data.getEvasion() + modMaterial.getEvasionForSlot(slot);
                strike = str + data.getStrikeMult() + modMaterial.getStrikeForSlot(slot);
                pierce = prc + data.getPierceMult() + modMaterial.getPierceForSlot(slot);
                slash = sls + data.getSlashMult() + modMaterial.getSlashForSlot(slot);
                crush = crs + data.getCrushMult() + modMaterial.getCrushForSlot(slot);
                generic = gen + data.getGenericMult() + modMaterial.getGenericForSlot(slot);
            } else {
                armour = phys + this.getDefense();
                defense = def + this.getToughness();
                knockbackRes = kbr + this.knockbackResistance;
                fireRes = fire + modMaterial.getFireResForSlot(slot);
                waterRes = water + modMaterial.getWaterResForSlot(slot);
                lightningRes = lightning + modMaterial.getLightningResForSlot(slot);
                magicRes = magic + modMaterial.getMagicResForSlot(slot);
                darkRes = dark + modMaterial.getDarkResForSlot(slot);
                maxHp = hp + modMaterial.getMaxHPForSlot(slot);
                movespeed = ms + modMaterial.getMovespeedForSlot(slot);
                luck = lk + modMaterial.getLuckForSlot(slot);
                evasion = eva + modMaterial.getEvasionForSlot(slot);
                strike = str + modMaterial.getStrikeForSlot(slot);
                pierce = prc + modMaterial.getPierceForSlot(slot);
                slash = sls + modMaterial.getSlashForSlot(slot);
                crush = crs + modMaterial.getCrushForSlot(slot);
                generic = gen + modMaterial.getGenericForSlot(slot);
            }
        } else {
            if (data != null) {
                armour = phys + this.getDefense() + data.getArmour();
                defense = def + this.getToughness() + data.getArmourToughness();
                knockbackRes = kbr + this.knockbackResistance + data.getKnockbackRes();
                fireRes = fire + data.getFire();
                waterRes = water + data.getWater();
                lightningRes = lightning + data.getLightning();
                magicRes = magic + data.getMagic();
                darkRes = dark + data.getDark();
                maxHp = hp + data.getMaxHP();
                movespeed = ms + data.getMovespeed();
                luck = lk + data.getLuck();
                evasion = eva + data.getEvasion();
                strike = str + data.getStrikeMult();
                pierce = prc + data.getPierceMult();
                slash = sls + data.getSlashMult();
                crush = crs + data.getCrushMult();
                generic = gen + data.getGenericMult();
            } else {
                armour = phys + this.getDefense();
                defense = def + this.getToughness();
                knockbackRes = kbr + this.knockbackResistance;
                fireRes = fire;
                waterRes = water;
                lightningRes = lightning;
                magicRes = magic;
                darkRes = dark;
                maxHp = hp;
                movespeed = ms;
                luck = lk;
                evasion = eva;
                strike = str;
                pierce = prc;
                slash = sls;
                crush = crs;
                generic = gen;
            }
        }
        this.defaultModifiers = Util.buildDefaultAttributesArmor(slot, armour, defense, fireRes, waterRes, lightningRes, magicRes, darkRes,
                knockbackRes, maxHp, movespeed, luck, evasion, strike, pierce, slash, crush, generic);
    }

    @Override
    public int getDefense() {
        return (int) (armour + fireRes + waterRes + lightningRes + magicRes + darkRes);
    }

    @Override
    public float getToughness() {
        return (float) defense;
    }

}
