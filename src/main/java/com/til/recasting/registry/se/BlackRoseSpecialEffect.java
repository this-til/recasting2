package com.til.recasting.registry.se;

import com.til.recasting.capability.ITimeRun;
import com.til.recasting.event.AttackAmplifierEvent;
import com.til.recasting.handler.AttackHelper;
import com.til.recasting.handler.BuffSourceHelper;
import com.til.recasting.handler.CapabilityRegistryHandler;
import com.til.recasting.registry.RecastingAttackTypes;
import com.til.recasting.registry.RecastingBuffTypes;
import com.til.recasting.util.DamageStructure;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.List;

/***
 * 黑色玫瑰
 * 叠加伤害，每 tick 造成伤害，伤害减半
 */
public class BlackRoseSpecialEffect extends ExtendedSpecialEffect {

    private final String timerName = "black_rose_tick";

    float attack = 0.05f;
    float attenuation = 0.75f;
    int attackIntervalTicks = 5;

    @SubscribeEvent
    public void onAttackAmplifier(AttackAmplifierEvent event) {
        LivingEntity target = resolveServerLivingTarget(event);
        if (target == null) {
            return;
        }
        if (event.getAttackTypeList().contains(RecastingAttackTypes.NO_RECURSION_ATTACK.get())) {
            return;
        }

        LivingEntity attacker = event.getAttacker();
        AttributeInstance attribute = attacker.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attribute == null) {
            return;
        }

        float baseDamage = (float) (attribute.getValue() * event.getUltimatelyModifiedRatio());
        baseDamage += event.getExtraDamage();

        int addUnits = Math.max(1, (int) (baseDamage * attack * 10f));
        target.getCapability(CapabilityRegistryHandler.BUFF_STACK_DATA).ifPresent(buffStackData -> {
            int currentUnits = buffStackData.getLevel(RecastingBuffTypes.BLACK_ROSE.get(), target.level());
            buffStackData.setLevel(RecastingBuffTypes.BLACK_ROSE.get(), currentUnits + addUnits, target.level());
            BuffSourceHelper.recordSourceEntity(buffStackData, RecastingBuffTypes.BLACK_ROSE.get(), target, attacker);
            ensureBlackRoseTimer(target);
        });
    }

    private void ensureBlackRoseTimer(LivingEntity target) {
        target.getCapability(CapabilityRegistryHandler.TIME_RUN).ifPresent(timeRun -> {
            if (timeRun.getNamedTimerCell(timerName) != null) {
                return;
            }

            timeRun.addNamedTimerCell(
                    timerName,
                    new ITimeRun.TimerCell(
                            () -> tickBlackRose(target, timeRun),
                            attackIntervalTicks,
                            true
                    )
            );
        });
    }

    private void tickBlackRose(LivingEntity target, ITimeRun timeRun) {
        if (!target.isAlive() || target.level().isClientSide()) {
            timeRun.removeNamedTimerCell(timerName);
            return;
        }

        target.getCapability(CapabilityRegistryHandler.BUFF_STACK_DATA).ifPresent(buffStackData -> {
            int units = buffStackData.getLevel(RecastingBuffTypes.BLACK_ROSE.get(), target.level());
            if (units <= 0) {
                timeRun.removeNamedTimerCell(timerName);
                return;
            }

            var entry = buffStackData.getEntry(RecastingBuffTypes.BLACK_ROSE.get());
            LivingEntity attacker = BuffSourceHelper.getSourceEntity(entry, target.level());
            if (attacker == null) {
                buffStackData.setLevel(RecastingBuffTypes.BLACK_ROSE.get(), 0, target.level());
                timeRun.removeNamedTimerCell(timerName);
                return;
            }

            float damage = units / 10f;
            if (damage <= 0) {
                buffStackData.setLevel(RecastingBuffTypes.BLACK_ROSE.get(), 0, target.level());
                timeRun.removeNamedTimerCell(timerName);
                return;
            }

            AttackHelper.attack(
                    attacker,
                    target,
                    new DamageStructure(0f, damage),
                    List.of(
                            RecastingAttackTypes.BLACK_ROSE_ATTACK.get(),
                            RecastingAttackTypes.NO_RECURSION_ATTACK.get(),
                            RecastingAttackTypes.NO_KNOCKBACK_ATTACK.get()
                    )
            );

            int nextUnits = (int) (units * attenuation);
            if (nextUnits <= 0) {
                buffStackData.setLevel(RecastingBuffTypes.BLACK_ROSE.get(), 0, target.level());
                timeRun.removeNamedTimerCell(timerName);
                return;
            }

            buffStackData.setLevel(RecastingBuffTypes.BLACK_ROSE.get(), nextUnits, target.level());
        });
    }
}
