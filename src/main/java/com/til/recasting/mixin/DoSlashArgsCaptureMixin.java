package com.til.recasting.mixin;

import com.til.recasting.handler.DoSlashArgsCapture;
import mods.flammpfeil.slashblade.slasharts.SakuraEnd;
import mods.flammpfeil.slashblade.util.AttackManager;
import mods.flammpfeil.slashblade.util.KnockBacks;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 不替换挥刀，只把原版 {@code doSlash} 的 offset / mute / color 交给 {@code VanillaDoSlashHandler}。
 */
@Mixin(value = {AttackManager.class, SakuraEnd.class}, remap = false)
public abstract class DoSlashArgsCaptureMixin {

    @Inject(
            method = "doSlash(Lnet/minecraft/world/entity/LivingEntity;FILnet/minecraft/world/phys/Vec3;ZZDLmods/flammpfeil/slashblade/util/KnockBacks;)Lmods/flammpfeil/slashblade/entity/EntitySlashEffect;",
            at = @At("HEAD")
    )
    private static void recasting$capture(
            LivingEntity playerIn,
            float roll,
            int colorCode,
            Vec3 centerOffset,
            boolean mute,
            boolean critical,
            double comboRatio,
            KnockBacks knockback,
            CallbackInfoReturnable<?> cir
    ) {
        DoSlashArgsCapture.push(centerOffset, mute, colorCode);
    }

    @Inject(
            method = "doSlash(Lnet/minecraft/world/entity/LivingEntity;FILnet/minecraft/world/phys/Vec3;ZZDLmods/flammpfeil/slashblade/util/KnockBacks;)Lmods/flammpfeil/slashblade/entity/EntitySlashEffect;",
            at = @At("RETURN")
    )
    private static void recasting$clear(
            LivingEntity playerIn,
            float roll,
            int colorCode,
            Vec3 centerOffset,
            boolean mute,
            boolean critical,
            double comboRatio,
            KnockBacks knockback,
            CallbackInfoReturnable<?> cir
    ) {
        DoSlashArgsCapture.pop();
    }
}
