package com.til.recasting.handler;

import com.til.recasting.capability.ITimeRun;
import com.til.recasting.registry.RecastingAttachments;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TimeRun 集中激活表：仅驱动当前有定时任务的实体。
 */
public final class TimeRunManage {

    private static final Set<LivingEntity> ACTIVE = ConcurrentHashMap.newKeySet();

    private TimeRunManage() {
    }

    /**
     * 将实体加入激活集（仅服务端）。
     */
    public static void activate(LivingEntity entity) {
        if (entity == null || entity.level().isClientSide()) {
            return;
        }
        ACTIVE.add(entity);
    }

    /**
     * 将实体移出激活集。
     */
    public static void deactivate(LivingEntity entity) {
        if (entity == null) {
            return;
        }
        ACTIVE.remove(entity);
    }

    /**
     * 对激活集内实体执行一次 {@link ITimeRun#tick()}。
     */
    public static void tickAll() {
        if (ACTIVE.isEmpty()) {
            return;
        }

        for (LivingEntity entity : List.copyOf(ACTIVE)) {
            if (entity.isRemoved()) {
                ACTIVE.remove(entity);
                continue;
            }
            RecastingAttachments.timeRun(entity).tick();
        }
    }

}
