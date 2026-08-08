package com.til.recasting.entity;

import com.til.recasting.handler.EntityPredicateHelper;
import com.til.recasting.handler.MathHelper;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

/**
 * 追踪幻影飞刃：在 {@link SummondSwordEntity} 之上移植旧版 {@code EntitySummonedBlade} 的索敌/限速转向。
 * 位移、碰撞、生成仍走父类。
 */
public class TrackingSummondSwordEntity extends SummondSwordEntity {

    protected static final EntityDataAccessor<Integer> TARGET_ENTITY_ID =
            SynchedEntityData.defineId(TrackingSummondSwordEntity.class, EntityDataSerializers.INT);
    /** 开始转向前的等待 tick（旧版 Interval） */
    protected static final EntityDataAccessor<Integer> INTERVAL =
            SynchedEntityData.defineId(TrackingSummondSwordEntity.class, EntityDataSerializers.INT);

    private static final double ACQUIRE_RANGE = 15.0;
    private static final float TURN_STEP = 10.0f;

    @Nullable
    protected Entity targetEntity;

    /** 命中实体时冻结渲染自旋（旧版 hitTime / hitStopFactor） */
    public long hitTime = 0L;
    public float hitStopFactor = 0.0f;

    public TrackingSummondSwordEntity(
            EntityType<? extends TrackingSummondSwordEntity> entityTypeIn,
            Level worldIn,
            LivingEntity shooting
    ) {
        super(entityTypeIn, worldIn, shooting);
        hitStopFactor = random.nextFloat();
        setInterval(10);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        getEntityData().define(TARGET_ENTITY_ID, -1);
        getEntityData().define(INTERVAL, 10);
    }

    @Override
    public void tick() {
        if (getActionType() == ActionType.FLYING) {
            doTargeting();
        }
        super.tick();
    }

    @Override
    public void onHitEntity(Entity targetEntity, SummondAttackType summondAttackType) {
        hitTime = level().getGameTime();
        super.onHitEntity(targetEntity, summondAttackType);
    }

    /**
     * 旧版 {@code EntitySummonedBlade#doTargeting}：15 格索敌 + 每 tick 最多转 10° + 转弯减速。
     * 朝向/速度一律走父类 {@link #setRot} / {@link #updateMotion(float)}。
     */
    protected void doTargeting() {
        int targetId = getTargetEntityId();

        if (targetId <= 0) {
            acquireNearestTarget();
            return;
        }

        if (getInterval() >= tickCount) {
            return;
        }

        Entity target = level().getEntity(targetId);
        if (target == null || !target.isAlive()) {
            return;
        }

        float lastYaw = getYRot();
        float lastPitch = getXRot();
        float lastSpeed = (float) getDeltaMovement().length();

        faceEntity(target, TURN_STEP, TURN_STEP);

        float speedFactor = Math.abs(getYRot() - lastYaw) / TURN_STEP
                + Math.abs(getXRot() - lastPitch) / TURN_STEP;
        speedFactor = 1.0f - Math.min(speedFactor, 0.75f);
        speedFactor = (0.75f * speedFactor + lastSpeed * 9.0f) / 10.0f;

        updateMotion(speedFactor);
    }

    private void acquireNearestTarget() {
        LivingEntity viewer = getShooter();
        if (viewer == null) {
            return;
        }

        AABB searchBox = getBoundingBox().inflate(ACQUIRE_RANGE);
        double nearest = ACQUIRE_RANGE;
        Entity pointed = null;

        for (Entity entity : level().getEntities(this, searchBox)) {
            if (entity == null || !entity.isPickable()) {
                continue;
            }
            if (!EntityPredicateHelper.canTarget(viewer, entity)) {
                continue;
            }
            if (pierce != null && pierce.contains(entity.getId())) {
                continue;
            }
            if (!viewer.hasLineOfSight(entity)) {
                continue;
            }

            double distance = distanceTo(entity);
            if (distance < nearest) {
                pointed = entity;
                nearest = distance;
            }
        }

        if (pointed != null) {
            setTargetEntity(pointed);
        }
    }

    /**
     * 旧版 faceEntity：对当前 yRot/xRot 做步进，不另开驱动角。
     */
    protected void faceEntity(Entity target, float yawStep, float pitchStep) {
        double d0 = target.getX() - getX();
        double d1 = target.getZ() - getZ();
        double d2;
        if (target instanceof LivingEntity living) {
            d2 = living.getY() + living.getEyeHeight() - (getY() + getEyeHeight());
        } else {
            AABB box = target.getBoundingBox();
            d2 = (box.minY + box.maxY) / 2.0D - (getY() + getEyeHeight());
        }

        double d3 = MathHelper.sqrt(d0 * d0 + d1 * d1);
        float desiredYaw = (float) (Math.atan2(d1, d0) * 180.0D / Math.PI) - 90.0F;
        float desiredPitch = (float) (-(Math.atan2(d2, d3) * 180.0D / Math.PI));

        setRot(
                updateRotation(getYRot(), desiredYaw, yawStep),
                updateRotation(getXRot(), desiredPitch, pitchStep),
                false
        );
    }

    private static float updateRotation(float current, float target, float maxStep) {
        float delta = MathHelper.wrapDegrees(target - current);
        if (delta > maxStep) {
            delta = maxStep;
        }
        if (delta < -maxStep) {
            delta = -maxStep;
        }
        return current + delta;
    }

    public int getTargetEntityId() {
        return entityData.get(TARGET_ENTITY_ID);
    }

    @Nullable
    public Entity getTargetEntity() {
        int id = getTargetEntityId();
        if (targetEntity != null && targetEntity.getId() != id) {
            targetEntity = null;
        }
        if (targetEntity == null && id > 0) {
            Entity entity = level().getEntity(id);
            if (entity != null && entity.isAlive()) {
                targetEntity = entity;
            }
        }
        return targetEntity;
    }

    public void setTargetEntity(@Nullable Entity target) {
        entityData.set(TARGET_ENTITY_ID, target != null ? target.getId() : -1);
        this.targetEntity = target;
    }

    public int getInterval() {
        return entityData.get(INTERVAL);
    }

    public void setInterval(int interval) {
        entityData.set(INTERVAL, Math.max(0, interval));
    }
}
