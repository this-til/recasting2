package com.til.recasting.handler;

import com.til.recasting.Recasting;
import com.til.recasting.capability.provider.InventorySlashBladeSeCacheProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 为活体实体挂载背包 SE 查询缓存。
 */
@Mod.EventBusSubscriber(modid = Recasting.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class InventorySlashBladeSeCacheHandler {

    private InventorySlashBladeSeCacheHandler() {
    }

    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (!(event.getObject() instanceof LivingEntity)) {
            return;
        }
        InventorySlashBladeSeCacheProvider provider = new InventorySlashBladeSeCacheProvider();
        event.addCapability(Recasting.prefix("inventory_slash_blade_se_cache"), provider);
        event.addListener(provider::invalidate);
    }
}
