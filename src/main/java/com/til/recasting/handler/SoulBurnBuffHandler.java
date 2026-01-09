package com.til.recasting.handler;

import com.til.recasting.Recasting;
import com.til.recasting.capability.IBuffStackData;
import com.til.recasting.event.AttackAmplifierEvent;
import com.til.recasting.mixin.DamageSourcesAccessor;
import com.til.recasting.registry.RecastingBuffTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 灵魂燃烧Buff处理器
 * 功能：
 * 当攻击者拥有 soul_burn buff 时，额外造成目标当前生命值 6% 的火焰伤害
 * 使用 AttackAmplifierEvent 处理
 */
@Mod.EventBusSubscriber(modid = Recasting.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SoulBurnBuffHandler {

    /**
     * 火焰伤害比例（每层对当前生命值的百分比）
     */
    private static final float FIRE_DAMAGE_PERCENTAGE = 0.06f; // 6%

    /**
     * 处理攻击放大事件
     * 检查攻击者是否有 soul_burn buff，如果有则添加额外的火焰伤害
     */
    @SubscribeEvent
    public static void onAttackAmplifier(AttackAmplifierEvent event) {
        // 只在服务端处理
        if (event.getAttacker().level().isClientSide()) {
            return;
        }

        // 检查目标是否为 LivingEntity（需要获取当前生命值）
        if (!(event.getTarget() instanceof LivingEntity target)) {
            return;
        }

        // 检查目标是否免疫火焰
        if (target.fireImmune()) {
            return;
        }

        // 获取攻击者的 buff 数据
        event.getAttacker().getCapability(CapabilityRegistryHandler.BUFF_STACK_DATA).ifPresent(data -> {
            // 获取当前 soul_burn buff 的层数
            int currentLevel = data.getLevel(RecastingBuffTypes.SOUL_BURN.get(), event.getAttacker().level());
            
            // 应用额外火焰伤害：每层造成目标当前生命值 6% 的火焰伤害
            if (currentLevel > 0) {
                // 计算目标当前生命值
                float currentHealth = target.getHealth();
                
                // 计算火焰伤害（每层 6% 当前生命值）
                float fireDamage = currentHealth * FIRE_DAMAGE_PERCENTAGE * currentLevel;
                
                // 创建火焰伤害源
                DamageSourcesAccessor accessor = (DamageSourcesAccessor) event.getAttacker().damageSources();
                DamageSource fireDamageSource = accessor.callSource(DamageTypes.ON_FIRE, target, event.getAttacker());
                
                // 添加额外的火焰伤害
                event.addDamageSourceInfo(fireDamageSource, fireDamage);
            }
        });
    }
}

