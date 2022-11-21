package net.cwjn.idf.rpg.capability;

import net.cwjn.idf.rpg.RpgClass;
import net.minecraft.nbt.CompoundTag;

public class RpgItem extends RpgClass {

    public void copyFrom(RpgItem source) {
        this.CONSTITUTION = source.CONSTITUTION;
        this.STRENGTH = source.STRENGTH;
        this.DEXTERITY = source.DEXTERITY;
        this.AGILITY = source.AGILITY;
        this.INTELLIGENCE = source.INTELLIGENCE;
        this.WISDOM = source.WISDOM;
        this.FAITH = source.FAITH;
    }

    public void saveNBTData(CompoundTag tag) {
        tag.putInt("CONSTITUTION.level", CONSTITUTION.getLeft());
        tag.putDouble("CONSTITUTION.scalar", CONSTITUTION.getRight());
        tag.putInt("STRENGTH.level", STRENGTH.getLeft());
        tag.putDouble("STRENGTH.scalar", STRENGTH.getRight());
        tag.putInt("DEXTERITY.level", DEXTERITY.getLeft());
        tag.putDouble("DEXTERITY.scalar", DEXTERITY.getRight());
        tag.putInt("AGILITY.level", AGILITY.getLeft());
        tag.putDouble("AGILITY.scalar", AGILITY.getRight());
        tag.putInt("INTELLIGENCE.level", INTELLIGENCE.getLeft());
        tag.putDouble("INTELLIGENCE.scalar", INTELLIGENCE.getRight());
        tag.putInt("WISDOM.level", WISDOM.getLeft());
        tag.putDouble("WISDOM.scalar", WISDOM.getRight());
        tag.putInt("FAITH.level", FAITH.getLeft());
        tag.putDouble("FAITH.scalar", FAITH.getRight());
    }

    public void loadNBTData(CompoundTag tag) {
        CONSTITUTION.setLeft(tag.getInt("CONSTITUTION.level"));
        CONSTITUTION.setRight(tag.getDouble("CONSTITUTION.scalar"));
        STRENGTH.setLeft(tag.getInt("STRENGTH.level"));
        STRENGTH.setRight(tag.getDouble("STRENGTH.scalar"));
        DEXTERITY.setLeft(tag.getInt("DEXTERITY.level"));
        DEXTERITY.setRight(tag.getDouble("DEXTERITY.scalar"));
        AGILITY.setLeft(tag.getInt("AGILITY.level"));
        AGILITY.setRight(tag.getDouble("AGILITY.scalar"));
        INTELLIGENCE.setLeft(tag.getInt("INTELLIGENCE.level"));
        INTELLIGENCE.setRight(tag.getDouble("INTELLIGENCE.scalar"));
        WISDOM.setLeft(tag.getInt("WISDOM.level"));
        WISDOM.setRight(tag.getDouble("WISDOM.scalar"));
        FAITH.setLeft(tag.getInt("FAITH.level"));
        FAITH.setRight(tag.getDouble("FAITH.scalar"));
    }

}
