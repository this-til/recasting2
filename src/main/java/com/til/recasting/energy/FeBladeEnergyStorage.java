package com.til.recasting.energy;

import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.handler.CapabilityRegistryHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.energy.IEnergyStorage;

/**
 * 拔刀剑 FE 能量容器：内部 long 容量，对外兼容 {@link IEnergyStorage} int 协议。
 */
public class FeBladeEnergyStorage implements IEnergyStorage, INBTSerializable<CompoundTag> {

    private final ItemStack stack;
    private long energy;
    private int maxReceive = Integer.MAX_VALUE;
    private int maxExtract = Integer.MAX_VALUE;

    public FeBladeEnergyStorage(ItemStack stack) {
        this.stack = stack;
    }

    public long resolveCapacity() {
        return stack.getCapability(CapabilityRegistryHandler.PROPERTIES_DEFINITION_EXTENSION)
                .map(PropertiesDefinitionExtension::feCapacity)
                .orElse(0L);
    }

    public long getEnergyLong() {
        long capacity = resolveCapacity();
        if (capacity <= 0L) {
            return 0L;
        }
        return Math.max(0L, Math.min(energy, capacity));
    }

    public void setEnergyLong(long energy) {
        this.energy = Math.max(0L, energy);
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
            this.energy = stored - extracted;
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
            this.energy = stored + accepted;
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

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putLong("Energy", energy);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if (nbt == null) {
            return;
        }
        energy = nbt.getLong("Energy");
    }
}
