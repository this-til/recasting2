package com.til.recasting.handler;

import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class PosHelper {

    private static final double VIEW_CONE_HALF_ANGLE_RAD = Math.toRadians(15.0);
    private static final double VIEW_CONE_COS = Math.cos(VIEW_CONE_HALF_ANGLE_RAD);

    /**
     * 光线物理碰撞结果：落点、命中实体（若有）、是否撞到方块
     */
    public record BeamHit(Vec3 hitPos, @Nullable LivingEntity entity, boolean hitBlock) {
    }

    /**
     * 获取攻击目标位置。
     * 优先锁定目标；否则取半角 15° 视锥内沿途最近可攻击活体；锥内无实体则取视线中心远端（方块截断或 maxDistance）。
     *
     * @param livingEntity    发起攻击的实体
     * @param slashBladeState 刀剑状态
     * @param maxDistance     最大追踪距离
     * @return 攻击目标位置
     */
    public static Vec3 getAttackTargetPosition(LivingEntity livingEntity, ISlashBladeState slashBladeState, double maxDistance) {
        Level worldIn = livingEntity.level();

        Entity target = slashBladeState.getTargetEntity(worldIn);
        if (target != null && target.isAlive() && !target.isRemoved()) {
            return aimPointOnEntity(target);
        }

        Vec3 eye = livingEntity.getEyePosition(1.0f);
        Vec3 look = livingEntity.getLookAngle();
        LivingEntity nearest = findNearestInViewCone(worldIn, livingEntity, eye, look, maxDistance);
        if (nearest != null) {
            return aimPointOnEntity(nearest);
        }

        Vec3 farEnd = eye.add(look.scale(maxDistance));
        HitResult result = worldIn.clip(
                new ClipContext(
                        eye,
                        farEnd,
                        ClipContext.Block.COLLIDER,
                        ClipContext.Fluid.NONE,
                        livingEntity
                )
        );
        return result.getLocation();
    }

    /**
     * 获取攻击目标位置（默认距离 64）
     *
     * @param livingEntity    发起攻击的实体
     * @param slashBladeState 刀剑状态
     * @return 攻击目标位置
     */
    public static Vec3 getAttackTargetPosition(LivingEntity livingEntity, ISlashBladeState slashBladeState) {
        return getAttackTargetPosition(livingEntity, slashBladeState, 64.0);
    }

    private static Vec3 aimPointOnEntity(Entity entity) {
        return new Vec3(
                entity.getX(),
                entity.getY() + entity.getEyeHeight() * 0.5,
                entity.getZ()
        );
    }

    /**
     * 在视锥内选取距眼点最近的可攻击活体。
     */
    @Nullable
    private static LivingEntity findNearestInViewCone(
            Level level,
            LivingEntity user,
            Vec3 eye,
            Vec3 look,
            double maxDistance
    ) {
        double pad = maxDistance * Math.tan(VIEW_CONE_HALF_ANGLE_RAD);
        AABB box = new AABB(eye, eye.add(look.scale(maxDistance))).inflate(pad);
        double maxDistanceSq = maxDistance * maxDistance;

        LivingEntity best = null;
        double bestDistSq = Double.MAX_VALUE;
        for (LivingEntity candidate : level.getEntitiesOfClass(LivingEntity.class, box, LivingEntity::isAlive)) {
            if (!EntityPredicateHelper.canTarget(user, candidate)) {
                continue;
            }
            Vec3 center = candidate.getBoundingBox().getCenter();
            Vec3 to = center.subtract(eye);
            double distSq = to.lengthSqr();
            if (distSq > maxDistanceSq || distSq < 1.0E-8) {
                continue;
            }
            double cosAngle = to.dot(look) / Math.sqrt(distSq);
            if (cosAngle < VIEW_CONE_COS) {
                continue;
            }
            if (distSq < bestDistSq) {
                bestDistSq = distSq;
                best = candidate;
            }
        }
        return best;
    }

    /**
     * 头顶上方发射点（头顶再上 {@code offset} 格）
     */
    public static Vec3 getAboveHead(LivingEntity livingEntity, double offset) {
        return livingEntity.position().add(0.0, livingEntity.getBbHeight() + offset, 0.0);
    }

    /**
     * 沿线段做方块与可攻击实体碰撞；方块截断射线，实体取截断段内最近命中
     */
    public static BeamHit castLivingBeam(Level level, LivingEntity shooter, Vec3 start, Vec3 end) {
        if (start.distanceToSqr(end) <= 1.0E-8) {
            return new BeamHit(start, null, false);
        }

        BlockHitResult blockHit = level.clip(
                new ClipContext(
                        start,
                        end,
                        ClipContext.Block.COLLIDER,
                        ClipContext.Fluid.NONE,
                        shooter
                )
        );
        Vec3 clippedEnd = blockHit.getType() == HitResult.Type.BLOCK ? blockHit.getLocation() : end;

        EntityHitResult entityHit = ProjectileUtil.getEntityHitResult(
                level,
                shooter,
                start,
                clippedEnd,
                new AABB(start, clippedEnd).inflate(1.0),
                entity -> canHitLivingBeam(shooter, entity)
        );

        if (entityHit != null && entityHit.getEntity() instanceof LivingEntity living) {
            // 特效与线段终点落在实体中心，避免射线贴 AABB 表面偏到脚底
            return new BeamHit(living.getBoundingBox().getCenter(), living, false);
        }
        if (blockHit.getType() == HitResult.Type.BLOCK) {
            return new BeamHit(blockHit.getLocation(), null, true);
        }
        return new BeamHit(clippedEnd, null, false);
    }

    private static boolean canHitLivingBeam(LivingEntity shooter, Entity entity) {
        return entity instanceof LivingEntity
                && entity.isAlive()
                && EntityPredicateHelper.canTarget(shooter, entity);
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

    /**
     * 点到线段的最短距离
     */
    public static double distancePointToSegment(Vec3 point, Vec3 start, Vec3 end) {
        Vec3 segment = end.subtract(start);
        double lengthSqr = segment.lengthSqr();
        if (MathHelper.epsilonEquals(lengthSqr, 0.0)) {
            return point.distanceTo(start);
        }
        double t = point.subtract(start).dot(segment) / lengthSqr;
        t = Math.max(0.0, Math.min(1.0, t));
        Vec3 closest = start.add(segment.scale(t));
        return point.distanceTo(closest);
    }

}
