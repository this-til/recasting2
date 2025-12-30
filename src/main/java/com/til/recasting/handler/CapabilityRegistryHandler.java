package com.til.recasting.handler;

import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.capability.RenderDefinitionExtension;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.til.recasting.Recasting.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CapabilityRegistryHandler {
    public static final Capability<PropertiesDefinitionExtension> PROPERTIES_DEFINITION_EXTENSION =
            CapabilityManager.get(new CapabilityToken<>() {
            });
    public static final Capability<RenderDefinitionExtension> RENDER_DEFINITION_EXTENSION =
            CapabilityManager.get(new CapabilityToken<>() {
            });


    @SubscribeEvent
    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.register(PropertiesDefinitionExtension.class);
        event.register(RenderDefinitionExtension.class);
    }
}

