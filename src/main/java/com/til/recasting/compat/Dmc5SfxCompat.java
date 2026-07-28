package com.til.recasting.compat;

import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.fml.ModList;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

/**
 * true_power_dmc5_sfx 软依赖：不引入编译依赖，运行时探测 ComboSoundManager，
 * 判断当前连段是否有自定义音表（用于静音 Recasting 斩击实体默认音）。
 */
public final class Dmc5SfxCompat {

    private static final Supplier<Boolean> LOADED = memoize(() -> ModList.get().isLoaded("true_power_dmc5_sfx"));
    private static final Supplier<Object> COMBO_SOUND_MANAGER = memoize(Dmc5SfxCompat::resolveInstance);
    private static final Supplier<Method> GET_SOUNDS = memoize(Dmc5SfxCompat::resolveGetSounds);

    private Dmc5SfxCompat() {
    }

    /**
     * 当前主手刀连段在 DMC5 音表中有条目时返回 true，应对齐对方对 EntitySlashEffect 的 setSilent 意图。
     */
    public static boolean shouldMuteSlashEffect(LivingEntity player) {
        Object comboSoundManager = COMBO_SOUND_MANAGER.get();
        Method getSounds = GET_SOUNDS.get();
        if (!LOADED.get() || comboSoundManager == null || getSounds == null || player == null) {
            return false;
        }

        return player.getMainHandItem().getCapability(ItemSlashBlade.BLADESTATE)
                .map(state -> {
                    ResourceLocation comboId = state.getComboSeq();
                    if (comboId == null) {
                        return false;
                    }
                    try {
                        Object sounds = getSounds.invoke(comboSoundManager, comboId);
                        return sounds instanceof Collection<?> collection && !collection.isEmpty();
                    } catch (Throwable ignored) {
                        return false;
                    }
                })
                .orElse(false);
    }

    @Nullable
    private static Object resolveInstance() {
        if (!LOADED.get()) {
            return null;
        }
        try {
            Class<?> clazz = Class.forName(
                    "net.mrqx.slashblade.sfx.dmc5.data.combo.ComboSoundManager",
                    false,
                    Dmc5SfxCompat.class.getClassLoader()
            );
            Field field = clazz.getField("INSTANCE");
            return field.get(null);
        } catch (Throwable ignored) {
            return null;
        }
    }

    @Nullable
    private static Method resolveGetSounds() {
        Object comboSoundManager = COMBO_SOUND_MANAGER.get();
        if (comboSoundManager == null) {
            return null;
        }
        try {
            return comboSoundManager.getClass().getMethod("getSounds", ResourceLocation.class);
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
