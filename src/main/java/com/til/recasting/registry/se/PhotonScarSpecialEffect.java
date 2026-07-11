package com.til.recasting.registry.se;

import com.til.recasting.event.AttackAmplifierEvent;
import com.til.recasting.handler.CapabilityRegistryHandler;
import com.til.recasting.registry.RecastingAttackTypes;
import com.til.recasting.registry.RecastingBuffTypes;
import com.til.recasting.registry.instance.BuffType;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * 光子灼痕
 * SE 只负责：激光叠加光子灼烧；灼烧提供全伤害增伤
 * 灼痕叠层与满层短光束由 PhotonScarBuffHandler 在灼烧状态下处理
 */
@Setter
@Accessors(chain = true)
public class PhotonScarSpecialEffect extends ExtendedSpecialEffect {

    float maxLaserBonus = 0.33f;
    @Getter
    int cooldownTicks = 30;
    int addLevel = 1;

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

        Level world = target.level();
        BuffType photonBurnBuffType = RecastingBuffTypes.PHOTON_BURN.get();
        boolean isLaser = event.getAttackTypeList().contains(RecastingAttackTypes.LASER_ATTACK.get());
        boolean isNoRecursion = event.getAttackTypeList().contains(RecastingAttackTypes.NO_RECURSION_ATTACK.get());

        target.getCapability(CapabilityRegistryHandler.BUFF_STACK_DATA).ifPresent(buffStackData -> {
            // 灼烧全伤害增伤
            int burnLevel = buffStackData.getLevel(photonBurnBuffType, world);
            if (burnLevel > 0) {
                int burnMax = photonBurnBuffType.getMaxLevel();
                float damageBonus = burnMax <= 0
                        ? 0f
                        : maxLaserBonus * ((float) burnLevel / (float) burnMax);
                if (damageBonus > 0f) {
                    event.addModifiedRatioAmplifier(damageBonus);
                }
            }

            if (isNoRecursion) {
                return;
            }

            // 激光叠灼烧
            if (isLaser) {
                int currentBurn = buffStackData.getLevel(photonBurnBuffType, world);
                int newBurn = Math.min(currentBurn + addLevel, photonBurnBuffType.getMaxLevel());
                buffStackData.setLevel(photonBurnBuffType, newBurn, world);
            }
        });
    }

}
