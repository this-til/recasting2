package com.til.recasting.handler;

import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
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
import org.jetbrains.annotations.Nullable;

/**
 * 位置 / 射线几何辅助（方法名对齐 1.20，便于 SA/SE/Buff 移植）。
 */
public final class PosHelper {

    private PosHelper() {
    }

    /**
     * 光线碰撞结果：落点、命中实体（若有）、是否撞方块。
     */
    public record BeamHit(Vec3 hitPos, @Nullable LivingEntity entity, boolean hitBlock) {
    }

    public static boolean epsilonEquals(float x, float y) {
        return Math.abs(y - x) < 1.0E-5F;
    }

    public static boolean epsilonEquals(double x, double y) {
        return Math.abs(y - x) < 1.0E-5D;
    }

    /**
     * 优先锁定目标，否则视线追踪。
     */
    public static Vec3 getAttackTargetPosition(LivingEntity livingEntity, ISlashBladeState slashBladeState, double maxDistance) {
        Level worldIn = livingEntity.level();
        Entity target = slashBladeState.getTargetEntity(worldIn);
        if (target != null && target.isAlive() && !target.isRemoved()) {
            return getEntityAimPosition(target);
        }
        Vec3 start = livingEntity.getEyePosition(1.0f);
        Vec3 end = start.add(livingEntity.getLookAngle().scale(maxDistance));
        HitResult result = worldIn.clip(new ClipContext(
                start,
                end,
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                livingEntity
        ));
        return result.getLocation();
    }

    public static Vec3 getAttackTargetPosition(LivingEntity livingEntity, ISlashBladeState slashBladeState) {
        return getAttackTargetPosition(livingEntity, slashBladeState, 64.0);
    }

    /**
     * 实体瞄准点：脚底 + 半眼高。
     */
    public static Vec3 getEntityAimPosition(Entity entity) {
        return new Vec3(
                entity.getX(),
                entity.getY() + entity.getEyeHeight() * 0.5,
                entity.getZ()
        );
    }

    /**
     * 头顶上方发射点。
     */
    public static Vec3 getAboveHead(LivingEntity livingEntity, double offset) {
        return livingEntity.position().add(0.0, livingEntity.getBbHeight() + offset, 0.0);
    }

    /**
     * 沿线段做方块与可攻击实体碰撞。
     */
    public static BeamHit castLivingBeam(Level level, LivingEntity shooter, Vec3 start, Vec3 end) {
        if (start.distanceToSqr(end) <= 1.0E-8) {
            return new BeamHit(start, null, false);
        }

        BlockHitResult blockHit = level.clip(new ClipContext(
                start,
                end,
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                shooter
        ));
        Vec3 clippedEnd = blockHit.getType() == HitResult.Type.BLOCK
                ? blockHit.getLocation()
                : end;

        EntityHitResult entityHit = ProjectileUtil.getEntityHitResult(
                level,
                shooter,
                start,
                clippedEnd,
                new AABB(start, clippedEnd).inflate(1.0),
                entity -> canHitLivingBeam(shooter, entity)
        );

        if (entityHit != null && entityHit.getEntity() instanceof LivingEntity living) {
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

    public static Vec3 getRandomVectorInCircle(RandomSource random, float distance) {
        double theta = random.nextDouble() * Math.PI;
        double phi = random.nextDouble() * 2.0 * Math.PI;
        double x = distance * Math.sin(theta) * Math.cos(phi);
        double y = distance * Math.sin(theta) * Math.sin(phi);
        double z = distance * Math.cos(theta);
        return new Vec3(x, y, z);
    }

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
