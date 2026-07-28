package com.til.recasting.compat;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;

import javax.annotation.Nullable;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

/**
 * srelic 软依赖探测：不引入编译依赖，仅在运行时识别 {@code ISrelicblade}。
 */
public final class SrelicCompat {

    private static final Supplier<Boolean> LOADED = memoize(() -> ModList.get().isLoaded("srelic"));
    private static final Supplier<Class<?>> SRELIC_BLADE = memoize(() -> resolve("com.dinzeer.srelic.blade.ISrelicblade"));

    private SrelicCompat() {
    }

    public static boolean isSrelicBlade(ItemStack stack) {
        Class<?> srelicBlade = SRELIC_BLADE.get();
        if (!LOADED.get() || srelicBlade == null || stack == null || stack.isEmpty()) {
            return false;
        }
        return srelicBlade.isInstance(stack.getItem());
    }

    @Nullable
    private static Class<?> resolve(String name) {
        if (!LOADED.get()) {
            return null;
        }
        try {
            return Class.forName(name, false, SrelicCompat.class.getClassLoader());
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static <T> Supplier<T> memoize(Supplier<T> factory) {
        AtomicReference<LazyValue<T>> reference = new AtomicReference<>();
        return () -> {
            LazyValue<T> cached = reference.get();
            if (cached != null) {
                return cached.value();
            }

            T created = factory.get();
            LazyValue<T> lazyValue = new LazyValue<>(created);
            if (reference.compareAndSet(null, lazyValue)) {
                return created;
            }
            return reference.get().value();
        };
    }

    private record LazyValue<T>(T value) {
    }
}
