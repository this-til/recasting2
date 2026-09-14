package com.til.recasting.mixin;

import com.til.recasting.handler.SlashBladeItemHelper;
import com.til.recasting.handler.VanillaJudgementCutHelper;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.entity.EntityJudgementCut;
import mods.flammpfeil.slashblade.slasharts.JudgementCut;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

/**
 * 规则命中时将原版次元斩换成 Recasting {@code JudgementCutEntity}。
 */
@Mixin(value = JudgementCut.class, remap = false)
public abstract class JudgementCutMixin {

    @Inject(method = "doJudgementCutJust", at = @At("HEAD"), cancellable = true)
    private static void recasting$just(LivingEntity user, CallbackInfoReturnable<EntityJudgementCut> cir) {
        if (!SlashBladeItemHelper.matchesReplaceRule(user.getMainHandItem())) {
            return;
        }
        VanillaJudgementCutHelper.spawn(user, true);
        cir.setReturnValue(dummyJudgementCut(user));
    }

    @Inject(method = "doJudgementCut", at = @At("HEAD"), cancellable = true)
    private static void recasting$cut(LivingEntity user, CallbackInfoReturnable<EntityJudgementCut> cir) {
        if (!SlashBladeItemHelper.matchesReplaceRule(user.getMainHandItem())) {
            return;
        }
        VanillaJudgementCutHelper.spawn(user, false);
        cir.setReturnValue(dummyJudgementCut(user));
    }

    @Inject(method = "doJudgementCutSuper(Lnet/minecraft/world/entity/LivingEntity;Ljava/util/List;)V", at = @At("HEAD"), cancellable = true)
    private static void recasting$super(LivingEntity owner, List<Entity> exclude, CallbackInfo ci) {
        if (!SlashBladeItemHelper.matchesReplaceRule(owner.getMainHandItem())) {
            return;
        }
        VanillaJudgementCutHelper.spawnSuper(owner, exclude);
        ci.cancel();
    }

    private static EntityJudgementCut dummyJudgementCut(LivingEntity user) {
        return new EntityJudgementCut(SlashBlade.RegistryEvents.JudgementCut, user.level());
    }
}
