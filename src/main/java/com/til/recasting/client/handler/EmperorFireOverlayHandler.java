package com.til.recasting.client.handler;

import com.til.recasting.Recasting;
import com.til.recasting.handler.EmperorLineSeHelper;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.RenderBlockScreenEffectEvent;
import net.neoforged.bus.api.SubscribeEvent;

/**
 * 人皇领域生效时取消原版着火屏幕叠加层。
 */
@EventBusSubscriber(modid = Recasting.MODID, value = Dist.CLIENT)
public final class EmperorFireOverlayHandler {

    private EmperorFireOverlayHandler() {
    }

    @SubscribeEvent
    public static void onRenderBlockScreenEffect(RenderBlockScreenEffectEvent event) {
        if (event.getOverlayType() != RenderBlockScreenEffectEvent.OverlayType.FIRE) {
            return;
        }
        if (EmperorLineSeHelper.resolveHighestEmperor(event.getPlayer()) == null) {
            return;
        }
        event.setCanceled(true);
    }
}
