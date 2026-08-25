package com.til.recasting.capability.provider;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * 向拔刀剑 ItemStack 暴露 {@link ForgeCapabilities#ENERGY}。
 */
public class FeBladeCapabilityProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {

    private final FeBladeEnergyStorage energyStorage;
    private final LazyOptional<IEnergyStorage> lazyOptional;

    public FeBladeCapabilityProvider(ItemStack stack) {
        this.energyStorage = new FeBladeEnergyStorage(stack);
        this.lazyOptional = LazyOptional.of(() -> energyStorage);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY) {
            if (energyStorage.resolveCapacity() <= 0L) {
                return LazyOptional.empty();
            }
            return lazyOptional.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        return energyStorage.serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        energyStorage.deserializeNBT(nbt);
    }
}
