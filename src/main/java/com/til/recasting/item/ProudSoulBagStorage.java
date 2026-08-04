package com.til.recasting.item;

import com.til.recasting.registry.RecastingTags;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 耀魂背包 NBT 存取：类型无限，单类型数量为 long。
 * <p>
 * 条目格式 {@code BagItems:[{Item:<template NBT Count=1>, Count:long}, ...]}，
 * 类型键与 {@link ItemStack#isSameItemSameTags} 一致。
 */
public final class ProudSoulBagStorage {

    private static final String BAG_ITEMS_KEY = "BagItems";
    private static final String ENTRY_ITEM_KEY = "Item";
    private static final String ENTRY_COUNT_KEY = "Count";

    private ProudSoulBagStorage() {
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
            ItemStack template = ItemStack.of(entry.getCompound(ENTRY_ITEM_KEY));
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

    /**
     * 将可存物品全部或尽可能多地写入背包，从 {@code incoming} 扣除已存数量。
     *
     * @return 实际插入数量
     */
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
            entry.put(ENTRY_ITEM_KEY, template.save(new CompoundTag()));
            entry.putLong(ENTRY_COUNT_KEY, amount);
            list.add(entry);
        }
        incoming.setCount(0);
        return amount;
    }

    /**
     * 按模板从背包取出至多 {@code want} 个，返回实际取出的堆叠（数量可能小于 want）。
     */
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
        ItemStack template = ItemStack.of(entry.getCompound(ENTRY_ITEM_KEY));
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
            CompoundTag tag = bag.getTag();
            if (tag != null) {
                tag.remove(BAG_ITEMS_KEY);
                if (tag.isEmpty()) {
                    bag.setTag(null);
                }
            }
        }
        template.setCount((int) Math.min(take, Integer.MAX_VALUE));
        return template;
    }

    /**
     * 按列表下标取出（用于 GUI 虚拟格）。
     */
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
            ItemStack template = ItemStack.of(entry.getCompound(ENTRY_ITEM_KEY));
            if (!template.isEmpty() && ItemStack.isSameItemSameTags(template, match)) {
                return i;
            }
        }
        return -1;
    }

    private static ListTag getOrCreateList(ItemStack bag, boolean create) {
        CompoundTag tag = bag.getTag();
        if (tag == null) {
            if (!create) {
                return null;
            }
            tag = new CompoundTag();
            bag.setTag(tag);
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

    public record StoredEntry(@NotNull ItemStack template, long count) {
    }
}
