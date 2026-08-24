package com.til.recasting.handler;

import com.til.recasting.Recasting;
import com.til.recasting.registry.RecastingBuffTypes;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.NewRegistryEvent;

/**
 * 自定义注册表接入 NeoForge NewRegistryEvent。
 */
@EventBusSubscriber(modid = Recasting.MODID)
public final class RegistryHandler {

    private RegistryHandler() {
    }

    @SubscribeEvent
    public static void registerRegistries(NewRegistryEvent event) {
        event.register(RecastingBuffTypes.REGISTRY);
    }
}
