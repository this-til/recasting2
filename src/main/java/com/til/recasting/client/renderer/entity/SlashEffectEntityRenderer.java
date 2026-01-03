package com.til.recasting.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.til.recasting.entity.SlashEffectEntity;
import mods.flammpfeil.slashblade.client.renderer.model.BladeModelManager;
import mods.flammpfeil.slashblade.client.renderer.model.obj.Face;
import mods.flammpfeil.slashblade.client.renderer.model.obj.WavefrontObject;
import mods.flammpfeil.slashblade.client.renderer.util.BladeRenderState;
import mods.flammpfeil.slashblade.client.renderer.util.MSAutoCloser;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

/**
 * @Author: til
 * @Description: 斩击特效渲染器
 */
@OnlyIn(Dist.CLIENT)
public class SlashEffectEntityRenderer<T extends SlashEffectEntity> extends EntityRenderer<T> {

    public SlashEffectEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull T entity) {
        return entity.getTexture();
    }

    /**
     * 获取进度值，子类可以覆盖此方法来自定义进度计算
     * @param entity 实体
     * @param partialTicks 部分刻
     * @return 进度值 (0.0 ~ 1.0)
     */
    protected float getProgress(T entity, float partialTicks) {
        int lifetime = entity.getMaxLifeTime();
        return Math.min(lifetime, (entity.tickCount + partialTicks)) / lifetime;
    }

    @Override
    public void render(@NotNull T entity, float entityYaw, float partialTicks, @NotNull PoseStack matrixStackIn,
                       @NotNull MultiBufferSource bufferIn, int packedLightIn) {

        try (MSAutoCloser msac = MSAutoCloser.pushMatrix(matrixStackIn)) {

            // 设置旋转
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(-Mth.lerp(partialTicks, entity.yRotO, entity.getYRot()) - 90.0F));
            matrixStackIn.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTicks, entity.xRotO, entity.getXRot())));
            matrixStackIn.mulPose(Axis.XP.rotationDegrees(entity.getRoll()));

            // 获取模型
            WavefrontObject model = BladeModelManager.getInstance().getModel(entity.getModel());

            int lifetime = entity.getMaxLifeTime();
            float progress = getProgress(entity, partialTicks);

            // 计算透明度
            double deathTime = lifetime;
            double baseAlpha = (Math.min(deathTime, Math.max(0, (lifetime - (entity.tickCount) - partialTicks))) / deathTime);
            baseAlpha = -Math.pow(baseAlpha - 1, 4.0) + 1.0;

            // 时间相关的旋转
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(entity.getRotationOffset() - 135.0F * progress));

            // 缩放
            matrixStackIn.scale(1, 0.25f, 1);

            float baseScale = 1.2f * entity.getSize();
            matrixStackIn.scale(baseScale, baseScale, baseScale);

            float yscale = 0.03f;
            float scale = Mth.lerp(progress, 0.03f, 0.035f);

            int color = entity.getColor().getRGB() & 0xFFFFFF;
            int alpha = ((0xFF & (int) (0xFF * baseAlpha)) << 24);

            ResourceLocation texture = getTextureLocation(entity);

            // 渲染颜色基础层（写入颜色）
            try (MSAutoCloser msacb = MSAutoCloser.pushMatrix(matrixStackIn)) {
                matrixStackIn.scale(scale, yscale, scale);
                Face.setAlphaOverride(Face.alphaOverrideYZZ);
                Face.setUvOperator(1, 1, 0, -0.35f + progress * -0.15f);
                BladeRenderState.setCol(color | alpha);
                BladeRenderState.renderOverridedColorWrite(ItemStack.EMPTY, model, "base", texture, matrixStackIn,
                        bufferIn, packedLightIn);
            }

            // 渲染外部白色发光层
            try (MSAutoCloser msacb = MSAutoCloser.pushMatrix(matrixStackIn)) {
                float windscale = Mth.lerp(progress, 0.03f, 0.0375f);
                matrixStackIn.scale(windscale, yscale, windscale);
                Face.setAlphaOverride(Face.alphaOverrideYZZ);
                Face.setUvOperator(1, 1, 0, -0.5f + progress * -0.2f);
                BladeRenderState.setCol(0x404040 | alpha);
                BladeRenderState.renderOverridedLuminous(ItemStack.EMPTY, model, "base", texture, matrixStackIn,
                        bufferIn, packedLightIn);
            }

            // 渲染颜色发光层
            try (MSAutoCloser msacb = MSAutoCloser.pushMatrix(matrixStackIn)) {
                matrixStackIn.scale(scale, yscale, scale);
                Face.setAlphaOverride(Face.alphaOverrideYZZ);
                Face.setUvOperator(1, 1, 0, -0.35f + progress * -0.15f);
                BladeRenderState.setCol(color | alpha);
                BladeRenderState.renderOverridedLuminous(ItemStack.EMPTY, model, "base", texture, matrixStackIn, bufferIn,
                        packedLightIn);
            }
        }
    }
}

