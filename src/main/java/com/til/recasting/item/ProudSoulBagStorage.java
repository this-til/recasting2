package com.til.recasting.item;

import com.til.recasting.handler.SlashBladeRegistryHelper;
import com.til.recasting.registry.RecastingTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 耀魂背包 NBT 存取：类型无限，单类型数量为 long。
 */
public final class ProudSoulBagStorage {

    private static final HolderLookup.Provider FALLBACK_REGISTRIES =
            RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);

    private static final String BAG_ITEMS_KEY = "BagItems";
    private static final String ENTRY_ITEM_KEY = "Item";
    private static final String ENTRY_COUNT_KEY = "Count";

    private ProudSoulBagStorage() {
    }

    /**
     * 附魔等为数据包注册表，必须使用当前世界的 RegistryAccess；静态 BuiltInRegistries 不足以编码 ItemStack。
     */
    private static HolderLookup.Provider registries() {
        return SlashBladeRegistryHelper.getRegistryAccess()
                .map(access -> (HolderLookup.Provider) access)
                .orElse(FALLBACK_REGISTRIES);
    }

    public static boolean isProudSoul(@NotNull ItemStack stack) {
        return !stack.isEmpty() && stack.is(RecastingTags.PROUD_SOULS);
    }

    public static @NotNull List<StoredEntry> list(@NotNull ItemStack bag) {
        ListTag list = getOrCreateList(bag, false);
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        List<StoredEntry> result = new ArrayList<>(list.size());
        for (int i = 0; i < list.size(); i++) {
            CompoundTag entry = list.getCompound(i);
            ItemStack template = ItemStack.parseOptional(registries(), entry.getCompound(ENTRY_ITEM_KEY));
            if (template.isEmpty()) {
                continue;
            }
            template.setCount(1);
            long count = entry.getLong(ENTRY_COUNT_KEY);
            if (count <= 0) {
                continue;
            }
            result.add(new StoredEntry(template, count));
        }
        return result;
    }

    public static long insert(@NotNull ItemStack bag, @NotNull ItemStack incoming) {
        if (!isProudSoul(incoming) || incoming.isEmpty()) {
            return 0;
        }
        long amount = incoming.getCount();
        if (amount <= 0) {
            return 0;
        }

        ListTag list = getOrCreateList(bag, true);
        ItemStack template = incoming.copyWithCount(1);
        int index = findMatchingIndex(list, template);
        if (index >= 0) {
            CompoundTag entry = list.getCompound(index);
            long existing = entry.getLong(ENTRY_COUNT_KEY);
            entry.putLong(ENTRY_COUNT_KEY, existing + amount);
        } else {
            CompoundTag entry = new CompoundTag();
            entry.put(ENTRY_ITEM_KEY, saveTemplate(template));
            entry.putLong(ENTRY_COUNT_KEY, amount);
            list.add(entry);
        }
        incoming.setCount(0);
        return amount;
    }

    public static @NotNull ItemStack extract(@NotNull ItemStack bag, @NotNull ItemStack match, long want) {
        if (match.isEmpty() || want <= 0) {
            return ItemStack.EMPTY;
        }
        ListTag list = getOrCreateList(bag, false);
        if (list == null || list.isEmpty()) {
            return ItemStack.EMPTY;
        }
        int index = findMatchingIndex(list, match);
        if (index < 0) {
            return ItemStack.EMPTY;
        }
        CompoundTag entry = list.getCompound(index);
        ItemStack template = ItemStack.parseOptional(registries(), entry.getCompound(ENTRY_ITEM_KEY));
        if (template.isEmpty()) {
            list.remove(index);
            return ItemStack.EMPTY;
        }
        long stored = entry.getLong(ENTRY_COUNT_KEY);
        long take = Math.min(want, stored);
        if (take <= 0) {
            return ItemStack.EMPTY;
        }
        long remain = stored - take;
        if (remain <= 0) {
            list.remove(index);
        } else {
            entry.putLong(ENTRY_COUNT_KEY, remain);
        }
        if (list.isEmpty()) {
            clearCustomData(bag);
        }
        template.setCount((int) Math.min(take, Integer.MAX_VALUE));
        return template;
    }

    public static @NotNull ItemStack extractByIndex(@NotNull ItemStack bag, int index, long want) {
        List<StoredEntry> entries = list(bag);
        if (index < 0 || index >= entries.size()) {
            return ItemStack.EMPTY;
        }
        return extract(bag, entries.get(index).template(), want);
    }

    public static @NotNull StoredEntry getByIndex(@NotNull ItemStack bag, int index) {
        List<StoredEntry> entries = list(bag);
        if (index < 0 || index >= entries.size()) {
            return new StoredEntry(ItemStack.EMPTY, 0);
        }
        return entries.get(index);
    }

    private static int findMatchingIndex(ListTag list, ItemStack match) {
        for (int i = 0; i < list.size(); i++) {
            CompoundTag entry = list.getCompound(i);
            ItemStack template = ItemStack.parseOptional(registries(), entry.getCompound(ENTRY_ITEM_KEY));
            if (!template.isEmpty() && ItemStack.isSameItemSameComponents(template, match)) {
                return i;
            }
        }
        return -1;
    }

    private static ListTag getOrCreateList(ItemStack bag, boolean create) {
        CompoundTag tag = getCustomTag(bag);
        if (tag == null) {
            if (!create) {
                return null;
            }
            tag = new CompoundTag();
            setCustomTag(bag, tag);
        }
        if (!tag.contains(BAG_ITEMS_KEY, Tag.TAG_LIST)) {
            if (!create) {
                return null;
            }
            ListTag created = new ListTag();
            tag.put(BAG_ITEMS_KEY, created);
            return created;
        }
        return tag.getList(BAG_ITEMS_KEY, Tag.TAG_COMPOUND);
    }

    private static CompoundTag getCustomTag(ItemStack stack) {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData == null) {
            return null;
        }
        CompoundTag tag = customData.copyTag();
        return tag.isEmpty() ? null : tag;
    }

    private static void setCustomTag(ItemStack stack, CompoundTag tag) {
        if (tag == null || tag.isEmpty()) {
            stack.remove(DataComponents.CUSTOM_DATA);
        } else {
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        }
    }

    private static void clearCustomData(ItemStack stack) {
        CompoundTag tag = getCustomTag(stack);
        if (tag == null) {
            return;
        }
        tag.remove(BAG_ITEMS_KEY);
        setCustomTag(stack, tag);
    }

    private static CompoundTag saveTemplate(ItemStack template) {
        return (CompoundTag) template.saveOptional(registries());
    }

    public record StoredEntry(@NotNull ItemStack template, long count) {
    }

    public static void writeTemplate(RegistryFriendlyByteBuf buf, @NotNull ItemStack template) {
        ItemStack.STREAM_CODEC.encode(buf, template);
    }

    public static @NotNull ItemStack readTemplate(RegistryFriendlyByteBuf buf) {
        ItemStack stack = ItemStack.STREAM_CODEC.decode(buf);
        if (!stack.isEmpty()) {
            stack.setCount(1);
        }
        return stack;
    }
}
