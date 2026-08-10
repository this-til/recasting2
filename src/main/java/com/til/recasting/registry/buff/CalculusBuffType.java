package com.til.recasting.registry.buff;

import com.til.recasting.event.AttackAmplifierEvent;
import com.til.recasting.handler.CapabilityRegistryHandler;
import com.til.recasting.registry.instance.BuffType;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * 演算：每层提高所受伤害。
 */
@Getter
@Setter
@Accessors(chain = true)
public class CalculusBuffType extends BuffType {

    float damageIncreasePerLevel = 0.05f;

    public CalculusBuffType() {
        decayInterval = 20;
        maxLevel = 16;
    }

    @SubscribeEvent
    public void onAttackAmplifier(AttackAmplifierEvent event) {
        if (event.getAttacker().level().isClientSide()) {
            return;
        }

        if (!(event.getTarget() instanceof LivingEntity target)) {
            return;
        }

        target.getCapability(CapabilityRegistryHandler.BUFF_STACK_DATA).ifPresent(data -> {
            int currentLevel = data.getLevel(this, target.level());
            if (currentLevel > 0) {
                event.addModifiedRatioAmplifier(currentLevel * damageIncreasePerLevel);
            }
        });
    }
}
