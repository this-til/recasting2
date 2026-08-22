package com.til.recasting.capability.provider;

import com.til.recasting.capability.IJieYuanDogBond;
import com.til.recasting.handler.CapabilityRegistryHandler;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 玩家结缘剑「犬」进度 Capability Provider。
 */
public class JieYuanDogBondProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {

    private final IJieYuanDogBond.JieYuanDogBond data = new IJieYuanDogBond.JieYuanDogBond();
    private final LazyOptional<IJieYuanDogBond> lazyOptional = LazyOptional.of(() -> data);

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityRegistryHandler.JIE_YUAN_DOG_BOND) {
            return lazyOptional.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        return data.serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        data.deserializeNBT(nbt);
    }

    public void invalidate() {
        lazyOptional.invalidate();
    }
}
