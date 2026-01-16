package com.til.recasting.handler;

import com.til.recasting.Recasting;
import com.til.recasting.capability.IBuffStackData;
import com.til.recasting.mixin.DamageSourcesAccessor;
import com.til.recasting.registry.RecastingBuffTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 灵魂燃烧Buff处理器
 * 功能：
 * 每秒对拥有 soul_burn buff 的实体造成当前生命值 6% 的火属性伤害
 * 使用 LivingEvent.LivingTickEvent 处理，每20个tick（1秒）触发一次
 */
@Mod.EventBusSubscriber(modid = Recasting.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SoulBurnBuffHandler {

    /**
     * 火焰伤害比例（对当前生命值的百分比）
     */
    private static final float FIRE_DAMAGE_PERCENTAGE = 0.06f; // 6%

    /**
     * 每秒触发的tick间隔（20 tick = 1秒）
     */
    private static final int TICKS_PER_SECOND = 20;

    /**
     * 处理生物实体每tick事件
     * 每20个tick（1秒）检查实体是否有 soul_burn buff，如果有则造成火焰伤害
     */
    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        
        // 只在服务端处理
        if (entity.level().isClientSide()) {
            return;
        }

        // 每20个tick（1秒）触发一次
        if (entity.tickCount % TICKS_PER_SECOND != 0) {
            return;
        }

        // 获取实体的 buff 数据
        entity.getCapability(CapabilityRegistryHandler.BUFF_STACK_DATA).ifPresent(data -> {
            // 获取当前 soul_burn buff 的层数
            int currentLevel = data.getLevel(RecastingBuffTypes.SOUL_BURN.get(), entity.level());
            
            // 如果有层数，则造成火焰伤害
            if (currentLevel > 0) {
                // 计算实体当前生命值
                float currentHealth = entity.getHealth();
                
                // 计算火焰伤害（当前生命值的 6%）
                float fireDamage = currentHealth * FIRE_DAMAGE_PERCENTAGE;
                
                // 创建火焰伤害源
                DamageSource fireDamageSource = entity.damageSources().inFire();
                
                // 造成火焰伤害
                entity.hurt(fireDamageSource, fireDamage);
            }
        });
    }
}

