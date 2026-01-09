package com.til.recasting.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Entity;

/**
 * 实体渲染扩展接口
 * 允许为实体添加额外的渲染逻辑
 */
@FunctionalInterface
public interface EntityRenderExtension {

    /**
     * 渲染实体的额外内容
     * 
     * @param entity 要渲染的实体
     * @param partialTicks 部分刻度（用于平滑动画）
     * @param poseStack 矩阵栈
     * @param bufferSource 缓冲区源
     * @param packedLight 打包的光照值
     */
    void render(Entity entity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight);

    /**
     * 检查是否应该为此实体渲染
     * 默认返回true，可以重写以添加条件
     * 
     * @param entity 要检查的实体
     * @return 如果应该渲染返回true
     */
    default boolean shouldRender(Entity entity) {
        return true;
    }

    /**
     * 渲染优先级（数值越小越先渲染）
     * 默认为0
     * 
     * @return 优先级
     */
    default int getPriority() {
        return 0;
    }
}

