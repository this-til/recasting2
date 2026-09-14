package com.til.recasting.handler;

import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

/**
 * {@code AttackManager}/{@code SakuraEnd.doSlash} 在投递事件前的方法参数。
 * 原版 {@code DoSlashEvent} 不含 offset / mute / color。
 */
public final class DoSlashArgsCapture {

    private static final ThreadLocal<Captured> CURRENT = new ThreadLocal<>();

    private DoSlashArgsCapture() {
    }

    public record Captured(Vec3 centerOffset, boolean mute, int colorCode) {
    }

    public static void set(Vec3 centerOffset, boolean mute, int colorCode) {
        CURRENT.set(new Captured(centerOffset == null ? Vec3.ZERO : centerOffset, mute, colorCode));
    }

    @Nullable
    public static Captured get() {
        return CURRENT.get();
    }

    public static void clear() {
        CURRENT.remove();
    }
}
