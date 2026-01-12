package com.til.recasting.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.til.recasting.client.renderer.RenderStateManage;
import com.til.recasting.entity.MatrixEntity;
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
 * Matrix 实体渲染器
 */
@OnlyIn(Dist.CLIENT)
public class MatrixEntityRender<E extends MatrixEntity> extends EntityRenderer<E> {

    public MatrixEntityRender(EntityRendererProvider.Context context) {
        super(context);
    }

    @Nullable
    @Override
    public ResourceLocation getTextureLocation(@NotNull E entity) {
        return entity.getTexture();
    }

    @Override
    public void render(@NotNull E entity, float entityYaw, float partialTicks, @NotNull PoseStack matrixStack,
                       @NotNull MultiBufferSource bufferIn, int packedLightIn) {
        try (MSAutoCloser msac = MSAutoCloser.pushMatrix(matrixStack)) {
            float time = entity.tickCount + partialTicks;
            time = time / 40;
            matrixStack.mulPose(Axis.YP.rotation(time));
            float scale = entity.getSize() * 0.0075f;
            matrixStack.scale(scale, scale, scale);
            WavefrontObject model = BladeModelManager.getInstance().getModel(entity.getModel());
            BladeRenderState.setCol(entity.getColor());
            BladeRenderState.renderOverrided(ItemStack.EMPTY, model, "ss", getTextureLocation(entity), matrixStack, bufferIn, BladeRenderState.MAX_LIGHT, RenderStateManage::mackLuminous, true);
        }
    }
}

