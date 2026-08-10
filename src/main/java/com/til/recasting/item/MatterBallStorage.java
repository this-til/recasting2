package com.til.recasting.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.PlayerMainInvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
            long remain = entry.count();
            while (remain > 0) {
                int chunk = (int) Math.min(remain, Integer.MAX_VALUE);
                stack.setCount(chunk);
                insert(target, stack);
                remain -= chunk;
            }
        }
    }

    /** 将 {@code source} 全部内容并入 {@code target}，并清空 source。 */
    public static void mergeFrom(@NotNull ItemStack source, @NotNull ItemStack target) {
        if (source == target || isEmpty(source)) {
            clear(source);
            return;
        }
        for(StoredEntry entry : list(source)) {
            ItemStack stack = entry.template().copy();
            long remain = entry.count();
            while (remain > 0) {
                int chunk = (int) Math.min(remain, Integer.MAX_VALUE);
                stack.setCount(chunk);
                insert(target, stack);
                remain -= chunk;
            }
        }
        clear(source);
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
     * 尽量填入玩家主背包（热键栏+背包，不含护甲/副手）；塞不下的留在球内。
     * <p>
     * 不走 {@code Inventory#add}：创造模式下塞不下时会把剩余直接 {@code setCount(0)}，造成球内物品被误扣销毁。
     *
     * @return 实际取出数量
     */
    public static long extractToPlayer(@NotNull ItemStack ball, @NotNull Player player) {
        return extractToHandler(ball, new PlayerMainInvWrapper(player.getInventory()));
    }

    /**
     * 尽量填入物品处理器（容器）；塞不下的留在球内。
     *
     * @return 实际取出数量
     */
    public static long extractToHandler(@NotNull ItemStack ball, @NotNull IItemHandler handler) {
        ListTag list = getOrCreateList(ball, false);
        if (list == null || list.isEmpty()) {
            return 0L;
        }
        long total = 0L;
        for(int i = 0; i < list.size(); ) {
            CompoundTag entry = list.getCompound(i);
            ItemStack template = ItemStack.of(entry.getCompound(ENTRY_ITEM_KEY));
            if (template.isEmpty()) {
                list.remove(i);
                continue;
            }
            template.setCount(1);
            long remain = entry.getLong(ENTRY_COUNT_KEY);
            if (remain <= 0) {
                list.remove(i);
                continue;
            }
            while (remain > 0) {
                int take = (int) Math.min(remain, template.getMaxStackSize());
                ItemStack give = template.copyWithCount(take);
                ItemStack leftover = ItemHandlerHelper.insertItemStacked(handler, give, false);
                int inserted = take - leftover.getCount();
                if (inserted <= 0) {
                    break;
                }
                remain -= inserted;
                total += inserted;
            }
            if (remain <= 0) {
                list.remove(i);
            } else {
                entry.putLong(ENTRY_COUNT_KEY, remain);
                i++;
            }
        }
        if (list.isEmpty()) {
            clear(ball);
        }
        return total;
    }

    /**
     * 查找玩家主背包或副手中的物质球；{@code exclude} 非空时跳过该引用。
     */
    public static @NotNull ItemStack findBall(@NotNull Player player, @Nullable ItemStack exclude) {
        for(ItemStack stack : player.getInventory().items) {
            if (stack.getItem() instanceof MatterBallItem && stack != exclude) {
                return stack;
            }
        }
        ItemStack offhand = player.getOffhandItem();
        if (offhand.getItem() instanceof MatterBallItem && offhand != exclude) {
            return offhand;
        }
        return ItemStack.EMPTY;
    }

    /**
     * 若玩家已有另一颗物质球，则将 {@code incoming} 并入其中并清空 incoming。
     *
     * @return 是否完成合并
     */
    public static boolean absorbIntoExisting(@NotNull Player player, @NotNull ItemStack incoming) {
        if (incoming.isEmpty() || !(incoming.getItem() instanceof MatterBallItem)) {
            return false;
        }
        ItemStack existing = findBall(player, incoming);
        if (existing.isEmpty()) {
            return false;
        }
        mergeFrom(incoming, existing);
        incoming.setCount(0);
        return true;
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
