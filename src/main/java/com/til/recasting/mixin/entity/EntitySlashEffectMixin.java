package com.til.recasting.mixin.entity;

import com.til.recasting.registry.RecastingAttackTypes;
import com.til.recasting.handler.AttackHelper;
import com.til.recasting.util.DamageStructure;
import mods.flammpfeil.slashblade.entity.EntitySlashEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.entity.EntityAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

/**
 * Mixin 用于修改 EntitySlashEffect.tick 方法中的 areaAttack 调用
 * 使用自定义的 AttackManager 并传入攻击距离和攻击类型
 */
@Mixin(value = EntitySlashEffect.class)
public abstract class EntitySlashEffectMixin implements EntityAccess {

    @Shadow(remap = false)
    public abstract float getBaseSize();

    @Shadow(remap = false)
    @Nullable
    public abstract Entity getShooter();


    @Shadow(remap = false)
    public abstract double getDamage();

    /**
     * 重写 shouldBeSaved 方法，防止 EntitySlashEffect 被持久化
     * 作为临时效果实体，它不应该被保存到世界文件中
     * 这个方法会被 Mixin 自动添加到目标类，覆盖父类的实现
     */
    public boolean shouldBeSaved() {
        return false;
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
    private void checkShooter(CallbackInfo ci) {
        EntitySlashEffect self = (EntitySlashEffect) (Object) this;
        if (this.getShooter() == null) {
            self.discard();
            ci.cancel();
        }
    }

    /**
     * 重定向原版的 areaAttack 调用（LivingEntity 版本）
     * 使用自定义的 AttackManager，传入攻击距离和攻击类型
     */
    @Redirect(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lmods/flammpfeil/slashblade/util/AttackManager;areaAttack(Lnet/minecraft/world/entity/LivingEntity;Ljava/util/function/Consumer;FZZZLjava/util/List;)Ljava/util/List;",
                    remap = false
            ),
            remap = false
    )
    private List<Entity> redirectAreaAttackLiving(
            LivingEntity shooter,
            Consumer<LivingEntity> beforeHit,
            float ratio,
            boolean forceHit,
            boolean resetHit,
            boolean mute,
            List<Entity> exclude
    ) {
        // 计算攻击范围：4 * baseSize
        float attackRange = 4.0f * this.getBaseSize();

        EntitySlashEffect self = (EntitySlashEffect) (Object) this;

        // 调用自定义的 AttackManager.areaAttack
        return AttackHelper.areaAttack(shooter, self.getPosition(0), new DamageStructure((float) getDamage(), 0), attackRange, List.of(RecastingAttackTypes.SLASH_EFFECT_ATTACK.get()), exclude, beforeHit)
                .stream()
                .map(e -> (Entity) e)
                .toList();
    }

    /**
     * shooter 缺失，这是不应该继续的，
     */
    @Redirect(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lmods/flammpfeil/slashblade/util/AttackManager;areaAttack(Lnet/minecraft/world/entity/Entity;Ljava/util/function/Consumer;DZZLjava/util/List;)Ljava/util/List;",
                    remap = false
            ),
            remap = false
    )
    private List<Entity> redirectAreaAttackShootable(
            Entity owner,
            Consumer<LivingEntity> beforeHit,
            double reach,
            boolean forceHit,
            boolean resetHit,
            List<Entity> exclude
    ) {
        return List.of();
    }
}

