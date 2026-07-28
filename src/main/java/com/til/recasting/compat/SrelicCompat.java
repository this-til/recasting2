package com.til.recasting.compat;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;

import javax.annotation.Nullable;

/**
 * srelic 软依赖探测：不引入编译依赖，仅在运行时识别 {@code ISrelicblade}。
 */
public final class SrelicCompat {

    private static final boolean LOADED = ModList.get().isLoaded("srelic");
    @Nullable
    private static final Class<?> SRELIC_BLADE = resolve("com.dinzeer.srelic.blade.ISrelicblade");

    private SrelicCompat() {
    }

    public static boolean isSrelicBlade(ItemStack stack) {
        if (!LOADED || SRELIC_BLADE == null || stack == null || stack.isEmpty()) {
            return false;
        }
        return SRELIC_BLADE.isInstance(stack.getItem());
    }

    @Nullable
    private static Class<?> resolve(String name) {
        if (!LOADED) {
            return null;
        }
        try {
            return Class.forName(name, false, SrelicCompat.class.getClassLoader());
        } catch (Throwable ignored) {
            return null;
        }
    }
}
