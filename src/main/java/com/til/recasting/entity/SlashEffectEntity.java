package com.til.recasting.entity;

import com.til.recasting.Recasting;
import com.til.recasting.registry.RecastingAttackTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector4f;

import java.util.List;

/**
 * @Author: til
 * @Description: 剑气
 */
public class SlashEffectEntity extends ContinuousDamageEntity {

    /***
     * 是否重击
     */
    protected static final EntityDataAccessor<Boolean> THUMP = SynchedEntityData.defineId(SlashEffectEntity.class, EntityDataSerializers.BOOLEAN);

    /***
     * 旋转偏移
     */
    protected static final EntityDataAccessor<Float> ROTATION_OFFSET = SynchedEntityData.defineId(SlashEffectEntity.class, EntityDataSerializers.FLOAT);

    /***
     * 是否生成碰撞方块粒子
     */
    protected static final EntityDataAccessor<Boolean> USE_BLOCK_PARTICLE = SynchedEntityData.defineId(SlashEffectEntity.class, EntityDataSerializers.BOOLEAN);

    public SoundEvent tapLightly = SoundEvents.TRIDENT_THROW;
    public SoundEvent heavyStrike = SoundEvents.PLAYER_ATTACK_SWEEP;

    public SlashEffectEntity(EntityType<?> entityTypeIn, Level worldIn, LivingEntity shooting) {
        super(entityTypeIn, worldIn, shooting);
        setMaxLifeTime(10);
        setModel(Recasting.prefix("default_slash_model"));
        setTexture(Recasting.prefix("default_slash_texture"));
        if (shooting != null) {
            setShooter(shooting);
            Vec3 pos = shooting.position();
            setPos(pos);
            setRot(shooting.getYRot(), 0, true);
        }
        
        // 设置斩击特效的攻击类型
        attackTypeModelList.add(RecastingAttackTypes.SLASH_EFFECT_ATTACK.get());
        
        setRepeatedAttack(false);
        setParameterRange(4);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(THUMP, false);
        this.entityData.define(ROTATION_OFFSET, 0.0f);
        this.entityData.define(USE_BLOCK_PARTICLE, true);
    }

    @Override
    public void tick() {
        super.tick();

        if (tickCount == 2) {

            if (!isMute()) {
                if (isThump()) {
                    this.playSound(heavyStrike, 0.5F, 0.4F / (this.random.nextFloat() * 0.4F + 0.8F));
                } else {
                    this.playSound(tapLightly, 0.80F, 0.625F + 0.1f * this.random.nextFloat());
                }
            }

        }

        if (getShooter() == null) {
            return;
        }

        if (tickCount % 2 == 0) {
            Vec3 start = this.position();
            Vector4f normal = new Vector4f(1, 0, 0, 1);
            Vector4f dir = new Vector4f(0, 0, 1, 1);

            float progress = this.tickCount / (float) getMaxLifeTime();

            // 计算法线方向
            normal.rotate(new Quaternionf().rotateY((float) Math.toRadians(-this.getYRot() - 90)));
            normal.rotate(new Quaternionf().rotateZ((float) Math.toRadians(this.getXRot())));
            normal.rotate(new Quaternionf().rotateX((float) Math.toRadians(this.getRoll())));
            normal.rotate(new Quaternionf().rotateY((float) Math.toRadians(140 + this.getRotationOffset() - 200.0F * progress)));

            // 计算方向向量
            dir.rotate(new Quaternionf().rotateY((float) Math.toRadians(-this.getYRot() - 90)));
            dir.rotate(new Quaternionf().rotateZ((float) Math.toRadians(this.getXRot())));
            dir.rotate(new Quaternionf().rotateX((float) Math.toRadians(this.getRoll())));
            dir.rotate(new Quaternionf().rotateY((float) Math.toRadians(140 + this.getRotationOffset() - 200.0F * progress)));

            Vec3 normal3d = new Vec3(normal.x(), normal.y(), normal.z());

            // 方块粒子效果（需要射手存在且不在水中或雨中）
            if (isUseBlockParticle() && tickCount < (getMaxLifeTime() * 0.75)) {
                BlockHitResult rayResult = this.level().clip(
                        new ClipContext(
                                start.add(normal3d.scale(1.5 * getSize())),
                                start.add(normal3d.scale(3 * getSize())),
                                ClipContext.Block.COLLIDER,
                                ClipContext.Fluid.NONE,
                                this
                        )
                );

                if (getShooter() != null 
                        && !getShooter().isInWaterOrRain() 
                        && rayResult.getType() == HitResult.Type.BLOCK) {
                    spawnBlockParticle(rayResult.getLocation(), normal3d, 3);
                }
            }

            // 暴击粒子效果（客户端，只在重击时显示）
            if (level().isClientSide() && isThump()) {
                // 生成两个暴击粒子
                Vec3 vec3 = start.add(normal3d.scale(getSize() * 2.5));
                this.level().addParticle(ParticleTypes.CRIT, vec3.x(), vec3.y(), vec3.z(), 
                        dir.x() + normal.x(), dir.y() + normal.y(), dir.z() + normal.z());
                
                float randScale = random.nextFloat() + 0.5f;
                vec3 = vec3.add(dir.x() * randScale, dir.y() * randScale, dir.z() * randScale);
                this.level().addParticle(ParticleTypes.CRIT, vec3.x(), vec3.y(), vec3.z(), 
                        dir.x() + normal.x(), dir.y() + normal.y(), dir.z() + normal.z());
            }
        }


    }


    public boolean isThump() {
        // TODO > S 时暴击
        return this.entityData.get(THUMP);
    }

    public void setThump(boolean thump) {
        this.entityData.set(THUMP, thump);
    }

    public float getRotationOffset() {
        return this.entityData.get(ROTATION_OFFSET);
    }

    public void setRotationOffset(float rotationOffset) {
        this.entityData.set(ROTATION_OFFSET, rotationOffset);
    }

    public boolean isUseBlockParticle() {
        return this.entityData.get(USE_BLOCK_PARTICLE);
    }

    public void setUseBlockParticle(boolean useBlockParticle) {
        this.entityData.set(USE_BLOCK_PARTICLE, useBlockParticle);
    }

    /**
     * 生成方块粒子效果
     * @param targetPos 目标位置
     * @param normal 法线方向
     * @param fallFactor 粒子强度因子
     */
    protected void spawnBlockParticle(Vec3 targetPos, Vec3 normal, float fallFactor) {
        if (!level().isClientSide()) {
            Vec3 blockPos = targetPos.add(normal.normalize().scale(0.5f));

            int x = Mth.floor(blockPos.x());
            int y = Mth.floor(blockPos.y());
            int z = Mth.floor(blockPos.z());
            BlockPos pos = new BlockPos(x, y, z);
            BlockState state = level().getBlockState(pos);

            float f = (float) Mth.ceil(fallFactor);
            if (!state.isAir()) {
                double d0 = Math.min(0.2F + f / 15.0F, 2.5D);
                int particleCount = (int) (150.0D * d0);
                ((ServerLevel) level()).sendParticles(
                        new BlockParticleOption(ParticleTypes.BLOCK, state),
                        targetPos.x(), targetPos.y(), targetPos.z(),
                        particleCount,
                        0.0D, 0.0D, 0.0D,
                        0.15F
                );
            }
        }
    }

}

