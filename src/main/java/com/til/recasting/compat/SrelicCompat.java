package com.til.recasting.compat;

import net.minecraft.world.item.ItemStack;

/**
 * srelic 软依赖占位：正式兼容移植前始终返回 false。
 */
public final class SrelicCompat {

    private SrelicCompat() {
    }

    public static boolean isSrelicBlade(ItemStack stack) {
        return false;
    }
}
