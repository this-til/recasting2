package com.til.recasting.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.til.recasting.client.renderer.RenderStateManage;
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
 * 召唤剑实体渲染器
 */
@OnlyIn(Dist.CLIENT)
public class SummondSwordEntityRenderer<E extends SummondSwordEntity> extends EntityRenderer<E> {

    public SummondSwordEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @NotNull
    @Override
    public ResourceLocation getTextureLocation(@NotNull SummondSwordEntity entity) {
        return entity.getTexture();
    }


    @Override
    public void render(SummondSwordEntity entity, float entityYaw, float partialTicks, @NotNull PoseStack matrixStack, @NotNull MultiBufferSource bufferIn, int packedLightIn) {
        if (entity.tickCount < 2) {
            return;
        }

        try (MSAutoCloser msac = MSAutoCloser.pushMatrix(matrixStack)) {

            matrixStack.mulPose(Axis.YP.rotationDegrees(-Mth.rotLerp(partialTicks, entity.yRotO, entity.getYRot())));
            matrixStack.mulPose(Axis.XP.rotationDegrees(Mth.rotLerp(partialTicks, entity.xRotO, entity.getXRot())));
            matrixStack.mulPose(Axis.ZP.rotationDegrees(entity.getRoll()));


            //matrixStack.mulPose(Axis.YP.rotationDegrees(-Mth.lerp(partialTicks, entity.xRotO, entity.getXRot())));
            //matrixStack.mulPose(Axis.XP.rotationDegrees(Mth.lerp(partialTicks, entity.yRotO, entity.getYRot())));
            //matrixStack.mulPose(Axis.ZP.rotationDegrees(entity.getRoll()));

            float scale = 0.0075f * entity.getSize();
            matrixStack.scale(scale, scale, scale);


            if (entity.getHitEntity() != null) {
                matrixStack.translate(0, 0, -100);
            }

            WavefrontObject model = BladeModelManager.getInstance().getModel(entity.getModel());
            BladeRenderState.setCol(entity.getColor().getRGB(), false);
            BladeRenderState.renderOverrided(ItemStack.EMPTY, model, "ss", this.getTextureLocation(entity), matrixStack, bufferIn, packedLightIn, RenderStateManage::mackLuminous, false);
        }
    }
}

