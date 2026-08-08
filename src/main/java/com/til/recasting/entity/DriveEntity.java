package com.til.recasting.entity;

import com.til.recasting.handler.MathHelper;
import com.til.recasting.registry.RecastingAttackTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * @Author: til
 * @Description: 剑气
 */
public class DriveEntity extends SlashEffectEntity {

    /***
     * 速度
     */
    protected static final EntityDataAccessor<Float> SEEP = SynchedEntityData.defineId(DriveEntity.class, EntityDataSerializers.FLOAT);

    /***
     * 穿透墙体
     */
    protected static final EntityDataAccessor<Boolean> PARAMETER = SynchedEntityData.defineId(DriveEntity.class, EntityDataSerializers.BOOLEAN);

    /**
     * 每 tick 速度倍率（对齐旧 RoundaboutDrive ×1.05）
     */
    protected static final EntityDataAccessor<Float> SPEED_SCALE_PER_TICK =
            SynchedEntityData.defineId(DriveEntity.class, EntityDataSerializers.FLOAT);

    public DriveEntity(EntityType<? extends DriveEntity> entityTypeIn, Level worldIn, LivingEntity shooting) {
        super(entityTypeIn, worldIn, shooting);

        setMaxLifeTime(40);

        if (shooting != null) {
            setPos(shooting.getX(), shooting.getY(), shooting.getZ());
            setRot(shooting.getYRot(), shooting.getXRot());
            lookAt(getDeltaMovement(), true);
        }

        setAttackInterval(1);

        setUseBlockParticle(false);
    }

    @Override
    protected void registerAttackTypes() {
        addAttackType(RecastingAttackTypes.DRIVE_ATTACK.get());
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        getEntityData().define(SEEP, 4f);
        getEntityData().define(PARAMETER, false);
        getEntityData().define(SPEED_SCALE_PER_TICK, 1.0f);
    }

    @Override
    public void tick() {
        super.tick();

        Vec3 deltaMovement = getDeltaMovement();
        float speedScale = getSpeedScalePerTick();
        if (!MathHelper.epsilonEquals(speedScale, 1.0f)) {
            deltaMovement = deltaMovement.scale(speedScale);
            setDeltaMovement(deltaMovement);
        }

        Vec3 positionVec = getPos();
        Vec3 movedVec = positionVec.add(deltaMovement.x, deltaMovement.y, deltaMovement.z);

        double mx = movedVec.x;
        double my = movedVec.y;
        double mz = movedVec.z;
        setPos(mx, my, mz);

        if (!level().isClientSide() && !isParameter()) {

            BlockHitResult hitResult = level().clip(
                    new ClipContext(
                            positionVec,
                            movedVec,
                            ClipContext.Block.COLLIDER,
                            ClipContext.Fluid.NONE,
                            this
                    )
            );

            if (hitResult.getType() == HitResult.Type.BLOCK && tickCount > 5) {
                this.discard();
            }

        }

    }

    @Override
    public void lookAt(Vec3 target, boolean isDistance, boolean prevSynchronous, boolean updateMotion) {
        super.lookAt(target, isDistance, prevSynchronous, updateMotion);
        updateMotion(getSeep());
    }


    public float getSeep() {
        return this.entityData.get(SEEP);
    }

    public void setSeep(float seep) {
        this.entityData.set(SEEP, seep);
    }

    public boolean isParameter() {
        return this.entityData.get(PARAMETER);
    }

    public void setParameter(boolean parameter) {
        this.entityData.set(PARAMETER, parameter);
    }

    public float getSpeedScalePerTick() {
        return this.entityData.get(SPEED_SCALE_PER_TICK);
    }

    public void setSpeedScalePerTick(float speedScalePerTick) {
        this.entityData.set(SPEED_SCALE_PER_TICK, speedScalePerTick);
    }

}
