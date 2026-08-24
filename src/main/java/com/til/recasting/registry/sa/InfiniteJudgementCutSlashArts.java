package com.til.recasting.registry.sa;

import com.til.recasting.capability.ITimeRun;
import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.capability.RenderDefinitionExtension;
import com.til.recasting.entity.JudgementCutEntity;
import com.til.recasting.handler.CapabilityRegistryHandler;
import com.til.recasting.handler.EntityHelper;
import com.til.recasting.handler.PosHelper;
import com.til.recasting.registry.RecastingEntities;
import lombok.Setter;
import lombok.experimental.Accessors;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.LazyOptional;

import java.util.List;

/**
 * 无限次元斩 Slash Arts
 * 在范围内的敌人位置随机发动大量次元斩
 */
@Setter
@Accessors(chain = true)
public class InfiniteJudgementCutSlashArts extends ExtendedSlashArts {

    float attackRange = 12f;
    int attackNumber = 12;
    float hit = 0.5f;
    int delayTicks = 2;

    @Override
    public void trigger(LivingEntity livingEntity, ItemStack itemStack, ISlashBladeState slashBladeState, RenderDefinitionExtension renderDefinitionExtension, PropertiesDefinitionExtension propertiesDefinitionExtension) {

        Level worldIn = livingEntity.level();

        // 获取攻击目标位置
        Vec3 attackPos = PosHelper.getAttackTargetPosition(livingEntity, slashBladeState);

        // 获取范围内的所有敌对实体
        List<LivingEntity> attackEntities = new java.util.ArrayList<>(EntityHelper.getTargettableLivingEntityWithinAABB(
                livingEntity.level(),
                livingEntity,
                attackPos,
                attackRange
        ));

        // 获取实体的定时器
        LazyOptional<ITimeRun> timeRunOptional = livingEntity.getCapability(CapabilityRegistryHandler.TIME_RUN);

        timeRunOptional.ifPresent(timeRun -> {
            for(int i = 0; i < attackNumber; i++) {
                int _delay = delayTicks * i;

                timeRun.addTimerCell(
                        () -> {
                            Vec3 targetPos;

                            // 如果有敌人，随机选择一个存活的敌人
                            if (!attackEntities.isEmpty()) {
                                Entity target = null;

                                // 尝试找到一个存活的目标
                                for(int attempt = 0; attempt < 10 && !attackEntities.isEmpty(); attempt++) {
                                    Entity candidate = attackEntities.get(livingEntity.getRandom().nextInt(attackEntities.size()));

                                    if (candidate.isAlive()) {
                                        target = candidate;
                                        break;
                                    } else {
                                        attackEntities.remove(candidate);
                                    }
                                }

                                if (target != null) {
                                    targetPos = new Vec3(
                                            target.getX(),
                                            target.getY() + target.getEyeHeight() * 0.5,
                                            target.getZ()
                                    );
                                } else {
                                    targetPos = attackPos;
                                }
                            } else {
                                // 如果没有敌人，在原位置发动
                                targetPos = attackPos;
                            }

                            // 创建次元斩
                            JudgementCutEntity jc =
                                    new JudgementCutEntity(
                                            RecastingEntities.JUDGEMENT_CUT.get(),
                                            worldIn,
                                            livingEntity
                                    );

                            jc.setPos(targetPos.x, targetPos.y, targetPos.z);

                            // 设置颜色
                            jc.setColor(slashBladeState.getColorCode());

                            // 设置伤害倍率
                            jc.setModifiedRatio(hit);

                            // 设置生命时间
                            jc.setMaxLifeTime(10);

                            // 添加到世界
                            worldIn.addFreshEntity(jc);

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
