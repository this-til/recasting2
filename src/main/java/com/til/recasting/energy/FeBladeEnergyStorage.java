package com.til.recasting.energy;

import com.til.recasting.capability.FeBladeEnergyData;
import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.registry.RecastingDataComponents;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.energy.IEnergyStorage;

/**
 * 拔刀剑 FE 能量容器：内部 long 容量，对外兼容 {@link IEnergyStorage} int 协议。
 * 储能写入 {@link RecastingDataComponents#FE_BLADE_ENERGY}；容量取自
 * {@link RecastingDataComponents#PROPERTIES_DEFINITION_EXTENSION}。
 */
public class FeBladeEnergyStorage implements IEnergyStorage {

    // TODO(P4): 通过 RegisterCapabilitiesEvent 向拔刀剑 ItemStack 暴露 Capabilities.EnergyStorage.ITEM

    private final ItemStack stack;
    private int maxReceive = Integer.MAX_VALUE;
    private int maxExtract = Integer.MAX_VALUE;

    public FeBladeEnergyStorage(ItemStack stack) {
        this.stack = stack;
    }

    public long resolveCapacity() {
        PropertiesDefinitionExtension extension = stack.get(RecastingDataComponents.PROPERTIES_DEFINITION_EXTENSION);
        if (extension == null) {
            return 0L;
        }
        return extension.feCapacity();
    }

    public long getEnergyLong() {
        long capacity = resolveCapacity();
        if (capacity <= 0L) {
            return 0L;
        }
        FeBladeEnergyData data = stack.getOrDefault(RecastingDataComponents.FE_BLADE_ENERGY, FeBladeEnergyData.EMPTY);
        return Math.max(0L, Math.min(data.energy(), capacity));
    }

    public void setEnergyLong(long energy) {
        stack.set(RecastingDataComponents.FE_BLADE_ENERGY, new FeBladeEnergyData(Math.max(0L, energy)));
    }

    /**
     * @return 实际抽出量
     */
    public long extractEnergyLong(long maxExtract, boolean simulate) {
        if (!canExtract()) {
            return 0L;
        }
        long capacity = resolveCapacity();
        if (capacity <= 0L) {
            return 0L;
        }
        long stored = getEnergyLong();
        long extracted = Math.min(stored, Math.min(maxExtract, this.maxExtract));
        if (!simulate) {
            setEnergyLong(stored - extracted);
        }
        return extracted;
    }

    /**
     * @return 实际接收量
     */
    public long receiveEnergyLong(long maxReceive, boolean simulate) {
        if (!canReceive()) {
            return 0L;
        }
        long capacity = resolveCapacity();
        if (capacity <= 0L) {
            return 0L;
        }
        long stored = getEnergyLong();
        long room = capacity - stored;
        long accepted = Math.min(room, Math.min(maxReceive, this.maxReceive));
        if (!simulate) {
            setEnergyLong(stored + accepted);
        }
        return accepted;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if (maxReceive <= 0) {
            return 0;
        }
        return (int) receiveEnergyLong(maxReceive, simulate);
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        if (maxExtract <= 0) {
            return 0;
        }
        return (int) extractEnergyLong(maxExtract, simulate);
    }

    @Override
    public int getEnergyStored() {
        long capacity = resolveCapacity();
        if (capacity <= 0L) {
            return 0;
        }
        long stored = getEnergyLong();
        if (stored >= capacity) {
            return Integer.MAX_VALUE;
        }
        return (int) Math.min(stored, Integer.MAX_VALUE - 1L);
    }

    @Override
    public int getMaxEnergyStored() {
        if (resolveCapacity() <= 0L) {
            return 0;
        }
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean canExtract() {
        return maxExtract > 0 && resolveCapacity() > 0L;
    }

    @Override
    public boolean canReceive() {
        return maxReceive > 0 && resolveCapacity() > 0L && getEnergyLong() < resolveCapacity();
    }
}
