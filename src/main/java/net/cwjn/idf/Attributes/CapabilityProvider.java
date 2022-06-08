package net.cwjn.idf.Attributes;

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

public class CapabilityProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {

    public static Capability<AuxiliaryData> AUXILIARY_DATA = CapabilityManager.get(new CapabilityToken<>() {});
    private AuxiliaryData auxiliaryData = null;
    private final LazyOptional<AuxiliaryData> optional = LazyOptional.of(this::createAuxiliaryData);

    @Nonnull
    private AuxiliaryData createAuxiliaryData() {
        if (auxiliaryData == null) {
            auxiliaryData = new AuxiliaryData();
        }
        return auxiliaryData;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap) {
        if (cap == AUXILIARY_DATA) {
            return optional.cast();
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
        createAuxiliaryData().saveNBTData(nbt);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        createAuxiliaryData().loadNBTData(nbt);
    }


}
