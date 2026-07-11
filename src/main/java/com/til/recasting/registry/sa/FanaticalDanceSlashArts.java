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
 * 乱舞 Slash Arts
 * 快速连续发动多次随机角度的斩击
 */
@Setter
@Accessors(chain = true)
public class FanaticalDanceSlashArts extends ExtendedSlashArts {

    int attackNumber = 15;
    int attackDeviation = 3;
    float hit = 0.4f;
    int delay = 1;
    int offset = 3;

    @Override
    public void trigger(LivingEntity livingEntity, ItemStack itemStack, ISlashBladeState slashBladeState, RenderDefinitionExtension renderDefinitionExtension, PropertiesDefinitionExtension propertiesDefinitionExtension) {

        // 计算总攻击次数
        int number = attackNumber + livingEntity.getRandom().nextInt(attackDeviation + 1);

        // 获取实体的定时器
        LazyOptional<ITimeRun> timeRunOptional = livingEntity.getCapability(CapabilityRegistryHandler.TIME_RUN);

        timeRunOptional.ifPresent(timeRun -> {
            for(int i = 0; i < number; i++) {
                int _delay = delay * i;

                timeRun.addTimerCell(
                        () -> {
                            // 随机角度 (0-360度)
                            float randomRoll = livingEntity.getRandom().nextFloat() * 360;

                            // 随机偏移向量
                            Vec3 randomOffset = new Vec3(
                                    livingEntity.getRandom().nextFloat() - 0.5f,
                                    livingEntity.getRandom().nextFloat() - 0.5f,
                                    0
                            ).scale(offset);

                            // 执行斩击
                            AttackManager.doSlash(
                                    livingEntity,
                                    randomRoll,
                                    randomOffset,
                                    false,  // mute
                                    true,   // critical
                                    hit     // comboRatio
                            );
                        },
                        _delay
                );
            }
        });
    }
}
