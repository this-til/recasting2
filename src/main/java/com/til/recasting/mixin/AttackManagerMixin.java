package com.til.recasting.mixin;

import com.til.recasting.entity.SlashEffectEntity;
import com.til.recasting.handler.AttackHelper;
import com.til.recasting.registry.RecastingAttackTypes;
import com.til.recasting.util.DamageStructure;
import mods.flammpfeil.slashblade.RegistryEvents;
import mods.flammpfeil.slashblade.entity.EntitySlashEffect;
import mods.flammpfeil.slashblade.util.KnockBacks;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.function.Consumer;

/**
 * 接管 SlashBlade {@code AttackManager} 的关键攻击入口，接入本模组 AttackHelper / DoSlashExtendEvent。
 */
@Mixin(value = mods.flammpfeil.slashblade.util.AttackManager.class, remap = false)
public abstract class AttackManagerMixin {

    @Inject(
            method = "doSlash(Lnet/minecraft/world/entity/LivingEntity;FILnet/minecraft/world/phys/Vec3;ZZDLmods/flammpfeil/slashblade/util/KnockBacks;)Lmods/flammpfeil/slashblade/entity/EntitySlashEffect;",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private static void recasting$doSlash(
            LivingEntity playerIn,
            float roll,
            int colorCode,
            Vec3 centerOffset,
            boolean mute,
            boolean critical,
            double comboRatio,
            KnockBacks knockback,
            CallbackInfoReturnable<EntitySlashEffect> cir
    ) {
        float attackDistance = AttackHelper.propertiesOf(playerIn.getMainHandItem()).attackDistance();

        AttackHelper.doSlash(
                playerIn,
                roll,
                colorCode,
                centerOffset,
                mute,
                critical,
                new DamageStructure((float) comboRatio, 0),
                attackDistance,
                knockback
        );

        // 真实伤害实体已入世；返回哑元以满足 SlashBlade 方法签名（与 1.20 一致）
        cir.setReturnValue(new EntitySlashEffect(RegistryEvents.SlashEffect, playerIn.level()));
        cir.cancel();
    }

    @Inject(
            method = "areaAttack(Lnet/minecraft/world/entity/LivingEntity;Ljava/util/function/Consumer;FZZZLjava/util/List;)Ljava/util/List;",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private static void recasting$areaAttack(
            LivingEntity playerIn,
            Consumer<LivingEntity> beforeHit,
            float comboRatio,
            boolean forceHit,
            boolean resetHit,
            boolean mute,
            List<Entity> exclude,
            CallbackInfoReturnable<List<Entity>> cir
    ) {
        float attackDistance = AttackHelper.propertiesOf(playerIn.getMainHandItem()).attackDistance();

        List<Entity> hits = AttackHelper.areaAttack(
                        playerIn,
                        playerIn.position(),
                        new DamageStructure(comboRatio, 0),
                        attackDistance,
                        List.of(RecastingAttackTypes.SLASH_EFFECT_ATTACK.get()),
                        exclude,
                        beforeHit
                )
                .stream()
                .map(e -> (Entity) e)
                .toList();

        cir.setReturnValue(hits);
        cir.cancel();
    }

    @Inject(
            method = "doMeleeAttack(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/Entity;ZZF)V",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private static void recasting$doMeleeAttack(
            LivingEntity attacker,
            Entity target,
            boolean forceHit,
            boolean resetHit,
            float comboRatio,
            CallbackInfo ci
    ) {
        AttackHelper.doMeleeAttack(
                attacker,
                target,
                new DamageStructure(comboRatio, 0),
                List.of(RecastingAttackTypes.SLASH_EFFECT_ATTACK.get())
        );
        // TODO(P3): cancel 后补回 AttackManager 原有 ArrowReflector / TNTExtinguisher 调用
        ci.cancel();
    }
}
