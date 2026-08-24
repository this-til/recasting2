package com.til.recasting.client;

import com.til.recasting.Recasting;
import com.til.recasting.client.particle.AttackParticleProvider;
import com.til.recasting.client.particle.BurstRingParticleProvider;
import com.til.recasting.client.particle.GoldenHalberdParticleProvider;
import com.til.recasting.client.particle.LightningHitParticleProvider;
import com.til.recasting.client.particle.MortalDustHitParticleProvider;
import com.til.recasting.client.particle.MortalDustTrailParticleProvider;
import com.til.recasting.client.particle.StarBlinkParticleProvider;
import com.til.recasting.client.particle.TeaAromaParticleProvider;
import com.til.recasting.client.registry.BuffLevelRendererRegistry;
import com.til.recasting.client.registry.EntityClientExtensionRegistry;
import com.til.recasting.client.registry.EntityRenderExtensionRegistry;
import com.til.recasting.registry.RecastingParticleTypes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

/**
 * 客户端初始化，仅在客户端执行。
 */
@EventBusSubscriber(modid = Recasting.MODID, value = Dist.CLIENT)
public final class ClientSetup {

    private ClientSetup() {
    }

    /**
     * 初始化客户端注册表，须在 mod 构造函数中、在类加载之前调用。
     */
    public static void initRegistries(IEventBus modEventBus) {
        EntityClientExtensionRegistry.ENTITY_CLIENT_EXTENSIONS.register(modEventBus);
        EntityRenderExtensionRegistry.ENTITY_RENDER_EXTENSIONS.register(modEventBus);
        BuffLevelRendererRegistry.BUFF_LEVEL_RENDER_CONFIGS.register(modEventBus);
        RecastingShaderHandler.register(modEventBus);
    }

    @SubscribeEvent
    public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpecial(RecastingParticleTypes.DEFAULT_PARTICLE.get(), new AttackParticleProvider());
        event.registerSpecial(RecastingParticleTypes.STAR_BLINK.get(), new StarBlinkParticleProvider());
        event.registerSpecial(RecastingParticleTypes.GOLDEN_HALBERD.get(), new GoldenHalberdParticleProvider());
        event.registerSpecial(RecastingParticleTypes.TEA_AROMA.get(), new TeaAromaParticleProvider());
        event.registerSpecial(RecastingParticleTypes.LIGHTNING_HIT.get(), new LightningHitParticleProvider());
        event.registerSpecial(RecastingParticleTypes.MORTAL_DUST_TRAIL.get(), new MortalDustTrailParticleProvider());
        event.registerSpecial(RecastingParticleTypes.MORTAL_DUST_HIT.get(), new MortalDustHitParticleProvider());
        event.registerSpecial(RecastingParticleTypes.BURST_RING.get(), new BurstRingParticleProvider());
    }
}
