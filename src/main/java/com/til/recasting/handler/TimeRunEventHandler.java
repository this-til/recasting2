package com.til.recasting.handler;

import com.til.recasting.Recasting;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

/**
 * TIME_RUN 定时器驱动（Attachment 已由 {@link com.til.recasting.registry.RecastingAttachments} 提供）。
 */
@EventBusSubscriber(modid = Recasting.MODID)
public final class TimeRunEventHandler {

    private TimeRunEventHandler() {
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        TimeRunManage.tickAll();
    }

    @SubscribeEvent
    public static void onEntityLeaveLevel(EntityLeaveLevelEvent event) {
        if (!(event.getEntity() instanceof LivingEntity livingEntity)) {
            return;
        }
        if (livingEntity.level().isClientSide()) {
            return;
        }
        TimeRunManage.deactivate(livingEntity);
    }
}
