package net.cwjn.idf.capability.provider;

import net.cwjn.idf.capability.data.IDFEntityData;
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

public class IDFEntityDataProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {

    public static Capability<IDFEntityData> ENTITY_DATA = CapabilityManager.get(new CapabilityToken<>() {});
    private IDFEntityData IDFEntityData = null;
    private final LazyOptional<IDFEntityData> optionalEntityData = LazyOptional.of(this::createEntityData);

    @Nonnull
    private IDFEntityData createEntityData() {
        if (IDFEntityData == null) {
            IDFEntityData = new IDFEntityData();
        }
        return IDFEntityData;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap) {
        if (cap == ENTITY_DATA) {
            return optionalEntityData.cast();
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
        createEntityData().saveNBTData(nbt);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        createEntityData().loadNBTData(nbt);
    }

}
