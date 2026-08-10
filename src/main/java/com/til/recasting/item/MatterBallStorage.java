package com.til.recasting.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 物质球 NBT 存取：类型无限，单类型数量为 long（无尽贪婪式）。
 * <p>
 * 条目格式 {@code MatterItems:[{Item:<template NBT Count=1>, Count:long}, ...]}，
 * 类型键与 {@link ItemStack#isSameItemSameTags} 一致。
 */
public final class MatterBallStorage {

    private static final String MATTER_ITEMS_KEY = "MatterItems";
    private static final String ENTRY_ITEM_KEY = "Item";
    private static final String ENTRY_COUNT_KEY = "Count";

    private MatterBallStorage() {
    }

    public static boolean isEmpty(@NotNull ItemStack ball) {
        ListTag list = getOrCreateList(ball, false);
        return list == null || list.isEmpty();
    }

    public static @NotNull List<StoredEntry> list(@NotNull ItemStack ball) {
        ListTag list = getOrCreateList(ball, false);
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        List<StoredEntry> result = new ArrayList<>(list.size());
        for(int i = 0; i < list.size(); i++) {
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

    public static long totalCount(@NotNull ItemStack ball) {
        long total = 0L;
        for(StoredEntry entry : list(ball)) {
            total += entry.count();
        }
        return total;
    }

    /**
     * 将物品写入物质球；从 {@code incoming} 扣除已存数量。
     *
     * @return 实际插入数量
     */
    public static long insert(@NotNull ItemStack ball, @NotNull ItemStack incoming) {
        if (incoming.isEmpty()) {
            return 0;
        }
        long amount = incoming.getCount();
        if (amount <= 0) {
            return 0;
        }

        ListTag list = getOrCreateList(ball, true);
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

    public static void copyFrom(@NotNull ItemStack source, @NotNull ItemStack target) {
        clear(target);
        for(StoredEntry entry : list(source)) {
            ItemStack stack = entry.template().copy();
            // insert 按 int count 扣除；按 long 分批写入
            long remain = entry.count();
            while (remain > 0) {
                int chunk = (int) Math.min(remain, Integer.MAX_VALUE);
                stack.setCount(chunk);
                insert(target, stack);
                remain -= chunk;
            }
        }
    }

    public static void clear(@NotNull ItemStack ball) {
        CompoundTag tag = ball.getTag();
        if (tag == null) {
            return;
        }
        tag.remove(MATTER_ITEMS_KEY);
        if (tag.isEmpty()) {
            ball.setTag(null);
        }
    }

    /**
     * 将球内全部内容尽量给予玩家；塞不下的由 {@link ItemHandlerHelper#giveItemToPlayer} 掉落。
     */
    public static void extractToPlayer(@NotNull ItemStack ball, @NotNull Player player) {
        List<StoredEntry> entries = list(ball);
        clear(ball);
        for(StoredEntry entry : entries) {
            long remain = entry.count();
            while (remain > 0) {
                int take = (int) Math.min(remain, entry.template().getMaxStackSize());
                ItemStack give = entry.template().copyWithCount(take);
                ItemHandlerHelper.giveItemToPlayer(player, give);
                remain -= take;
            }
        }
    }

    private static int findMatchingIndex(ListTag list, ItemStack match) {
        for(int i = 0; i < list.size(); i++) {
            CompoundTag entry = list.getCompound(i);
            ItemStack template = ItemStack.of(entry.getCompound(ENTRY_ITEM_KEY));
            if (!template.isEmpty() && ItemStack.isSameItemSameTags(template, match)) {
                return i;
            }
        }
        return -1;
    }

    private static ListTag getOrCreateList(ItemStack ball, boolean create) {
        CompoundTag tag = ball.getTag();
        if (tag == null) {
            if (!create) {
                return null;
            }
            tag = new CompoundTag();
            ball.setTag(tag);
        }
        if (!tag.contains(MATTER_ITEMS_KEY, Tag.TAG_LIST)) {
            if (!create) {
                return null;
            }
            ListTag created = new ListTag();
            tag.put(MATTER_ITEMS_KEY, created);
            return created;
        }
        return tag.getList(MATTER_ITEMS_KEY, Tag.TAG_COMPOUND);
    }

    public record StoredEntry(@NotNull ItemStack template, long count) {
    }
}
