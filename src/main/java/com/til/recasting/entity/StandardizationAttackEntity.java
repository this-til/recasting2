package com.til.recasting.entity;

import com.til.recasting.Recasting;
import com.til.recasting.registry.RecastingEntityDataSerializers;
import com.til.recasting.registry.instance.AttackType;
import com.til.recasting.util.CallbackPoint;
import com.til.recasting.util.DamageStructure;
import lombok.Getter;
import lombok.Setter;
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

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * 所有攻击实体的基类。
 */
public abstract class StandardizationAttackEntity extends Entity {

    protected static final EntityDataAccessor<Integer> SHOOTER_ENTITY_ID =
            SynchedEntityData.defineId(StandardizationAttackEntity.class, EntityDataSerializers.INT);

    protected static final EntityDataAccessor<ResourceLocation> MODEL =
            SynchedEntityData.defineId(StandardizationAttackEntity.class, RecastingEntityDataSerializers.RESOURCE_LOCATION.get());

    protected static final EntityDataAccessor<ResourceLocation> TEXTURE =
            SynchedEntityData.defineId(StandardizationAttackEntity.class, RecastingEntityDataSerializers.RESOURCE_LOCATION.get());

    protected static final EntityDataAccessor<Integer> MAX_LIFE_TIEM =
            SynchedEntityData.defineId(StandardizationAttackEntity.class, EntityDataSerializers.INT);

    protected static final EntityDataAccessor<Integer> COLOR =
            SynchedEntityData.defineId(StandardizationAttackEntity.class, EntityDataSerializers.INT);

    protected static final EntityDataAccessor<Float> DAMAGE =
            SynchedEntityData.defineId(StandardizationAttackEntity.class, EntityDataSerializers.FLOAT);

    protected static final EntityDataAccessor<Float> MODIFIED_RATIO =
            SynchedEntityData.defineId(StandardizationAttackEntity.class, EntityDataSerializers.FLOAT);

    protected static final EntityDataAccessor<Float> ROLL =
            SynchedEntityData.defineId(StandardizationAttackEntity.class, EntityDataSerializers.FLOAT);

    protected static final EntityDataAccessor<Float> SIZE =
            SynchedEntityData.defineId(StandardizationAttackEntity.class, EntityDataSerializers.FLOAT);

    protected static final EntityDataAccessor<Boolean> MUTE =
            SynchedEntityData.defineId(StandardizationAttackEntity.class, EntityDataSerializers.BOOLEAN);

    protected static final EntityDataAccessor<Boolean> CRITICAL =
            SynchedEntityData.defineId(StandardizationAttackEntity.class, EntityDataSerializers.BOOLEAN);

    protected static final EntityDataAccessor<ResourceLocation[]> CLIENT_EXTENSIONS =
            SynchedEntityData.defineId(StandardizationAttackEntity.class, RecastingEntityDataSerializers.RESOURCE_LOCATION_ARRAY.get());

    protected boolean completeSetup = false;

    public final CallbackPoint<ISetup> setupCallbackPoint = new CallbackPoint<>();
    public final CallbackPoint<IEnd> endCallbackPoint = new CallbackPoint<>();
    public final CallbackPoint<Runnable> clientTickCallbackPoint = new CallbackPoint<>();

    @Getter
    @Setter
    protected List<AttackType> attackTypeModelList = new ArrayList<>();

    @Nullable
    protected LivingEntity shooter;

    public StandardizationAttackEntity(EntityType<?> entityTypeIn, Level worldIn, LivingEntity shooting) {
        super(entityTypeIn, worldIn);
        if (!worldIn.isClientSide() && shooting == null) {
            this.discard();
        }
        setShooter(shooting);
        setNoGravity(true);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(SHOOTER_ENTITY_ID, -1);
        builder.define(MODEL, Recasting.prefix("default"));
        builder.define(TEXTURE, Recasting.prefix("default"));
        builder.define(MAX_LIFE_TIEM, 100);
        builder.define(COLOR, new Color(0x3333FF).getRGB());
        builder.define(DAMAGE, 0f);
        builder.define(MODIFIED_RATIO, 1f);
        builder.define(ROLL, 0f);
        builder.define(SIZE, 1f);
        builder.define(MUTE, false);
        builder.define(CRITICAL, false);
        builder.define(CLIENT_EXTENSIONS, new ResourceLocation[0]);
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
            clientTickCallbackPoint.call(Runnable::run);
            return;
        }

        if (getMaxLifeTime() < tickCount) {
            discard();
        }
    }

    @Override
    public void onSyncedDataUpdated(@NotNull EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        // TODO(P5): EntityClientExtensionHandler.refresh(this)
    }

    @Override
    public void remove(@NotNull RemovalReason reason) {
        super.remove(reason);
        endCallbackPoint.call(IEnd::end);
    }

    public void setUp() {
        setupCallbackPoint.call(ISetup::setup);
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        double d0 = 256.0D * getViewScale();
        return distance < d0 * d0;
    }

    public void addAttackType(AttackType attackType) {
        attackTypeModelList.add(attackType);
    }

    @Nullable
    public LivingEntity getShooter() {
        int id = entityData.get(SHOOTER_ENTITY_ID);

        if (shooter != null && shooter.getId() != id) {
            shooter = null;
        }

        if (shooter == null && id > 0) {
            Entity entity = level().getEntity(id);
            if (entity instanceof LivingEntity living) {
                shooter = living;
            }
        }

        return shooter;
    }

    public void setShooter(@Nullable LivingEntity shooter) {
        entityData.set(SHOOTER_ENTITY_ID, shooter != null ? shooter.getId() : -1);
        this.shooter = shooter;
    }

    public ResourceLocation getModel() {
        return entityData.get(MODEL);
    }

    public void setModel(ResourceLocation model) {
        entityData.set(MODEL, model);
    }

    public ResourceLocation getTexture() {
        return entityData.get(TEXTURE);
    }

    public void setTexture(ResourceLocation texture) {
        entityData.set(TEXTURE, texture);
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

    public void setColor(int color) {
        setColor(new Color(color));
    }

    public float getDamage() {
        return entityData.get(DAMAGE);
    }

    @Deprecated(since = "请使用setModifiedRatio，除非特别情况继续使用本方法")
    public void setDamage(float damage) {
        entityData.set(DAMAGE, damage);
    }

    public float getModifiedRatio() {
        return entityData.get(MODIFIED_RATIO);
    }

    public void setModifiedRatio(float modifiedRatio) {
        entityData.set(MODIFIED_RATIO, modifiedRatio);
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

    public boolean isCritical() {
        return entityData.get(CRITICAL);
    }

    public void setCritical(boolean critical) {
        entityData.set(CRITICAL, critical);
    }

    public ResourceLocation[] getClientExtensions() {
        return entityData.get(CLIENT_EXTENSIONS).clone();
    }

    public void setClientExtensions(ResourceLocation... clientExtensions) {
        entityData.set(CLIENT_EXTENSIONS, clientExtensions.clone());
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
        lookAt(target, isDistance, true, true);
    }

    public void lookAt(Vec3 target, boolean isDistance, boolean prevSynchronous, boolean updateMotion) {
        Vec3 distance = isDistance ? target : target.subtract(getPos());
        distance = distance.normalize();
        double d0 = distance.x;
        double d1 = distance.y;
        double d2 = distance.z;
        double d3 = Math.sqrt(d0 * d0 + d2 * d2);

        float rotationPitch = Mth.wrapDegrees((float) (-(Mth.atan2(d1, d3) * (double) (180F / (float) Math.PI))));
        float rotationYaw = Mth.wrapDegrees((float) (Mth.atan2(d2, d0) * (double) (180F / (float) Math.PI)) - 90.0F);

        setRot(rotationYaw, rotationPitch, prevSynchronous);

        if (updateMotion) {
            updateMotion();
        }
    }

    public void updateMotion() {
        updateMotion(1.0f);
    }

    public void updateMotion(float seep) {
        float fYawDtoR = (getYRot() / 180F) * (float) Math.PI;
        float fPitDtoR = (getXRot() / 180F) * (float) Math.PI;
        float motionX = -Mth.sin(fYawDtoR) * Mth.cos(fPitDtoR) * seep;
        float motionY = -Mth.sin(fPitDtoR) * seep;
        float motionZ = Mth.cos(fYawDtoR) * Mth.cos(fPitDtoR) * seep;
        setDeltaMovement(motionX, motionY, motionZ);
    }

    public DamageStructure getDamageStructure() {
        return new DamageStructure(getModifiedRatio(), getDamage());
    }

    @Override
    public boolean shouldBeSaved() {
        return false;
    }

    public interface ISetup {
        void setup();
    }

    public interface IEnd {
        void end();
    }
}
