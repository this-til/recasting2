package com.til.recasting.client.handler;

import com.til.recasting.Recasting;
import com.til.recasting.client.renderer.entity.LightningEntityRenderer;
import com.til.recasting.registry.RecastingEntities;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 客户端渲染器注册事件处理器
 */
@Mod.EventBusSubscriber(modid = Recasting.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientRenderHandler {

    /**
     * 注册实体渲染器
     */
    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        // 注册闪电实体渲染器
        event.registerEntityRenderer(RecastingEntities.LIGHTNING.get(), LightningEntityRenderer::new);
    }
}

