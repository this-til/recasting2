package com.til.recasting.handler;

import com.til.recasting.Recasting;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 清理背包 SE 槽位缓存。
 */
@Mod.EventBusSubscriber(modid = Recasting.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class InventorySlashBladeSeCacheHandler {

    private InventorySlashBladeSeCacheHandler() {
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        InventorySlashBladeSeHelper.clearPlayerCache(event.getEntity().getUUID());
    }
}
