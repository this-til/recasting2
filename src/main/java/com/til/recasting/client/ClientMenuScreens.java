package com.til.recasting.client;

import com.til.recasting.Recasting;
import com.til.recasting.client.screen.ProudSoulBagScreen;
import com.til.recasting.registry.RecastingMenus;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = Recasting.MODID, value = Dist.CLIENT)
public final class ClientMenuScreens {

    private ClientMenuScreens() {
    }

    @SubscribeEvent
    public static void onRegisterMenuScreens(RegisterMenuScreensEvent event) {
        event.register(RecastingMenus.PROUD_SOUL_BAG.get(), ProudSoulBagScreen::new);
    }
}
