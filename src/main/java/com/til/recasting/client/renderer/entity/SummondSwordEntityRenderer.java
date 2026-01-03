package com.til.recasting.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.til.recasting.entity.SummondSwordEntity;
import mods.flammpfeil.slashblade.client.renderer.model.BladeModelManager;
import mods.flammpfeil.slashblade.client.renderer.model.obj.WavefrontObject;
import mods.flammpfeil.slashblade.client.renderer.util.BladeRenderState;
import mods.flammpfeil.slashblade.client.renderer.util.MSAutoCloser;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

/**
 * @Author: til
 * @Description: 幻影剑渲染器
 */
@OnlyIn(Dist.CLIENT)
public class SummondSwordEntityRenderer<T extends SummondSwordEntity> extends EntityRenderer<T> {

    public SummondSwordEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull T entity) {
        return entity.getTexture();
    }

    @Override
    public void render(@NotNull T entity, float entityYaw, float partialTicks, @NotNull PoseStack matrixStack,
                       @NotNull MultiBufferSource bufferIn, int packedLightIn) {

        try (MSAutoCloser msac = MSAutoCloser.pushMatrix(matrixStack)) {
            Entity hits = entity.getHitEntity();
            boolean hasHitEntity = hits != null;

            if (hasHitEntity) {
                // 如果命中实体，跟随实体旋转
                matrixStack.mulPose(Axis.YN.rotationDegrees(Mth.rotLerp(partialTicks, hits.yRotO, hits.getYRot()) - 90));
            } else {
                // 否则使用实体自身的旋转
                matrixStack.mulPose(Axis.YP.rotationDegrees(Mth.rotLerp(partialTicks, entity.yRotO, entity.getYRot()) - 90.0F));
            }

            matrixStack.mulPose(Axis.ZP.rotationDegrees(Mth.rotLerp(partialTicks, entity.xRotO, entity.getXRot())));
            matrixStack.mulPose(Axis.XP.rotationDegrees(entity.getRoll()));

            // 缩放
            float scale = 0.0075f * entity.getSize();
            matrixStack.scale(scale, scale, scale);
            matrixStack.mulPose(Axis.YP.rotationDegrees(90.0F));

            if (hasHitEntity) {
                // 如果命中实体，稍微偏移位置
                matrixStack.translate(0, 0, -100);
            }

            // 渲染模型
            WavefrontObject model = BladeModelManager.getInstance().getModel(entity.getModel());
            BladeRenderState.setCol(entity.getColor().getRGB(), false);
            BladeRenderState.renderOverridedLuminous(ItemStack.EMPTY, model, "ss", getTextureLocation(entity),
                    matrixStack, bufferIn, packedLightIn);
        }
    }
}

