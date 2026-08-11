package com.til.recasting.handler;

import com.til.recasting.Recasting;
import com.til.recasting.capability.ITimeRun;
import com.til.recasting.capability.provider.TimeRunProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
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
        if (!(event.getObject() instanceof LivingEntity)) {
            return;
        }

        TimeRunProvider provider = new TimeRunProvider();
        event.addCapability(Recasting.prefix("time_run"), provider);

        // 确保在实体移除时失效
        event.addListener(provider::invalidate);
    }

    /**
     * 每 tick 更新定时器
     */
    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if (!entity.level().isClientSide()) {
            entity.getCapability(CapabilityRegistryHandler.TIME_RUN).ifPresent(ITimeRun::tick);
        }
    }
}

