package com.til.recasting.mixin.entity;

import com.til.recasting.mixin.EntityAccessor;
import com.til.recasting.mixin_api.IEntityModifiedRatio;
import com.til.recasting.mixin_api.IEntitySize;
import com.til.recasting.registry.RecastingAttackTypes;
import lombok.Getter;
import lombok.Setter;
import mods.flammpfeil.slashblade.entity.EntityJudgementCut;
import net.minecraft.world.entity.Entity;
import mods.flammpfeil.slashblade.entity.EntitySlashEffect;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

/**
 * Mixin 用于修改 EntityJudgementCut（幻影剑/次元斩）的持久化行为
 * 防止幻影剑实体被保存到世界文件，并使用正确的攻击类型
 */
@Mixin(value = EntityJudgementCut.class, remap = false)
public abstract class EntityJudgementCutMixin implements EntityAccess, IEntityModifiedRatio, IEntitySize {

    @Unique
    private static final EntityDataAccessor<Float> RECASTING$SIZE = SynchedEntityData.defineId(EntityJudgementCut.class, EntityDataSerializers.FLOAT);

    @Unique
    @Getter
    @Setter
    private float recasting$modifiedRatio = 0;

    @Shadow
    @Nullable
    public abstract Entity getShooter();

    @Shadow
    private double damage;

    /**
     * 获取 EntityData，使用 EntityAccessor
     */
    @Unique
    private SynchedEntityData recasting2$getEntityData() {
        Entity self = (Entity) (Object) this;
        return ((EntityAccessor) self).getEntityData();
    }


    @Shadow
    public abstract double getDamage();

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

    /**
     * 在 tick 方法开始时检查是否有 shooter
     * 如果没有 shooter，直接删除实体
     */
    @Inject(
            method = "tick",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private void checkShooter(CallbackInfo ci) {
        EntityJudgementCut self = (EntityJudgementCut) (Object) this;
        // 只在服务端检查
        if (this.getShooter() == null) {
            // 必须有 shooter，否则删除实体
            self.discard();
            ci.cancel();
        }
    }

    /**
     * 重定向 tick 方法中的 areaAttack 调用
     * 使用自定义的 AttackManager.areaAttack，传入正确的攻击类型
     */
    @Redirect(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lmods/flammpfeil/slashblade/util/AttackManager;areaAttack(Lnet/minecraft/world/entity/Entity;Ljava/util/function/Consumer;DZZFLjava/util/List;)Ljava/util/List;",
                    remap = false
            ),
            remap = false
    )
    private List<Entity> redirectAreaAttack(
            Entity owner,
            Consumer<LivingEntity> beforeHit,
            double reach,
            boolean forceHit,
            boolean resetHit,
            float comboRatio,
            List<Entity> exclude
    ) {
        // 调用自定义的 AttackManager.areaAttack，传入次元斩攻击类型
        if (getShooter() instanceof LivingEntity living) {
            // 根据 size 调整攻击范围
            float adjustedReach = (float) reach * getRecasting$size();

            return com.til.recasting.util.AttackManager.areaAttack(
                    living,
                    beforeHit,
                    recasting$modifiedRatio,
                    (float) damage,
                    forceHit,
                    resetHit,
                    false, // mute
                    adjustedReach,
                    exclude,
                    List.of(RecastingAttackTypes.JUDGEMENT_CUT_ATTACK.get())
            );
        }
        return List.of();
    }

    /**
     * 拦截 EntitySlashEffect 的 addFreshEntity 调用
     * 在添加实体前设置其 modifiedRatio 和 damage，让它们按比例继承
     */
    @Redirect(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"
            ),
            remap = false
    )
    private boolean redirectAddFreshEntity(Level level, Entity entity) {
        // 如果是 EntitySlashEffect，设置其属性
        if (entity instanceof EntitySlashEffect slashEffect) {
            // 按比例继承 modifiedRatio
            if (slashEffect instanceof IEntityModifiedRatio entityModifiedRatio) {
                entityModifiedRatio.setRecasting$modifiedRatio(0.1f * this.recasting$modifiedRatio); // TODO 写入配置
            }

            slashEffect.setDamage(0.1 * this.getDamage()); // TODO 写入配置
        }

        // 执行原始的 addFreshEntity
        return level.addFreshEntity(entity);
    }
}

