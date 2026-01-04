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
public class LightningEntity extends Entity {

    // 数据同步
    private static final EntityDataAccessor<Long> BOLT_VERTEX = SynchedEntityData.defineId(LightningEntity.class, EntityDataSerializers.LONG);
    private static final EntityDataAccessor<Float> SIZE = SynchedEntityData.defineId(LightningEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> MUTE = SynchedEntityData.defineId(LightningEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> COLOR = SynchedEntityData.defineId(LightningEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> OWNER_ID = SynchedEntityData.defineId(LightningEntity.class, EntityDataSerializers.INT);

    // 实体状态
    private boolean isInitialized = false;
    @Getter
    @Setter
    private int maxLifeTime = 20; // 默认 1 秒
    @Getter
    @Setter
    private float modifiedRatio = 1.0f; // 伤害倍率（areaAttack 需要）
    @Getter
    @Setter
    private float extraDamage = 0.0f; // 额外伤害（areaAttack 需要）
    @Getter
    @Setter
    private int attackInterval = 5; // 攻击间隔（tick），默认 5

    // Owner 缓存
    private Entity cachedOwner;

    public LightningEntity(EntityType<? extends LightningEntity> entityType, Level level) {
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
        this.entityData.define(OWNER_ID, -1);
    }

    @Override
    public void tick() {
        super.tick();

        if (getOwner() == null) {
            remove(RemovalReason.DISCARDED);
            return;
        }

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
        float attackRange = 1.5f * this.getSize();

        // 创建攻击类型列表
        List<AttackType> attackTypes = List.of(RecastingAttackTypes.LIGHTNING_ATTACK.get());

        // 排除列表（排除幻影剑实体）
        List<Entity> exclude = List.of();

        // 使用 areaAttack 进行范围攻击
        AttackManager.areaAttack(
                attacker,
                living -> {},  // beforeHit - 攻击前回调
                modifiedRatio,  // 伤害倍率
                extraDamage,    // 额外伤害
                true,           // forceHit - 强制命中
                true,           // resetHit - 重置无敌时间
                this.isMute(),  // mute - 静音
                attackRange,    // 攻击范围
                exclude,        // 排除列表
                attackTypes     // 攻击类型列表
        );
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

    /**
     * 获取所有者实体
     */
    public Entity getOwner() {
        if (this.cachedOwner != null && !this.cachedOwner.isRemoved()) {
            return this.cachedOwner;
        }

        int ownerId = this.entityData.get(OWNER_ID);
        if (ownerId == -1) {
            return null;
        }

        if (this.level() != null && !this.level().isClientSide()) {
            this.cachedOwner = this.level().getEntity(ownerId);
            return this.cachedOwner;
        }

        return null;
    }

    /**
     * 设置所有者实体
     */
    public void setOwner(Entity owner) {
        if (owner != null) {
            this.entityData.set(OWNER_ID, owner.getId());
            this.cachedOwner = owner;
        } else {
            this.entityData.set(OWNER_ID, -1);
            this.cachedOwner = null;
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        double renderDistance = 256.0D * getViewScale();
        return distance < renderDistance * renderDistance;
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag tag) {
        if (tag.contains("OwnerUUID")) {
            // 从 UUID 加载 owner（如果需要持久化）
            // 注意：由于 shouldBeSaved 返回 false，这个方法通常不会被调用
        }
        if (tag.contains("ModifiedRatio")) {
            this.modifiedRatio = tag.getFloat("ModifiedRatio");
        }
        if (tag.contains("ExtraDamage")) {
            this.extraDamage = tag.getFloat("ExtraDamage");
        }
        if (tag.contains("MaxLifeTime")) {
            this.maxLifeTime = tag.getInt("MaxLifeTime");
        }
        if (tag.contains("AttackInterval")) {
            this.attackInterval = tag.getInt("AttackInterval");
        }
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag tag) {
        tag.putFloat("ModifiedRatio", this.modifiedRatio);
        tag.putFloat("ExtraDamage", this.extraDamage);
        tag.putInt("MaxLifeTime", this.maxLifeTime);
        tag.putInt("AttackInterval", this.attackInterval);
    }

    @Override
    public boolean shouldBeSaved() {
        return false;
    }

}

