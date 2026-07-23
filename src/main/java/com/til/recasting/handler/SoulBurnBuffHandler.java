package com.til.recasting.handler;

import com.til.recasting.Recasting;
import com.til.recasting.capability.IBuffStackData;
import com.til.recasting.capability.ITimeRun;
import com.til.recasting.registry.RecastingAttackTypes;
import com.til.recasting.registry.RecastingBuffTypes;
import com.til.recasting.registry.instance.BuffType;
import com.til.recasting.util.DamageStructure;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

/**
 * 灵魂燃烧Buff处理器
 * 功能：
 * 每秒对拥有 soul_burn buff 的实体造成当前生命值 6% 的火属性伤害
 * 使用 LivingEvent.LivingTickEvent 处理，每20个tick（1秒）触发一次
 */
@Mod.EventBusSubscriber(modid = Recasting.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SoulBurnBuffHandler {

    private static final String TIMER_NAME = "soul_burn_tick";

    /**
     * 火焰伤害比例（对当前生命值的百分比）
     */
    private static final float FIRE_DAMAGE_PERCENTAGE = 0.06f; // 6%

    /**
     * 每秒触发的tick间隔（20 tick = 1秒）
     */
    private static final int TICKS_PER_SECOND = 20;

    /**
     * 确保目标已注册灵魂燃烧持续伤害定时器。
     */
    public static void ensureSoulBurnTimer(LivingEntity target) {
        if (target.level().isClientSide()) {
            return;
        }

        BuffType soulBurnBuffType = RecastingBuffTypes.SOUL_BURN.get();
        target.getCapability(CapabilityRegistryHandler.TIME_RUN).ifPresent(timeRun -> {
            if (timeRun.getNamedTimerCell(TIMER_NAME) != null) {
                return;
            }

            timeRun.addNamedTimerCell(
                    TIMER_NAME,
                    new ITimeRun.TimerCell(
                            () -> tickSoulBurn(target, soulBurnBuffType, timeRun),
                            TICKS_PER_SECOND,
                            true
                    )
            );
        });
    }

    private static void tickSoulBurn(LivingEntity target, BuffType soulBurnBuffType, ITimeRun timeRun) {
        if (!target.isAlive() || target.level().isClientSide()) {
            removeSoulBurnTimer(timeRun);
            return;
        }

        target.getCapability(CapabilityRegistryHandler.BUFF_STACK_DATA).ifPresent(data -> {
            int currentLevel = data.getLevel(soulBurnBuffType, target.level());
            if (currentLevel <= 0) {
                removeSoulBurnTimer(timeRun);
                return;
            }

            float currentHealth = target.getHealth();
            float fireDamage = currentHealth * FIRE_DAMAGE_PERCENTAGE;
            if (fireDamage <= 0) {
                removeSoulBurnTimer(timeRun);
                return;
            }

            IBuffStackData.BuffEntry entry = data.getEntry(soulBurnBuffType);
            LivingEntity source = BuffSourceHelper.getSourceEntity(entry, target.level());
            if (source == null) {
                removeSoulBurnTimer(timeRun);
                return;
            }

            AttackHelper.attack(
                    source,
                    target,
                    new DamageStructure(0f, fireDamage),
                    List.of(
                            RecastingAttackTypes.SOUL_BURN_ATTACK.get(),
                            RecastingAttackTypes.NO_RECURSION_ATTACK.get()
                    )
            );
        });
    }

    private static void removeSoulBurnTimer(ITimeRun timeRun) {
        timeRun.removeNamedTimerCell(TIMER_NAME);
    }
}

