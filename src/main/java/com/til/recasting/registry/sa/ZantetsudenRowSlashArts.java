package com.til.recasting.registry.sa;

import com.til.recasting.capability.ITimeRun;
import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.capability.RenderDefinitionExtension;
import com.til.recasting.entity.DriveEntity;
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

/**
 * 斩铁式·行 Slash Arts
 * 在目标位置向各个方向发射大量驱动剑气（DriveEntity）
 * 剑气会从目标位置向四面八方飞散
 */
@Setter
@Accessors(chain = true)
public class ZantetsudenRowSlashArts extends ExtendedSlashArts {

    int driveNumber = 20;        // 剑气数量
    int delay = 1;                // 每次生成间隔（tick）
    float attack = 0.015f;         // 每次伤害倍率
    float speed = 2.5f;           // 剑气速度
    int life = 20;                 // 剑气持续时间
    float size = 2.0f;             // 剑气大小
    float range = 2.0f;            // 生成位置随机偏移范围
    boolean ignoreBlock = true;   // 是否穿透墙体

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
        jc.setMaxLifeTime(driveNumber * delay);
        worldIn.addFreshEntity(jc);

        // 获取实体的定时器
        LazyOptional<ITimeRun> timeRunOptional = livingEntity.getCapability(CapabilityRegistryHandler.TIME_RUN);

        timeRunOptional.ifPresent(timeRun -> {
            // 延迟生成驱动剑气
            for(int i = 0; i < driveNumber; i++) {
                int _delay = delay * i;

                timeRun.addTimerCell(
                        () -> {
                            net.minecraft.util.RandomSource random = livingEntity.getRandom();

                            DriveEntity driveEntity = new DriveEntity(
                                    RecastingEntities.DRIVE.get(),
                                    worldIn,
                                    livingEntity
                            );

                            // 在目标位置附近随机偏移
                            Vec3 randomOffset = new Vec3(
                                    (random.nextFloat() - 0.5f) * range,
                                    (random.nextFloat() - 0.5f) * range,
                                    (random.nextFloat() - 0.5f) * range
                            );
                            Vec3 spawnPos = targetPos.add(randomOffset);

                            // 设置位置
                            driveEntity.setPos(spawnPos.x, spawnPos.y, spawnPos.z);

                            // 随机朝向（向各个方向发射）
                            float randomYaw = random.nextFloat() * 360;
                            float randomPitch = (random.nextFloat() - 0.5f) * 180; // -90 到 +90 度
                            driveEntity.setRot(randomYaw, randomPitch, true);

                            // 计算目标方向（从生成位置向随机方向）
                            Vec3 direction = new Vec3(
                                    Math.cos(Math.toRadians(randomYaw)) * Math.cos(Math.toRadians(randomPitch)),
                                    Math.sin(Math.toRadians(randomPitch)),
                                    Math.sin(Math.toRadians(randomYaw)) * Math.cos(Math.toRadians(randomPitch))
                            );
                            Vec3 targetDirection = spawnPos.add(direction.scale(10)); // 向前10格作为目标点

                            // 设置属性
                            driveEntity.setColor(slashBladeState.getColorCode());
                            driveEntity.setModifiedRatio(attack);
                            driveEntity.setMaxLifeTime(life);
                            driveEntity.setSize(size);
                            driveEntity.setSeep(speed);
                            driveEntity.setParameter(ignoreBlock); // 是否穿透墙体
                            driveEntity.setRoll(random.nextFloat() * 360); // 随机旋转角度

                            // 设置朝向和速度
                            driveEntity.lookAt(targetDirection, false);

                            // 添加到世界
                            worldIn.addFreshEntity(driveEntity);

                            // 播放音效（音量较小，因为频率很高）
                            worldIn.playSound(null, spawnPos.x, spawnPos.y, spawnPos.z,
                                    SoundEvents.CHORUS_FRUIT_TELEPORT,
                                    net.minecraft.sounds.SoundSource.PLAYERS, 0.15F,
                                    1.2F + random.nextFloat() * 0.4F);
                        },
                        _delay
                );
            }
        });

        // 播放主音效
        livingEntity.playSound(
                SoundEvents.CHORUS_FRUIT_TELEPORT,
                0.4F,
                1.2F
        );
    }
}
