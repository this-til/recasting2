package com.til.recasting.client.handler;

import com.mojang.blaze3d.vertex.PoseStack;
import com.til.recasting.Recasting;
import com.til.recasting.client.registry.EntityRenderExtensionRegistry;
import com.til.recasting.client.renderer.EntityRenderExtension;
import lombok.extern.log4j.Log4j2;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 实体渲染扩展处理器
 * 负责在实体渲染时应用注册的渲染扩展
 */
@Log4j2
@Mod.EventBusSubscriber(modid = Recasting.MODID, value = Dist.CLIENT)
public class EntityRenderExtensionHandler {

    /**
     * 缓存已排序的渲染扩展列表
     */
    private static List<EntityRenderExtension> sortedExtensions = null;

    /**
     * 获取所有已排序的渲染扩展
     * 按优先级排序（数值越小越先渲染）
     */
    private static List<EntityRenderExtension> getSortedExtensions() {
        if (sortedExtensions == null) {
            var registry = EntityRenderExtensionRegistry.REGISTRY.get();
            if (registry == null) {
                sortedExtensions = Collections.emptyList();
            } else {
                sortedExtensions = registry.getValues().stream()
                        .sorted(Comparator.comparingInt(EntityRenderExtension::getPriority))
                        .collect(Collectors.toList());
            }
        }
        return sortedExtensions;
    }

    /**
     * 清除缓存（用于重新加载）
     */
    public static void clearCache() {
        sortedExtensions = null;
    }

    /**
     * 在生物实体渲染后应用渲染扩展
     */
    @SubscribeEvent
    public static void onRenderLivingPost(RenderLivingEvent.Post<?, ?> event) {
        LivingEntity entity = event.getEntity();
        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource bufferSource = event.getMultiBufferSource();
        int packedLight = event.getPackedLight();
        float partialTicks = event.getPartialTick();

        // 应用所有适用的渲染扩展
        applyRenderExtensions(entity, partialTicks, poseStack, bufferSource, packedLight);
    }

    /**
     * 应用所有适用的渲染扩展到实体
     * 
     * @param entity 要渲染的实体
     * @param partialTicks 部分刻度
     * @param poseStack 矩阵栈
     * @param bufferSource 缓冲区源
     * @param packedLight 打包的光照值
     */
    public static void applyRenderExtensions(Entity entity, float partialTicks, PoseStack poseStack, 
                                            MultiBufferSource bufferSource, int packedLight) {
        List<EntityRenderExtension> extensions = getSortedExtensions();
        
        for (EntityRenderExtension extension : extensions) {
            try {
                if (extension.shouldRender(entity)) {
                    poseStack.pushPose();
                    extension.render(entity, partialTicks, poseStack, bufferSource, packedLight);
                    poseStack.popPose();
                }
            } catch (Exception e) {
                log.error("Error applying render extension to entity {}: {}", 
                        entity.getClass().getSimpleName(), e.getMessage());
            }
        }
    }

    /**
     * 手动注册渲染扩展（用于动态注册）
     * 注意：这会清除缓存
     * 
     * @param extension 要添加的渲染扩展
     */
    public static void registerExtension(EntityRenderExtension extension) {
        clearCache();
    }
}

