package com.til.recasting.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.til.recasting.capability.IBuffStackData;
import com.til.recasting.client.registry.BuffLevelRendererRegistry;
import com.til.recasting.client.registry.instance.BuffLevelRenderConfig;
import com.til.recasting.handler.CapabilityRegistryHandler;
import com.til.recasting.registry.instance.BuffType;
import mods.flammpfeil.slashblade.client.renderer.util.MSAutoCloser;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.util.LazyOptional;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

/**
 * Buff层数文本渲染器
 * 在实体名称标签上方显示 Buff 层数信息
 */
public class BuffLevelTextRenderer implements EntityRenderExtension {

    /**
     * Buff信息数据类
     */
    private static class BuffInfo {
        final String displayText;
        final BuffType buffType;

        BuffInfo(String displayText, BuffType buffType) {
            this.displayText = displayText;
            this.buffType = buffType;
        }
    }

    @Override
    public void render(Entity entity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        // 只处理生物实体
        if (!(entity instanceof LivingEntity livingEntity)) {
            return;
        }

        // 获取实体的 buff 数据
        LazyOptional<IBuffStackData> capability = entity.getCapability(CapabilityRegistryHandler.BUFF_STACK_DATA);
        if (!capability.isPresent()) {
            return;
        }

        //noinspection DataFlowIssue
        IBuffStackData buffData = capability.orElse(null);
        if (buffData == null) {
            return;
        }

        // 收集需要显示的 Buff 信息
        List<BuffInfo> buffsToRender = new ArrayList<>();
        var registry = BuffLevelRendererRegistry.REGISTRY.get();
        if (registry == null) {
            return;
        }

        for(BuffLevelRenderConfig config : registry.getValues()) {
            BuffType buffType = config.getBuffType();
            if (buffType == null) {
                continue;
            }
            int level = buffData.getLevel(buffType, entity.level());

            // 只显示层数不为 0 的 Buff
            if (level != 0) {
                String translationKey = config.getTranslationKey();
                String buffName = Component.translatable(translationKey).getString();

                // 格式：{名称}:{当前层级}/{最大层级}
                int maxLevel = buffType.getMaxLevel();
                String displayText;
                if (maxLevel > 0) {
                    displayText = buffName + ": " + level + "/" + maxLevel;
                } else {
                    // 如果没有最大层级限制，只显示当前层级
                    displayText = buffName + ": " + level;
                }

                buffsToRender.add(new BuffInfo(displayText, buffType));
            }
        }

        // 如果没有要显示的 Buff，直接返回
        if (buffsToRender.isEmpty()) {
            return;
        }

        // 渲染文本
        renderBuffTexts(livingEntity, buffsToRender, poseStack, bufferSource, packedLight);
    }

    /**
     * 渲染 Buff 文本列表
     */
    private void renderBuffTexts(
            LivingEntity entity,
            List<BuffInfo> buffInfos,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight
    ) {
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;

        try (MSAutoCloser msac = MSAutoCloser.pushMatrix(poseStack)) {
            // 计算实体头顶位置
            // 名称标签通常在实体高度 + 0.5 的位置
            float entityHeight = entity.getBbHeight();
            float nameTagOffset = 0.5F;
            float baseYOffset = entityHeight + nameTagOffset;

            // 从名称标签上方开始渲染（每个 Buff 占据 0.25 的高度）
            float lineHeight = 0.25F;
            float startY = baseYOffset + lineHeight;

            // 移动到实体头顶
            poseStack.translate(0.0D, startY, 0.0D);

            // 面向玩家视角
            poseStack.mulPose(minecraft.getEntityRenderDispatcher().cameraOrientation());

            // 缩放文本（使其更小更精致）
            float scale = 0.025F;
            poseStack.scale(-scale, -scale, scale);

            Matrix4f matrix = poseStack.last().pose();

            // 从上到下渲染每个 Buff
            int yOffset = 0;
            for(BuffInfo buffInfo : buffInfos) {
                String text = buffInfo.displayText;
                float textWidth = font.width(text);
                float x = -textWidth / 2.0F;

                // 渲染半透明背景（提高可读性）
                int backgroundOpacity = 64; // 25% 不透明度
                int backgroundColor = backgroundOpacity << 24; // ARGB格式
                font.drawInBatch(
                        text,
                        x,
                        yOffset,
                        0xFFFFFF, // 白色文本
                        false,
                        matrix,
                        bufferSource,
                        Font.DisplayMode.NORMAL,
                        backgroundColor,
                        packedLight
                );

                // 移动到下一行
                yOffset += 10; // 每行间距 10 像素
            }
        }
    }

    @Override
    public int getPriority() {
        // 设置较高的优先级，确保在其他渲染之后执行
        return 1000;
    }
}

