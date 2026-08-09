package com.til.recasting.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.til.recasting.entity.FinalGlowBlackHoleEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 末辉黑洞：不渲染模型，视觉由实体客户端粒子绘制。
 */
@OnlyIn(Dist.CLIENT)
public class FinalGlowBlackHoleEntityRender<E extends FinalGlowBlackHoleEntity> extends EntityRenderer<E> {

    public FinalGlowBlackHoleEntityRender(EntityRendererProvider.Context context) {
        super(context);
    }

    @Nullable
    @Override
    public ResourceLocation getTextureLocation(@NotNull E entity) {
        return null;
    }

    @Override
    public void render(
            @NotNull E entity,
            float entityYaw,
            float partialTicks,
            @NotNull PoseStack matrixStack,
            @NotNull MultiBufferSource bufferIn,
            int packedLightIn
    ) {
    }
}
