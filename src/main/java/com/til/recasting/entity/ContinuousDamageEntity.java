package com.til.recasting.entity;

import com.til.recasting.registry.instance.AttackType;
import com.til.recasting.handler.AttackHelper;
import com.til.recasting.util.CallbackPoint;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class ContinuousDamageEntity extends StandardizationAttackEntity {

    /***
     * 攻击间隔
     */
    protected static final EntityDataAccessor<Integer> ATTACK_INTERVAL = SynchedEntityData.defineId(ContinuousDamageEntity.class, EntityDataSerializers.INT);

    /***
     * 是否重复攻击
     */
    protected static final EntityDataAccessor<Boolean> REPEATED_ATTACK = SynchedEntityData.defineId(ContinuousDamageEntity.class, EntityDataSerializers.BOOLEAN);

    /***
     * 参数单位范围，索敌范围为 SIZE * PARAMETER_RANGE
     */
    protected static final EntityDataAccessor<Float> PARAMETER_RANGE = SynchedEntityData.defineId(ContinuousDamageEntity.class, EntityDataSerializers.FLOAT);

    /***
     * 所有以攻击的实体id
     */
    @Nullable
    protected List<Entity> alreadyHits;

    public final CallbackPoint<IAttackAction> attackActionCallbackPoint = new CallbackPoint<>();
    public final CallbackPoint<IAttackEnd> attackEndCallbackPoint = new CallbackPoint<>();

    public SoundEvent hitEntitySound = SoundEvents.PLAYER_ATTACK_WEAK;

    public ContinuousDamageEntity(EntityType<?> entityTypeIn, Level worldIn, LivingEntity shooting) {
        super(entityTypeIn, worldIn, shooting);
        this.setNoGravity(true);
    }


    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        getEntityData().define(ATTACK_INTERVAL, 2);
        getEntityData().define(REPEATED_ATTACK, false);
        getEntityData().define(PARAMETER_RANGE, 1.0f);
    }


    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide()) {
            if (tickCount % getAttackInterval() == 0) {
                onAttackTime();
            }
        }
    }

    public void onAttackTime() {

        LivingEntity shooter = getShooter();
        if (shooter == null) {
            return;
        }

        if (alreadyHits == null) {
            alreadyHits = new ArrayList<>();
            alreadyHits.add(this);
            alreadyHits.add(shooter);
        }

        List<LivingEntity> list = AttackHelper.areaAttack(
                        shooter,
                        getPos(),
                        getDamageStructure(),
                        getSize() * getParameterRange(),
                        new ArrayList<>(attackTypeModelList),
                        alreadyHits,
                        null
                )
                .stream()
                .peek(
                        e -> {
                            if (!isRepeatedAttack()) {
                                alreadyHits.add(e);
                            }
                            attackActionCallbackPoint.call(r -> r.attack(e));
                        }
                )

                .toList();

        if (!list.isEmpty()) {
            if (!isMute()) {
                playAttackSound();
            }
            attackEndCallbackPoint.call(r -> r.attacked(list));
        }

    }

    public void playAttackSound() {
        playSound(hitEntitySound, 1.0F, 1.2F / (random.nextFloat() * 0.2F + 0.9F));
    }

    public int getAttackInterval() {
        return this.entityData.get(ATTACK_INTERVAL);
    }

    public void setAttackInterval(int interval) {
        this.entityData.set(ATTACK_INTERVAL, interval);
    }

    public boolean isRepeatedAttack() {
        return this.entityData.get(REPEATED_ATTACK);
    }

    public void setRepeatedAttack(boolean repeatedAttack) {
        this.entityData.set(REPEATED_ATTACK, repeatedAttack);
    }

    public float getParameterRange() {
        return this.entityData.get(PARAMETER_RANGE);
    }

    public void setParameterRange(float parameterRange) {
        this.entityData.set(PARAMETER_RANGE, parameterRange);
    }

    /***
     * 攻击到实体时
     */
    public interface IAttackAction {
        void attack(LivingEntity hitEntity);
    }

    /***
     * 攻击判定结束并且打到目标
     */
    public interface IAttackEnd {
        void attacked(List<LivingEntity> entities);
    }
}
