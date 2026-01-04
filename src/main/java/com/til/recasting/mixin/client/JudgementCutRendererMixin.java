package com.til.recasting.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.til.recasting.mixin_api.IEntitySize;
import mods.flammpfeil.slashblade.entity.EntityJudgementCut;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * Mixin 用于为 JudgementCutRenderer 添加自定义大小支持
 */
@OnlyIn(Dist.CLIENT)
@Mixin(targets = "mods.flammpfeil.slashblade.client.renderer.entity.JudgementCutRenderer", remap = false)
public abstract class JudgementCutRendererMixin<T extends EntityJudgementCut> {

    /**
     * 修改 scale 变量的值以支持自定义大小
     * 在 scale 变量被赋值后，scale() 方法调用之前拦截并修改
     */
    @ModifyVariable(
            method = "render*",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/vertex/PoseStack;scale(FFF)V",
                    ordinal = 0
            ),
            ordinal = 0,
            remap = false,
            argsOnly = true)
    private float modifyScale(float originalScale, T entity, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        // 获取自定义大小
        if (entity instanceof IEntitySize sizeEntity) {
            float sizeMultiplier = sizeEntity.getRecasting$size();
            return originalScale * sizeMultiplier;
        }
        return originalScale;
    }
}

