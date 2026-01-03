package com.til.recasting.client.renderer.entity;

import com.til.recasting.entity.DriveEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @Author: til
 * @Description: 剑气渲染器 (继承自 SlashEffectEntityRenderer，固定 progress 为 0.5)
 */
@OnlyIn(Dist.CLIENT)
public class DriveEntityRenderer<T extends DriveEntity> extends SlashEffectEntityRenderer<T> {

    public DriveEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected float getProgress(T entity, float partialTicks) {
        // DriveEntity 固定 progress 为 0.5，不随时间变化
        return 0.5f;
    }
}

