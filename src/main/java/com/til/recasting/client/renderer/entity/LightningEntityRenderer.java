package com.til.recasting.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.til.recasting.entity.LightningEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import java.awt.*;
import java.util.Random;

/**
 * 闪电实体渲染器
 * 基于原版闪电渲染逻辑，支持自定义颜色和大小
 */
@OnlyIn(Dist.CLIENT)
public class LightningEntityRenderer<E extends LightningEntity> extends EntityRenderer<E> {

    public LightningEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(LightningEntity entityIn, float entityYaw, float partialTicks,
                       @NotNull PoseStack matrixStackIn, @NotNull MultiBufferSource bufferIn, int packedLightIn) {
        float[] afloat = new float[8];
        float[] afloat1 = new float[8];
        float f = 0.0F;
        float f1 = 0.0F;
        Random random = new Random(entityIn.getBoltVertex());

        for(int i = 7; i >= 0; --i) {
            afloat[i] = f;
            afloat1[i] = f1;
            f += (float) (random.nextInt(11) - 5);
            f1 += (float) (random.nextInt(11) - 5);
        }

        Color color = entityIn.getColor();
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();

        float scale = entityIn.getSize();
        matrixStackIn.scale(scale, scale, scale);

        VertexConsumer vertexConsumer = bufferIn.getBuffer(RenderType.lightning());
        Matrix4f matrix4f = matrixStackIn.last().pose();

        for(int j = 0; j < 4; ++j) {
            Random random1 = new Random(entityIn.getBoltVertex());

            for(int k = 0; k < 3; ++k) {
                int l = 7;
                int i1 = 0;
                if (k > 0) {
                    l = 7 - k;
                }

                if (k > 0) {
                    i1 = l - 2;
                }

                float f2 = afloat[l] - f;
                float f3 = afloat1[l] - f1;

                for(int j1 = l; j1 >= i1; --j1) {
                    float f4 = f2;
                    float f5 = f3;
                    if (k == 0) {
                        f2 += (float) (random1.nextInt(11) - 5);
                        f3 += (float) (random1.nextInt(11) - 5);
                    } else {
                        f2 += (float) (random1.nextInt(31) - 15);
                        f3 += (float) (random1.nextInt(31) - 15);
                    }

                    float f10 = 0.1F + (float) j * 0.2F;
                    if (k == 0) {
                        f10 = (float) ((double) f10 * ((double) j1 * 0.1D + 1.0D));
                    }

                    float f11 = 0.1F + (float) j * 0.2F;
                    if (k == 0) {
                        f11 *= (float) (j1 - 1) * 0.1F + 1.0F;
                    }

                    int alpha = 76;
                    renderVertex(matrix4f, vertexConsumer, f2, f3, j1, f4, f5, r, g, b, alpha, f10, f11, false, false, true, false);
                    renderVertex(matrix4f, vertexConsumer, f2, f3, j1, f4, f5, r, g, b, alpha, f10, f11, true, false, true, true);
                    renderVertex(matrix4f, vertexConsumer, f2, f3, j1, f4, f5, r, g, b, alpha, f10, f11, true, true, false, true);
                    renderVertex(matrix4f, vertexConsumer, f2, f3, j1, f4, f5, r, g, b, alpha, f10, f11, false, true, false, false);
                }
            }
        }
    }

    /**
     * 渲染闪电的一个顶点
     */
    private static void renderVertex(Matrix4f matrix4f, VertexConsumer vertexConsumer,
                                     float x, float z, int segment,
                                     float prevX, float prevZ,
                                     int r, int g, int b, int alpha,
                                     float width1, float width2,
                                     boolean flag1, boolean flag2, boolean flag3, boolean flag4) {
        vertexConsumer.vertex(matrix4f, x + (flag1
                        ? width2
                        : -width2), (float) (segment * 16), z + (flag2
                        ? width2
                        : -width2))
                .color(r, g, b, alpha)
                .endVertex();
        vertexConsumer.vertex(matrix4f, prevX + (flag1
                        ? width1
                        : -width1), (float) ((segment + 1) * 16), prevZ + (flag2
                        ? width1
                        : -width1))
                .color(r, g, b, alpha)
                .endVertex();
        vertexConsumer.vertex(matrix4f, prevX + (flag3
                        ? width1
                        : -width1), (float) ((segment + 1) * 16), prevZ + (flag4
                        ? width1
                        : -width1))
                .color(r, g, b, alpha)
                .endVertex();
        vertexConsumer.vertex(matrix4f, x + (flag3
                        ? width2
                        : -width2), (float) (segment * 16), z + (flag4
                        ? width2
                        : -width2))
                .color(r, g, b, alpha)
                .endVertex();
    }

    @NotNull
    @Override
    public ResourceLocation getTextureLocation(@NotNull LightningEntity entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
