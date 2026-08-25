package com.til.recasting.item;

import com.til.recasting.handler.SlashBladeRegistryHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.wrapper.PlayerMainInvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 物质球 NBT 存取：类型无限，单类型数量为 long（无尽贪婪式）。
 */
public final class MatterBallStorage {

    private static final HolderLookup.Provider FALLBACK_REGISTRIES =
            RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);

    private static final String MATTER_ITEMS_KEY = "MatterItems";
    private static final String ENTRY_ITEM_KEY = "Item";
    private static final String ENTRY_COUNT_KEY = "Count";

    private MatterBallStorage() {
    }

    /**
     * 附魔等为数据包注册表，必须使用当前世界的 RegistryAccess；静态 BuiltInRegistries 不足以编码 ItemStack。
     */
    private static HolderLookup.Provider registries() {
        return SlashBladeRegistryHelper.getRegistryAccess()
                .map(access -> (HolderLookup.Provider) access)
                .orElse(FALLBACK_REGISTRIES);
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

    public static long totalCount(@NotNull ItemStack ball) {
        long total = 0L;
        for (StoredEntry entry : list(ball)) {
            total += entry.count();
        }
        return total;
    }

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
            entry.put(ENTRY_ITEM_KEY, (CompoundTag) template.saveOptional(registries()));
            entry.putLong(ENTRY_COUNT_KEY, amount);
            list.add(entry);
        }
        incoming.setCount(0);
        return amount;
    }

    public static void copyFrom(@NotNull ItemStack source, @NotNull ItemStack target) {
        clear(target);
        for (StoredEntry entry : list(source)) {
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

    public static void mergeFrom(@NotNull ItemStack source, @NotNull ItemStack target) {
        if (source == target || isEmpty(source)) {
            clear(source);
            return;
        }
        for (StoredEntry entry : list(source)) {
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
        CompoundTag tag = getCustomTag(ball);
        if (tag == null) {
            return;
        }
        tag.remove(MATTER_ITEMS_KEY);
        setCustomTag(ball, tag);
    }

    public static long extractToPlayer(@NotNull ItemStack ball, @NotNull Player player) {
        return extractToHandler(ball, new PlayerMainInvWrapper(player.getInventory()));
    }

    public static long extractToHandler(@NotNull ItemStack ball, @NotNull IItemHandler handler) {
        ListTag list = getOrCreateList(ball, false);
        if (list == null || list.isEmpty()) {
            return 0L;
        }
        long total = 0L;
        for (int i = 0; i < list.size(); ) {
            CompoundTag entry = list.getCompound(i);
            ItemStack template = ItemStack.parseOptional(registries(), entry.getCompound(ENTRY_ITEM_KEY));
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

    public static @NotNull ItemStack findBall(@NotNull Player player, @Nullable ItemStack exclude) {
        for (ItemStack stack : player.getInventory().items) {
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
        for (int i = 0; i < list.size(); i++) {
            CompoundTag entry = list.getCompound(i);
            ItemStack template = ItemStack.parseOptional(registries(), entry.getCompound(ENTRY_ITEM_KEY));
            if (!template.isEmpty() && ItemStack.isSameItemSameComponents(template, match)) {
                return i;
            }
        }
        return -1;
    }

    private static ListTag getOrCreateList(ItemStack ball, boolean create) {
        CompoundTag tag = getCustomTag(ball);
        if (tag == null) {
            if (!create) {
                return null;
            }
            tag = new CompoundTag();
            setCustomTag(ball, tag);
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

    public record StoredEntry(@NotNull ItemStack template, long count) {
    }
}
