package com.til.recasting.registry.sa;

import com.til.recasting.capability.ITimeRun;
import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.capability.RenderDefinitionExtension;
import com.til.recasting.handler.AttackHelper;
import com.til.recasting.handler.CapabilityRegistryHandler;
import com.til.recasting.registry.RecastingAttackTypes;
import com.til.recasting.util.DamageStructure;
import lombok.Setter;
import lombok.experimental.Accessors;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;

import java.util.List;

/**
 * 激光光束 Slash Arts
 * 按玩家当前视角延迟连射直线伤害
 */
@Setter
@Accessors(chain = true)
public class LaserBeamSlashArts extends ExtendedSlashArts {

    int beamCount = 1;
    int delay = 3;
    float attack = 1.0f;
    float range = 20f;
    float radius = 0.75f;

    @Override
    public void trigger(LivingEntity livingEntity, ItemStack itemStack, ISlashBladeState slashBladeState, RenderDefinitionExtension renderDefinitionExtension, PropertiesDefinitionExtension propertiesDefinitionExtension) {
        if (livingEntity.level().isClientSide()) {
            return;
        }

        float distMul = propertiesDefinitionExtension.attackDistance();
        float finalRange = range * distMul;
        int color = slashBladeState.getColorCode();
        DamageStructure damageStructure = new DamageStructure(attack, 0);
        List<com.til.recasting.registry.instance.AttackType> attackTypes = List.of(RecastingAttackTypes.LASER_ATTACK.get());

        LazyOptional<ITimeRun> timeRunOptional = livingEntity.getCapability(CapabilityRegistryHandler.TIME_RUN);
        timeRunOptional.ifPresent(timeRun -> {
            for (int i = 0; i < beamCount; i++) {
                timeRun.addTimerCell(
                        () -> AttackHelper.attackAlongLook(
                                livingEntity,
                                finalRange,
                                radius,
                                damageStructure,
                                attackTypes,
                                color
                        ),
                        delay * i
                );
            }
        });

        livingEntity.playSound(SoundEvents.BEACON_ACTIVATE, 0.35F, 1.6F);
    }
}
