package com.til.recasting.handler;

import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class PosHelper {
    /**
     * 获取攻击目标位置
     * 优先返回锁定目标的位置，如果没有锁定目标则使用视线追踪
     *
     * @param livingEntity    发起攻击的实体
     * @param slashBladeState 刀剑状态
     * @param maxDistance     最大追踪距离
     * @return 攻击目标位置
     */
    public static Vec3 getAttackTargetPosition(LivingEntity livingEntity, ISlashBladeState slashBladeState, double maxDistance) {
        Level worldIn = livingEntity.level();

        // 尝试获取锁定的目标
        var target = slashBladeState.getTargetEntity(worldIn);
        if (target != null && target.isAlive() && !target.isRemoved()) {
            return new Vec3(
                    target.getX(),
                    target.getY() + target.getEyeHeight() * 0.5,
                    target.getZ()
            );
        } else {
            // 如果没有锁定目标，使用视线追踪
            Vec3 start = livingEntity.getEyePosition(1.0f);
            Vec3 end = start.add(livingEntity.getLookAngle().scale(maxDistance));
            HitResult result = worldIn.clip(
                    new ClipContext(
                            start,
                            end,
                            ClipContext.Block.COLLIDER,
                            ClipContext.Fluid.NONE,
                            livingEntity
                    )
            );
            return result.getLocation();
        }
    }

    /**
     * 获取攻击目标位置（使用默认距离40）
     *
     * @param livingEntity    发起攻击的实体
     * @param slashBladeState 刀剑状态
     * @return 攻击目标位置
     */
    public static Vec3 getAttackTargetPosition(LivingEntity livingEntity, ISlashBladeState slashBladeState) {
        return getAttackTargetPosition(livingEntity, slashBladeState, 64.0);
    }

    /**
     * 在圆形范围内生成随机向量
     */
    public static Vec3 getRandomVectorInCircle(net.minecraft.util.RandomSource random, float distance) {
        double theta = random.nextDouble() * Math.PI;
        double phi = random.nextDouble() * (double) 2.0F * Math.PI;
        double x = distance * Math.sin(theta) * Math.cos(phi);
        double y = distance * Math.sin(theta) * Math.sin(phi);
        double z = distance * Math.cos(theta);
        return new Vec3(x, y, z);
    }

}
