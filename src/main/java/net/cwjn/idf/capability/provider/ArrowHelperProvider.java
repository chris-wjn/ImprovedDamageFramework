package net.cwjn.idf.capability.provider;

import net.cwjn.idf.capability.data.ProjectileHelper;
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

public class ArrowHelperProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {

    public static Capability<ProjectileHelper> PROJECTILE_HELPER = CapabilityManager.get(new CapabilityToken<>() {
    });
    private ProjectileHelper projectileHelper = null;
    private final LazyOptional<ProjectileHelper> optionalProjectileHelper = LazyOptional.of(this::createProjectileHelper);

    @Nonnull
    private ProjectileHelper createProjectileHelper() {
        if (projectileHelper == null) {
            projectileHelper = new ProjectileHelper();
        }
        return projectileHelper;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap) {
        if (cap == PROJECTILE_HELPER) {
            return optionalProjectileHelper.cast();
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
        createProjectileHelper().saveNBTData(nbt);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        createProjectileHelper().loadNBTData(nbt);
    }

}
