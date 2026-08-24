package com.til.recasting.registry.sa;

import com.til.recasting.capability.ITimeRun;
import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.capability.RenderDefinitionExtension;
import com.til.recasting.entity.JudgementCutEntity;
import com.til.recasting.handler.CapabilityRegistryHandler;
import com.til.recasting.handler.PosHelper;
import com.til.recasting.registry.RecastingEntities;
import lombok.Setter;
import lombok.experimental.Accessors;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.LazyOptional;

import java.util.concurrent.atomic.AtomicReference;

/**
 * 多重次元斩·决 Slash Arts
 * 在同一位置连续发动多次次元斩，每次都在前一次的位置
 */
@Setter
@Accessors(chain = true)
public class MultipleJudgementCutSlashArts extends ExtendedSlashArts {

    int attackNumber = 4;
    float hit = 0.3f;
    int delayTicks = 4;

    @Override
    public void trigger(LivingEntity livingEntity, ItemStack itemStack, ISlashBladeState slashBladeState, RenderDefinitionExtension renderDefinitionExtension, PropertiesDefinitionExtension propertiesDefinitionExtension) {

        // 用于记录上一次次元斩的位置
        final AtomicReference<Vec3> lastPos = new AtomicReference<>(null);

        // 获取实体的定时器
        LazyOptional<ITimeRun> timeRunOptional = livingEntity.getCapability(CapabilityRegistryHandler.TIME_RUN);

        timeRunOptional.ifPresent(timeRun -> {
            for(int i = 0; i < attackNumber; i++) {
                int _delay = delayTicks * i;

                timeRun.addTimerCell(
                        () -> {
                            Level worldIn = livingEntity.level();

                            // 确定生成位置：如果有上次的位置，使用上次的位置，否则使用目标位置
                            Vec3 pos;
                            if (lastPos.get() != null) {
                                pos = lastPos.get();
                            } else {
                                pos = PosHelper.getAttackTargetPosition(livingEntity, slashBladeState);
                            }

                            // 创建次元斩
                            JudgementCutEntity jc =
                                    new JudgementCutEntity(
                                            RecastingEntities.JUDGEMENT_CUT.get(),
                                            worldIn,
                                            livingEntity
                                    );

                            jc.setPos(pos.x, pos.y, pos.z);

                            // 设置颜色
                            itemStack.getCapability(mods.flammpfeil.slashblade.item.ItemSlashBlade.BLADESTATE)
                                    .ifPresent(state -> jc.setColor(state.getColorCode()));

                            // 设置伤害倍率
                            jc.setModifiedRatio(hit);

                            // 设置生命时间
                            jc.setColor(slashBladeState.getColorCode());

                            // 添加到世界
                            worldIn.addFreshEntity(jc);

                            // 更新位置记录
                            lastPos.set(pos);

                            // 播放音效
                            worldIn.playSound(null, jc.getX(), jc.getY(), jc.getZ(),
                                    SoundEvents.ENDERMAN_TELEPORT,
                                    net.minecraft.sounds.SoundSource.PLAYERS, 0.5F,
                                    0.8F / (livingEntity.getRandom().nextFloat() * 0.4F + 0.8F));
                        },
                        _delay
                );
            }
        });
    }
}
