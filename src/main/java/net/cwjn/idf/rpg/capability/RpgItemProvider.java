package net.cwjn.idf.rpg.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RpgItemProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {

    public static Capability<RpgItemProvider> RPG_ITEM = CapabilityManager.get(new CapabilityToken<>() {});
    private RpgItem rpgItem = null;
    private final LazyOptional<RpgItem> optionalRpgItem = LazyOptional.of(this::createRpgItem);

    @Nonnull
    private RpgItem createRpgItem() {
        if (rpgItem == null) {
            rpgItem = new RpgItem();
        }
        return rpgItem;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap) {
        if (cap == RPG_ITEM) {
            return optionalRpgItem.cast();
        }
        return LazyOptional.empty();
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return getCapability(cap);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        createRpgItem().saveNBTData(nbt);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        createRpgItem().loadNBTData(nbt);
    }

}
