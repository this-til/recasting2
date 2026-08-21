package com.til.recasting.handler;

import com.til.recasting.Recasting;
import com.til.recasting.capability.provider.TimeRunProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * TimeRun 事件处理器
 * 负责附加 Capability 和更新定时器
 */
@Mod.EventBusSubscriber(modid = Recasting.MODID)
public class TimeRunEventHandler {

    /**
     * 为 LivingEntity 附加 TimeRun Capability
     */
    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (!(event.getObject() instanceof LivingEntity livingEntity)) {
            return;
        }

        TimeRunProvider provider = new TimeRunProvider();
        provider.setEntity(livingEntity);
        event.addCapability(Recasting.prefix("time_run"), provider);

        // 确保在实体移除时失效
        event.addListener(provider::invalidate);
    }

    /**
     * 仅驱动激活集内实体的定时器（服务端）
     */
    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        TimeRunManage.tickAll();
    }

    /**
     * 实体离开维度时从激活集移除
     */
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
