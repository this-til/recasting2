package com.til.recasting.mixin;

import com.til.recasting.registry.RecastingAttackTypes;
import com.til.recasting.util.DamageStructure;
import mods.flammpfeil.slashblade.util.AttackHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

/**
 * 接管 {@link AttackHelper#attack}；使用 HEAD cancellable 保留原方法体，
 * 供 sbr_core 等模组的 WrapOperation 找到注入点。
 */
@Mixin(value = AttackHelper.class)
public abstract class AttackHelperMixin {

    @Inject(
            method = "attack(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/Entity;F)V",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private static void recasting$attack(LivingEntity attacker, Entity target, float modifiedRatio, CallbackInfo ci) {
        com.til.recasting.handler.AttackHelper.attack(
                attacker, target, new DamageStructure(modifiedRatio, 0),
                List.of(RecastingAttackTypes.SLASH_EFFECT_ATTACK.get())
        );
        ci.cancel();
    }
}
