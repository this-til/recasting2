package com.til.recasting.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.til.recasting.entity.FinalGlowBlackHoleEntity;
import mods.flammpfeil.slashblade.client.renderer.model.BladeModelManager;
import mods.flammpfeil.slashblade.client.renderer.model.obj.WavefrontObject;
import mods.flammpfeil.slashblade.client.renderer.util.BladeRenderState;
import mods.flammpfeil.slashblade.client.renderer.util.MSAutoCloser;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

/**
 * 末辉黑洞：只渲染次元斩模型的黑球 {@code base}，不画 wind 刀刃。
 */
@OnlyIn(Dist.CLIENT)
public class FinalGlowBlackHoleEntityRender<E extends FinalGlowBlackHoleEntity> extends JudgementCutEntityRenderer<E> {

    public FinalGlowBlackHoleEntityRender(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(
            @NotNull E entity,
            float entityYaw,
            float partialTicks,
            @NotNull PoseStack matrixStackIn,
            @NotNull MultiBufferSource bufferIn,
            int packedLightIn
    ) {
        try (MSAutoCloser msac = MSAutoCloser.pushMatrix(matrixStackIn)) {
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, entity.yRotO, entity.getYRot()) - 90.0F));
            matrixStackIn.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTicks, entity.xRotO, entity.getXRot())));

            ResourceLocation modelLocation = entity.getModel();
            WavefrontObject model = BladeModelManager.getInstance().getModel(modelLocation);

            matrixStackIn.mulPose(Axis.YP.rotationDegrees(entity.getSeed()));
            float horizon = entity.horizonRadius(partialTicks);
            if (horizon <= 0.05f) {
                return;
            }
            float scale = 0.01f * horizon;
            matrixStackIn.scale(scale, scale, scale);

            Color col = entity.getColor();
            float[] hsb = Color.RGBtoHSB(col.getRed(), col.getGreen(), col.getBlue(), null);
            int baseColor = Color.HSBtoRGB(0.5f + hsb[0], hsb[1], 0.2f) & 0xFFFFFF;

            try (MSAutoCloser msacB = MSAutoCloser.pushMatrix(matrixStackIn)) {
                for(int l = 0; l < 5; l++) {
                    matrixStackIn.scale(0.95f, 0.95f, 0.95f);
                    BladeRenderState.setCol(baseColor | (0x66 << 24));
                    BladeRenderState.renderOverridedReverseLuminous(
                            ItemStack.EMPTY,
                            model,
                            "base",
                            this.getTextureLocation(entity),
                            matrixStackIn,
                            bufferIn,
                            packedLightIn
                    );
                }
            }
        }
    }
}
