package com.til.recasting.registry.buff;

import com.til.recasting.capability.IBuffStackData;
import com.til.recasting.capability.ITimeRun;
import com.til.recasting.handler.AttackHelper;
import com.til.recasting.handler.BuffSourceHelper;
import com.til.recasting.handler.CapabilityRegistryHandler;
import com.til.recasting.registry.RecastingAttackTypes;
import com.til.recasting.registry.instance.BuffType;
import com.til.recasting.util.DamageStructure;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

/**
 * 光子灼烧：周期按层结算火焰伤害。
 */
@Getter
@Setter
@Accessors(chain = true)
public class PhotonBurnBuffType extends BuffType {

    private static final String TIMER_NAME = "photon_burn_tick";

    float damagePerStack = 0.1f;
    int ticksPerInterval = 10;

    public PhotonBurnBuffType() {
        decayInterval = 60;
        maxLevel = 50;
    }

    public void ensureTimer(LivingEntity target) {
        if (target.level().isClientSide()) {
            return;
        }

        target.getCapability(CapabilityRegistryHandler.TIME_RUN).ifPresent(timeRun -> {
            if (timeRun.getNamedTimerCell(TIMER_NAME) != null) {
                return;
            }

            timeRun.addNamedTimerCell(
                    TIMER_NAME,
                    new ITimeRun.TimerCell(
                            () -> tick(target, timeRun),
                            ticksPerInterval,
                            true
                    )
            );
        });
    }

    private void tick(LivingEntity target, ITimeRun timeRun) {
        if (!target.isAlive() || target.level().isClientSide()) {
            timeRun.removeNamedTimerCell(TIMER_NAME);
            return;
        }

        target.getCapability(CapabilityRegistryHandler.BUFF_STACK_DATA).ifPresent(data -> {
            int currentLevel = data.getLevel(this, target.level());
            if (currentLevel <= 0) {
                timeRun.removeNamedTimerCell(TIMER_NAME);
                return;
            }

            float damage = currentLevel * damagePerStack;
            if (damage <= 0) {
                timeRun.removeNamedTimerCell(TIMER_NAME);
                return;
            }

            IBuffStackData.BuffEntry entry = data.getEntry(this);
            LivingEntity source = BuffSourceHelper.getSourceEntity(entry, target.level());
            if (source == null) {
                timeRun.removeNamedTimerCell(TIMER_NAME);
                return;
            }

            AttackHelper.attack(
                    source,
                    target,
                    new DamageStructure(0f, damage),
                    List.of(RecastingAttackTypes.PHOTON_BURN_ATTACK.get())
            );
        });
    }
}
