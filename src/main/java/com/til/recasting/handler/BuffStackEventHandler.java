package com.til.recasting.handler;

import com.til.recasting.registry.instance.BuffType;
import net.minecraft.world.entity.LivingEntity;

/**
 * Buff 叠加同步入口。
 */
public final class BuffStackEventHandler {

    // TODO(P4): 移植完整 BuffStackEventHandler（衰减 tick、死亡复制、同步发送等）

    private BuffStackEventHandler() {
    }

    /**
     * 标记指定 buff 需要同步到追踪客户端。
     */
    public static void markForSync(LivingEntity entity, BuffType buffType) {
        if (entity == null || buffType == null || entity.level().isClientSide()) {
            return;
        }
        // TODO(P4/P5): 发送 BuffStackSyncMessage 到追踪客户端
    }
}
