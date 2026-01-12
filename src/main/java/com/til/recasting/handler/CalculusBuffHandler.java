package com.til.recasting.handler;

import com.til.recasting.Recasting;
import com.til.recasting.capability.IBuffStackData;
import com.til.recasting.event.AttackAmplifierEvent;
import com.til.recasting.registry.RecastingBuffTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 演算Buff处理器
 * 功能：
 * 根据目标身上的 calculus buff 层数增加受到的伤害（每层 10%）
 * 使用 AttackAmplifierEvent 处理，不做叠加
 */
@Mod.EventBusSubscriber(modid = Recasting.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CalculusBuffHandler {

    /**
     * 伤害增加比例（每层）
     */
    private static final float DAMAGE_INCREASE_PER_LEVEL = 0.05f; // 10%

    /**
     * 处理攻击放大事件
     * 检查目标是否有 calculus buff，如果有则增加攻击倍率
     */
    @SubscribeEvent
    public static void onAttackAmplifier(AttackAmplifierEvent event) {
        // 只在服务端处理
        if (event.getAttacker().level().isClientSide()) {
            return;
        }

        // 检查目标是否为 LivingEntity
        if (!(event.getTarget() instanceof LivingEntity target)) {
            return;
        }

        // 获取目标的 buff 数据
        target.getCapability(CapabilityRegistryHandler.BUFF_STACK_DATA).ifPresent(data -> {
            // 获取当前 calculus buff 的层数
            int currentLevel = data.getLevel(RecastingBuffTypes.CALCULUS.get(), target.level());
            
            // 应用伤害增加：每层增加 10% 伤害
            if (currentLevel > 0) {
                float damageIncrease = currentLevel * DAMAGE_INCREASE_PER_LEVEL;
                
                // 通过增加攻击倍率来实现伤害增加
                // 例如：如果有 1 层（10% 增加），则攻击倍率增加 10%
                event.addModifiedRatioAmplifier(damageIncrease);
            }
        });
    }
}

