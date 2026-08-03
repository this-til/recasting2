package com.til.recasting.handler;

import com.til.recasting.Recasting;
import com.til.recasting.capability.ITimeRun;
import com.til.recasting.registry.RecastingBuffTypes;
import com.til.recasting.registry.instance.BuffType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

/**
 * 增益压制：持有 Buff 期间每 tick 驱散增益效果。
 */
@Mod.EventBusSubscriber(modid = Recasting.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class BuffSuppressHandler {

    private static final String TIMER = "buff_suppress_tick";

    private BuffSuppressHandler() {
    }

    public static void apply(LivingEntity target, int seconds) {
        if (target.level().isClientSide()) {
            return;
        }
        BuffType buffType = RecastingBuffTypes.BUFF_SUPPRESS.get();
        target.getCapability(CapabilityRegistryHandler.BUFF_STACK_DATA).ifPresent(data -> {
            data.setLevel(buffType, Math.max(1, seconds), target.level());
        });
        ensureTimer(target);
        dispelBeneficial(target);
    }

    public static void ensureTimer(LivingEntity target) {
        target.getCapability(CapabilityRegistryHandler.TIME_RUN).ifPresent(timeRun -> {
            if (timeRun.getNamedTimerCell(TIMER) != null) {
                return;
            }
            BuffType buffType = RecastingBuffTypes.BUFF_SUPPRESS.get();
            timeRun.addNamedTimerCell(
                    TIMER,
                    new ITimeRun.TimerCell(
                            () -> tick(target, buffType, timeRun),
                            1,
                            true
                    )
            );
        });
    }

    private static void tick(LivingEntity target, BuffType buffType, ITimeRun timeRun) {
        if (!target.isAlive() || target.level().isClientSide()) {
            timeRun.removeNamedTimerCell(TIMER);
            return;
        }
        Level world = target.level();
        target.getCapability(CapabilityRegistryHandler.BUFF_STACK_DATA).ifPresent(data -> {
            int level = data.getLevel(buffType, world);
            if (level <= 0) {
                timeRun.removeNamedTimerCell(TIMER);
                return;
            }
            dispelBeneficial(target);
        });
    }

    public static void dispelBeneficial(LivingEntity entity) {
        List<MobEffectInstance> toRemove = new ArrayList<>();
        for (MobEffectInstance instance : entity.getActiveEffects()) {
            if (instance.getEffect().isBeneficial()) {
                toRemove.add(instance);
            }
        }
        for (MobEffectInstance instance : toRemove) {
            entity.removeEffect(instance.getEffect());
        }
    }

    public static void dispelHarmful(LivingEntity entity) {
        List<MobEffectInstance> toRemove = new ArrayList<>();
        for (MobEffectInstance instance : entity.getActiveEffects()) {
            if (!instance.getEffect().isBeneficial()) {
                toRemove.add(instance);
            }
        }
        for (MobEffectInstance instance : toRemove) {
            entity.removeEffect(instance.getEffect());
        }
    }
}
