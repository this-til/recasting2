package com.til.recasting.entity;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

/**
 * @Author: til
 * @Description: 螺旋剑实体 - 能够围绕目标旋转的幻影剑
 */
public class SummondSpiralSwordEntity extends SummondSwordEntity {

    /**
     * 旋转中心实体的ID
     */
    protected static final EntityDataAccessor<Integer> CENTER_ENTITY_ID = SynchedEntityData.defineId(SummondSpiralSwordEntity.class, EntityDataSerializers.INT);

    /**
     * 旋转速度（度/tick）
     */
    protected static final EntityDataAccessor<Float> ROTATION_SPEED = SynchedEntityData.defineId(SummondSpiralSwordEntity.class, EntityDataSerializers.FLOAT);

    /**
     * 旋转速度修饰
     * 每 tick: ROTATION_SPEED *= ROTATION_SPEED_MODIFIER
     * = 1.0: 速度不变
     * > 1.0: 速度越来越快（加速）
     * < 1.0: 速度越来越慢（减速）
     */
    protected static final EntityDataAccessor<Float> ROTATION_SPEED_MODIFIER = SynchedEntityData.defineId(SummondSpiralSwordEntity.class, EntityDataSerializers.FLOAT);

    /**
     * 当前旋转角度（度）
     */
    protected static final EntityDataAccessor<Float> ROTATION_ANGLE = SynchedEntityData.defineId(SummondSpiralSwordEntity.class, EntityDataSerializers.FLOAT);

    /**
     * 旋转半径
     */
    protected static final EntityDataAccessor<Float> ROTATION_RADIUS = SynchedEntityData.defineId(SummondSpiralSwordEntity.class, EntityDataSerializers.FLOAT);

    /**
     * 旋转半径修饰
     * 每 tick: ROTATION_RADIUS *= ROTATION_RADIUS_MODIFIER
     * = 1.0: 半径不变
     * > 1.0: 半径越来越大（向外扩散）
     * < 1.0: 半径越来越小（向内收缩）
     */
    protected static final EntityDataAccessor<Float> ROTATION_RADIUS_MODIFIER = SynchedEntityData.defineId(SummondSpiralSwordEntity.class, EntityDataSerializers.FLOAT);

    /**
     * 旋转轴 X 分量
     */
    protected static final EntityDataAccessor<Float> ROTATION_AXIS_X = SynchedEntityData.defineId(SummondSpiralSwordEntity.class, EntityDataSerializers.FLOAT);

    /**
     * 旋转轴 Y 分量
     */
    protected static final EntityDataAccessor<Float> ROTATION_AXIS_Y = SynchedEntityData.defineId(SummondSpiralSwordEntity.class, EntityDataSerializers.FLOAT);

    /**
     * 旋转轴 Z 分量
     */
    protected static final EntityDataAccessor<Float> ROTATION_AXIS_Z = SynchedEntityData.defineId(SummondSpiralSwordEntity.class, EntityDataSerializers.FLOAT);

    /**
     * 旋转朝向（true=朝外, false=朝内指向中心）
     */
    protected static final EntityDataAccessor<Boolean> ROTATION_DIRECTION_OUTWARD = SynchedEntityData.defineId(SummondSpiralSwordEntity.class, EntityDataSerializers.BOOLEAN);

    /**
     * 旋转时是否能够攻击目标
     * true = 旋转时可以攻击目标
     * false = 旋转时不攻击，只在发射后攻击（默认）
     */
    protected static final EntityDataAccessor<Boolean> CAN_ATTACK_DURING_ROTATION = SynchedEntityData.defineId(SummondSpiralSwordEntity.class, EntityDataSerializers.BOOLEAN);

    @Nullable
    protected Entity centerEntity;

    public SummondSpiralSwordEntity(EntityType<? extends SummondSpiralSwordEntity> entityTypeIn, Level worldIn, LivingEntity shooting) {
        super(entityTypeIn, worldIn, shooting);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        getEntityData().define(CENTER_ENTITY_ID, -1);
        getEntityData().define(ROTATION_SPEED, 6.0f); // 默认6度/tick
        getEntityData().define(ROTATION_SPEED_MODIFIER, 1.0f); // 默认1.0（速度不变）
        getEntityData().define(ROTATION_ANGLE, 0.0f);
        getEntityData().define(ROTATION_RADIUS, 2.0f); // 默认半径2格
        getEntityData().define(ROTATION_RADIUS_MODIFIER, 1.0f); // 默认1.0（半径不变）
        getEntityData().define(ROTATION_AXIS_X, 0.0f); // 默认Y轴旋转 (0, 1, 0)
        getEntityData().define(ROTATION_AXIS_Y, 1.0f);
        getEntityData().define(ROTATION_AXIS_Z, 0.0f);
        getEntityData().define(ROTATION_DIRECTION_OUTWARD, true); // 默认朝外
        getEntityData().define(CAN_ATTACK_DURING_ROTATION, false); // 默认旋转时不攻击
    }

    @Override
    public void tick() {
        // 在 PREPARE 阶段执行旋转逻辑
        if (getActionType() == ActionType.PREPARE) {
            setOldPosAndRot();
            
            // 执行旋转
            performRotation();
            
            // 检查是否需要切换到 FLYING 阶段
            if (tickCount > getStartDelay()) {
                updateMotion(getSeep());
                setActionType(ActionType.FLYING);
            }
            
            return;
        }
        
        // 其他阶段使用父类的逻辑
        super.tick();
    }

    /**
     * 执行旋转逻辑
     */
    protected void performRotation() {
        Entity center = getCenterEntity();

        // 如果没有中心实体，使用发射者作为中心
        if (center == null) {
            center = getShooter();
            if (center != null) {
                setCenterEntity(center);
            }
        }

        // 如果仍然没有中心点，则丢弃实体
        if (center == null) {
            discard();
            return;
        }

        // 更新旋转速度（应用速度修饰）
        float currentSpeed = getRotationSpeed();
        float modifier = getRotationSpeedModifier();
        float newSpeed = currentSpeed * modifier;
        setRotationSpeed(newSpeed);

        // 更新旋转角度
        float currentAngle = getRotationAngle();
        float newAngle = (currentAngle + newSpeed) % 360.0f;
        setRotationAngle(newAngle);

        // 更新旋转半径（应用半径修饰）
        float currentRadius = getRotationRadius();
        float radiusModifier = getRotationRadiusModifier();
        float newRadius = currentRadius * radiusModifier;
        setRotationRadius(newRadius);


        // 计算新位置 - 使用旋转轴进行旋转
        Vec3 centerPos = center.position().add(0, center.getEyeHeight() / 2.0, 0);
        double angleRad = Math.toRadians(newAngle);

        // 获取旋转轴并归一化
        Vec3 rotationAxis = getRotationAxis().normalize();

        // 计算垂直于旋转轴的初始半径向量
        Vec3 radiusVector = getPerpendicularVector(rotationAxis).normalize().scale(newRadius);

        // 使用罗德里格斯旋转公式绕旋转轴旋转
        Vec3 rotatedOffset = rotateAroundAxis(radiusVector, rotationAxis, angleRad);

        Vec3 newPos = centerPos.add(rotatedOffset);
        setPos(newPos.x, newPos.y, newPos.z);

        // 更新朝向
        if (isRotationDirectionOutward()) {
            // 朝外：朝向切线方向（速度方向，垂直于半径和旋转轴）
            Vec3 tangentDirection = rotationAxis.cross(rotatedOffset).normalize();
            lookAt(tangentDirection, true, false);
        } else {
            // 朝内：朝向中心
            Vec3 directionToCenter = centerPos.subtract(newPos).normalize();
            lookAt(directionToCenter, true, false);
        }

        // 如果启用了旋转时攻击，进行碰撞检测
        if (canAttackDuringRotation() && !level().isClientSide()) {
            checkEntityCollisionsDuringRotation(newPos);
        }
    }

    /**
     * 旋转时检查与实体的碰撞
     */
    protected void checkEntityCollisionsDuringRotation(Vec3 currentPos) {
        // 使用上一帧的位置和当前位置进行射线追踪
        Vec3 prevPos = new Vec3(xo, yo, zo);
        EntityHitResult entityHitResult = getRayTrace(prevPos, currentPos);

        if (entityHitResult != null && entityHitResult.getType() == Type.ENTITY) {
            Entity target = entityHitResult.getEntity();
            onHitEntity(target, SummondAttackType.HIT);
        }
    }

    /**
     * 使用罗德里格斯旋转公式绕任意轴旋转向量
     *
     * @param vector 要旋转的向量
     * @param axis   旋转轴（已归一化）
     * @param angle  旋转角度（弧度）
     * @return 旋转后的向量
     */
    protected Vec3 rotateAroundAxis(Vec3 vector, Vec3 axis, double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        double dot = vector.dot(axis);

        // v_rot = v*cos(θ) + (k×v)*sin(θ) + k*(k·v)*(1-cos(θ))
        Vec3 term1 = vector.scale(cos);
        Vec3 term2 = axis.cross(vector).scale(sin);
        Vec3 term3 = axis.scale(dot * (1 - cos));

        return term1.add(term2).add(term3);
    }

    /**
     * 获取垂直于给定向量的一个向量
     *
     * @param vector 输入向量
     * @return 垂直向量
     */
    protected Vec3 getPerpendicularVector(Vec3 vector) {
        // 选择一个不平行于输入向量的向量
        Vec3 arbitrary = Math.abs(vector.y) < 0.9
                ? new Vec3(0, 1, 0)
                : new Vec3(1, 0, 0);
        // 使用叉积获得垂直向量
        return vector.cross(arbitrary).normalize();
    }

    // Getter 和 Setter 方法

    @Nullable
    public Entity getCenterEntity() {
        int id = entityData.get(CENTER_ENTITY_ID);

        if (centerEntity != null && centerEntity.getId() != id) {
            centerEntity = null;
        }

        if (centerEntity == null && id > 0) {
            Entity entity = level().getEntity(id);
            if (entity != null) {
                centerEntity = entity;
            }
        }

        return centerEntity;
    }

    public void setCenterEntity(@Nullable Entity centerEntity) {
        entityData.set(
                CENTER_ENTITY_ID,
                centerEntity != null
                        ? centerEntity.getId()
                        : -1
        );
        this.centerEntity = centerEntity;

        // 初始化旋转半径（基于当前位置到中心的距离）
        if (centerEntity != null) {
            Vec3 centerPos = centerEntity.position().add(0, centerEntity.getEyeHeight() / 2.0, 0);
            Vec3 currentPos = position();
            Vec3 offset = currentPos.subtract(centerPos);

            // 初始化半径为到中心的距离
            double initialRadius = offset.length();
            if (initialRadius > 0.1) {
                setRotationRadius((float) initialRadius);
            }

            // 角度从 0 开始，或者保持当前设置的角度
        }
    }

    public float getRotationSpeed() {
        return entityData.get(ROTATION_SPEED);
    }

    public void setRotationSpeed(float speed) {
        entityData.set(ROTATION_SPEED, speed);
    }

    public float getRotationSpeedModifier() {
        return entityData.get(ROTATION_SPEED_MODIFIER);
    }

    public void setRotationSpeedModifier(float modifier) {
        entityData.set(ROTATION_SPEED_MODIFIER, modifier);
    }

    public float getRotationAngle() {
        return entityData.get(ROTATION_ANGLE);
    }

    public void setRotationAngle(float angle) {
        entityData.set(ROTATION_ANGLE, angle);
    }

    public float getRotationRadius() {
        return entityData.get(ROTATION_RADIUS);
    }

    public void setRotationRadius(float radius) {
        entityData.set(ROTATION_RADIUS, radius);
    }

    public float getRotationRadiusModifier() {
        return entityData.get(ROTATION_RADIUS_MODIFIER);
    }

    public void setRotationRadiusModifier(float modifier) {
        entityData.set(ROTATION_RADIUS_MODIFIER, modifier);
    }

    public Vec3 getRotationAxis() {
        return new Vec3(
                entityData.get(ROTATION_AXIS_X),
                entityData.get(ROTATION_AXIS_Y),
                entityData.get(ROTATION_AXIS_Z)
        );
    }

    public void setRotationAxis(Vec3 axis) {
        Vec3 normalized = axis.normalize();
        entityData.set(ROTATION_AXIS_X, (float) normalized.x);
        entityData.set(ROTATION_AXIS_Y, (float) normalized.y);
        entityData.set(ROTATION_AXIS_Z, (float) normalized.z);
    }

    public boolean isRotationDirectionOutward() {
        return entityData.get(ROTATION_DIRECTION_OUTWARD);
    }

    public void setRotationDirectionOutward(boolean outward) {
        entityData.set(ROTATION_DIRECTION_OUTWARD, outward);
    }

    public boolean canAttackDuringRotation() {
        return entityData.get(CAN_ATTACK_DURING_ROTATION);
    }

    public void setCanAttackDuringRotation(boolean canAttack) {
        entityData.set(CAN_ATTACK_DURING_ROTATION, canAttack);
    }

    // ==================== 辅助计算方法 ====================

    /**
     * 计算半径修饰参数
     * 根据目标时间、初始半径和目标半径计算修饰系数
     * 
     * @param initialRadius 初始半径
     * @param targetRadius 目标半径
     * @param ticks 时间（tick数）
     * @return 半径修饰参数（每 tick 半径 × modifier）
     * 
     * @example
     * // 计算 40 tick 后从 2.5 格扩展到 8 格的修饰参数
     * float modifier = calculateRadiusModifier(2.5f, 8.0f, 40);
     * // 结果：约 1.0295
     */
    public static float calculateRadiusModifier(float initialRadius, float targetRadius, int ticks) {
        if (ticks <= 0 || initialRadius <= 0) {
            return 1.0f;
        }
        if (targetRadius <= 0) {
            targetRadius = 0.01f; // 避免除零，使用很小的值
        }
        
        // modifier^ticks = targetRadius / initialRadius
        // modifier = (targetRadius / initialRadius)^(1/ticks)
        double ratio = targetRadius / initialRadius;
        return (float) Math.pow(ratio, 1.0 / ticks);
    }

    /**
     * 计算速度修饰参数
     * 根据目标时间、初始速度和目标速度计算修饰系数
     * 
     * @param initialSpeed 初始速度（度/tick）
     * @param targetSpeed 目标速度（度/tick），通常接近 0
     * @param ticks 时间（tick数）
     * @return 速度修饰参数（每 tick 速度 × modifier）
     * 
     * @example
     * // 计算 40 tick 后从 16 度/tick 衰减到接近 0 的修饰参数
     * float modifier = calculateSpeedModifier(16.0f, 0.01f, 40);
     * // 结果：约 0.8316
     */
    public static float calculateSpeedModifier(float initialSpeed, float targetSpeed, int ticks) {
        if (ticks <= 0 || initialSpeed <= 0) {
            return 1.0f;
        }
        if (targetSpeed <= 0) {
            targetSpeed = 0.01f; // 避免除零，使用很小的值
        }
        
        // modifier^ticks = targetSpeed / initialSpeed
        // modifier = (targetSpeed / initialSpeed)^(1/ticks)
        double ratio = targetSpeed / initialSpeed;
        return (float) Math.pow(ratio, 1.0 / ticks);
    }

    /**
     * 便捷方法：设置半径扩展参数
     * 
     * @param initialRadius 初始半径
     * @param targetRadius 目标半径
     * @param ticks 时间（tick数）
     */
    public void setRadiusExpansion(float initialRadius, float targetRadius, int ticks) {
        setRotationRadius(initialRadius);
        setRotationRadiusModifier(calculateRadiusModifier(initialRadius, targetRadius, ticks));
    }

    /**
     * 便捷方法：设置速度衰减参数
     * 
     * @param initialSpeed 初始速度（度/tick）
     * @param targetSpeed 目标速度（度/tick），通常接近 0
     * @param ticks 时间（tick数）
     */
    public void setSpeedDecay(float initialSpeed, float targetSpeed, int ticks) {
        setRotationSpeed(initialSpeed);
        setRotationSpeedModifier(calculateSpeedModifier(initialSpeed, targetSpeed, ticks));
    }
}

