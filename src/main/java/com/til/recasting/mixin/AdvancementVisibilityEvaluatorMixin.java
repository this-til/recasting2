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
 * 对本模组「回到未来 SA」「附魔」「锻造」分支：若任一祖先已完成（含自动 hub），整条链直接可见。
 */
@Mixin(AdvancementVisibilityEvaluator.class)
public abstract class AdvancementVisibilityEvaluatorMixin {

    @Inject(
            method = "evaluateVisibility(Lnet/minecraft/advancements/Advancement;Ljava/util/function/Predicate;Lnet/minecraft/server/advancements/AdvancementVisibilityEvaluator$Output;)V",
            at = @At("RETURN")
    )
    private static void recasting$forceLinearBranchVisible(
            Advancement root,
            Predicate<Advancement> doneTest,
            AdvancementVisibilityEvaluator.Output output,
            CallbackInfo ci
    ) {
        forceShowForcedBranches(root, doneTest, output, false);
    }

    private static void forceShowForcedBranches(
            Advancement advancement,
            Predicate<Advancement> doneTest,
            AdvancementVisibilityEvaluator.Output output,
            boolean ancestorDone
    ) {
        boolean selfOrAncestorDone = ancestorDone || doneTest.test(advancement);
        if (selfOrAncestorDone && isForcedVisibleBranch(advancement.getId())) {
            output.accept(advancement, true);
        }
        for (Advancement child : advancement.getChildren()) {
            forceShowForcedBranches(child, doneTest, output, selfOrAncestorDone);
        }
    }

    private static boolean isForcedVisibleBranch(ResourceLocation id) {
        if (!"recasting".equals(id.getNamespace())) {
            return false;
        }
        String path = id.getPath();
        return path.startsWith("growth/drop/btf/")
                || path.startsWith("growth/enchant/")
                || path.startsWith("growth/forge/");
    }
}
