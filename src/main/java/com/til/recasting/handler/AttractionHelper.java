package com.til.recasting.handler;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

/**
 * 径向吸引力：距离越近力度越大（平方衰减）。
 */
public final class AttractionHelper {

    private AttractionHelper() {
    }

    /**
     * 将实体速度沿指向中心的方向叠加拉力。
     *
     * @param center 吸引中心
     * @param entity 目标实体
     * @param range  有效半径
     * @param power  力度系数（实际力度为 {@code power * range}）
     */
    public static void applyRadialPull(Vec3 center, Entity entity, float range, float power) {
        Vec3 direction = center.subtract(entity.position());
        double length = direction.length();
        if (length > range || length < 0.1) {
            return;
        }
        double lengthRatio = length / range;
        double strength = (1.0 - lengthRatio) * (1.0 - lengthRatio);
        double scaledPower = power * range;
        entity.setDeltaMovement(entity.getDeltaMovement().add(
                (direction.x / length) * strength * scaledPower,
                (direction.y / length) * strength * scaledPower,
                (direction.z / length) * strength * scaledPower
        ));
    }
}
