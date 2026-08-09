package com.til.recasting.mixin.entity;

import com.til.recasting.handler.AttackHelper;
import com.til.recasting.registry.RecastingAttackTypes;
import com.til.recasting.util.DamageStructure;
import mods.flammpfeil.slashblade.entity.EntityDrive;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Mixin 用于修改 EntityDrive (剑气) 的伤害计算
 * 使用自定义的攻击系统
 */
@Mixin(value = EntityDrive.class)
public abstract class EntityDriveMixin implements EntityAccess {

    @Shadow(remap = false)
    @Nullable
    public abstract Entity getShooter();

    @Shadow(remap = false)
    public abstract double getDamage();

    /**
     * 重定向 onHitEntity 中的伤害计算
     * 使用自定义的 AttackHelper.attack 方法
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
            AttackHelper.attack(
                    attacker,
                    target,
                    new DamageStructure(0, (float) getDamage()),
                    List.of(RecastingAttackTypes.DRIVE_ATTACK.get())
            );

            // 取消原版的伤害逻辑
            ci.cancel();
        }
    }

}

