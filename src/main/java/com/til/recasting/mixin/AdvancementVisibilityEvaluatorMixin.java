package com.til.recasting.mixin;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementNode;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.advancements.AdvancementVisibilityEvaluator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Predicate;

/**
 * 「重铸之路」整棵成就树始终公开可见。
 */
@Mixin(AdvancementVisibilityEvaluator.class)
public abstract class AdvancementVisibilityEvaluatorMixin {

    @Inject(
            method = "evaluateVisibility(Lnet/minecraft/advancements/AdvancementNode;Ljava/util/function/Predicate;Lnet/minecraft/server/advancements/AdvancementVisibilityEvaluator$Output;)V",
            at = @At("RETURN")
    )
    private static void recasting$forceGrowthTreeVisible(
            AdvancementNode root,
            Predicate<AdvancementHolder> doneTest,
            AdvancementVisibilityEvaluator.Output output,
            CallbackInfo ci
    ) {
        forceShowGrowthTree(root, output);
    }

    private static void forceShowGrowthTree(AdvancementNode node, AdvancementVisibilityEvaluator.Output output) {
        AdvancementHolder holder = node.holder();
        if (isRecastingGrowth(holder)) {
            output.accept(node, true);
        }
        for (AdvancementNode child : node.children()) {
            forceShowGrowthTree(child, output);
        }
    }

    private static boolean isRecastingGrowth(AdvancementHolder holder) {
        var id = holder.id();
        return "recasting".equals(id.getNamespace()) && id.getPath().startsWith("growth/");
    }
}
