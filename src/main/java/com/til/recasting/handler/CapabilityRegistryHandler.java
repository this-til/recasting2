package com.til.recasting.handler;

import com.til.recasting.capability.IBuffStackData;
import com.til.recasting.capability.ISpecialEffectCrystalData;
import com.til.recasting.capability.ITimeRun;
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
    public static final Capability<PropertiesDefinitionExtension> PROPERTIES_DEFINITION_EXTENSION = CapabilityManager.get(new CapabilityToken<>() {});
    public static final Capability<RenderDefinitionExtension> RENDER_DEFINITION_EXTENSION = CapabilityManager.get(new CapabilityToken<>() {});
    public static final Capability<ISpecialEffectCrystalData> SE_CRYSTAL_DATA = CapabilityManager.get(new CapabilityToken<>() {});
    public static final Capability<ITimeRun> TIME_RUN = CapabilityManager.get(new CapabilityToken<>() {});
    public static final Capability<IBuffStackData> BUFF_STACK_DATA = CapabilityManager.get(new CapabilityToken<>() {});



    @SubscribeEvent
    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.register(PropertiesDefinitionExtension.class);
        event.register(RenderDefinitionExtension.class);
        event.register(ISpecialEffectCrystalData.class);
        event.register(IBuffStackData.class);
        event.register(ITimeRun.class);
    }
}

