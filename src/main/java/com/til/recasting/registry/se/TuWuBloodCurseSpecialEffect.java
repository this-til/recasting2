package com.til.recasting.registry.se;

import com.til.recasting.event.AttackAmplifierEvent;
import com.til.recasting.handler.InventorySlashBladeSeHelper;
import com.til.recasting.registry.SpecialEffectsRegistry;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * 屠巫血咒
 * 手持时全伤害提升；致命抵挡由 ProudSoulLethalAbsorbHelper 处理。
 */
@Setter
@Accessors(chain = true)
public class TuWuBloodCurseSpecialEffect extends ExtendedSpecialEffect {

    float damageAmplifier = 0.3f;

    @SubscribeEvent
    public void onAttackAmplifier(AttackAmplifierEvent event) {
        if (!InventorySlashBladeSeHelper.isHoldingSpecialEffect(event.getAttacker(), SpecialEffectsRegistry.TU_WU_BLOOD_CURSE)) {
            return;
        }
        if (event.getAttacker().level().isClientSide()) {
            return;
        }
        event.addModifiedRatioAmplifier(damageAmplifier);
    }
}
