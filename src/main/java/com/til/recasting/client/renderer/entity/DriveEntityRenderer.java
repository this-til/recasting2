package com.til.recasting.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.til.recasting.entity.DriveEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

/**
 * Drive 实体渲染器
 * 继承自 SlashEffectEntityRenderer，自定义渲染时间
 */
@OnlyIn(Dist.CLIENT)
public class DriveEntityRenderer extends SlashEffectEntityRenderer<DriveEntity> {

    public DriveEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(DriveEntity entity, float entityYaw, float partialTicks, 
                       @NotNull PoseStack matrixStack, @NotNull MultiBufferSource bufferIn, int packedLightIn) {
        super.render(entity, entityYaw, partialTicks, matrixStack, bufferIn, packedLightIn);
    }

    /**
     * 重写渲染时间，使用实体最大生命周期的 75%
     * 这样动画会更快完成
     */
    @Override
    protected int getRenderTime(DriveEntity entity) {
        return (int) (entity.getMaxLifeTime() * 0.75f);
    }

    /**
     * 重写部分 tick 偏移
     * 返回 0 表示不使用偏移
     */
    @Override
    protected float getPartialTicksOffset(float partialTicks) {
        return 0;
    }
}

