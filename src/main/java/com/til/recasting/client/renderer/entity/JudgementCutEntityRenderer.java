package com.til.recasting.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.til.recasting.entity.JudgementCutEntity;
import mods.flammpfeil.slashblade.client.renderer.model.BladeModelManager;
import mods.flammpfeil.slashblade.client.renderer.model.obj.WavefrontObject;
import mods.flammpfeil.slashblade.client.renderer.util.BladeRenderState;
import mods.flammpfeil.slashblade.client.renderer.util.MSAutoCloser;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

/**
 * 次元斩实体渲染器
 * 参考 JudgementCutRendererMixin 实现，添加自定义大小支持和修复长生命周期渲染问题
 */
@OnlyIn(Dist.CLIENT)
public class JudgementCutEntityRenderer<E extends JudgementCutEntity> extends EntityRenderer<E> {

    public JudgementCutEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull JudgementCutEntity entity) {
        return entity.getTexture();
    }

    @Override
    public void render(JudgementCutEntity entity, float entityYaw, float partialTicks, PoseStack matrixStackIn,
                       @NotNull MultiBufferSource bufferIn, int packedLightIn) {

        try (MSAutoCloser msac = MSAutoCloser.pushMatrix(matrixStackIn)) {

            matrixStackIn.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, entity.yRotO, entity.getYRot()) - 90.0F));
            matrixStackIn.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTicks, entity.xRotO, entity.getXRot())));

            // 获取模型
            ResourceLocation modelLocation = entity.getModel();
            WavefrontObject model = BladeModelManager.getInstance().getModel(modelLocation);

            double baseAlpha = getBaseAlpha(entity, partialTicks);

            int seed = entity.getSeed();

            matrixStackIn.mulPose(Axis.YP.rotationDegrees(seed));

            // 支持自定义大小
            float scale = 0.01f * entity.getSize();
            matrixStackIn.scale(scale, scale, scale);

            Color col = entity.getColor();
            float[] hsb = Color.RGBtoHSB(col.getRed(), col.getGreen(), col.getBlue(), null);
            int baseColor = Color.HSBtoRGB(0.5f + hsb[0], hsb[1], 0.2f/*hsb[2]*/) & 0xFFFFFF;

            // 渲染基础层（5层渐缩）
            try (MSAutoCloser msacB = MSAutoCloser.pushMatrix(matrixStackIn)) {
                for(int l = 0; l < 5; l++) {
                    matrixStackIn.scale(0.95f, 0.95f, 0.95f);

                    BladeRenderState.setCol(baseColor | ((0xFF & (int) (0x66 * baseAlpha)) << 24));
                    BladeRenderState.renderOverridedReverseLuminous(ItemStack.EMPTY, model, "base",
                            this.getTextureLocation(entity), matrixStackIn, bufferIn, packedLightIn);
                }
            }

            // 渲染波动层（3层循环）
            int loop = 3;
            for(int l = 0; l < loop; l++) {
                try (MSAutoCloser msacB = MSAutoCloser.pushMatrix(matrixStackIn)) {
                    float cycleTicks = 15;
                    float wave = (entity.tickCount + (cycleTicks / (float) loop * l) + partialTicks) % cycleTicks;
                    float waveScale = 1.0f + 0.03f * wave;
                    matrixStackIn.scale(waveScale, waveScale, waveScale);

                    BladeRenderState.setCol(baseColor | ((int) (0x88 * ((cycleTicks - wave) / cycleTicks) * baseAlpha) << 24));
                    BladeRenderState.renderOverridedReverseLuminous(ItemStack.EMPTY, model, "base",
                            this.getTextureLocation(entity), matrixStackIn, bufferIn, packedLightIn);
                }
            }

            // 渲染风旋层（5层旋转）
            int windCount = 5;
            for(int l = 0; l < windCount; l++) {
                try (MSAutoCloser msacB = MSAutoCloser.pushMatrix(matrixStackIn)) {

                    matrixStackIn.mulPose(Axis.XP.rotationDegrees((360.0f / windCount) * l));
                    matrixStackIn.mulPose(Axis.YP.rotationDegrees(30.0f));

                    double rotWind = 360.0 / 20.0;

                    double offsetBase = 7;

                    double offset = l * offsetBase;

                    double motionLen = offsetBase * (windCount - 1);

                    double ticks = entity.tickCount + partialTicks + seed;
                    double offsetTicks = ticks + offset;
                    double progress = (offsetTicks % motionLen) / motionLen;

                    double rad = (Math.PI) * 2.0;
                    rad *= progress;

                    float windScale = (float) (0.4 + progress);
                    matrixStackIn.scale(windScale, windScale, windScale);

                    matrixStackIn.mulPose(Axis.ZP.rotationDegrees((float) (rotWind * offsetTicks)));

                    // 修复原版 bug：Math.min(0, ...) 改为 Math.max(0, ...)
                    Color cc = new Color(col.getRed(), col.getGreen(), col.getBlue(),
                            0xff & (int) Math.max(0, 0xFF * Math.sin(rad) * baseAlpha));
                    BladeRenderState.setCol(cc);
                    BladeRenderState.renderOverridedColorWrite(ItemStack.EMPTY, model, "wind",
                            this.getTextureLocation(entity), matrixStackIn, bufferIn, BladeRenderState.MAX_LIGHT);
                }
            }
        }
    }

    /**
     * 计算基础透明度
     * 使用固定的淡入淡出时间，不受 lifetime 影响，确保动画速度恒定
     */
    private static double getBaseAlpha(JudgementCutEntity entity, float partialTicks) {
        int lifetime = entity.getMaxLifeTime();

        double remainingTicks = Math.max(0, (lifetime - entity.tickCount - partialTicks));

        // 使用固定的淡入淡出时间，不受 lifetime 影响，确保动画速度恒定
        double baseAlpha;

        double elapsedTicks = lifetime - remainingTicks;
        double fadeInDuration = 20;  // 固定淡入时间 20tick
        double fadeOutDuration = 40; // 固定淡出时间 40tick

        if (elapsedTicks < fadeInDuration) {
            // 淡入阶段
            double fadeInProgress = elapsedTicks / fadeInDuration;
            baseAlpha = -Math.pow(fadeInProgress - 1, 4.0) + 1.0;
        } else if (remainingTicks < fadeOutDuration) {
            // 淡出阶段
            double fadeOutProgress = remainingTicks / fadeOutDuration;
            baseAlpha = -Math.pow(fadeOutProgress - 1, 4.0) + 1.0;
        } else {
            // 完全显示阶段
            baseAlpha = 1.0;
        }
        return baseAlpha;
    }
}

