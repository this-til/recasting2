package com.til.recasting.entity;

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
     * 更新追踪目标的方向
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

        // 每N tick更新一次方向
        if (tickCount % getTrackingUpdateInterval() == 0) {
            Vec3 targetPos = target.position().add(0, target.getBbHeight() * 0.5, 0);
            lookAt(targetPos, false);
            updateMotion(getSeep());
        }
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
}

