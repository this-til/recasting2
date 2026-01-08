package com.til.recasting.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * SE Crystal Capability
 * 为 SE_CRYSTAL 物品提供数据存储能力
 */
public class SECrystalCapability {
    
    public static final Capability<ISECrystalData> SE_CRYSTAL_DATA = 
            CapabilityManager.get(new CapabilityToken<>() {});

    /**
     * Capability Provider
     */
    public static class Provider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
        private final ISECrystalData data = new SECrystalData();
        private final LazyOptional<ISECrystalData> lazyOptional = LazyOptional.of(() -> data);

        @Override
        public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
            if (cap == SE_CRYSTAL_DATA) {
                return lazyOptional.cast();
            }
            return LazyOptional.empty();
        }

        @Override
        public CompoundTag serializeNBT() {
            return ((SECrystalData) data).serializeNBT();
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            ((SECrystalData) data).deserializeNBT(nbt);
        }

        public void invalidate() {
            lazyOptional.invalidate();
        }
    }
}

