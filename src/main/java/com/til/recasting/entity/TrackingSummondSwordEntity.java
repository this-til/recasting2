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
        Vec3 targetPos = calculatePredictedTargetPosition(target);
        
        // 计算目标方向向量
        Vec3 desiredDirection = targetPos.subtract(getPos()).normalize();
        
        // 计算当前速度方向
        Vec3 currentVelocity = getDeltaMovement();
        Vec3 currentDirection = currentVelocity.length() > 0.001 ? currentVelocity.normalize() : desiredDirection;
        
        // 使用速度向量的平滑插值来实现物理弹道效果
        float smoothness = getTurnSmoothness();

        // 计算转向角度
        double dot = currentDirection.dot(desiredDirection);
        dot = MathHelper.clamp(dot, -1.0, 1.0);
        double angle = Math.acos(dot);
        
        // 如果角度很小，直接使用目标方向
        if (angle < 0.01) {
            lookAt(desiredDirection, true, false, false);
            updateMotion(getSeep());
            return;
        }
        
        // 计算最大可转向角度（基于最大转向速度）
        float maxTurnSpeed = getMaxTurnSpeed();
        double maxTurnAngle = Math.toRadians(maxTurnSpeed);
        
        // 限制转向角度
        double turnAngle = Math.min(angle, maxTurnAngle);
        
        // 计算插值参数（基于平滑度和转向角度）
        double t = smoothness * (turnAngle / angle);
        t = MathHelper.clamp(t, 0.0, 1.0);
        
        // 使用球面线性插值（SLERP）来平滑转向
        Vec3 newDirection = slerp(currentDirection, desiredDirection, t);
        
        // 更新朝向和速度
        lookAt(newDirection, true, false, false);
        updateMotion(getSeep());
    }
    
    /**
     * 计算预测的目标位置（考虑目标的移动速度）
     */
    protected Vec3 calculatePredictedTargetPosition(Entity target) {
        Vec3 targetPos = target.position().add(0, target.getBbHeight() * 0.5, 0);
        float predictionFactor = getPredictionFactor();
        
        if (predictionFactor <= 0.0f) {
            return targetPos;
        }
        
        // 获取目标的速度
        Vec3 targetVelocity = target.getDeltaMovement();
        
        // 计算到达目标所需的时间（简化估算）
        Vec3 toTarget = targetPos.subtract(getPos());
        double distance = toTarget.length();
        double speed = getDeltaMovement().length();
        
        if (speed > 0.001 && distance > 0.001) {
            double timeToTarget = distance / speed;
            // 应用预测因子
            timeToTarget *= predictionFactor;
            
            // 预测目标未来位置
            Vec3 predictedPos = targetPos.add(targetVelocity.scale(timeToTarget));
            return predictedPos;
        }
        
        return targetPos;
    }
    
    /**
     * 球面线性插值（SLERP）
     * 
     * @param start 起始方向向量（已归一化）
     * @param end 目标方向向量（已归一化）
     * @param t 插值参数（0.0-1.0）
     * @return 插值后的方向向量（已归一化）
     */
    protected Vec3 slerp(Vec3 start, Vec3 end, double t) {
        double dot = MathHelper.clamp(start.dot(end), -1.0, 1.0);
        double angle = Math.acos(dot);
        double sinAngle = Math.sin(angle);
        
        if (sinAngle < 0.001) {
            // 角度太小，直接线性插值并归一化
            return start.scale(1.0 - t).add(end.scale(t)).normalize();
        }
        
        double t1 = Math.sin((1.0 - t) * angle) / sinAngle;
        double t2 = Math.sin(t * angle) / sinAngle;
        
        return start.scale(t1).add(end.scale(t2)).normalize();
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

