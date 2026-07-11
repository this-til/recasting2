package com.til.recasting.registry.se;

import com.til.recasting.event.AttackAmplifierEvent;
import com.til.recasting.handler.CapabilityRegistryHandler;
import com.til.recasting.registry.RecastingAttackTypes;
import com.til.recasting.registry.RecastingBuffTypes;
import com.til.recasting.registry.instance.BuffType;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * 光子灼痕
 * <ul>
 *   <li>激光命中叠加光子灼烧</li>
 *   <li>灼烧层数提供全伤害增伤（上限由 {@code maxLaserBonus} 控制）</li>
 *   <li>灼痕叠层与满层短光束由 {@link com.til.recasting.handler.PhotonScarBuffHandler} 处理</li>
 * </ul>
 */
@Setter
@Accessors(chain = true)
public class PhotonScarSpecialEffect extends ExtendedSpecialEffect {

    /** 灼烧满层时的全伤害增伤上限 */
    float maxLaserBonus = 0.33f;
    /** 每次激光命中叠加的灼烧层数 */
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
