package com.til.recasting.registry.buff;

import com.til.recasting.event.AttackAmplifierEvent;
import com.til.recasting.handler.CapabilityRegistryHandler;
import com.til.recasting.registry.RecastingBuffTypes;
import com.til.recasting.registry.instance.BuffType;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * 红尘：按层提高所受伤害；受击时按层造成固定伤害（内置冷却）。
 */
@Getter
@Setter
@Accessors(chain = true)
public class MortalDustBuffType extends BuffType {

    float damageAmpAtMaxLevel = 0.33f;
    float fixedDamagePerLevel = 0.1f;
    int procCooldownTicks = 5;

    public MortalDustBuffType() {
        decayInterval = 20;
        maxLevel = 160;
    }

    @SubscribeEvent
    public void onAttackAmplifier(AttackAmplifierEvent event) {
        if (event.getAttacker().level().isClientSide()) {
            return;
        }

        if (!(event.getTarget() instanceof LivingEntity target) || !target.isAlive()) {
            return;
        }

        target.getCapability(CapabilityRegistryHandler.BUFF_STACK_DATA).ifPresent(data -> {
            int currentLevel = data.getLevel(this, target.level());
            if (currentLevel <= 0 || maxLevel <= 0) {
                return;
            }

            event.addModifiedRatioAmplifier(damageAmpAtMaxLevel * currentLevel / (float) maxLevel);

            if (data.getLevel(RecastingBuffTypes.MORTAL_DUST_PROC_CD.get(), target.level()) > 0) {
                return;
            }

            data.setLevel(RecastingBuffTypes.MORTAL_DUST_PROC_CD.get(), procCooldownTicks, target.level());
            event.addExtraDamage(currentLevel * fixedDamagePerLevel);
        });
    }
}
