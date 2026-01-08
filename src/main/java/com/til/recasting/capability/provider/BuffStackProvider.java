package com.til.recasting.capability.provider;

import com.til.recasting.capability.BuffStackData;
import com.til.recasting.capability.IBuffStackData;
import com.til.recasting.handler.CapabilityRegistryHandler;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Buff叠加数据 Capability Provider
 * 为实体提供buff叠加数据存储能力
 */
public class BuffStackProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {

    private final BuffStackData data = new BuffStackData();
    private final LazyOptional<IBuffStackData> lazyOptional = LazyOptional.of(() -> data);

    /**
     * 设置关联的实体
     */
    public void setEntity(LivingEntity entity) {
        data.setEntity(entity);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityRegistryHandler.BUFF_STACK_DATA) {
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
        ((BuffStackData) data).deserializeNBT(nbt);
    }

    public void invalidate() {
        lazyOptional.invalidate();
    }
}

