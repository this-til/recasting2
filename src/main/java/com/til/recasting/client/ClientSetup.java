package com.til.recasting.client;

import com.til.recasting.Recasting;
import com.til.recasting.client.handler.ClientRenderHandler;
import com.til.recasting.client.registry.EntityRenderExtensionRegistry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * 客户端初始化
 * 仅在客户端执行
 */
@Mod.EventBusSubscriber(modid = Recasting.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {

    /**
     * 客户端设置事件
     */
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        @SuppressWarnings("removal")
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // 注册实体渲染扩展注册表（客户端专用）
        EntityRenderExtensionRegistry.ENTITY_RENDER_EXTENSIONS.register(modEventBus);
    }
}

