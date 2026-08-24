package com.til.recasting.capability;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * 保存活体实体背包 SE 查询的运行时缓存。
 */
public final class InventorySlashBladeSeCache {

    public record CachedBlade(ItemStack blade, int slot) {
    }

    private final Map<ResourceLocation, CachedBlade> blades = new HashMap<>();
    private final Map<ResourceLocation, Long> negativeUntilGameTime = new HashMap<>();

    @Nullable
    public CachedBlade getBlade(ResourceLocation effectId) {
        return blades.get(effectId);
    }

    @Nullable
    public Long getNegativeUntilGameTime(ResourceLocation effectId) {
        return negativeUntilGameTime.get(effectId);
    }

    public void putBlade(ResourceLocation effectId, ItemStack blade, int slot) {
        blades.put(effectId, new CachedBlade(blade, slot));
        negativeUntilGameTime.remove(effectId);
    }

    public void putNegative(ResourceLocation effectId, long untilGameTime) {
        blades.remove(effectId);
        negativeUntilGameTime.put(effectId, untilGameTime);
    }

    public void remove(ResourceLocation effectId) {
        blades.remove(effectId);
        negativeUntilGameTime.remove(effectId);
    }

    public void clear() {
        blades.clear();
        negativeUntilGameTime.clear();
    }
}
