package com.til.recasting.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.til.recasting.entity.SlashEffectEntity;
import mods.flammpfeil.slashblade.capability.concentrationrank.IConcentrationRank.ConcentrationRanks;
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
 * 斩击特效实体渲染器
 * 参考 SlashBlade 的 SlashEffectRenderer 实现
 */
@OnlyIn(Dist.CLIENT)
public class SlashEffectEntityRenderer<T extends SlashEffectEntity> extends EntityRenderer<T> {

    public SlashEffectEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @NotNull
    @Override
    public ResourceLocation getTextureLocation(@NotNull T entity) {
        return entity.getTexture();
    }

    @Override
    public void render(T entity, float entityYaw, float partialTicks,
                       @NotNull PoseStack matrixStackIn, @NotNull MultiBufferSource bufferIn, int packedLightIn) {

        try (MSAutoCloser msac = MSAutoCloser.pushMatrix(matrixStackIn)) {

            coverPose(entity, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);

            WavefrontObject model = BladeModelManager.getInstance().getModel(entity.getModel());


            int lifetime = entity.getMaxLifeTime();

            int ticksExisted = renderTime(entity);
            partialTicks = ofPartialTicks(partialTicks);

            float progress = Math.min(lifetime, (ticksExisted + partialTicks)) / lifetime;

            double deathTime = lifetime;
            double baseAlpha = (Math.min(deathTime, Math.max(0, (lifetime - (entity.tickCount) - partialTicks)))
                    / deathTime);
            baseAlpha = -Math.pow(baseAlpha - 1, 4.0) + 1.0;

            // 旋转动画
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(entity.getRotationOffset() - 135.0F * progress));

            matrixStackIn.scale(1, 0.25f, 1);

            float baseScale = 1.2f * entity.getSize();
            matrixStackIn.scale(baseScale, baseScale, baseScale);

            float yscale = 0.03f;
            float scale = Mth.lerp(progress, 0.03f, 0.035f);

            int color = entity.getColor().getRGB() & 0xFFFFFF;

            ConcentrationRanks rank = getRank(entity);

            // 等级颜色覆盖
            if (rank.level < ConcentrationRanks.C.level) {
                color = 0x555555;
            }

            ResourceLocation rl = getTextureLocation(entity);

            int alpha = ((0xFF & (int) (0xFF * baseAlpha)) << 24);

            // 黑色内层（S 等级及以上）
            if (ConcentrationRanks.S.level <= rank.level) {
                try (MSAutoCloser msacb = MSAutoCloser.pushMatrix(matrixStackIn)) {
                    float windscale = Mth.lerp(progress, 0.035f, 0.03f);
                    matrixStackIn.scale(windscale, yscale, windscale);
                    Face.setAlphaOverride(Face.alphaOverrideYZZ);
                    Face.setUvOperator(1, 1, 0, -0.8f + progress * 0.3f);
                    BladeRenderState.setCol(0x222222 | alpha);
                    BladeRenderState.renderOverridedColorWrite(ItemStack.EMPTY, model, "base", rl, matrixStackIn,
                            bufferIn, packedLightIn);
                }
            }

            // 颜色基础层（D 等级及以上）
            if (ConcentrationRanks.D.level <= rank.level) {
                try (MSAutoCloser msacb = MSAutoCloser.pushMatrix(matrixStackIn)) {
                    matrixStackIn.scale(scale, yscale, scale);
                    Face.setAlphaOverride(Face.alphaOverrideYZZ);
                    Face.setUvOperator(1, 1, 0, -0.35f + progress * -0.15f);
                    BladeRenderState.setCol(color | alpha);
                    BladeRenderState.renderOverridedColorWrite(ItemStack.EMPTY, model, "base", rl, matrixStackIn,
                            bufferIn, packedLightIn);
                }
            }

            // 白色外层（B 等级及以上）
            if (ConcentrationRanks.B.level <= rank.level) {
                try (MSAutoCloser msacb = MSAutoCloser.pushMatrix(matrixStackIn)) {
                    float windscale = Mth.lerp(progress, 0.03f, 0.0375f);
                    matrixStackIn.scale(windscale, yscale, windscale);
                    Face.setAlphaOverride(Face.alphaOverrideYZZ);
                    Face.setUvOperator(1, 1, 0, -0.5f + progress * -0.2f);
                    BladeRenderState.setCol(0x404040 | alpha);
                    BladeRenderState.renderOverridedLuminous(ItemStack.EMPTY, model, "base", rl, matrixStackIn,
                            bufferIn, packedLightIn);
                }
            }

            // 颜色发光层
            try (MSAutoCloser msacb = MSAutoCloser.pushMatrix(matrixStackIn)) {
                matrixStackIn.scale(scale, yscale, scale);
                Face.setAlphaOverride(Face.alphaOverrideYZZ);
                Face.setUvOperator(1, 1, 0, -0.35f + progress * -0.15f);
                BladeRenderState.setCol(color | alpha);
                BladeRenderState.renderOverridedLuminous(ItemStack.EMPTY, model, "base", rl, matrixStackIn, bufferIn,
                        packedLightIn);
            }
        }
    }


    protected void coverPose(T entity, float entityYaw, float partialTicks, @NotNull PoseStack matrixStackIn, @NotNull MultiBufferSource bufferIn, int packedLightIn) {

        matrixStackIn.mulPose(Axis.YP.rotationDegrees(-Mth.lerp(partialTicks, entity.yRotO, entity.getYRot()) - 90.0F));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTicks, entity.xRotO, entity.getXRot())));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(entity.getRoll()));

    }

    /**
     * 获取集中力等级
     * 可被子类重写以自定义等级
     */
    protected ConcentrationRanks getRank(T entity) {
        // 默认为 S 等级，显示完整效果
        return ConcentrationRanks.S;
    }

    protected int renderTime(T t) {
        return t.tickCount;
    }

    protected float ofPartialTicks(float partialTicks) {
        return partialTicks;
    }
}

