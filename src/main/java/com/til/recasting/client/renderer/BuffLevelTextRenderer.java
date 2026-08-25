package com.til.recasting.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.til.recasting.capability.IBuffStackData;
import com.til.recasting.client.registry.BuffLevelRendererRegistry;
import com.til.recasting.client.registry.instance.BuffLevelRenderConfig;
import com.til.recasting.registry.RecastingAttachments;
import com.til.recasting.registry.instance.BuffType;
import mods.flammpfeil.slashblade.client.renderer.util.MSAutoCloser;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

/**
 * Buff层数文本渲染器，在实体名称标签上方显示 Buff 层数信息。
 */
public class BuffLevelTextRenderer implements EntityRenderExtension {

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
        if (!(entity instanceof LivingEntity livingEntity)) {
            return;
        }

        IBuffStackData buffData = RecastingAttachments.buffStackData(entity);

        List<BuffInfo> buffsToRender = new ArrayList<>();
        for (BuffLevelRenderConfig config : BuffLevelRendererRegistry.REGISTRY) {
            BuffType buffType = config.getBuffType();
            if (buffType == null) {
                continue;
            }
            int level = buffData.getLevel(buffType, entity.level());

            if (level != 0) {
                String translationKey = config.getTranslationKey();
                String buffName = Component.translatable(translationKey).getString();

                int maxLevel = buffType.getMaxLevel();
                String displayText;
                if (maxLevel > 0) {
                    displayText = buffName + ": " + level + "/" + maxLevel;
                } else {
                    displayText = buffName + ": " + level;
                }

                buffsToRender.add(new BuffInfo(displayText, buffType));
            }
        }

        if (buffsToRender.isEmpty()) {
            return;
        }

        renderBuffTexts(livingEntity, buffsToRender, poseStack, bufferSource, packedLight);
    }

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
            float entityHeight = entity.getBbHeight();
            float nameTagOffset = 0.5F;
            float lineHeight = 0.25F;
            float startY = entityHeight + nameTagOffset + lineHeight;

            poseStack.translate(0.0D, startY, 0.0D);
            poseStack.mulPose(minecraft.getEntityRenderDispatcher().cameraOrientation());

            float scale = 0.025F;
            poseStack.scale(scale, -scale, scale);

            Matrix4f matrix = poseStack.last().pose();
            float backgroundOpacity = minecraft.options.getBackgroundOpacity(0.25F);
            int backgroundColor = (int) (backgroundOpacity * 255.0F) << 24;

            float yOffset = 0.0F;
            for (BuffInfo buffInfo : buffInfos) {
                String text = buffInfo.displayText;
                float textWidth = font.width(text);
                float x = -textWidth / 2.0F;

                // 对齐原版名称标签：先透视层再实色层，保证可读
                font.drawInBatch(
                        text,
                        x,
                        yOffset,
                        0x20FFFFFF,
                        false,
                        matrix,
                        bufferSource,
                        Font.DisplayMode.SEE_THROUGH,
                        backgroundColor,
                        packedLight
                );
                font.drawInBatch(
                        text,
                        x,
                        yOffset,
                        0xFFFFFFFF,
                        false,
                        matrix,
                        bufferSource,
                        Font.DisplayMode.NORMAL,
                        0,
                        packedLight
                );

                yOffset += 10.0F;
            }
        }
    }

    @Override
    public int getPriority() {
        return 1000;
    }
}
