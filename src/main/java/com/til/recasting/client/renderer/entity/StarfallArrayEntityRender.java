package com.til.recasting.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.til.recasting.client.renderer.RenderStateManage;
import com.til.recasting.entity.StarfallArrayEntity;
import mods.flammpfeil.slashblade.client.renderer.model.BladeModelManager;
import mods.flammpfeil.slashblade.client.renderer.model.obj.WavefrontObject;
import mods.flammpfeil.slashblade.client.renderer.util.BladeRenderState;
import mods.flammpfeil.slashblade.client.renderer.util.MSAutoCloser;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 群星坠落阵渲染：组名 model，基准缩放 0.02。
 */
@OnlyIn(Dist.CLIENT)
public class StarfallArrayEntityRender<E extends StarfallArrayEntity> extends EntityRenderer<E> {

    private static final float BASE_SCALE = 0.02f;
    private static final int ALPHA = (int) (0xFF * 0.65f);

    public StarfallArrayEntityRender(EntityRendererProvider.Context context) {
        super(context);
    }

    @Nullable
    @Override
    public ResourceLocation getTextureLocation(@NotNull E entity) {
        return entity.getTexture();
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
        try (MSAutoCloser msac = MSAutoCloser.pushMatrix(matrixStack)) {
            float time = entity.tickCount + partialTicks;
            matrixStack.mulPose(Axis.YP.rotationDegrees(time));
            float scale = BASE_SCALE * entity.getSize();
            matrixStack.scale(scale, scale, scale);
            WavefrontObject model = BladeModelManager.getInstance().getModel(entity.getModel());
            int color = entity.getColor().getRGB() & 0x00FFFFFF;
            BladeRenderState.setCol(color | (ALPHA << 24));
            BladeRenderState.renderOverrided(
                    ItemStack.EMPTY,
                    model,
                    "model",
                    getTextureLocation(entity),
                    matrixStack,
                    bufferIn,
                    BladeRenderState.MAX_LIGHT,
                    RenderStateManage::mackModel,
                    true
            );
        }
    }
}
