package com.til.recasting.mixin;

import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.compat.Dmc5SfxCompat;
import com.til.recasting.entity.SlashEffectEntity;
import com.til.recasting.handler.AttackHelper;
import com.til.recasting.handler.CapabilityRegistryHandler;
import com.til.recasting.registry.RecastingAttackTypes;
import com.til.recasting.util.DamageStructure;
import mods.flammpfeil.slashblade.SlashBlade;
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
 * Mixin 用于接管 SlashBlade 原版的 AttackManager 方法
 * 添加自定义攻击距离支持；使用 HEAD cancellable 保留原方法体供其他模组注入。
 */
@Mixin(value = mods.flammpfeil.slashblade.util.AttackManager.class)
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
        float attackDistance = playerIn.getMainHandItem()
                .getCapability(CapabilityRegistryHandler.PROPERTIES_DEFINITION_EXTENSION)
                .map(PropertiesDefinitionExtension::attackDistance)
                .orElse(1.0f);

        SlashEffectEntity effect = AttackHelper.doSlash(
                playerIn, roll, colorCode, centerOffset,
                mute, critical, new DamageStructure((float) comboRatio, 0), attackDistance, knockback
        );

        if (effect != null && Dmc5SfxCompat.shouldMuteSlashEffect(playerIn)) {
            effect.setMute(true);
        }

        // 放弃使用 EntitySlashEffect 但不能直接返回 null，希望它是安全的
        cir.setReturnValue(new EntitySlashEffect(SlashBlade.RegistryEvents.SlashEffect, playerIn.level()));
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
        float attackDistance = playerIn.getMainHandItem()
                .getCapability(CapabilityRegistryHandler.PROPERTIES_DEFINITION_EXTENSION)
                .map(PropertiesDefinitionExtension::attackDistance)
                .orElse(1.0f);

        List<Entity> hits = AttackHelper.areaAttack(
                        playerIn, playerIn.getPosition(0), new DamageStructure(comboRatio, 0), attackDistance,
                        List.of(RecastingAttackTypes.SLASH_EFFECT_ATTACK.get()), exclude, beforeHit
                ).stream()
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
                attacker, target, new DamageStructure(comboRatio, 0),
                List.of(RecastingAttackTypes.SLASH_EFFECT_ATTACK.get())
        );
        ci.cancel();
    }
}
