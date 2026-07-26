package com.til.recasting.entity;

import com.til.recasting.handler.MathHelper;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

/**
 * @Author: til
 * @Description: 具有追踪目标功能的幻影剑
 */
public class TrackingSummondSwordEntity extends SummondSwordEntity {

    /**
     * 追踪目标实体的ID
     */
    protected static final EntityDataAccessor<Integer> TARGET_ENTITY_ID = SynchedEntityData.defineId(TrackingSummondSwordEntity.class, EntityDataSerializers.INT);

    /**
     * 追踪更新间隔（每N tick更新一次方向）
     */
    protected static final EntityDataAccessor<Integer> TRACKING_UPDATE_INTERVAL = SynchedEntityData.defineId(TrackingSummondSwordEntity.class, EntityDataSerializers.INT);

    /**
     * 最大追踪距离
     */
    protected static final EntityDataAccessor<Float> MAX_TRACKING_DISTANCE = SynchedEntityData.defineId(TrackingSummondSwordEntity.class, EntityDataSerializers.FLOAT);

    /**
     * 最大转向速度（度/tick）
     */
    protected static final EntityDataAccessor<Float> MAX_TURN_SPEED = SynchedEntityData.defineId(TrackingSummondSwordEntity.class, EntityDataSerializers.FLOAT);

    /**
     * 转向平滑度（0.0-1.0，值越大转向越平滑但响应越慢）
     */
    protected static final EntityDataAccessor<Float> TURN_SMOOTHNESS = SynchedEntityData.defineId(TrackingSummondSwordEntity.class, EntityDataSerializers.FLOAT);

    /**
     * 预测提前量（预测目标未来位置的倍数，0表示不预测）
     */
    protected static final EntityDataAccessor<Float> PREDICTION_FACTOR = SynchedEntityData.defineId(TrackingSummondSwordEntity.class, EntityDataSerializers.FLOAT);

    @Nullable
    protected Entity targetEntity;

    public TrackingSummondSwordEntity(EntityType<? extends TrackingSummondSwordEntity> entityTypeIn, Level worldIn, LivingEntity shooting) {
        super(entityTypeIn, worldIn, shooting);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        getEntityData().define(TARGET_ENTITY_ID, -1);
        getEntityData().define(TRACKING_UPDATE_INTERVAL, 1);
        getEntityData().define(MAX_TRACKING_DISTANCE, 64.0f);
        getEntityData().define(MAX_TURN_SPEED, 15.0f); // 默认每tick最多转向15度
        getEntityData().define(TURN_SMOOTHNESS, 0.3f); // 默认平滑度0.3
        getEntityData().define(PREDICTION_FACTOR, 0.5f); // 默认预测提前量0.5
    }

    @Override
    public void tick() {
        super.tick();

        // 在飞行状态下更新追踪
        if (getActionType() == ActionType.FLYING && !level().isClientSide()) {
            updateTracking();
        }
    }

    /**
     * 更新追踪目标的方向（使用物理弹道效果）
     */
    protected void updateTracking() {
        Entity target = getTargetEntity();
        
        // 如果没有目标或目标已死亡，立刻销毁自身
        if (target == null || !target.isAlive()) {
            discard();
            return;
        }

        // 检查距离
        double distance = getPos().distanceTo(target.position());
        if (distance > (double) getMaxTrackingDistance()) {
            // 超出追踪距离，销毁自身
            discard();
            return;
        }

        // 计算目标位置（考虑预测提前量）
        Vec3 targetPos = MathHelper.predictEntityCenterPosition(this, target, getPredictionFactor());
        
        // 计算目标方向向量
        Vec3 desiredDirection = targetPos.subtract(getPos()).normalize();
        
        // 计算当前速度方向
        Vec3 currentVelocity = getDeltaMovement();
        Vec3 currentDirection = currentVelocity.length() > 0.001 ? currentVelocity.normalize() : desiredDirection;
        
        Vec3 newDirection = MathHelper.smoothDirection(
                currentDirection,
                desiredDirection,
                getMaxTurnSpeed(),
                getTurnSmoothness()
        );

        // 更新朝向和速度
        lookAt(newDirection, true, false, false);
        updateMotion(getSeep());
    }


    @Nullable
    public Entity getTargetEntity() {
        int id = entityData.get(TARGET_ENTITY_ID);

        if (targetEntity != null && targetEntity.getId() != id) {
            targetEntity = null;
        }

        if (targetEntity == null) {
            if (id > 0) {
                Entity entity = level().getEntity(id);
                if (entity != null && entity.isAlive()) {
                    targetEntity = entity;
                } else {
                    // 实体不存在或已死亡，清除ID
                    entityData.set(TARGET_ENTITY_ID, -1);
                }
            }
        }

        return targetEntity;
    }

    public void setTargetEntity(@Nullable Entity targetEntity) {
        entityData.set(
                TARGET_ENTITY_ID,
                targetEntity != null
                        ? targetEntity.getId()
                        : -1
        );
        this.targetEntity = targetEntity;
    }

    public int getTrackingUpdateInterval() {
        return entityData.get(TRACKING_UPDATE_INTERVAL);
    }

    public void setTrackingUpdateInterval(int interval) {
        entityData.set(TRACKING_UPDATE_INTERVAL, interval);
    }

    public float getMaxTrackingDistance() {
        return entityData.get(MAX_TRACKING_DISTANCE);
    }

    public void setMaxTrackingDistance(float distance) {
        entityData.set(MAX_TRACKING_DISTANCE, distance);
    }

    public float getMaxTurnSpeed() {
        return entityData.get(MAX_TURN_SPEED);
    }

    public void setMaxTurnSpeed(float maxTurnSpeed) {
        entityData.set(MAX_TURN_SPEED, maxTurnSpeed);
    }

    public float getTurnSmoothness() {
        return entityData.get(TURN_SMOOTHNESS);
    }

    public void setTurnSmoothness(float turnSmoothness) {
        entityData.set(TURN_SMOOTHNESS, MathHelper.clamp(turnSmoothness, 0.0f, 1.0f));
    }

    public float getPredictionFactor() {
        return entityData.get(PREDICTION_FACTOR);
    }

    public void setPredictionFactor(float predictionFactor) {
        entityData.set(PREDICTION_FACTOR, Math.max(0.0f, predictionFactor));
    }
}
