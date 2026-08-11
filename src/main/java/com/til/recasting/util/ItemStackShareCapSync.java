package com.til.recasting.util;

import com.til.recasting.Recasting;
import com.til.recasting.mixin.CapabilityProviderAccessor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

/**
 * 将本模组 ItemStack Cap 嵌入/还原到网络 ShareTag（与存档 ForgeCaps 同形）。
 */
public final class ItemStackShareCapSync {

    private static final String FORGE_CAPS = "ForgeCaps";
    private static final String CAP_KEY_PREFIX = Recasting.MODID + ":";

    private ItemStackShareCapSync() {
    }

    /**
     * @param baseShare {@link ItemStack#getItem()}{@link net.minecraft.world.item.Item#getShareTag} 的结果，禁止再调 ItemStack.getShareTag 以免递归
     */
    @Nullable
    public static CompoundTag appendToShareTag(ItemStack stack, @Nullable CompoundTag baseShare) {
        CompoundTag filteredCaps = filterRecastingCaps(
                ((CapabilityProviderAccessor) (Object) stack).recasting$serializeCaps()
        );
        if (filteredCaps == null || filteredCaps.isEmpty()) {
            return baseShare;
        }

        CompoundTag share = baseShare == null ? new CompoundTag() : baseShare.copy();
        share.put(FORGE_CAPS, filteredCaps);
        return share;
    }

    /**
     * @param nbt 网络读入的 ShareTag；通过 {@link net.minecraft.world.item.Item#readShareTag} 落地，避免 ItemStack.readShareTag 递归
     */
    public static void readFromShareTag(ItemStack stack, @Nullable CompoundTag nbt) {
        if (nbt == null || !nbt.contains(FORGE_CAPS)) {
            stack.getItem().readShareTag(stack, nbt);
            return;
        }

        CompoundTag caps = nbt.getCompound(FORGE_CAPS);
        CompoundTag rest = nbt.copy();
        rest.remove(FORGE_CAPS);

        stack.getItem().readShareTag(stack, rest.isEmpty() ? null : rest);
        ((CapabilityProviderAccessor) (Object) stack).recasting$deserializeCaps(caps);
    }

    @Nullable
    private static CompoundTag filterRecastingCaps(@Nullable CompoundTag allCaps) {
        if (allCaps == null || allCaps.isEmpty()) {
            return null;
        }

        CompoundTag filtered = new CompoundTag();
        for (String key : allCaps.getAllKeys()) {
            if (!key.startsWith(CAP_KEY_PREFIX)) {
                continue;
            }
            Tag value = allCaps.get(key);
            if (value != null) {
                filtered.put(key, value.copy());
            }
        }
        return filtered.isEmpty() ? null : filtered;
    }
}
