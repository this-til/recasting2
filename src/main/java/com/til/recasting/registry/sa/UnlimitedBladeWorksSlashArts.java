package com.til.recasting.registry.sa;

import com.til.recasting.capability.ITimeRun;
import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.capability.RenderDefinitionExtension;
import com.til.recasting.entity.SummondSwordEntity;
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
 * 无限剑制 Slash Arts
 * 在目标位置上方的半球面上生成大量幻影剑，均匀分布并延迟发射
 */
@Setter
@Accessors(chain = true)
public class UnlimitedBladeWorksSlashArts extends ExtendedSlashArts {

    float attack = 0.04f;
    int totalSwords = 1024;
    int spawnDurationTicks = 40;  // 生成持续时间（tick）
    float sphereRadius = 64f;  // 半球半径
    float targetOffsetRange = 8f;  // 命中点偏移范围
    int maxLaunchDelayTicks = 40;  // 最大发射延迟

    @Override
    public void trigger(LivingEntity livingEntity, ItemStack itemStack, ISlashBladeState slashBladeState, RenderDefinitionExtension renderDefinitionExtension, PropertiesDefinitionExtension propertiesDefinitionExtension) {
        Level worldIn = livingEntity.level();

        // 获取目标位置
        Vec3 targetPos = PosHelper.getAttackTargetPosition(livingEntity, slashBladeState);

        // 获取实体的定时器
        LazyOptional<ITimeRun> timeRunOptional = livingEntity.getCapability(CapabilityRegistryHandler.TIME_RUN);

        timeRunOptional.ifPresent(timeRun -> {
            // 计算每个tick需要生成多少把剑
            int swordsPerTick = (int) Math.ceil((double) totalSwords / spawnDurationTicks);

            // 在40 tick内逐步生成剑
            for(int tick = 0; tick < spawnDurationTicks; tick++) {
                int finalTick = tick;

                timeRun.addTimerCell(
                        () -> {
                            // 计算这个tick要生成的剑的数量
                            int startIndex = finalTick * swordsPerTick;
                            int endIndex = Math.min(startIndex + swordsPerTick, totalSwords);

                            // 生成这个tick的剑
                            for(int i = startIndex; i < endIndex; i++) {
                                spawnSwordOnHemisphere(
                                        livingEntity,
                                        worldIn,
                                        targetPos,
                                        i,
                                        totalSwords,
                                        slashBladeState
                                );
                            }
                        },
                        tick
                );
            }
        });

        // 播放音效
        livingEntity.playSound(
                SoundEvents.CHORUS_FRUIT_TELEPORT,
                0.5F,
                1.2F
        );
    }

    /**
     * 在半球面上生成一把剑
     */
    private void spawnSwordOnHemisphere(
            LivingEntity livingEntity,
            Level worldIn,
            Vec3 targetPos,
            int index,
            int total,
            ISlashBladeState slashBladeState
    ) {
        net.minecraft.util.RandomSource random = livingEntity.getRandom();

        // 使用 Fibonacci 球面均匀分布算法（仅上半球）
        // 这种方法比随机采样更均匀
        double goldenRatio = (1.0 + Math.sqrt(5.0)) / 2.0;
        double angleIncrement = 2.0 * Math.PI * goldenRatio;

        // 计算极角 theta（0 到 π/2，上半球）
        // 使用 cos(theta) 在 [0, 1] 均匀分布来保证球面均匀
        double cosTheta = (double) index / (double) total;  // 0 到 1
        double theta = Math.acos(cosTheta);  // 0 到 π/2

        // 计算方位角 phi
        double phi = angleIncrement * index;
        phi = phi % (2.0 * Math.PI);  // 限制在 [0, 2π)

        // 球坐标转笛卡尔坐标
        double sinTheta = Math.sin(theta);
        double x = sphereRadius * sinTheta * Math.cos(phi);
        double y = sphereRadius * cosTheta;  // 使用 cosTheta（已知值）更精确
        double z = sphereRadius * sinTheta * Math.sin(phi);

        // 剑的生成位置：目标位置 + 球面偏移
        Vec3 swordPos = targetPos.add(x, y, z);

        // 创建幻影剑
        SummondSwordEntity summonedSword = new SummondSwordEntity(
                RecastingEntities.SUMMOND_SWORD.get(),
                worldIn,
                livingEntity
        );

        // 设置位置
        summonedSword.setPos(swordPos.x, swordPos.y, swordPos.z);

        // 计算带有正态分布偏移的目标位置
        Vec3 offsetTargetPos = calculateGaussianOffset(targetPos, random);

        // 设置朝向偏移后的目标位置
        summonedSword.lookAt(offsetTargetPos, false);

        // 设置属性
        summonedSword.setColor(slashBladeState.getColorCode());
        summonedSword.setModifiedRatio(attack);

        // 计算当前剑在第几个tick生成
        int currentSpawnTick = index / ((int) Math.ceil((double) totalSwords / spawnDurationTicks));

        // 发射延迟 = 等待所有剑生成完毕的时间 + 随机延迟(0~maxLaunchDelay)
        // 等待时间 = (spawnDuration - currentSpawnTick)，确保所有剑都在第40tick后才开始发射
        int waitForAllSpawn = spawnDurationTicks - currentSpawnTick;
        int randomLaunchDelay = random.nextInt(maxLaunchDelayTicks + 1);
        int totalDelay = waitForAllSpawn + randomLaunchDelay;

        summonedSword.setStartDelay(totalDelay);

        // 设置随机旋转
        summonedSword.setRoll(random.nextFloat() * 360.0f);

        // 添加到世界
        worldIn.addFreshEntity(summonedSword);
    }

    /**
     * 计算带有正态分布偏移的目标位置
     */
    private Vec3 calculateGaussianOffset(Vec3 basePos, net.minecraft.util.RandomSource random) {
        // 使用 Box-Muller 变换生成正态分布的偏移
        // 标准差设为 offsetRange/3，使得约99.7%的点在偏移范围内
        double sigma = targetOffsetRange / 3.0;

        // 生成三个独立的正态分布随机数
        double offsetX = random.nextGaussian() * sigma;
        double offsetY = random.nextGaussian() * sigma;
        double offsetZ = random.nextGaussian() * sigma;

        return basePos.add(offsetX, offsetY, offsetZ);
    }
}
