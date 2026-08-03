package com.til.recasting.client.handler;

import com.til.recasting.Recasting;
import com.til.recasting.handler.EmperorLineSeHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderBlockScreenEffectEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 人皇领域生效时取消原版着火屏幕叠加层。
 */
@Mod.EventBusSubscriber(modid = Recasting.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
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
