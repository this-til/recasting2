package com.til.recasting.handler;

import com.til.recasting.Recasting;
import com.til.recasting.event.AttackAmplifierEvent;
import com.til.recasting.registry.RecastingBuffTypes;
import com.til.recasting.registry.instance.BuffType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 红尘 Buff 处理器
 * <ul>
 *   <li>按层提高目标所受全部伤害，满层额外 33%</li>
 *   <li>受击时按当前层数造成层数 × 0.1 固定伤害，内置 5 tick 冷却</li>
 * </ul>
 */
@Mod.EventBusSubscriber(modid = Recasting.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class MortalDustBuffHandler {

    private static final float DAMAGE_AMP_AT_MAX_LEVEL = 0.33f;
    private static final float FIXED_DAMAGE_PER_LEVEL = 0.1f;
    private static final int PROC_COOLDOWN_TICKS = 5;

    private MortalDustBuffHandler() {
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
            BuffType mortalDust = RecastingBuffTypes.MORTAL_DUST.get();
            int currentLevel = data.getLevel(mortalDust, target.level());
            int maxLevel = mortalDust.getMaxLevel();
            if (currentLevel <= 0 || maxLevel <= 0) {
                return;
            }

            event.addModifiedRatioAmplifier(DAMAGE_AMP_AT_MAX_LEVEL * currentLevel / (float) maxLevel);

            BuffType procCd = RecastingBuffTypes.MORTAL_DUST_PROC_CD.get();
            if (data.getLevel(procCd, target.level()) > 0) {
                return;
            }

            data.setLevel(procCd, PROC_COOLDOWN_TICKS, target.level());
            event.addExtraDamage(currentLevel * FIXED_DAMAGE_PER_LEVEL);
        });
    }
}
