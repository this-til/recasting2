package com.til.recasting.registry.sa;

import com.til.recasting.capability.ITimeRun;
import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.capability.RenderDefinitionExtension;
import com.til.recasting.entity.JudgementCutEntity;
import com.til.recasting.entity.SlashEffectEntity;
import com.til.recasting.registry.RecastingAttachments;
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

/**
 * 斩铁式·极 Slash Arts
 * 在目标位置超高频率连续发动大量斩击（乱舞的远程版本）
 * 类似瞬狱杀的效果
 */
@Setter
@Accessors(chain = true)
public class ZantetsudenMaxSlashArts extends ExtendedSlashArts {

    int attackNumber = 25;        // 攻击次数
    int delayTicks = 1;           // 每次攻击间隔（tick）
    float hit = 0.03f;            // 每次伤害倍率
    float range = 3.0f;           // 攻击范围（随机偏移）
    int lifeTicks = 8;            // 每次斩击的持续时间
    float size = 2.0f;            // 斩击大小

    @Override
    public void trigger(LivingEntity livingEntity, ItemStack itemStack, ISlashBladeState slashBladeState, RenderDefinitionExtension renderDefinitionExtension, PropertiesDefinitionExtension propertiesDefinitionExtension) {

        Level worldIn = livingEntity.level();

        // 获取攻击目标位置（远程锁定点）
        Vec3 targetPos = PosHelper.getAttackTargetPosition(livingEntity, slashBladeState);

        // 在目标位置创建大小为8的次元斩（只生成一次）
        JudgementCutEntity jc = new JudgementCutEntity(
                RecastingEntities.JUDGEMENT_CUT.get(),
                worldIn,
                livingEntity
        );
        jc.setPos(targetPos.x, targetPos.y, targetPos.z);
        jc.setColor(slashBladeState.getColorCode());
        jc.setSize(8.0f);
        jc.setMaxLifeTime(attackNumber * delayTicks);
        worldIn.addFreshEntity(jc);

        ITimeRun timeRun = RecastingAttachments.timeRun(livingEntity);
        for(int i = 0; i < attackNumber; i++) {
            int delay = delayTicks * i;

            timeRun.addTimerCell(
                        () -> {
                            // 在目标位置附近随机偏移
                            Vec3 randomOffset = new Vec3(
                                    (livingEntity.getRandom().nextFloat() - 0.5f) * range,
                                    (livingEntity.getRandom().nextFloat() - 0.5f) * range,
                                    (livingEntity.getRandom().nextFloat() - 0.5f) * range
                            );
                            Vec3 attackPos = targetPos.add(randomOffset);

                            // 创建斩击特效
                            SlashEffectEntity slashEffect = new SlashEffectEntity(
                                    RecastingEntities.SLASH_EFFECT.get(),
                                    worldIn,
                                    livingEntity
                            );

                            // 设置位置
                            slashEffect.setPos(attackPos.x, attackPos.y, attackPos.z);

                            // 随机朝向（完全随机的 Yaw 和 Pitch）
                            float randomYaw = livingEntity.getRandom().nextFloat() * 360;
                            float randomPitch = (livingEntity.getRandom().nextFloat() - 0.5f) * 180; // -90 到 +90 度
                            slashEffect.setRot(randomYaw, randomPitch, true);

                            // 随机旋转角度（Roll）
                            float randomRoll = livingEntity.getRandom().nextFloat() * 360;
                            slashEffect.setRoll(randomRoll);

                            // 设置属性
                            slashEffect.setColor(slashBladeState.getColorCode());
                            slashEffect.setModifiedRatio(hit);
                            slashEffect.setMaxLifeTime(lifeTicks);
                            slashEffect.setSize(size);
                            slashEffect.setThump(true); // 暴击效果

                            // 添加到世界
                            worldIn.addFreshEntity(slashEffect);

                            // 播放音效（音量较小，因为频率很高）
                            worldIn.playSound(null, attackPos.x, attackPos.y, attackPos.z,
                                    SoundEvents.PLAYER_ATTACK_SWEEP,
                                    net.minecraft.sounds.SoundSource.PLAYERS, 0.15F,
                                    1.2F + livingEntity.getRandom().nextFloat() * 0.4F);
                        },
                        delay
                );
        }

        // 播放主音效
        livingEntity.playSound(
                SoundEvents.PLAYER_ATTACK_SWEEP,
                1.0F,
                0.8F
        );
    }
}
