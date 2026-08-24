package com.til.recasting.registry.buff;

import com.til.recasting.capability.IBuffStackData;
import com.til.recasting.capability.ITimeRun;
import com.til.recasting.handler.AttackHelper;
import com.til.recasting.handler.BuffSourceHelper;
import com.til.recasting.registry.RecastingAttachments;
import com.til.recasting.registry.RecastingAttackTypes;
import com.til.recasting.registry.instance.BuffType;
import com.til.recasting.util.DamageStructure;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

/**
 * 灵魂燃烧：每秒造成当前生命值比例的火属性伤害。
 */
@Getter
@Setter
@Accessors(chain = true)
public class SoulBurnBuffType extends BuffType {

    private static final String TIMER_NAME = "soul_burn_tick";

    float fireDamagePercentage = 0.06f;
    int ticksPerInterval = 20;

    public SoulBurnBuffType() {
        decayInterval = 20;
        maxLevel = 99;
    }

    public void ensureTimer(LivingEntity target) {
        if (target.level().isClientSide()) {
            return;
        }

        ITimeRun timeRun = RecastingAttachments.timeRun(target);
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
    }

    private void tick(LivingEntity target, ITimeRun timeRun) {
        if (!target.isAlive() || target.level().isClientSide()) {
            timeRun.removeNamedTimerCell(TIMER_NAME);
            return;
        }

        IBuffStackData data = RecastingAttachments.buffStackData(target);
        int currentLevel = data.getLevel(this, target.level());
        if (currentLevel <= 0) {
            timeRun.removeNamedTimerCell(TIMER_NAME);
            return;
        }

        float fireDamage = target.getHealth() * fireDamagePercentage;
        if (fireDamage <= 0) {
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
                new DamageStructure(0f, fireDamage),
                List.of(
                        RecastingAttackTypes.SOUL_BURN_ATTACK.get(),
                        RecastingAttackTypes.NO_KNOCKBACK_ATTACK.get()
                )
        );
    }
}
