package com.til.recasting.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.til.recasting.mixin_api.IEntitySize;
import mods.flammpfeil.slashblade.client.renderer.model.BladeModelManager;
import mods.flammpfeil.slashblade.client.renderer.model.obj.WavefrontObject;
import mods.flammpfeil.slashblade.client.renderer.util.BladeRenderState;
import mods.flammpfeil.slashblade.client.renderer.util.MSAutoCloser;
import mods.flammpfeil.slashblade.entity.EntityAbstractSummonedSword;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@OnlyIn(Dist.CLIENT)
@Mixin(targets = "mods.flammpfeil.slashblade.client.renderer.entity.SummonedSwordRenderer", remap = false)
public abstract class SummonedSwordRendererMixin {

    @Shadow
    public abstract @NotNull ResourceLocation getTextureLocation(EntityAbstractSummonedSword entity);

    /**
     * @author til
     * @reason 修正渲染位置
     */
    @Overwrite
    public void render(EntityAbstractSummonedSword entity, float entityYaw, float partialTicks, @NotNull PoseStack matrixStack, @NotNull MultiBufferSource bufferIn, int packedLightIn) {
        try (MSAutoCloser msac = MSAutoCloser.pushMatrix(matrixStack)) {
            Entity hits = entity.getHitEntity();
            boolean hasHitEntity = hits != null;
            // 无论是否命中实体，都使用剑实体自己的朝向
            // 剑的朝向在 tick 中已经正确设置为跟随实体的相对朝向
            matrixStack.mulPose(Axis.YP.rotationDegrees(Mth.rotLerp(partialTicks, entity.yRotO, entity.getYRot()) - 90.0F));
            matrixStack.mulPose(Axis.ZP.rotationDegrees(Mth.rotLerp(partialTicks, entity.xRotO, entity.getXRot())));
            matrixStack.mulPose(Axis.XP.rotationDegrees(entity.getRoll()));

            // 使用自定义的 size 字段控制缩放
            float size = 1.0f;
            if (entity instanceof IEntitySize sizeEntity) {
                size = sizeEntity.getRecasting$size();
            }
            float scale = 0.0075F * size;
            matrixStack.scale(scale, scale, scale);
            matrixStack.mulPose(Axis.YP.rotationDegrees(90.0F));

            if (hasHitEntity) {
                matrixStack.translate(0.0F, 0.0F, -100.0F);
            }

            WavefrontObject model = BladeModelManager.getInstance().getModel(entity.getModelLoc());
            BladeRenderState.setCol(entity.getColor(), false);
            BladeRenderState.renderOverridedLuminous(ItemStack.EMPTY, model, "ss", this.getTextureLocation(entity), matrixStack, bufferIn, packedLightIn);
        }

    }
}


