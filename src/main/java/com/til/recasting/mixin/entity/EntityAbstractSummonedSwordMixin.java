package com.til.recasting.mixin.entity;

import com.til.recasting.Config;
import com.til.recasting.mixin.EntityAccessor;
import com.til.recasting.mixin_api.IEntityModifiedRatio;
import com.til.recasting.mixin_api.IEntitySize;
import net.minecraft.world.entity.Entity;
import com.til.recasting.registry.RecastingAttackTypes;
import lombok.Getter;
import lombok.Setter;
import mods.flammpfeil.slashblade.entity.EntityAbstractSummonedSword;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Mixin 用于修改 EntityAbstractSummonedSword 的持久化行为和伤害计算
 * 防止召唤剑实体被保存到世界文件，并使用自定义攻击系统
 */
@Mixin(value = EntityAbstractSummonedSword.class, remap = false)
public abstract class EntityAbstractSummonedSwordMixin implements EntityAccess, IEntityModifiedRatio, IEntitySize {

    @Final
    @Shadow
    private static EntityDataAccessor<Integer> HIT_ENTITY_ID;

    @Unique
    private static final EntityDataAccessor<Float> RECASTING$SIZE = SynchedEntityData.defineId(EntityAbstractSummonedSword.class, EntityDataSerializers.FLOAT);

    @Unique
    @Getter
    @Setter
    private float recasting$modifiedRatio = 0;

    @Unique
    @OnlyIn(Dist.CLIENT)
    protected boolean recasting$recordAttackPos;
    @Unique
    @OnlyIn(Dist.CLIENT)
    protected double recasting$hitX;
    @Unique
    @OnlyIn(Dist.CLIENT)
    protected double recasting$hitY;
    @Unique
    @OnlyIn(Dist.CLIENT)
    protected double recasting$hitZ;
    @Unique
    @OnlyIn(Dist.CLIENT)
    protected float recasting$hitYaw;
    @Unique
    @OnlyIn(Dist.CLIENT)
    protected float recasting$hitPitch;

    @Shadow
    @Nullable
    public abstract Entity getShooter();

    @Shadow
    public abstract double getDamage();

    @Shadow
    private double damage;

    @Shadow
    @Nullable
    public abstract Entity getHitEntity();

    @Shadow
    public abstract int getDelay();

    @Shadow
    public abstract void setDelay(int delay);

    /**
     * 获取 EntityData，使用 EntityAccessor
     */
    @Unique
    private SynchedEntityData recasting2$getEntityData() {
        Entity self = (Entity) (Object) this;
        return ((EntityAccessor) self).getEntityData();
    }

    /**
     * 在 defineSynchedData 中添加 size 数据同步
     */
    @Inject(method = "defineSynchedData", at = @At("RETURN"), remap = false)
    private void defineSizeData(CallbackInfo ci) {
        this.recasting2$getEntityData().define(RECASTING$SIZE, 1.0f);
    }

    /**
     * 构造注入：初始化 damage 为 0
     */
    @Inject(method = "<init>", at = @At("RETURN"), remap = false)
    private void initDamage(CallbackInfo ci) {
        this.damage = 0;
        this.recasting$modifiedRatio = Config.SUMMONED_SWORD_BASE_DAMAGE.get().floatValue();
    }

    /**
     * 实现 IEntitySize 接口的 getRecasting$size 方法
     */
    @Override
    public float getRecasting$size() {
        return this.recasting2$getEntityData().get(RECASTING$SIZE);
    }

    /**
     * 实现 IEntitySize 接口的 setRecasting$size 方法
     */
    @Override
    public void setRecasting$size(float size) {
        this.recasting2$getEntityData().set(RECASTING$SIZE, size);
    }

    /**
     * 覆盖父类的 shouldBeSaved 方法
     * Mixin 会自动将这个方法添加到目标类中，覆盖 Entity 的实现
     */
    public boolean shouldBeSaved() {
        return false;
    }

    @Shadow
    public abstract void burst();

    /**
     * 在 tick 方法开始时检查是否有 shooter
     * 如果没有 shooter，直接删除实体（类似 StandardizationAttackEntity 的检查）
     */
    @Inject(
            method = "tick",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private void checkShooter(CallbackInfo ci) {
        EntityAbstractSummonedSword self = (EntityAbstractSummonedSword) (Object) this;
        // 只在服务端检查
        if (this.getShooter() == null) {
            // 必须有 shooter，否则删除实体
            self.discard();
            ci.cancel();
        }
    }

    /**
     * 在 tick 方法开始时检查是否有 shooter
     * 如果没有 shooter，直接删除实体（类似 StandardizationAttackEntity 的检查）
     */
    @Inject(
            method = "tick",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private void hitTick(CallbackInfo ci) {
        EntityAbstractSummonedSword self = (EntityAbstractSummonedSword) (Object) this;

        Entity hits = getHitEntity();

        if (hits == null) {
            return;
        }

        if (!recasting$recordAttackPos) {
            recasting$recordAttackPos = true;
            recasting$hitYaw = self.getYRot() - hits.getYRot();
            recasting$hitPitch = self.getXRot() - hits.getXRot();
            recasting$hitX = self.getX() - hits.getX();
            recasting$hitY = self.getY() - hits.getY();
            recasting$hitZ = self.getZ() - hits.getZ();
        }

        double radians = Math.toRadians(hits.getYRot());
        double posX = hits.getX() + (recasting$hitX * Math.cos(radians) - recasting$hitZ * Math.sin(radians));
        double posY = hits.getY() + recasting$hitY;
        double posZ = hits.getZ() + (recasting$hitX * Math.sin(radians) + recasting$hitZ * Math.cos(radians));

        self.setPos(posX, posY, posZ);
        self.setYRot(hits.getYRot() + recasting$hitYaw);
        self.setXRot(hits.getXRot() + recasting$hitPitch);

        int delay = this.getDelay();
        --delay;
        this.setDelay(delay);

        if (!self.level().isClientSide() && delay < 0) {
            if (getShooter() instanceof LivingEntity attacker) {
                com.til.recasting.util.AttackHelper.attack(
                        attacker,
                        hits,
                        recasting$modifiedRatio,
                        (float) getDamage(),
                        List.of(RecastingAttackTypes.SUMMOND_SWORD_ATTACK.get())
                );
            }

            this.burst();
        }

        if (!hits.isAlive()) {
            this.burst();
        }

        ci.cancel();
    }

    /**
     * 重定向 onHitEntity 中的伤害计算
     * 使用自定义的攻击系统，并保留命中后的跟随逻辑
     */
    @Inject(
            method = "onHitEntity",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z",
                    remap = true
            ),
            cancellable = true,
            remap = false
    )
    private void redirectDamageCalculation(EntityHitResult entityHitResult, CallbackInfo ci) {
        Entity shooter = this.getShooter();
        if (shooter instanceof LivingEntity attacker) {
            Entity target = entityHitResult.getEntity();
            // 调用自定义的攻击系统
            com.til.recasting.util.AttackHelper.attack(
                    attacker,
                    target,
                    recasting$modifiedRatio * 0.5f, // TODO 写入配置
                    (float) getDamage() * 0.5f, // TODO 写入配置
                    List.of(RecastingAttackTypes.SUMMOND_SWORD_ATTACK.get())
            );

            this.setHitEntity(target);

            // 取消原版的伤害逻辑
            ci.cancel();
        }
    }

    /**
     * @author til
     * @reason 不乱转
     */
    @Overwrite
    public void setHitEntity(Entity hitEntity) {
        EntityAccessor entityAccessor = (EntityAccessor) this;
        entityAccessor.getEntityData().set(HIT_ENTITY_ID, hitEntity.getId());
        this.setDelay(100);
    }

    /**
     * @author til
     * @reason 减少性能消耗
     */
    @Overwrite
    public void burst(List<MobEffectInstance> effects, @Nullable Entity focusEntity) {
    }


}

