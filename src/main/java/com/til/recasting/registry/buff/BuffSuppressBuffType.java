package com.til.recasting.registry.buff;

import com.til.recasting.capability.ITimeRun;
import com.til.recasting.handler.CapabilityRegistryHandler;
import com.til.recasting.registry.instance.BuffType;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

/**
 * 增益压制：持有期间每 tick 驱散增益效果。
 */
@Getter
@Setter
@Accessors(chain = true)
public class BuffSuppressBuffType extends BuffType {

    private static final String TIMER = "buff_suppress_tick";

    public BuffSuppressBuffType() {
        decayInterval = 20;
        maxLevel = 0;
    }

    public void apply(LivingEntity target, int seconds) {
        if (target.level().isClientSide()) {
            return;
        }
        target.getCapability(CapabilityRegistryHandler.BUFF_STACK_DATA).ifPresent(data -> {
            data.setLevel(this, Math.max(1, seconds), target.level());
        });
        ensureTimer(target);
        dispelBeneficial(target);
    }

    public void ensureTimer(LivingEntity target) {
        target.getCapability(CapabilityRegistryHandler.TIME_RUN).ifPresent(timeRun -> {
            if (timeRun.getNamedTimerCell(TIMER) != null) {
                return;
            }
            timeRun.addNamedTimerCell(
                    TIMER,
                    new ITimeRun.TimerCell(
                            () -> tick(target, timeRun),
                            1,
                            true
                    )
            );
        });
    }

    private void tick(LivingEntity target, ITimeRun timeRun) {
        if (!target.isAlive() || target.level().isClientSide()) {
            timeRun.removeNamedTimerCell(TIMER);
            return;
        }
        Level world = target.level();
        target.getCapability(CapabilityRegistryHandler.BUFF_STACK_DATA).ifPresent(data -> {
            int level = data.getLevel(this, world);
            if (level <= 0) {
                timeRun.removeNamedTimerCell(TIMER);
                return;
            }
            dispelBeneficial(target);
        });
    }

    public void dispelBeneficial(LivingEntity entity) {
        List<MobEffectInstance> toRemove = new ArrayList<>();
        for(MobEffectInstance instance : entity.getActiveEffects()) {
            if (instance.getEffect().isBeneficial()) {
                toRemove.add(instance);
            }
        }
        for(MobEffectInstance instance : toRemove) {
            entity.removeEffect(instance.getEffect());
        }
    }

    public void dispelHarmful(LivingEntity entity) {
        List<MobEffectInstance> toRemove = new ArrayList<>();
        for(MobEffectInstance instance : entity.getActiveEffects()) {
            if (!instance.getEffect().isBeneficial()) {
                toRemove.add(instance);
            }
        }
        for(MobEffectInstance instance : toRemove) {
            entity.removeEffect(instance.getEffect());
        }
    }
}
