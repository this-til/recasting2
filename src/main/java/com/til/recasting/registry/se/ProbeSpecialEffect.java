package com.til.recasting.registry.se;

import com.til.recasting.Recasting;
import com.til.recasting.event.DoSlashExtendEvent;
import net.neoforged.bus.api.SubscribeEvent;

/**
 * P2 探针 SE：监听 {@link DoSlashExtendEvent}；刀上铭刻本 SE 时打日志。
 * TODO(P3): 正式 SE 批量注册后删除本类与 {@code SpecialEffectsRegistry.PROBE}。
 */
public class ProbeSpecialEffect extends ExtendedSpecialEffect {

    public ProbeSpecialEffect() {
        setMaxLevel(1);
        setSpecial(true);
    }

    @SubscribeEvent
    public void onDoSlashExtend(DoSlashExtendEvent event) {
        if (!hasSpecialEffect(event.getSlashBladeState())) {
            return;
        }
        if (event.getUser().level().isClientSide()) {
            return;
        }
        Recasting.LOGGER.info(
                "[P2] ProbeSpecialEffect.onDoSlashExtend user={} roll={} ratio={}",
                event.getUser().getName().getString(),
                event.getRoll(),
                event.getModifiedRatio()
        );
    }
}
