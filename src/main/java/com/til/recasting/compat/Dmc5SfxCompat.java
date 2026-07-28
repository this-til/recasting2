package com.til.recasting.compat;

import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.fml.ModList;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;

/**
 * true_power_dmc5_sfx 软依赖：不引入编译依赖，运行时探测 ComboSoundManager，
 * 判断当前连段是否有自定义音表（用于静音 Recasting 斩击实体默认音）。
 */
public final class Dmc5SfxCompat {

    private static final boolean LOADED = ModList.get().isLoaded("true_power_dmc5_sfx");

    @Nullable
    private static final Object COMBO_SOUND_MANAGER = resolveInstance();

    @Nullable
    private static final Method GET_SOUNDS = resolveGetSounds();

    private Dmc5SfxCompat() {
    }

    /**
     * 当前主手刀连段在 DMC5 音表中有条目时返回 true，应对齐对方对 EntitySlashEffect 的 setSilent 意图。
     */
    public static boolean shouldMuteSlashEffect(LivingEntity player) {
        if (!LOADED || COMBO_SOUND_MANAGER == null || GET_SOUNDS == null || player == null) {
            return false;
        }

        return player.getMainHandItem().getCapability(ItemSlashBlade.BLADESTATE)
                .map(state -> {
                    ResourceLocation comboId = state.getComboSeq();
                    if (comboId == null) {
                        return false;
                    }
                    try {
                        Object sounds = GET_SOUNDS.invoke(COMBO_SOUND_MANAGER, comboId);
                        return sounds instanceof Collection<?> collection && !collection.isEmpty();
                    } catch (Throwable ignored) {
                        return false;
                    }
                })
                .orElse(false);
    }

    @Nullable
    private static Object resolveInstance() {
        if (!LOADED) {
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
        if (COMBO_SOUND_MANAGER == null) {
            return null;
        }
        try {
            return COMBO_SOUND_MANAGER.getClass().getMethod("getSounds", ResourceLocation.class);
        } catch (Throwable ignored) {
            return null;
        }
    }
}
