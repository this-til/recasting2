package com.til.recasting.mixin;

import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.handler.AttackHelper;
import com.til.recasting.handler.CapabilityRegistryHandler;
import com.til.recasting.handler.SlashBladeItemHelper;
import com.til.recasting.registry.RecastingAttackTypes;
import com.til.recasting.util.DamageStructure;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.function.Consumer;

/**
 * 挥刀由 {@code VanillaDoSlashHandler} 接管。此处只替换 {@code areaAttack} / {@code doMeleeAttack}。
 */
@Mixin(value = mods.flammpfeil.slashblade.util.AttackManager.class, remap = false)
public abstract class AttackManagerMixin {

    @Inject(
            method = "areaAttack(Lnet/minecraft/world/entity/LivingEntity;Ljava/util/function/Consumer;FZZZLjava/util/List;)Ljava/util/List;",
            at = @At("HEAD"),
            cancellable = true
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
        if (!SlashBladeItemHelper.matchesReplaceRule(playerIn.getMainHandItem())) {
            return;
        }
        float attackDistance = playerIn.getMainHandItem()
                .getCapability(CapabilityRegistryHandler.PROPERTIES_DEFINITION_EXTENSION)
                .map(PropertiesDefinitionExtension::attackDistance)
                .orElse(1.0f);
        List<Entity> hits = AttackHelper.areaAttack(
                        playerIn,
                        playerIn.getPosition(0),
                        new DamageStructure(comboRatio, 0),
                        attackDistance,
                        List.of(RecastingAttackTypes.SLASH_EFFECT_ATTACK.get()),
                        exclude,
                        beforeHit
                ).stream()
                .map(e -> (Entity) e)
                .toList();
        cir.setReturnValue(hits);
    }

    @Inject(
            method = "doMeleeAttack(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/Entity;ZZF)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void recasting$doMeleeAttack(
            LivingEntity attacker,
            Entity target,
            boolean forceHit,
            boolean resetHit,
            float comboRatio,
            CallbackInfo ci
    ) {
        if (!SlashBladeItemHelper.matchesReplaceRule(attacker.getMainHandItem())) {
            return;
        }
        AttackHelper.doMeleeAttack(
                attacker, target, new DamageStructure(comboRatio, 0),
                List.of(RecastingAttackTypes.SLASH_EFFECT_ATTACK.get())
        );
        ci.cancel();
    }
}
