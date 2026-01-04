package com.til.recasting.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.til.recasting.entity.LightningEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Random;

/**
 * 闪电实体渲染器
 * 渲染闪电特效
 */
@OnlyIn(Dist.CLIENT)
public class LightningEntityRenderer extends EntityRenderer<LightningEntity> {

    public LightningEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(@NotNull LightningEntity entity, float entityYaw, float partialTicks, 
                      @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {
        
        float[] xOffsets = new float[8];
        float[] zOffsets = new float[8];
        float currentX = 0.0F;
        float currentZ = 0.0F;
        Random random = new Random(entity.getBoltVertex());

        // 生成闪电路径的随机偏移
        for (int i = 7; i >= 0; --i) {
            xOffsets[i] = currentX;
            zOffsets[i] = currentZ;
            currentX += (float) (random.nextInt(11) - 5);
            currentZ += (float) (random.nextInt(11) - 5);
        }

        // 获取颜色
        Color color = new Color(entity.getColor(), false);
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();

        // 应用缩放
        float scale = entity.getSize();
        poseStack.pushPose();
        poseStack.scale(scale, scale, scale);

        // 获取顶点构建器
        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.lightning());
        Matrix4f matrix4f = poseStack.last().pose();

        // 渲染闪电的多层效果
        for (int layer = 0; layer < 4; ++layer) {
            Random layerRandom = new Random(entity.getBoltVertex());

            // 渲染三个分支
            for (int branch = 0; branch < 3; ++branch) {
                int startSegment = 7;
                int endSegment = 0;
                
                if (branch > 0) {
                    startSegment = 7 - branch;
                }

                if (branch > 0) {
                    endSegment = startSegment - 2;
                }

                float prevX = xOffsets[startSegment] - currentX;
                float prevZ = zOffsets[startSegment] - currentZ;

                // 渲染每个线段
                for (int segment = startSegment; segment >= endSegment; --segment) {
                    float nextX = prevX;
                    float nextZ = prevZ;
                    
                    if (branch == 0) {
                        prevX += (float) (layerRandom.nextInt(11) - 5);
                        prevZ += (float) (layerRandom.nextInt(11) - 5);
                    } else {
                        prevX += (float) (layerRandom.nextInt(31) - 15);
                        prevZ += (float) (layerRandom.nextInt(31) - 15);
                    }

                    // 计算线宽
                    float width1 = 0.1F + (float) layer * 0.2F;
                    if (branch == 0) {
                        width1 = (float) ((double) width1 * ((double) segment * 0.1D + 1.0D));
                    }

                    float width2 = 0.1F + (float) layer * 0.2F;
                    if (branch == 0) {
                        width2 *= (float) (segment - 1) * 0.1F + 1.0F;
                    }

                    // 渲染四个面形成立体闪电
                    renderQuad(matrix4f, vertexConsumer, prevX, prevZ, segment, nextX, nextZ, 
                              r, g, b, width1, width2, false, false, true, false);
                    renderQuad(matrix4f, vertexConsumer, prevX, prevZ, segment, nextX, nextZ, 
                              r, g, b, width1, width2, true, false, true, true);
                    renderQuad(matrix4f, vertexConsumer, prevX, prevZ, segment, nextX, nextZ, 
                              r, g, b, width1, width2, true, true, false, true);
                    renderQuad(matrix4f, vertexConsumer, prevX, prevZ, segment, nextX, nextZ, 
                              r, g, b, width1, width2, false, true, false, false);
                }
            }
        }

        poseStack.popPose();
    }

    /**
     * 渲染闪电的一个四边形面
     */
    private static void renderQuad(Matrix4f matrix, VertexConsumer buffer, 
                                   float x1, float z1, int segment, 
                                   float x2, float z2,
                                   int red, int green, int blue,
                                   float width1, float width2,
                                   boolean negX1, boolean negZ1, 
                                   boolean negX2, boolean negZ2) {
        
        int alpha = 76; // 透明度
        
        // 计算四个顶点
        buffer.vertex(matrix, x1 + (negX1 ? width2 : -width2), (float) (segment * 16), 
                     z1 + (negZ1 ? width2 : -width2))
              .color(red, green, blue, alpha)
              .endVertex();
        
        buffer.vertex(matrix, x2 + (negX1 ? width1 : -width1), (float) ((segment + 1) * 16), 
                     z2 + (negZ1 ? width1 : -width1))
              .color(red, green, blue, alpha)
              .endVertex();
        
        buffer.vertex(matrix, x2 + (negX2 ? width1 : -width1), (float) ((segment + 1) * 16), 
                     z2 + (negZ2 ? width1 : -width1))
              .color(red, green, blue, alpha)
              .endVertex();
        
        buffer.vertex(matrix, x1 + (negX2 ? width2 : -width2), (float) (segment * 16), 
                     z1 + (negZ2 ? width2 : -width2))
              .color(red, green, blue, alpha)
              .endVertex();
    }

    @NotNull
    @Override
    public ResourceLocation getTextureLocation(@NotNull LightningEntity entity) {
        // 闪电不需要纹理
        return new ResourceLocation("minecraft", "textures/block/white_concrete.png");
    }
}

