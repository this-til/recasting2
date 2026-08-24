package com.til.recasting.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.til.recasting.entity.DriveEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

/**
 * Drive 实体渲染器
 * 继承自 SlashEffectEntityRenderer，自定义渲染时间
 */
@OnlyIn(Dist.CLIENT)
public class DriveEntityRenderer<E extends DriveEntity> extends SlashEffectEntityRenderer<E> {

    public DriveEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected int renderTime(DriveEntity e) {
        return (int) (e.getMaxLifeTime() * 0.75f);
    }

    @Override
    protected float ofPartialTicks(float partialTicks) {
        return 0;
    }

    @Override
    protected void coverPose(DriveEntity entity, float entityYaw, float partialTicks, @NotNull PoseStack matrixStackIn, @NotNull MultiBufferSource bufferIn, int packedLightIn) {

        matrixStackIn.mulPose(Axis.YP.rotationDegrees(-Mth.lerp(partialTicks, entity.yRotO, entity.getYRot()) - 90.0F));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(-Mth.lerp(partialTicks, entity.xRotO, entity.getXRot())));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(entity.getRoll()));

    }
}

