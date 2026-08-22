package com.til.recasting.handler;

import com.til.recasting.Recasting;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
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
    public static void onEntityLeaveLevel(EntityLeaveLevelEvent event) {
        if (event.getEntity() instanceof LivingEntity living) {
            InventorySlashBladeSeHelper.clearEntityCache(living.getUUID());
        }
    }
}
