package com.til.recasting.handler;

/**
 * 标记当前线程上的 setHealth 为模组合法扣血（咒令自残 / 绝对伤害），
 * 使人皇领域的 setHealth Mixin 不拦截。
 */
public final class AbsoluteHealthChangeGuard {

    private static final ThreadLocal<Integer> DEPTH = ThreadLocal.withInitial(() -> 0);

    private AbsoluteHealthChangeGuard() {
    }

    public static boolean isGuarded() {
        return DEPTH.get() > 0;
    }

    public static void run(Runnable action) {
        DEPTH.set(DEPTH.get() + 1);
        try {
            action.run();
        } finally {
            int next = DEPTH.get() - 1;
            if (next <= 0) {
                DEPTH.remove();
            } else {
                DEPTH.set(next);
            }
        }
    }
}
