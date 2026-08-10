package com.til.recasting.mixin;

import net.minecraft.advancements.Advancement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.advancements.AdvancementVisibilityEvaluator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Predicate;

/**
 * 原版对未完成节点只向前显示 {@code VISIBILITY_DEPTH}（2）层。
 * 「重铸之路」整棵成就树始终公开可见。
 */
@Mixin(AdvancementVisibilityEvaluator.class)
public abstract class AdvancementVisibilityEvaluatorMixin {

    @Inject(
            method = "evaluateVisibility(Lnet/minecraft/advancements/Advancement;Ljava/util/function/Predicate;Lnet/minecraft/server/advancements/AdvancementVisibilityEvaluator$Output;)V",
            at = @At("RETURN")
    )
    private static void recasting$forceGrowthTreeVisible(
            Advancement root,
            Predicate<Advancement> doneTest,
            AdvancementVisibilityEvaluator.Output output,
            CallbackInfo ci
    ) {
        forceShowGrowthTree(root, output);
    }

    private static void forceShowGrowthTree(Advancement advancement, AdvancementVisibilityEvaluator.Output output) {
        if (isRecastingGrowth(advancement.getId())) {
            output.accept(advancement, true);
        }
        for (Advancement child : advancement.getChildren()) {
            forceShowGrowthTree(child, output);
        }
    }

    private static boolean isRecastingGrowth(ResourceLocation id) {
        return "recasting".equals(id.getNamespace()) && id.getPath().startsWith("growth/");
    }
}
