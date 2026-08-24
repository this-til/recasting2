package com.til.recasting.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.til.recasting.client.renderer.RenderStateManage;
import com.til.recasting.entity.TrackingSummondSwordEntity;
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

/**
 * 对应旧版 {@code RenderSummonedBlade}：sb 组 + 绕 Y 轴 time*60；命中后用 hitTime 冻结。
 */
@OnlyIn(Dist.CLIENT)
public class TrackingSummondSwordEntityRenderer extends EntityRenderer<TrackingSummondSwordEntity> {

    public TrackingSummondSwordEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @NotNull
    @Override
    public ResourceLocation getTextureLocation(@NotNull TrackingSummondSwordEntity entity) {
        return entity.getTexture();
    }

    @Override
    public void render(
            @NotNull TrackingSummondSwordEntity entity,
            float entityYaw,
            float partialTicks,
            @NotNull PoseStack matrixStack,
            @NotNull MultiBufferSource bufferIn,
            int packedLightIn
    ) {
        if (entity.tickCount < 2) {
            return;
        }

        try (MSAutoCloser msac = MSAutoCloser.pushMatrix(matrixStack)) {
            matrixStack.mulPose(Axis.YP.rotationDegrees(-Mth.rotLerp(partialTicks, entity.yRotO, entity.getYRot())));
            matrixStack.mulPose(Axis.XP.rotationDegrees(Mth.rotLerp(partialTicks, entity.xRotO, entity.getXRot())));
            matrixStack.mulPose(Axis.ZP.rotationDegrees(entity.getRoll()));

            // 旧版 RenderSummonedBlade：飞行中 gameTime%6*60；命中后 hitTime 冻结
            if (entity.hitTime != 0L) {
                float time = (entity.hitTime % 6L) + entity.hitStopFactor;
                matrixStack.mulPose(Axis.YP.rotationDegrees(time * 60.0f));
            } else if (entity.getActionType() == TrackingSummondSwordEntity.ActionType.FLYING) {
                float time = (entity.level().getGameTime() % 6L) + partialTicks;
                matrixStack.mulPose(Axis.YP.rotationDegrees(time * 60.0f));
            }

            float scale = 0.0075f * entity.getSize();
            matrixStack.scale(scale, scale, scale);

            if (entity.getHitEntity() != null) {
                matrixStack.translate(0, 0, -100);
            }

            WavefrontObject model = BladeModelManager.getInstance().getModel(entity.getModel());
            BladeRenderState.setCol(entity.getColor().getRGB(), false);
            BladeRenderState.renderOverrided(
                    ItemStack.EMPTY,
                    model,
                    "sb",
                    getTextureLocation(entity),
                    matrixStack,
                    bufferIn,
                    packedLightIn,
                    RenderStateManage::mackLuminous,
                    false
            );
        }
    }
}
