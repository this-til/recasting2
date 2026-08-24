package com.til.recasting.handler;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

/**
 * 位置/几何辅助。
 * TODO(P3): 从 1.20 补全 getAttackTargetPosition / BeamHit / 射线索敌等其余方法。
 */
public final class PosHelper {

    private PosHelper() {
    }

    /**
     * 浮点近似相等（epsilon 1e-5）。
     */
    public static boolean epsilonEquals(float x, float y) {
        return Math.abs(y - x) < 1.0E-5F;
    }

    /**
     * 双精度近似相等（epsilon 1e-5）。
     */
    public static boolean epsilonEquals(double x, double y) {
        return Math.abs(y - x) < 1.0E-5D;
    }

    /**
     * 点到线段的最短距离。
     */
    public static double distancePointToSegment(Vec3 point, Vec3 start, Vec3 end) {
        Vec3 segment = end.subtract(start);
        double lengthSqr = segment.lengthSqr();
        if (Mth.equal((float) lengthSqr, 0.0f)) {
            return point.distanceTo(start);
        }
        double t = point.subtract(start).dot(segment) / lengthSqr;
        t = Math.max(0.0, Math.min(1.0, t));
        Vec3 closest = start.add(segment.scale(t));
        return point.distanceTo(closest);
    }
}
