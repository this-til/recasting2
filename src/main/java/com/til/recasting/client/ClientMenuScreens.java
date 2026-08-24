package com.til.recasting.client;

import com.til.recasting.Recasting;
import com.til.recasting.registry.RecastingMenus;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import com.til.recasting.inventory.ProudSoulBagMenu;

/**
 * P4：耀魂背包 Screen 占位，完整 UI 待 P5。
 */
@EventBusSubscriber(modid = Recasting.MODID, value = Dist.CLIENT)
public final class ClientMenuScreens {

    private ClientMenuScreens() {
    }

    @SubscribeEvent
    public static void onRegisterMenuScreens(RegisterMenuScreensEvent event) {
        event.register(RecastingMenus.PROUD_SOUL_BAG.get(), ProudSoulBagScreen::new);
    }

    public static final class ProudSoulBagScreen extends AbstractContainerScreen<ProudSoulBagMenu> {

        public ProudSoulBagScreen(ProudSoulBagMenu menu, Inventory inventory, Component title) {
            super(menu, inventory, title);
        }

        @Override
        protected void renderBg(net.minecraft.client.gui.GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
            // ponytail: empty screen placeholder until P5 UI
        }
    }
}
