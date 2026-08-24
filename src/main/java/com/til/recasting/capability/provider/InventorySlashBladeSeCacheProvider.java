package com.til.recasting.capability.provider;

import com.til.recasting.capability.InventorySlashBladeSeCache;
import com.til.recasting.handler.CapabilityRegistryHandler;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class InventorySlashBladeSeCacheProvider implements ICapabilityProvider {

    private final InventorySlashBladeSeCache cache = new InventorySlashBladeSeCache();
    private final LazyOptional<InventorySlashBladeSeCache> lazyOptional = LazyOptional.of(() -> cache);

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityRegistryHandler.INVENTORY_SLASH_BLADE_SE_CACHE) {
            return lazyOptional.cast();
        }
        return LazyOptional.empty();
    }

    public void invalidate() {
        cache.clear();
        lazyOptional.invalidate();
    }
}
