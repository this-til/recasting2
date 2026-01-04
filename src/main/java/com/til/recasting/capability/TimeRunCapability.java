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
 * TimeRun Capability
 * 为实体提供定时器功能
 */
public class TimeRunCapability {
    
    public static final Capability<ITimeRun> TIME_RUN = CapabilityManager.get(new CapabilityToken<>() {});

    /**
     * Capability Provider
     */
    public static class Provider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
        private final ITimeRun timeRun = new ITimeRun.TimeRun();
        private final LazyOptional<ITimeRun> lazyOptional = LazyOptional.of(() -> timeRun);

        @Override
        public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
            if (cap == TIME_RUN) {
                return lazyOptional.cast();
            }
            return LazyOptional.empty();
        }

        @Override
        public CompoundTag serializeNBT() {
            // TimeRun 不需要持久化
            return new CompoundTag();
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            // TimeRun 不需要持久化
        }

        public void invalidate() {
            lazyOptional.invalidate();
        }
    }
}

