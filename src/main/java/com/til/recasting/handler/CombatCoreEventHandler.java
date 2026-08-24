package com.til.recasting.handler;

import com.til.recasting.Recasting;
import com.til.recasting.event.DoSlashExtendEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

/**
 * P2 战斗核心探针：任意挥刀打通 {@link DoSlashExtendEvent} 时输出日志。
 * TODO(P3): 挥刀事件链验收完成后删除本类（或改为正式调试开关）。
 */
@EventBusSubscriber(modid = Recasting.MODID)
public final class CombatCoreEventHandler {

    private CombatCoreEventHandler() {
    }

    @SubscribeEvent
    public static void onDoSlashExtend(DoSlashExtendEvent event) {
        if (event.getUser().level().isClientSide()) {
            return;
        }
        Recasting.LOGGER.info(
                "[P2] DoSlashExtendEvent user={} roll={} ratio={} range={}",
                event.getUser().getName().getString(),
                event.getRoll(),
                event.getModifiedRatio(),
                event.getAttackRange()
        );
    }
}
