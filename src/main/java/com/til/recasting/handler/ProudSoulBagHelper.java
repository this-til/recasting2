package com.til.recasting.handler;

import com.til.recasting.item.ProudSoulBagItem;
import com.til.recasting.item.ProudSoulBagStorage;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * 耀魂背包查找与吸入的中立工具。
 */
public final class ProudSoulBagHelper {

    private ProudSoulBagHelper() {
    }

    /**
     * 在玩家物品栏中查找第一只耀魂背包（含副手）。
     */
    public static @NotNull ItemStack findBag(@NotNull Player player) {
        Inventory inventory = player.getInventory();
        ItemStack offhand = inventory.offhand.get(0);
        if (offhand.getItem() instanceof ProudSoulBagItem) {
            return offhand;
        }
        for (int i = 0; i < inventory.items.size(); i++) {
            ItemStack stack = inventory.items.get(i);
            if (stack.getItem() instanceof ProudSoulBagItem) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    /**
     * 尝试将堆叠吸入任意耀魂背包；成功则 {@code stack} 被清空或缩减。
     *
     * @return 是否至少插入了 1 个
     */
    public static boolean tryInsertIntoAnyBag(@NotNull Player player, @NotNull ItemStack stack) {
        if (!ProudSoulBagStorage.isProudSoul(stack) || stack.isEmpty()) {
            return false;
        }
        ItemStack bag = findBag(player);
        if (bag.isEmpty()) {
            return false;
        }
        long inserted = ProudSoulBagStorage.insert(bag, stack);
        return inserted > 0;
    }
}
