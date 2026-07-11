package com.til.recasting.client;

import com.til.recasting.Recasting;
import com.til.recasting.client.particle.AttackParticleProvider;
import com.til.recasting.client.particle.StarBlinkParticleProvider;
import com.til.recasting.client.registry.BuffLevelRendererRegistry;
import com.til.recasting.client.registry.EntityRenderExtensionRegistry;
import com.til.recasting.registry.RecastingParticleTypes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

/**
 * 客户端初始化
 * 仅在客户端执行
 */
@Mod.EventBusSubscriber(modid = Recasting.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {

    /**
     * 初始化客户端注册表
     * 必须在 mod 构造函数中、在类加载之前调用
     */
    public static void initRegistries(IEventBus modEventBus) {
        EntityRenderExtensionRegistry.ENTITY_RENDER_EXTENSIONS.register(modEventBus);
        BuffLevelRendererRegistry.BUFF_LEVEL_RENDER_CONFIGS.register(modEventBus);
    }

    /**
     * 客户端设置事件
     */
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        // 客户端初始化逻辑
    }

    /**
     * 注册粒子提供者
     */
    @SubscribeEvent
    public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpecial(RecastingParticleTypes.DEFAULT_PARTICLE.get(), new AttackParticleProvider());
        event.registerSpecial(RecastingParticleTypes.STAR_BLINK.get(), new StarBlinkParticleProvider());
    }
}

