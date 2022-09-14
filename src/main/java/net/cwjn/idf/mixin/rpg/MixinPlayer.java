package net.cwjn.idf.mixin.rpg;

import net.cwjn.idf.rpg.RpgPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import org.checkerframework.checker.units.qual.A;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public class MixinPlayer implements RpgPlayer {

    @Unique
    private int level, constitution, strength, dexterity, agility, intelligence, wisdom, faith;

    public int getLevel() {
        return level;
    }
    public int getCons() {
        return constitution;
    }
    public int getStr() {
        return strength;
    }
    public int getDex() {
        return dexterity;
    }
    public int getAgl() {
        return agility;
    }
    public int getIntel() {
        return intelligence;
    }
    public int getWis() {
        return wisdom;
    }
    public int getFth() {
        return faith;
    }
    public void setLevel(int i) {
        level = i;
    }
    public void setCons(int i) {
        constitution = i;
    }
    public void setStr(int i) {
        strength = i;
    }
    public void setDex(int i) {
        dexterity = i;
    }
    public void setAgl(int i) {
        agility = i;
    }
    public void setIntel(int i) {
        intelligence = i;
    }
    public void setWis(int i) {
        wisdom = i;
    }
    public void setFth(int i) {
        faith = i;
    }
    public void incrementCons(int i) {
        constitution += i;
    }
    public void incrementStr(int i) {
        strength += i;
    }
    public void incrementDex(int i) {
        dexterity += i;
    }
    public void incrementAgl(int i) {
        agility += i;
    }
    public void incrementIntel(int i) {
        intelligence += i;
    }
    public void incrementWis(int i) {
        wisdom += i;
    }
    public void incrementFth(int i) {
        faith += i;
    }

    @Inject(method = "addAdditionalSaveData", at = @At("HEAD"))
    private void saveStats(CompoundTag saveTag, CallbackInfo callback) {
        saveTag.putInt("RPGLevel", level);
        saveTag.putInt("RPGConstitution", constitution);
        saveTag.putInt("RPGStrength", strength);
        saveTag.putInt("RPGDexterity", dexterity);
        saveTag.putInt("RPGAgility", agility);
        saveTag.putInt("RPGIntelligence", intelligence);
        saveTag.putInt("RPGWisdom", wisdom);
        saveTag.putInt("RPGFaith", faith);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("HEAD"))
    private void readStats(CompoundTag saveTag, CallbackInfo callback) {
        level = saveTag.getInt("RPGLevel");
        constitution = saveTag.getInt("RPGConstitution");
        strength = saveTag.getInt("RPGStrength");
        dexterity = saveTag.getInt("RPGDexterity");
        agility = saveTag.getInt("RPGAgility");
        intelligence = saveTag.getInt("RPGIntelligence");
        wisdom = saveTag.getInt("RPGWisdom");
        faith = saveTag.getInt("RPGFaith");
    }

}
