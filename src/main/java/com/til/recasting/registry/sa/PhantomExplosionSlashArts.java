package com.til.recasting.registry.sa;

import com.til.recasting.capability.ITimeRun;
import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.capability.RenderDefinitionExtension;
import com.til.recasting.entity.SummondSpiralSwordEntity;
import com.til.recasting.handler.CapabilityRegistryHandler;
import com.til.recasting.handler.EntityHelper;
import com.til.recasting.handler.PosHelper;
import com.til.recasting.registry.RecastingAttackTypes;
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
 * 幻影爆破 Slash Arts
 * 在目标周围产生多组螺旋幻影剑，围绕目标旋转
 */
@Setter
@Accessors(chain = true)
public class PhantomExplosionSlashArts extends ExtendedSlashArts {

    float attack = 0.02f;
    int minCount = 12;
    int maxCount = 24;
    float minTiltAngle = 0f;
    float maxTiltAngle = 30f;
    int groupCount = 3;
    int groupInterval = 5;

    @Override
    public void trigger(LivingEntity livingEntity, ItemStack itemStack, ISlashBladeState slashBladeState, RenderDefinitionExtension renderDefinitionExtension, PropertiesDefinitionExtension propertiesDefinitionExtension) {
        Level worldIn = livingEntity.level();

        // 获取目标实体
        Entity targetEntity = slashBladeState.getTargetEntity(worldIn);
        LivingEntity target;

        // 如果有锁定目标且有效，使用锁定目标
        if (targetEntity != null && targetEntity.isAlive() && !targetEntity.isRemoved() && targetEntity instanceof LivingEntity) {
            target = (LivingEntity) targetEntity;
        } else {
            // 如果没有锁定目标，从看向位置附近选择最近的敌人
            Vec3 attackPos = PosHelper.getAttackTargetPosition(livingEntity, slashBladeState);

            // 获取看向位置附近的可攻击敌人
            List<LivingEntity> nearbyEntities = EntityHelper.getTargettableLivingEntityWithinAABB(
                    worldIn,
                    livingEntity,
                    attackPos,
                    16f  // 搜索范围
            );

            // 选择最近的敌人
            if (!nearbyEntities.isEmpty()) {
                target = nearbyEntities.stream()
                        .min((e1, e2) -> {
                            double dist1 = e1.position().distanceToSqr(attackPos);
                            double dist2 = e2.position().distanceToSqr(attackPos);
                            return Double.compare(dist1, dist2);
                        })
                        .orElse(null);
            } else {
                target = null;
            }
        }

        // 如果没有找到目标，直接返回
        if (target == null) {
            return;
        }

        // 获取实体的定时器
        LazyOptional<ITimeRun> timeRunOptional = livingEntity.getCapability(CapabilityRegistryHandler.TIME_RUN);

        timeRunOptional.ifPresent(timeRun -> {
            // 生成多组，每组间隔 groupInterval tick
            for(int group = 0; group < groupCount; group++) {
                int delay = group * groupInterval;

                timeRun.addTimerCell(
                        () -> {
                            // 检查目标是否还活着，如果已死亡则取消后续进程
                            if (!target.isAlive() || target.isRemoved()) {
                                return;
                            }

                            // 生成一组幻影剑
                            spawnPhantomSwordsGroup(livingEntity, target, slashBladeState, worldIn);
                        },
                        delay
                );
            }
        });

    }

    /**
     * 生成一组幻影剑
     */
    private void spawnPhantomSwordsGroup(LivingEntity livingEntity, LivingEntity target, ISlashBladeState slashBladeState, Level worldIn) {
        // 随机生成数量（12~24）
        int count = livingEntity.getRandom().nextInt(maxCount - minCount + 1) + minCount;

        // 随机初始角度偏移
        float off = livingEntity.getRandom().nextFloat() * 360;

        for(int i = 0; i < count; i++) {
            SummondSpiralSwordEntity ss = new SummondSpiralSwordEntity(
                    RecastingEntities.SUMMOND_SPIRAL_SWORD.get(),
                    worldIn,
                    livingEntity
            );

            // 设置旋转中心为目标
            ss.setCenterEntity(target);

            // 设置旋转参数（使用辅助方法自动计算修饰参数）
            ss.setRadiusExpansion(2.5f, 12.0f, 30);
            ss.setSpeedDecay(32.0f, 0.3f, 30);
            ss.setRotationAngle(off + (360.0f / count * i));

            // 设置旋转轴：Y 轴，稍微倾斜 10~30°
            float tiltAngle = livingEntity.getRandom().nextFloat() * (maxTiltAngle - minTiltAngle) + minTiltAngle;
            float tiltRad = (float) Math.toRadians(tiltAngle);

            // 在 XZ 平面上随机选择一个方向
            float horizontalAngle = livingEntity.getRandom().nextFloat() * 360f;
            float horizontalRad = (float) Math.toRadians(horizontalAngle);

            // 计算倾斜后的旋转轴向量
            // Y 分量 = cos(θ)
            // X 分量 = sin(θ) * cos(φ)
            // Z 分量 = sin(θ) * sin(φ)
            double y = Math.cos(tiltRad);
            double x = Math.sin(tiltRad) * Math.cos(horizontalRad);
            double z = Math.sin(tiltRad) * Math.sin(horizontalRad);

            ss.setRotationAxis(new Vec3(x, y, z));
            ss.setRotationDirectionOutward(false);
            ss.setIgnoringBlock(true);

            // 设置基本属性
            ss.setModifiedRatio(attack);
            ss.setColor(slashBladeState.getColorCode());
            ss.setRoll(0);
            ss.setStartDelay(30);

            ss.addAttackType(RecastingAttackTypes.SPIRAL_SWORD_ATTACK.get());

            worldIn.addFreshEntity(ss);


        }

        livingEntity.playSound(
                SoundEvents.CHORUS_FRUIT_TELEPORT,
                0.2F,
                1.45F
        );
    }
}
