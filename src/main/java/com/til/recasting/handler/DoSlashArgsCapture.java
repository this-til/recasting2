package com.til.recasting.handler;

import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * {@code AttackManager}/{@code SakuraEnd.doSlash} 在投递事件前的方法参数。
 * 原版 {@code DoSlashEvent} 不含 offset / mute / color。
 * 使用栈以支持嵌套 {@code doSlash}：内层结束后恢复外层捕获。
 */
public final class DoSlashArgsCapture {

    private static final ThreadLocal<Deque<Captured>> STACK = new ThreadLocal<>();

    private DoSlashArgsCapture() {
    }

    public record Captured(Vec3 centerOffset, boolean mute, int colorCode) {
    }

    public static void push(Vec3 centerOffset, boolean mute, int colorCode) {
        Deque<Captured> stack = STACK.get();
        if (stack == null) {
            stack = new ArrayDeque<>();
            STACK.set(stack);
        }
        stack.addLast(new Captured(centerOffset == null ? Vec3.ZERO : centerOffset, mute, colorCode));
    }

    @Nullable
    public static Captured get() {
        Deque<Captured> stack = STACK.get();
        if (stack == null || stack.isEmpty()) {
            return null;
        }
        return stack.peekLast();
    }

    public static void pop() {
        Deque<Captured> stack = STACK.get();
        if (stack == null || stack.isEmpty()) {
            return;
        }
        stack.removeLast();
        if (stack.isEmpty()) {
            STACK.remove();
        }
    }
}
