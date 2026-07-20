package com.til.recasting.registry.se;

import com.til.recasting.event.AttackAmplifierEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * 击晕
 * 对命中目标施加短暂击晕
 */
public class StunSpecialEffect extends ExtendedSpecialEffect {

    private static final int STUN_TICKS = 20;

    @SubscribeEvent
    public void onEvent(AttackAmplifierEvent event) {
        if (!hasSpecialEffect(event.getSlashBladeState())) {
            return;
        }

        if (event.getAttacker().level().isClientSide()) {
            return;
        }

        if (!(event.getTarget() instanceof LivingEntity target) || !target.isAlive()) {
            return;
        }

        // 高等级缓慢等效击晕：无法有效移动
        target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, STUN_TICKS, 255, false, false, true));
        target.setDeltaMovement(0, target.getDeltaMovement().y, 0);
        target.hurtMarked = true;
    }
}
