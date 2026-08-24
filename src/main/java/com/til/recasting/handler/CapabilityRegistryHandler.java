package com.til.recasting.handler;

import com.til.recasting.capability.*;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.til.recasting.Recasting.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CapabilityRegistryHandler {
    public static final Capability<PropertiesDefinitionExtension> PROPERTIES_DEFINITION_EXTENSION = CapabilityManager.get(new CapabilityToken<>() {
    });
    public static final Capability<RenderDefinitionExtension> RENDER_DEFINITION_EXTENSION = CapabilityManager.get(new CapabilityToken<>() {
    });
    public static final Capability<ISpecialEffectCrystalData> SE_CRYSTAL_DATA = CapabilityManager.get(new CapabilityToken<>() {
    });
    public static final Capability<ITimeRun> TIME_RUN = CapabilityManager.get(new CapabilityToken<>() {
    });
    public static final Capability<IBuffStackData> BUFF_STACK_DATA = CapabilityManager.get(new CapabilityToken<>() {
    });
    public static final Capability<IProudSoulDropCooldown> PROUD_SOUL_DROP_COOLDOWN = CapabilityManager.get(new CapabilityToken<>() {
    });
    public static final Capability<IJieYuanDogBond> JIE_YUAN_DOG_BOND = CapabilityManager.get(new CapabilityToken<>() {
    });
    public static final Capability<InventorySlashBladeSeCache> INVENTORY_SLASH_BLADE_SE_CACHE = CapabilityManager.get(new CapabilityToken<>() {
    });

    @SubscribeEvent
    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.register(PropertiesDefinitionExtension.class);
        event.register(RenderDefinitionExtension.class);
        event.register(ISpecialEffectCrystalData.class);
        event.register(IBuffStackData.class);
        event.register(ITimeRun.class);
        event.register(IProudSoulDropCooldown.class);
        event.register(IJieYuanDogBond.class);
        event.register(InventorySlashBladeSeCache.class);
    }
}

