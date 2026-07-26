package com.til.recasting.entity;


import com.til.recasting.handler.EntityHelper;
import com.til.recasting.handler.ParticleHelper;
import com.til.recasting.registry.RecastingAttackTypes;
import mods.flammpfeil.slashblade.SlashBlade;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
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

    private static final ResourceLocation modelLocation = ResourceLocation.fromNamespaceAndPath(SlashBlade.MODID, "model/util/slash.obj");
    private static final ResourceLocation textureLocation = ResourceLocation.fromNamespaceAndPath(SlashBlade.MODID, "model/util/slash.png");

    public SlashEffectEntity(EntityType<? extends SlashEffectEntity> entityTypeIn, Level worldIn, LivingEntity shooting) {
        super(entityTypeIn, worldIn, shooting);
        setMaxLifeTime(10);
        setModel(modelLocation);
        setTexture(textureLocation);

        if (shooting != null) {
            setShooter(shooting);
            Vec3 pos = EntityHelper.getEntityPosition(shooting);
            setPos(pos);
            setRot(shooting.getYRot(), 0, true);
        }
        registerAttackTypes();
        setRepeatedAttack(false);
        setParameterRange(4);
    }

    protected void registerAttackTypes() {
        addAttackType(RecastingAttackTypes.SLASH_EFFECT_ATTACK.get());
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        getEntityData().define(THUMP, false);
        getEntityData().define(ROTATION_OFFSET, 0.0f);
        getEntityData().define(USE_BLOCK_PARTICLE, true);
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

        if (isUseBlockParticle() && level().isClientSide() && tickCount < (getMaxLifeTime() * 0.75)) {

            Vec3 start = this.position();
            Vector4f normal = new Vector4f(1, 0, 0, 1);

            float progress = this.tickCount / (float) getMaxLifeTime();

            normal.rotate(new Quaternionf().rotateY((float) Math.toRadians(-this.getYRot() - 90)));
            normal.rotate(new Quaternionf().rotateZ((float) Math.toRadians(this.getXRot())));
            normal.rotate(new Quaternionf().rotateX((float) Math.toRadians(this.getRoll())));
            normal.rotate(new Quaternionf().rotateY((float) Math.toRadians(140 + this.getRotationOffset() - 200.0F * progress)));

            Vec3 normal3d = new Vec3(normal.x(), normal.y(), normal.z());

            BlockHitResult rayResult = this.level().clip(
                    new ClipContext(
                            start.add(normal3d.scale(1.5 * getSize())),
                            start.add(normal3d.scale(3 * getSize())),
                            ClipContext.Block.COLLIDER,
                            ClipContext.Fluid.NONE,
                            this
                    )
            );

            if (rayResult.getType() == HitResult.Type.BLOCK) {
                ParticleHelper.spawnBlockParticle(this, rayResult.getLocation(), normal3d, 3);
            }

        }

    }


    public boolean isThump() {
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

}
