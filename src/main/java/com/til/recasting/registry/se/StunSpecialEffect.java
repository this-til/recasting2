package com.til.recasting.registry.se;

import com.til.recasting.event.AttackAmplifierEvent;
import mods.flammpfeil.slashblade.ability.StunManager;
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
        LivingEntity target = resolveServerLivingTarget(event);
        if (target == null) {
            return;
        }

        StunManager.setStun(target, STUN_TICKS);
    }
}
