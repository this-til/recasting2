package com.til.recasting.registry.sa;

import com.til.recasting.capability.ITimeRun;
import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.capability.RenderDefinitionExtension;
import com.til.recasting.handler.CapabilityRegistryHandler;
import lombok.Setter;
import lombok.experimental.Accessors;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.util.AttackManager;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.LazyOptional;

/**
 * 青芒 Slash Arts
 * 以玩家为中心，按360度均匀分布发动多次斩击
 */
@Setter
@Accessors(chain = true)
public class CyanGlowSlashArts extends ExtendedSlashArts {

    int attackNumber = 8;
    float hit = 0.3f;
    int delay = 3;

    @Override
    public void trigger(LivingEntity livingEntity, ItemStack itemStack, ISlashBladeState slashBladeState, RenderDefinitionExtension renderDefinitionExtension, PropertiesDefinitionExtension propertiesDefinitionExtension) {

        // 计算每次攻击的角度间隔
        float angleStep = 360f / attackNumber;

        // 获取实体的定时器
        LazyOptional<ITimeRun> timeRunOptional = livingEntity.getCapability(CapabilityRegistryHandler.TIME_RUN);

        timeRunOptional.ifPresent(timeRun -> {
            for(int i = 0; i < attackNumber; i++) {
                int _delay = delay * i;
                int finalI = i;

                timeRun.addTimerCell(
                        () -> {
                            // 计算均匀分布的角度
                            float angle = angleStep * finalI;

                            // 执行斩击
                            AttackManager.doSlash(
                                    livingEntity,
                                    angle,
                                    Vec3.ZERO,  // 无偏移
                                    false,      // mute
                                    false,      // critical
                                    hit         // comboRatio
                            );
                        },
                        _delay
                );
            }
        });
    }
}
