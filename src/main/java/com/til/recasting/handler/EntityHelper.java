package com.til.recasting.handler;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class EntityHelper {

    /**
     * 默认视锥索敌距离（格）
     */
    public static final double DEFAULT_VIEW_CONE_RANGE = 128.0;

    /**
     * 默认视锥半角（度）
     */
    public static final float DEFAULT_VIEW_CONE_HALF_ANGLE_DEGREES = 30.0f;

    public static Vec3 getEntityPosition(Entity owner) {
        return new Vec3(owner.getX(), owner.getY() + owner.getEyeHeight(), owner.getZ());
    }

    public static List<Entity> getTargettableEntitiesWithinAABB(Level world, @Nullable LivingEntity shooter, Vec3 pos, float reach) {

        AABB aabb = new AABB(pos, pos).inflate(reach);


        return world.getEntities(null, aabb).stream()
                .filter(e -> !Objects.equals(e, shooter))
                .filter(e -> EntityPredicateHelper.canTarget(shooter, e))
                .toList();

    }

    public static List<LivingEntity> getTargettableLivingEntityWithinAABB(Level world, @Nullable LivingEntity shooter, Vec3 pos, float reach) {

        return getTargettableEntitiesWithinAABB(world, shooter, pos, reach)
                .stream()
                .filter(e -> e instanceof LivingEntity)
                .map(e -> (LivingEntity) e)
                .toList();


    }

    /**
     * 在默认视锥（{@link #DEFAULT_VIEW_CONE_RANGE} / {@link #DEFAULT_VIEW_CONE_HALF_ANGLE_DEGREES}）内选取目标。
     */
    public static Optional<LivingEntity> selectClosestInViewCone(LivingEntity viewer) {
        return selectClosestInViewCone(viewer, DEFAULT_VIEW_CONE_RANGE, DEFAULT_VIEW_CONE_HALF_ANGLE_DEGREES);
    }

    /**
     * 在视锥内选取目标：距离 ≤ {@code range}，与视线夹角 ≤ {@code halfAngleDegrees}。
     * 优先夹角更小，夹角接近时取更近者。
     */
    public static Optional<LivingEntity> selectClosestInViewCone(
            LivingEntity viewer,
            double range,
            float halfAngleDegrees
    ) {
        if (viewer == null || range <= 0.0 || halfAngleDegrees <= 0.0f) {
            return Optional.empty();
        }

        Vec3 eye = viewer.getEyePosition(1.0f);
        Vec3 look = viewer.getLookAngle();
        if (look.lengthSqr() < 1.0e-8) {
            return Optional.empty();
        }
        look = look.normalize();

        double cosThreshold = Math.cos(Math.toRadians(halfAngleDegrees));
        double rangeSq = range * range;

        AABB searchBox = new AABB(eye, eye).inflate(range);
        LivingEntity best = null;
        double bestDot = -1.0;
        double bestDistSq = Double.MAX_VALUE;

        for(LivingEntity candidate : viewer.level().getEntitiesOfClass(
                LivingEntity.class,
                searchBox,
                entity -> EntityPredicateHelper.canTarget(viewer, entity)
        )) {
            Vec3 toCenter = candidate.getBoundingBox().getCenter().subtract(eye);
            double distSq = toCenter.lengthSqr();
            if (distSq > rangeSq || distSq < 1.0e-8) {
                continue;
            }

            Vec3 direction = toCenter.scale(1.0 / Math.sqrt(distSq));
            double dot = look.dot(direction);
            if (dot < cosThreshold) {
                continue;
            }

            if (best == null
                    || dot > bestDot + 1.0e-6
                    || (Math.abs(dot - bestDot) <= 1.0e-6 && distSq < bestDistSq)) {
                best = candidate;
                bestDot = dot;
                bestDistSq = distSq;
            }
        }

        return Optional.ofNullable(best);
    }

    /**
     * 在自身中心 {@code range} 格内选取最近可攻击目标。
     */
    public static Optional<Entity> selectClosestWithinRange(LivingEntity viewer, double range) {
        if (viewer == null || range <= 0.0) {
            return Optional.empty();
        }
        return getTargettableEntitiesWithinAABB(viewer.level(), viewer, viewer.position(), (float) range)
                .stream()
                .min(Comparator.comparingDouble(viewer::distanceToSqr));
    }

    /**
     * 判断目标是否落在视锥内（距离 + 视线夹角）。
     */
    public static boolean isInsideViewCone(
            LivingEntity viewer,
            LivingEntity target,
            double range,
            float halfAngleDegrees
    ) {
        if (viewer == null || target == null || range <= 0.0 || halfAngleDegrees <= 0.0f) {
            return false;
        }
        Vec3 eye = viewer.getEyePosition(1.0f);
        Vec3 look = viewer.getLookAngle();
        if (look.lengthSqr() < 1.0e-8) {
            return false;
        }
        Vec3 to = target.getBoundingBox().getCenter().subtract(eye);
        double distSq = to.lengthSqr();
        if (distSq > range * range || distSq < 1.0e-8) {
            return false;
        }
        double dot = look.normalize().dot(to.normalize());
        return dot >= Math.cos(Math.toRadians(halfAngleDegrees));
    }
}
