package com.til.recasting.entity;

import com.til.recasting.Recasting;
import com.til.recasting.registry.RecastingAttackTypes;
import lombok.Getter;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @Author: til
 * @Description: 次次次次次元斩
 */
public class JudgementCutEntity extends ContinuousDamageEntity {

    /**
     * 是否暴击
     */
    protected static final EntityDataAccessor<Boolean> CRITICAL = SynchedEntityData.defineId(JudgementCutEntity.class, EntityDataSerializers.BOOLEAN);

    @Getter
    protected int seed;

    public JudgementCutEntity(EntityType<?> entityTypeIn, Level worldIn, LivingEntity shooting) {
        super(entityTypeIn, worldIn, shooting);

        this.setMaxLifeTime(10);
        this.seed = this.random.nextInt(360);

        // 设置次元斩的攻击类型
        attackTypeModelList.add(RecastingAttackTypes.JUDGEMENT_CUT_ATTACK.get());
        
        setRepeatedAttack(false);

        setModel(Recasting.prefix("default_judgement_cut_model"));
        setTexture(Recasting.prefix("default_judgement_cut_texture"));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(CRITICAL, false);
    }

    @Override
    public void tick() {
        super.tick();

        // 前8刻每2刻播放声音
        if (tickCount < 8 && tickCount % 2 == 0) {
            if (!isMute()) {
                this.playSound(SoundEvents.WITHER_HURT, 0.2F, 0.5F + 0.25f * this.random.nextFloat());
            }
        }

        if (getShooter() != null) {
            // 每2刻进行一次攻击判定
            if (tickCount % 2 == 0) {
                // TODO: 需要实现区域攻击
                // KnockBacks knockBackType = isCritical() ? KnockBacks.toss : KnockBacks.cancel;
                // AttackHelper.areaAttack(this, knockBackType.action, 4.0, true, false, 0.16f, null);
            }

            // 如果是暴击，在前3刻生成斩击特效
            final int count = 3;
            if (isCritical() && tickCount > 0 && tickCount <= count) {
                // TODO: 需要注册 SlashEffectEntity 的 EntityType 后才能创建
                // SlashEffectEntity slashEffect = new SlashEffectEntity(EntityTypes.SLASH_EFFECT, level(), (LivingEntity) getShooter());
                // slashEffect.absMoveTo(this.getX(), this.getY(), this.getZ(), (360.0f / count) * tickCount + this.seed, 0);
                // slashEffect.setRoll(30);
                // 
                // slashEffect.setShooter((LivingEntity) getShooter());
                // slashEffect.setMute(false);
                // slashEffect.setThump(true);
                // slashEffect.setDamage(0.1F);
                // slashEffect.setColor(getColor());
                // slashEffect.setSize(0.5f);
                // 
                // this.level().addFreshEntity(slashEffect);
            }
        }
    }

    @Override
    public void remove(@NotNull RemovalReason reason) {
        super.remove(reason);
        
        // 服务端生成暴击粒子
        if (!level().isClientSide()) {
            if (level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.CRIT, 
                        this.getX(), this.getY(), this.getZ(), 
                        16, 0.5, 0.5, 0.5, 0.25f);
            }
        }
    }

    public boolean isCritical() {
        return this.entityData.get(CRITICAL);
    }

    public void setCritical(boolean critical) {
        this.entityData.set(CRITICAL, critical);
    }

}

