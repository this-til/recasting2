package com.til.recasting.entity;

import com.til.recasting.registry.RecastingAttackTypes;
import com.til.recasting.registry.instance.AttackType;
import com.til.recasting.util.AttackManager;
import lombok.Getter;
import lombok.Setter;
import mods.flammpfeil.slashblade.entity.EntityAbstractSummonedSword;
import mods.flammpfeil.slashblade.util.TargetSelector;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 闪电特效实体
 * 用于创建闪电攻击特效，造成魔法伤害并带有视觉效果
 */
public class LightningEntity extends Projectile {

    // 数据同步
    private static final EntityDataAccessor<Long> BOLT_VERTEX = SynchedEntityData.defineId(LightningEntity.class, EntityDataSerializers.LONG);
    private static final EntityDataAccessor<Float> SIZE = SynchedEntityData.defineId(LightningEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> MUTE = SynchedEntityData.defineId(LightningEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> COLOR = SynchedEntityData.defineId(LightningEntity.class, EntityDataSerializers.INT);

    // 实体状态
    private boolean isInitialized = false;
    @Getter
    @Setter
    private int maxLifeTime = 20; // 默认 1 秒
    @Setter
    @Getter
    private float damage = 10.0f;
    @Getter
    @Setter
    private float modifiedRatio = 1.0f;
    @Getter
    @Setter
    private float extraDamage = 0.0f;
    @Getter
    @Setter
    private int attackInterval = 5; // 攻击间隔（tick），默认 2

    public LightningEntity(EntityType<? extends Projectile> entityType, Level level) {
        super(entityType, level);
        this.setNoGravity(true);
        this.setMaxLifeTime(this.random.nextInt(15) + 5);
        this.setBoltVertex(this.random.nextLong());
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(BOLT_VERTEX, 0L);
        this.entityData.define(SIZE, 1.0f);
        this.entityData.define(MUTE, false);
        this.entityData.define(COLOR, 0x3399FF); // 默认蓝色
    }

    @Override
    public void tick() {
        super.tick();

        // 服务器端：初始化和攻击逻辑
        if (!isInitialized) {
            isInitialized = true;
            // 初次生成时立即播放音效
            if (!this.isMute()) {
                this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.WEATHER, 10000.0F, 0.8F + this.random.nextFloat() * 0.2F);
                this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.LIGHTNING_BOLT_IMPACT, SoundSource.WEATHER, 2.0F, 0.5F + this.random.nextFloat() * 0.2F);
            }
        }

        if (this.tickCount % attackInterval == 0) {
            this.setBoltVertex(this.random.nextLong());

            if (!level().isClientSide()) {
                performLightningAttack();
            }
        }

        // 客户端：更新天空闪光效果
        if (this.level().isClientSide()) {
            this.level().setSkyFlashTime(2);
            return;
        }


        // 检查生命周期
        if (this.tickCount >= maxLifeTime) {
            this.discard();
        }
    }

    /**
     * 执行闪电攻击
     */
    private void performLightningAttack() {
        // 获取攻击者
        Entity owner = this.getOwner();
        if (!(owner instanceof LivingEntity attacker)) {
            return;
        }

        // 计算攻击范围
        float range = 3.0f * this.getSize();
        AABB attackBox = this.getBoundingBox().inflate(range);

        // 获取范围内的目标实体
        List<Entity> targets = TargetSelector.getTargettableEntitiesWithinAABB(
                this.level(),
                attacker,
                attackBox
        );

        // 排除幻影剑实体
        targets.removeIf(entity -> entity instanceof EntityAbstractSummonedSword);

        // 创建攻击类型列表
        List<AttackType> attackTypes = List.of(RecastingAttackTypes.LIGHTNING_ATTACK.get());

        // 对每个目标造成伤害
        for(Entity target : targets) {
            AttackManager.doMeleeAttack(
                    attacker,
                    target,
                    true,  // forceHit - 强制命中
                    true,  // resetHit - 重置无敌时间
                    this.modifiedRatio,
                    this.extraDamage,
                    attackTypes
            );
        }
    }

    public long getBoltVertex() {
        return this.entityData.get(BOLT_VERTEX);
    }

    public void setBoltVertex(long vertex) {
        this.entityData.set(BOLT_VERTEX, vertex);
    }

    public float getSize() {
        return this.entityData.get(SIZE);
    }

    public void setSize(float size) {
        this.entityData.set(SIZE, size);
    }

    public boolean isMute() {
        return this.entityData.get(MUTE);
    }

    public void setMute(boolean mute) {
        this.entityData.set(MUTE, mute);
    }

    public int getColor() {
        return this.entityData.get(COLOR);
    }

    public void setColor(int color) {
        this.entityData.set(COLOR, color);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        double renderDistance = 256.0D * getViewScale();
        return distance < renderDistance * renderDistance;
    }

    @Override
    public boolean shouldBeSaved() {
        return false;
    }

}

