package com.til.recasting.handler;

import com.til.recasting.Recasting;
import com.til.recasting.capability.IBuffStackData;
import com.til.recasting.capability.ITimeRun;
import com.til.recasting.event.AttackAmplifierEvent;
import com.til.recasting.registry.RecastingAttackTypes;
import com.til.recasting.registry.RecastingBuffTypes;
import com.til.recasting.registry.instance.BuffType;
import com.til.recasting.util.DamageStructure;
import java.util.List;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 翠火 Buff 处理器
 * <ul>
 *   <li>服务端每秒造成固定火焰伤害</li>
 *   <li>目标承受的全部伤害按层提高</li>
 * </ul>
 */
@Mod.EventBusSubscriber(modid = Recasting.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class JadeFireBuffHandler {

    private static final String TIMER_NAME = "jade_fire_tick";
    private static final float FIXED_FIRE_DAMAGE = 0.5f;
    private static final float DAMAGE_AMP_PER_LEVEL = 0.4f;
    private static final int TICKS_PER_INTERVAL = 20;

    private JadeFireBuffHandler() {
    }

    public static void ensureJadeFireTimer(LivingEntity target) {
        if (target.level().isClientSide()) {
            return;
        }

        BuffType jadeFireBuffType = RecastingBuffTypes.JADE_FIRE.get();
        target.getCapability(CapabilityRegistryHandler.TIME_RUN).ifPresent(timeRun -> {
            if (timeRun.getNamedTimerCell(TIMER_NAME) != null) {
                return;
            }

            timeRun.addNamedTimerCell(
                    TIMER_NAME,
                    new ITimeRun.TimerCell(
                            () -> tickJadeFire(target, jadeFireBuffType, timeRun),
                            TICKS_PER_INTERVAL,
                            true
                    )
            );
        });
    }

    private static void tickJadeFire(LivingEntity target, BuffType jadeFireBuffType, ITimeRun timeRun) {
        if (!target.isAlive() || target.level().isClientSide()) {
            removeJadeFireTimer(timeRun);
            return;
        }

        target.getCapability(CapabilityRegistryHandler.BUFF_STACK_DATA).ifPresent(data -> {
            int currentLevel = data.getLevel(jadeFireBuffType, target.level());
            if (currentLevel <= 0) {
                removeJadeFireTimer(timeRun);
                return;
            }

            IBuffStackData.BuffEntry entry = data.getEntry(jadeFireBuffType);
            LivingEntity source = BuffSourceHelper.getSourceEntity(entry, target.level());
            if (source == null) {
                removeJadeFireTimer(timeRun);
                return;
            }

            AttackHelper.attack(
                    source,
                    target,
                    new DamageStructure(0f, FIXED_FIRE_DAMAGE),
                    List.of(RecastingAttackTypes.JADE_FIRE_ATTACK.get())
            );
        });
    }

    private static void removeJadeFireTimer(ITimeRun timeRun) {
        timeRun.removeNamedTimerCell(TIMER_NAME);
    }

    @SubscribeEvent
    public static void onAttackAmplifier(AttackAmplifierEvent event) {
        if (event.getAttacker().level().isClientSide()) {
            return;
        }

        if (!(event.getTarget() instanceof LivingEntity target) || !target.isAlive()) {
            return;
        }

        target.getCapability(CapabilityRegistryHandler.BUFF_STACK_DATA).ifPresent(data -> {
            int currentLevel = data.getLevel(RecastingBuffTypes.JADE_FIRE.get(), target.level());
            if (currentLevel > 0) {
                event.addModifiedRatioAmplifier(currentLevel * DAMAGE_AMP_PER_LEVEL);
            }
        });
    }
}
