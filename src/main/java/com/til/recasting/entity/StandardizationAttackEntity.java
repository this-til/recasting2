package com.til.recasting.entity;

import com.til.recasting.Recasting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

/**
 * @Author: til
 * @Description: 所有攻击实体的基类
 */
public abstract class StandardizationAttackEntity extends Entity {

    /**
     * 发射实体的ID
     */
    protected static final EntityDataAccessor<Integer> SHOOTER_ENTITY_ID = SynchedEntityData.defineId(StandardizationAttackEntity.class, EntityDataSerializers.INT);

    /**
     * 模型资源位置
     */
    protected static final EntityDataAccessor<String> MODEL = SynchedEntityData.defineId(StandardizationAttackEntity.class, EntityDataSerializers.STRING);

    /**
     * 纹理资源位置
     */
    protected static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(StandardizationAttackEntity.class, EntityDataSerializers.STRING);

    /**
     * 最大存活时间
     */
    protected static final EntityDataAccessor<Integer> MAX_LIFE_TIEM = SynchedEntityData.defineId(StandardizationAttackEntity.class, EntityDataSerializers.INT);

    /**
     * 颜色
     */
    protected static final EntityDataAccessor<Integer> COLOR = SynchedEntityData.defineId(StandardizationAttackEntity.class, EntityDataSerializers.INT);

    /**
     * 伤害值
     */
    protected static final EntityDataAccessor<Float> DAMAGE = SynchedEntityData.defineId(StandardizationAttackEntity.class, EntityDataSerializers.FLOAT);

    /**
     * 旋转角度
     */
    protected static final EntityDataAccessor<Float> ROLL = SynchedEntityData.defineId(StandardizationAttackEntity.class, EntityDataSerializers.FLOAT);

    /**
     * 大小
     */
    protected static final EntityDataAccessor<Float> SIZE = SynchedEntityData.defineId(StandardizationAttackEntity.class, EntityDataSerializers.FLOAT);

    /**
     * 是否静音
     */
    protected static final EntityDataAccessor<Boolean> MUTE = SynchedEntityData.defineId(StandardizationAttackEntity.class, EntityDataSerializers.BOOLEAN);

    boolean completeSetup = false;

    public StandardizationAttackEntity(EntityType<?> entityTypeIn, Level worldIn, LivingEntity shooting) {
        super(entityTypeIn, worldIn);
        if (!worldIn.isClientSide && shooting == null) {
            //必须有shooting
            this.discard();
        }
        setShooter(shooting);
        setNoGravity(true);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(SHOOTER_ENTITY_ID, -1);
        this.entityData.define(MODEL, Recasting.prefix("default").toString());
        this.entityData.define(TEXTURE, Recasting.prefix("default").toString());
        this.entityData.define(MAX_LIFE_TIEM, 100);
        this.entityData.define(COLOR, new Color(0x3333FF).getRGB());
        this.entityData.define(DAMAGE, 1f);
        this.entityData.define(ROLL, 0f);
        this.entityData.define(SIZE, 1f);
        this.entityData.define(MUTE, false);
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag compoundTag) {
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag compoundTag) {
    }

    @Override
    public void tick() {
        super.tick();

        if (!completeSetup) {
            completeSetup = true;
            setUp();
        }

        if (level().isClientSide()) {
            return;
        }

        if (getMaxLifeTime() < tickCount) {
            discard();
        }

    }

    @Override
    public void remove(@NotNull RemovalReason reason) {
        super.remove(reason);
    }

    public void setUp() {
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        double d0 = 256.0D * getViewScale();
        return distance < d0 * d0;
    }

    @Nullable
    protected LivingEntity shooter;

    @Nullable
    public LivingEntity getShooter() {
        int id = entityData.get(SHOOTER_ENTITY_ID);

        if (shooter != null && shooter.getId() != id) {
            shooter = null;
        }

        if (shooter == null) {
            if (id > 0) {
                Entity entity = level().getEntity(id);
                if (entity instanceof LivingEntity) {
                    shooter = (LivingEntity) entity;
                }
            }
        }

        return shooter;
    }

    public void setShooter(@Nullable LivingEntity shooter) {
        entityData.set(
                SHOOTER_ENTITY_ID,
                shooter != null
                        ? shooter.getId()
                        : -1
        );
        this.shooter = shooter;
    }

    public ResourceLocation getModel() {
        return ResourceLocation.tryParse(entityData.get(MODEL));
    }

    public void setModel(ResourceLocation model) {
        entityData.set(MODEL, model.toString());
    }

    public ResourceLocation getTexture() {
        return ResourceLocation.tryParse(entityData.get(TEXTURE));
    }

    public void setTexture(ResourceLocation texture) {
        entityData.set(TEXTURE, texture.toString());
    }

    public int getMaxLifeTime() {
        return entityData.get(MAX_LIFE_TIEM);
    }

    public void setMaxLifeTime(int maxLife) {
        entityData.set(MAX_LIFE_TIEM, maxLife);
    }

    public Color getColor() {
        return new Color(entityData.get(COLOR));
    }

    public void setColor(Color color) {
        entityData.set(COLOR, color.getRGB());
    }

    public float getDamage() {
        return entityData.get(DAMAGE);
    }

    public void setDamage(float damage) {
        entityData.set(DAMAGE, damage);
    }

    public float getRoll() {
        return entityData.get(ROLL);
    }

    public void setRoll(float roll) {
        entityData.set(ROLL, roll);
    }

    public float getSize() {
        return entityData.get(SIZE);
    }

    public void setSize(float size) {
        entityData.set(SIZE, size);
    }

    public boolean isMute() {
        return entityData.get(MUTE);
    }

    public void setMute(boolean mute) {
        entityData.set(MUTE, mute);
    }

    public void setRot(float yRot, float xRot, boolean prevSynchronous) {
        setYRot(yRot % 360.0F);
        setXRot(xRot % 360.0F);

        if (prevSynchronous) {
            xRotO = xRot;
            yRotO = yRot;
        }
    }

    public Vec3 getPos() {
        return new Vec3(getX(), getY(), getZ());
    }

    public void lookAt(Vec3 target, boolean isDistance) {
        lookAt(target, isDistance, true);
    }

    public void lookAt(Vec3 target, boolean isDistance, boolean prevSynchronous) {
        Vec3 distance = isDistance
                ? target
                : target.subtract(getPos());

        distance = distance.normalize();
        double d0 = distance.x;
        double d1 = distance.y;
        double d2 = distance.z;
        double d3 = Mth.sqrt((float) (d0 * d0 + d2 * d2));

        float rotationPitch = Mth.wrapDegrees((float) (-(Mth.atan2(d1, d3) * (double) (180F / (float) Math.PI))));
        float rotationYaw = Mth.wrapDegrees((float) (Mth.atan2(d2, d0) * (double) (180F / (float) Math.PI)) - 90.0F);

        setRot(rotationYaw, rotationPitch, prevSynchronous);
    }

    public void updateMotion(float seep) {
        float fYawDtoR = (getYRot() / 180F) * (float) Math.PI;
        float fPitDtoR = (getXRot() / 180F) * (float) Math.PI;
        float motionX = -Mth.sin(fYawDtoR) * Mth.cos(fPitDtoR) * seep;
        float motionY = -Mth.sin(fPitDtoR) * seep;
        float motionZ = Mth.cos(fYawDtoR) * Mth.cos(fPitDtoR) * seep;
        setDeltaMovement(motionX, motionY, motionZ);
    }

}

