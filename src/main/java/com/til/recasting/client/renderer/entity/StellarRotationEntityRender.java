package com.til.recasting.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.til.recasting.entity.StellarRotationEntity;
import mods.flammpfeil.slashblade.client.renderer.util.MSAutoCloser;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import java.awt.*;
import java.util.Random;

/**
 * 星旋斩特效渲染器
 * 使用类似末影龙死亡特效的闪电渲染效果
 */
@OnlyIn(Dist.CLIENT)
public class StellarRotationEntityRender<E extends StellarRotationEntity> extends JudgementCutEntityRenderer<E> {

    private static final float SQRT_3_OVER_2 = (float) (Math.sqrt(3.0D) / 2.0D);

    public StellarRotationEntityRender(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(@NotNull E entity, float entityYaw, float partialTicks, @NotNull PoseStack matrixStackIn, @NotNull MultiBufferSource bufferIn, int packedLightIn) {
        super.render(entity, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);

        Color color = entity.getColor();

        // 计算生命周期进度
        float lifeProgress = (entity.tickCount + partialTicks) / (entity.getMaxLifeTime() - 5);
        // 计算淡出效果
        float fadeOut = Math.min(lifeProgress > 0.8F
                ? (lifeProgress - 0.8F) / 0.2F
                : 0.0F, 1.0F);

        Random random = new Random(432L);
        VertexConsumer vertexBuilder = bufferIn.getBuffer(RenderType.lightning());

        try (MSAutoCloser msac = MSAutoCloser.pushMatrix(matrixStackIn)) {
            float size = entity.getSize() / 10;
            matrixStackIn.scale(size, size, size);

            // 绘制闪电效果
            for(int i = 0; (float) i < (lifeProgress + lifeProgress * lifeProgress) / 2.0F * 60.0F; ++i) {
                matrixStackIn.mulPose(Axis.XP.rotationDegrees(random.nextFloat() * 360.0F));
                matrixStackIn.mulPose(Axis.YP.rotationDegrees(random.nextFloat() * 360.0F));
                matrixStackIn.mulPose(Axis.ZP.rotationDegrees(random.nextFloat() * 360.0F));
                matrixStackIn.mulPose(Axis.XP.rotationDegrees(random.nextFloat() * 360.0F));
                matrixStackIn.mulPose(Axis.YP.rotationDegrees(random.nextFloat() * 360.0F));
                matrixStackIn.mulPose(Axis.ZP.rotationDegrees(random.nextFloat() * 360.0F + lifeProgress * 90.0F));

                float length = random.nextFloat() * 20.0F + 5.0F + fadeOut * 10.0F;
                float width = random.nextFloat() * 2.0F + 1.0F + fadeOut * 2.0F;
                Matrix4f matrix4f = matrixStackIn.last().pose();
                int alpha = (int) (255.0F * (1.0F - fadeOut));

                // 绘制三角形组成的闪电效果
                drawVertex(vertexBuilder, matrix4f, alpha);
                drawVertex1(vertexBuilder, matrix4f, length, width, color);
                drawVertex2(vertexBuilder, matrix4f, length, width, color);
                drawVertex(vertexBuilder, matrix4f, alpha);
                drawVertex2(vertexBuilder, matrix4f, length, width, color);
                drawVertex3(vertexBuilder, matrix4f, length, width, color);
                drawVertex(vertexBuilder, matrix4f, alpha);
                drawVertex3(vertexBuilder, matrix4f, length, width, color);
                drawVertex1(vertexBuilder, matrix4f, length, width, color);
            }
        }
    }

    /**
     * 绘制中心顶点
     */
    private static void drawVertex(VertexConsumer builder, Matrix4f matrix, int alpha) {
        builder.vertex(matrix, 0.0F, 0.0F, 0.0F).color(255, 255, 255, alpha).endVertex();
        builder.vertex(matrix, 0.0F, 0.0F, 0.0F).color(255, 255, 255, alpha).endVertex();
    }

    /**
     * 绘制第一个外围顶点
     */
    private static void drawVertex1(VertexConsumer builder, Matrix4f matrix, float length, float width, Color color) {
        builder.vertex(matrix, -SQRT_3_OVER_2 * width, length, -0.5F * width)
                .color(color.getRed(), color.getGreen(), color.getBlue(), 0)
                .endVertex();
    }

    /**
     * 绘制第二个外围顶点
     */
    private static void drawVertex2(VertexConsumer builder, Matrix4f matrix, float length, float width, Color color) {
        builder.vertex(matrix, SQRT_3_OVER_2 * width, length, -0.5F * width)
                .color(color.getRed(), color.getGreen(), color.getBlue(), 0)
                .endVertex();
    }

    /**
     * 绘制第三个外围顶点
     */
    private static void drawVertex3(VertexConsumer builder, Matrix4f matrix, float length, float width, Color color) {
        builder.vertex(matrix, 0.0F, length, width)
                .color(color.getRed(), color.getGreen(), color.getBlue(), 0)
                .endVertex();
    }
}
