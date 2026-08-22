package com.til.recasting.entity;

import com.til.recasting.handler.EntityHelper;
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

import java.util.List;

/**
 * 追踪幻影飞刃：在 {@link SummondSwordEntity} 之上移植旧版 {@code EntitySummonedBlade} 的索敌/限速转向。
 * 位移、碰撞、生成仍走父类。
 */
public class TrackingSummondSwordEntity extends SummondSwordEntity {

    protected static final EntityDataAccessor<Integer> TARGET_ENTITY_ID = SynchedEntityData.defineId(TrackingSummondSwordEntity.class, EntityDataSerializers.INT);
    /**
     * 开始转向前的等待 tick（旧版 Interval）
     */
    protected static final EntityDataAccessor<Integer> INTERVAL = SynchedEntityData.defineId(TrackingSummondSwordEntity.class, EntityDataSerializers.INT);

    /**
     * 无有效目标时的重索敌范围
     */
    private static final double RETARGET_RANGE = 32.0;
    private static final float TURN_STEP = 10.0f;
    /**
     * 无锁定目标时的重索敌间隔（1s）
     */
    private static final int RETARGET_INTERVAL_TICKS = 20;

    /**
     * 无有效目标时是否自动重索敌（默认开启）
     */
    private boolean autoRetarget = true;

    @Nullable
    protected Entity targetEntity;

    /**
     * 下一次无目标索敌的 tickCount
     */
    private int nextRetargetTick = 0;

    /**
     * 命中实体时冻结渲染自旋（旧版 hitTime / hitStopFactor）
     */
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
        setIgnoringBlock(true);
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
     * 有有效目标则限速转向；无目标则保持当前朝向前进，并按间隔重索敌。
     * 锁定目标失效时立刻重索敌一次。朝向/速度走父类 {@link #setRot} / {@link #updateMotion(float)}。
     */
    protected void doTargeting() {
        Entity target = resolveOrAcquireTarget();
        if (target == null) {
            updateMotion(getSeep());
            return;
        }

        if (getInterval() >= tickCount) {
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

    /**
     * 解析当前锁定目标；失效则立刻索敌，本就无目标则每 {@link #RETARGET_INTERVAL_TICKS} 索敌一次。
     */
    @Nullable
    private Entity resolveOrAcquireTarget() {
        int targetId = getTargetEntityId();
        Entity target = targetId > 0
                ? level().getEntity(targetId)
                : null;
        if (target != null && target.isAlive()) {
            return target;
        }

        if (!autoRetarget) {
            return null;
        }

        boolean lostLockedTarget = targetId > 0;
        if (lostLockedTarget) {
            setTargetEntity(null);
            tryAcquireTarget();
            nextRetargetTick = tickCount + RETARGET_INTERVAL_TICKS;
        } else if (tickCount >= nextRetargetTick) {
            tryAcquireTarget();
            nextRetargetTick = tickCount + RETARGET_INTERVAL_TICKS;
        }

        targetId = getTargetEntityId();
        if (targetId <= 0) {
            return null;
        }
        target = level().getEntity(targetId);
        if (target == null || !target.isAlive()) {
            return null;
        }
        return target;
    }

    private void tryAcquireTarget() {
        if (level().isClientSide()) {
            return;
        }

        Entity replacement = findNearestAttackable(RETARGET_RANGE);
        if (replacement != null) {
            setTargetEntity(replacement);
        }
    }

    @Nullable
    private Entity findNearestAttackable(double range) {
        LivingEntity viewer = getShooter();
        if (viewer == null) {
            return null;
        }

        List<Entity> candidates = EntityHelper.getTargettableEntitiesWithinAABB(
                level(),
                viewer,
                position(),
                (float) range
        );

        double nearest = range;
        Entity pointed = null;
        for(Entity entity : candidates) {
            if (entity == this || !entity.isPickable()) {
                continue;
            }
            if (pierce != null && pierce.contains(entity.getId())) {
                continue;
            }

            double distance = distanceTo(entity);
            if (distance < nearest) {
                pointed = entity;
                nearest = distance;
            }
        }
        return pointed;
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
        entityData.set(TARGET_ENTITY_ID, target != null
                ? target.getId()
                : -1);
        this.targetEntity = target;
    }

    public int getInterval() {
        return entityData.get(INTERVAL);
    }

    public void setInterval(int interval) {
        entityData.set(INTERVAL, Math.max(0, interval));
    }

    public void setAutoRetarget(boolean autoRetarget) {
        this.autoRetarget = autoRetarget;
    }
}
