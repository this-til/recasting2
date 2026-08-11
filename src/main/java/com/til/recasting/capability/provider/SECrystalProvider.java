package com.til.recasting.capability.provider;

import com.til.recasting.capability.ISpecialEffectCrystalData;
import com.til.recasting.capability.SECrystalData;
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
 * SE Crystal Capability
 * 为 SE_CRYSTAL 物品提供数据存储能力
 */
public class SECrystalProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {

    private final ISpecialEffectCrystalData data = new SECrystalData();
    private final LazyOptional<ISpecialEffectCrystalData> lazyOptional = LazyOptional.of(() -> data);

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityRegistryHandler.SE_CRYSTAL_DATA) {
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

