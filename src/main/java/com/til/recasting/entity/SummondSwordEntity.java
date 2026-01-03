package com.til.recasting.entity;

import com.til.recasting.Recasting;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @Author: til
 * @Description: 幻影剑
 */
public class SummondSwordEntity extends StandardizationAttackEntity {

    /**
     * 命中实体的ID
     */
    protected static final EntityDataAccessor<Integer> HIT_ENTITY_ID = SynchedEntityData.defineId(SummondSwordEntity.class, EntityDataSerializers.INT);

    /**
     * 最大穿透数
     */
    protected static final EntityDataAccessor<Integer> MAX_PIERCE = SynchedEntityData.defineId(SummondSwordEntity.class, EntityDataSerializers.INT);

    /**
     * 破碎延迟
     */
    protected static final EntityDataAccessor<Integer> BREAK_DELAY = SynchedEntityData.defineId(SummondSwordEntity.class, EntityDataSerializers.INT);

    /**
     * 开始延迟时间
     */
    protected static final EntityDataAccessor<Integer> START_DELAY = SynchedEntityData.defineId(SummondSwordEntity.class, EntityDataSerializers.INT);

    /**
     * 飞行速度
     */
    protected static final EntityDataAccessor<Float> SEEP = SynchedEntityData.defineId(SummondSwordEntity.class, EntityDataSerializers.FLOAT);

    /**
     * 是否忽略方块碰撞
     */
    protected static final EntityDataAccessor<Boolean> IGNORING_BLOCK = SynchedEntityData.defineId(SummondSwordEntity.class, EntityDataSerializers.BOOLEAN);

    /**
     * 当前的行动类型
     */
    protected static final EntityDataAccessor<Integer> ACTION_TYPE = SynchedEntityData.defineId(SummondSwordEntity.class, EntityDataSerializers.INT);

    /***
     * 表示已经攻击的目标数量
     */
    @Nullable
    protected IntOpenHashSet pierce;

    @OnlyIn(Dist.CLIENT)
    protected boolean recordAttackPos;

    @OnlyIn(Dist.CLIENT)
    protected double hitX;
    @OnlyIn(Dist.CLIENT)
    protected double hitY;
    @OnlyIn(Dist.CLIENT)
    protected double hitZ;
    @OnlyIn(Dist.CLIENT)
    protected float hitYaw;
    @OnlyIn(Dist.CLIENT)
    protected float hitPitch;

    protected BlockState inBlockState;

    public SummondSwordEntity(EntityType<? extends SummondSwordEntity> entityTypeIn, Level worldIn, LivingEntity shooting) {
        super(entityTypeIn, worldIn, shooting);
        setModel(Recasting.prefix("default_summond_model"));
        setTexture(Recasting.prefix("default_summond_texture"));

        //设定初始角度等信息
        if (shooting != null) {
            setShooter(shooting);
            float dist = 2.0f;
            double ran = (random.nextFloat() - 0.5) * 2.0;
            double yaw = Math.toRadians(-shooting.getYRot() + 90);
            double x = ran * Math.sin(yaw);
            double y = 1.0 - Math.abs(ran);
            double z = ran * Math.cos(yaw);
            x *= dist;
            y *= dist;
            z *= dist;
            setPos(shooting.getX() + x, shooting.getY() + y, shooting.getZ() + z);
            setRot(shooting.getYRot(), shooting.getXRot());
            updateMotion(1);
            lookAt(getDeltaMovement(), true, true);
        }


    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(HIT_ENTITY_ID, -1);
        this.entityData.define(MAX_PIERCE, 1);
        this.entityData.define(BREAK_DELAY, 20);
        this.entityData.define(START_DELAY, 0);
        this.entityData.define(SEEP, 3.25f);
        this.entityData.define(IGNORING_BLOCK, false);
        this.entityData.define(ACTION_TYPE, ActionType.PREPARE.ordinal());
    }

    protected SoundEvent hitEntitySound = SoundEvents.TRIDENT_HIT;
    protected SoundEvent hitGroundSound = SoundEvents.TRIDENT_HIT_GROUND;
    protected SoundEvent breakSound = SoundEvents.GLASS_BREAK;

    @Override
    public void tick() {
        super.tick();

        setOldPosAndRot();

        switch (getActionType()) {
            case PREPARE -> {
                if (tickCount > getStartDelay()) {
                    setActionType(ActionType.FLYING);
                }
            }
            case FLYING -> {
                // 飞行状态处理
                Vec3 positionVec = getPos();
                Vec3 deltaMovement = getDeltaMovement();
                Vec3 movedVec = positionVec.add(deltaMovement.x, deltaMovement.y, deltaMovement.z);

                double mx = movedVec.x;
                double my = movedVec.y;
                double mz = movedVec.z;
                setPos(mx, my, mz);

                // 检测方块碰撞
                if (!isIgnoringBlock()) {
                    BlockHitResult hitResult = level().clip(
                            new ClipContext(
                                    positionVec,
                                    movedVec,
                                    ClipContext.Block.COLLIDER,
                                    ClipContext.Fluid.NONE,
                                    this
                            )
                    );

                    if (hitResult.getType() == HitResult.Type.BLOCK) {
                        onHitBlock(hitResult);
                        return;
                    }
                }

                // 检测实体碰撞
                EntityHitResult entityHitResult = getRayTrace(positionVec, movedVec);
                if (entityHitResult != null && entityHitResult.getType() == HitResult.Type.ENTITY) {
                    Entity target = entityHitResult.getEntity();
                    onHitEntity(target, SummondAttackType.HIT);
                    return;
                }

                // 水中气泡粒子
                if (isInWater()) {
                    for (int j = 0; j < 4; ++j) {
                        level().addParticle(ParticleTypes.BUBBLE, 
                                getX() - mx * 0.25D, 
                                getY() - my * 0.25D, 
                                getZ() - mz * 0.25D, 
                                mx, my, mz);
                    }
                }
            }
            case HIT_ENTITY -> {
                // 命中实体后跟随实体移动
                Entity hits = getHitEntity();

                if (hits == null || !hits.isAlive()) {
                    discard();
                    return;
                }

                if (!recordAttackPos) {
                    recordAttackPos = true;
                    hitYaw = getYRot() - hits.getYRot();
                    hitPitch = getXRot() - hits.getXRot();
                    hitX = getX() - hits.getX();
                    hitY = getY() - hits.getY();
                    hitZ = getZ() - hits.getZ();
                }

                double posX = hits.getX() + (hitX * Math.cos(Math.toRadians(hits.getYRot())) - hitZ * Math.sin(Math.toRadians(hits.getYRot())));
                double posY = hits.getY() + hitY;
                double posZ = hits.getZ() + (hitX * Math.sin(Math.toRadians(hits.getYRot())) + hitZ * Math.cos(Math.toRadians(hits.getYRot())));

                setPos(posX, posY, posZ);
                setRot(hits.getYRot() + hitYaw, hits.getXRot() + hitPitch, false);
            }
            case HIT_GROUND -> {
                // 命中地面后保持位置
            }
        }
    }

    public void onHitBlock(BlockHitResult blockHitResult) {
        inBlockState = level().getBlockState(blockHitResult.getBlockPos());
        setActionType(ActionType.HIT_GROUND);
        lerpMotion(0, 0, 0);
        Vec3 vec3d = blockHitResult.getLocation().subtract(getPos());
        Vec3 vec3d1 = getPos().subtract(vec3d.normalize().scale(0.05F));
        setPos(vec3d1.x, vec3d1.y, vec3d1.z);
        if (!isMute()) {
            playSound(hitGroundSound, 1.0F, 2.2F / (random.nextFloat() * 0.2F + 0.9F));
        }
        setMaxLifeTime(tickCount + getBreakDelay());
        setMaxPierce(0);
    }

    public void onHitEntity(Entity targetEntity, SummondAttackType summondAttackType) {
        if (pierce != null && pierce.contains(targetEntity.getId())) {
            return;
        }
        if (!level().isClientSide()) {
            doAttackEntity(targetEntity, summondAttackType);
        }
        if (!level().isClientSide() && summondAttackType != SummondAttackType.BROKEN) {
            if (pierce == null || getMaxPierce() == pierce.size()) {
                setHitEntity(targetEntity);
                setActionType(ActionType.HIT_ENTITY);
                setMaxLifeTime(tickCount + getBreakDelay());
            }
        }
        if (!isMute()) {
            playSound(hitEntitySound, 1.0F, 1.2F / (random.nextFloat() * 0.2F + 0.9F));
        }

    }

    public void doAttackEntity(Entity target, SummondAttackType summondAttackType) {
        // TODO: 需要实现 AttackHelper.doAttack
        // AttackHelper.doAttack(getShooter(), target, getDamage(), List.of(...));
        target.setDeltaMovement(0, 0.1, 0);

        if (target instanceof LivingEntity targetLivingEntity) {
            targetLivingEntity.hurtTime = 0;

            if (pierce != null && pierce.size() < getMaxPierce()) {
                pierce.add(target.getId());
            }
        }
    }

    @Nullable
    protected EntityHitResult getRayTrace(Vec3 startVec, Vec3 endVec) {
        return ProjectileUtil.getEntityHitResult(
                level(),
                this,
                startVec,
                endVec,
                getBoundingBox().expandTowards(getDeltaMovement()).inflate(1.0D),
                entity -> {
                    // TODO: 需要实现 EntityPredicateHelper.canTarget
                    return entity != getShooter() && entity instanceof LivingEntity;
                }
        );
    }

    @Override
    public void remove(@NotNull RemovalReason reason) {
        super.remove(reason);

        if (!isMute()) {
            playSound(breakSound, 1.0F, 1.2F / (random.nextFloat() * 0.2F + 0.9F));
        }

        if (!level().isClientSide()) {
            if (level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.CRIT, getX(), getY(), getZ(), 16, 0.5, 0.5, 0.5, 0.25f);
            }
        }

        if (getHitEntity() != null) {
            onHitEntity(getHitEntity(), SummondAttackType.BROKEN);
        }
    }

    @Nullable
    protected Entity hitEntity;

    @Nullable
    public Entity getHitEntity() {
        int id = entityData.get(HIT_ENTITY_ID);

        if (hitEntity != null && hitEntity.getId() != id) {
            hitEntity = null;
        }

        if (hitEntity == null) {
            if (id > 0) {
                Entity entity = level().getEntity(id);
                if (entity != null) {
                    hitEntity = entity;
                }
            }
        }

        return hitEntity;
    }

    public void setHitEntity(@Nullable Entity hitEntity) {
        entityData.set(
                HIT_ENTITY_ID,
                hitEntity != null
                        ? hitEntity.getId()
                        : -1
        );
        this.hitEntity = hitEntity;
    }

    public int getMaxPierce() {
        return entityData.get(MAX_PIERCE);
    }

    public void setMaxPierce(int maxPierce) {
        entityData.set(MAX_PIERCE, maxPierce);
        if (maxPierce > 0 && pierce == null) {
            pierce = new IntOpenHashSet(maxPierce);
        }
    }

    public int getBreakDelay() {
        return entityData.get(BREAK_DELAY);
    }

    public void setBreakDelay(int breakDelay) {
        entityData.set(BREAK_DELAY, breakDelay);
    }

    public int getStartDelay() {
        return entityData.get(START_DELAY);
    }

    public void setStartDelay(int startDelay) {
        entityData.set(START_DELAY, startDelay);
    }

    public float getSeep() {
        return entityData.get(SEEP);
    }

    public void setSeep(float seep) {
        entityData.set(SEEP, seep);
        if (!level().isClientSide) {
            updateMotion(seep);
        }
    }

    public boolean isIgnoringBlock() {
        return entityData.get(IGNORING_BLOCK);
    }

    public void setIgnoringBlock(boolean ignoringBlock) {
        entityData.set(IGNORING_BLOCK, ignoringBlock);
    }

    public ActionType getActionType() {
        return ActionType.values()[entityData.get(ACTION_TYPE)];
    }

    public void setActionType(ActionType actionType) {
        entityData.set(ACTION_TYPE, actionType.ordinal());
    }

    @Override
    public void lookAt(Vec3 target, boolean isDistance, boolean prevSynchronous) {
        super.lookAt(target, isDistance, prevSynchronous);
        updateMotion(getSeep());
    }

    /**
     * 召唤剑实体的动作类型枚举
     */
    public enum ActionType {
        /**
         * 准备状态
         */
        PREPARE,

        /**
         * 飞行状态
         */
        FLYING,

        /**
         * 命中实体状态
         */
        HIT_ENTITY,

        /**
         * 命中地面状态
         */
        HIT_GROUND
    }

    public enum SummondAttackType {
        HIT,
        BROKEN
    }

}

