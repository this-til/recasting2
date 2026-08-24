package com.til.recasting.registry.buff;

import com.til.recasting.capability.IBuffStackData;
import com.til.recasting.capability.ITimeRun;
import com.til.recasting.event.AttackAmplifierEvent;
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
import net.neoforged.bus.api.SubscribeEvent;

import java.util.List;

/**
 * 翠火：周期固定火焰伤害；按层提高所受伤害。
 */
@Getter
@Setter
@Accessors(chain = true)
public class JadeFireBuffType extends BuffType {

    private static final String TIMER_NAME = "jade_fire_tick";

    float fixedFireDamage = 0.5f;
    float damageAmpAtMaxLevel = 0.4f;
    int ticksPerInterval = 20;

    public JadeFireBuffType() {
        decayInterval = 20;
        maxLevel = 40;
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

        IBuffStackData.BuffEntry entry = data.getEntry(this);
        LivingEntity source = BuffSourceHelper.getSourceEntity(entry, target.level());
        if (source == null) {
            timeRun.removeNamedTimerCell(TIMER_NAME);
            return;
        }

        AttackHelper.attack(
                source,
                target,
                new DamageStructure(0f, fixedFireDamage),
                List.of(
                        RecastingAttackTypes.JADE_FIRE_ATTACK.get(),
                        RecastingAttackTypes.NO_KNOCKBACK_ATTACK.get()
                )
        );
    }

    @SubscribeEvent
    public void onAttackAmplifier(AttackAmplifierEvent event) {
        if (event.getAttacker().level().isClientSide()) {
            return;
        }

        if (!(event.getTarget() instanceof LivingEntity target) || !target.isAlive()) {
            return;
        }

        IBuffStackData data = RecastingAttachments.buffStackData(target);
        int currentLevel = data.getLevel(this, target.level());
        if (currentLevel > 0 && maxLevel > 0) {
            event.addModifiedRatioAmplifier(damageAmpAtMaxLevel * currentLevel / (float) maxLevel);
        }
    }
}
