package com.til.recasting.handler;

import com.til.recasting.Recasting;
import com.til.recasting.registry.RecastingBuffTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 光子灼烧 Buff 处理器
 * 每 0.25 秒造成固定魔法伤害：0.15 × 层数
 */
@Mod.EventBusSubscriber(modid = Recasting.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PhotonScarBuffHandler {

    private static final float DAMAGE_PER_STACK = 0.15f;
    private static final int TICKS_PER_INTERVAL = 5;

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();

        if (entity.level().isClientSide()) {
            return;
        }

        if (entity.tickCount % TICKS_PER_INTERVAL != 0) {
            return;
        }

        entity.getCapability(CapabilityRegistryHandler.BUFF_STACK_DATA).ifPresent(data -> {
            int currentLevel = data.getLevel(RecastingBuffTypes.PHOTON_BURN.get(), entity.level());
            if (currentLevel <= 0) {
                return;
            }

            float damage = currentLevel * DAMAGE_PER_STACK;
            if (damage <= 0) {
                return;
            }

            DamageSource damageSource = entity.damageSources().magic();
            entity.hurt(damageSource, damage);
        });
    }
}
