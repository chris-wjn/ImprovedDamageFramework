package net.cwjn.idf.mixin.rpg;

import net.cwjn.idf.rpg.RpgPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Mixin(Player.class)
public class MixinPlayer implements RpgPlayer {

    @Unique
    private final List<UUID> bonfires = new ArrayList<>();
    @Unique
    private int level = 1, maxBonfires = 1, constitution = 1, strength = 1, dexterity = 1, agility = 1, intelligence = 1, wisdom = 1, faith = 1;

    public int getMaxBonfires() {
        return maxBonfires;
    }
    public void setMaxBonfires(int i) {
        maxBonfires = i;
    }
    public List<UUID> getBonfires() {
        return bonfires;
    }
    public void addBonfire(UUID id) {
        bonfires.add(id);
    }
    public void removeBonfire(UUID id) {
        if (bonfires.contains(id)) {
            bonfires.remove(id);
        } else {
            throw new IllegalStateException("tried to remove a player's bonfire, but the player doesn't own it!");
        }
    }
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
        saveTag.putInt("maxBonfires", maxBonfires);
        if (!bonfires.isEmpty()) {
            for (int i = 0; i < maxBonfires; ++i) {
                saveTag.putUUID("bonfireUUID" + i, bonfires.get(i));
            }
        }
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
        maxBonfires = saveTag.getInt("maxBonfires");
        if (saveTag.contains("bonfireUUID0")) {
            for (int i = 0; i < maxBonfires; ++i) {
                bonfires.add(saveTag.getUUID("bonfireUUID" + i));
            }
        }
    }

}
