package com.til.recasting.client.registry;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.til.recasting.Recasting;
import com.til.recasting.capability.IBuffStackData;
import com.til.recasting.client.renderer.BuffLevelTextRenderer;
import com.til.recasting.client.renderer.EntityRenderExtension;
import com.til.recasting.client.renderer.RenderStateManage;
import com.til.recasting.constant.R;
import com.til.recasting.entity.SummondSwordEntity;
import com.til.recasting.handler.CapabilityRegistryHandler;
import com.til.recasting.registry.RecastingBuffTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;
import org.joml.Matrix4f;

import java.awt.*;
import java.util.function.Supplier;

/**
 * 实体渲染扩展注册表（客户端专用）
 * 用于注册和管理实体渲染扩展
 */
public class EntityRenderExtensionRegistry {

    /**
     * 实体渲染扩展注册表键
     */
    public static final ResourceKey<Registry<EntityRenderExtension>> ENTITY_RENDER_EXTENSION_REGISTRY_KEY =
            ResourceKey.createRegistryKey(Recasting.prefix("entity_render_extension"));

    /**
     * 实体渲染扩展注册表
     */
    public static final DeferredRegister<EntityRenderExtension> ENTITY_RENDER_EXTENSIONS =
            DeferredRegister.create(ENTITY_RENDER_EXTENSION_REGISTRY_KEY, Recasting.MODID);

    /**
     * 实体渲染扩展注册表实例
     */
    public static final Supplier<IForgeRegistry<EntityRenderExtension>> REGISTRY =
            ENTITY_RENDER_EXTENSIONS.makeRegistry(() -> new RegistryBuilder<EntityRenderExtension>()
                    .setDefaultKey(Recasting.prefix("default"))
            );

    // ==================== 预定义的渲染扩展 ====================

    /**
     * 星闪渲染扩展
     * 为拥有星闪buff的实体渲染层级效果
     */
    public static final RegistryObject<EntityRenderExtension> STAR_BLINK = ENTITY_RENDER_EXTENSIONS.register(
            "star_blink",
            () -> new EntityRenderExtension.BuffLevelRender(
                    R.Models.Mark.starBlink$obj,
                    new Color(210, 118, 246).getRGB(),
                    RecastingBuffTypes.STAR_BLINK
            )
    );

    public static final RegistryObject<EntityRenderExtension> SPIRAL_SPECIAL = ENTITY_RENDER_EXTENSIONS.register(
            "spiral_special",
            () -> new EntityRenderExtension.BuffLevelRender(
                    R.Models.Mark.spiralSpecial$obj,
                    new Color(118, 169, 246).getRGB(),
                    RecastingBuffTypes.SWORD_MOMENTUM
            )
    );

    /**
     * 雷光渲染扩展
     * 为拥有雷光buff的实体渲染层级效果
     */
    public static final RegistryObject<EntityRenderExtension> THUNDER_LIGHT = ENTITY_RENDER_EXTENSIONS.register(
            "thunder_light",
            () -> new EntityRenderExtension.BuffLevelRender(
                    R.Models.Mark.thunderLight$obj,
                    new Color(118, 169, 246).getRGB(),
                    RecastingBuffTypes.THUNDER_LIGHT
            )
    );

    /**
     * 灵魂燃烧渲染扩展
     * 为拥有灵魂燃烧buff的实体渲染蓝色火焰效果
     */
    public static final RegistryObject<EntityRenderExtension> SOUL_BURN = ENTITY_RENDER_EXTENSIONS.register(
            "soul_burn",
            SoulBurnRenderExtension::new
    );

    /**
     * Buff层数文本渲染扩展
     * 在实体名称标签上方显示 Buff 层数信息
     */
    public static final RegistryObject<EntityRenderExtension> BUFF_LEVEL_TEXT = ENTITY_RENDER_EXTENSIONS.register(
            "buff_level_text",
            BuffLevelTextRenderer::new
    );

    /**
     * 灵魂燃烧渲染扩展
     * 为拥有灵魂燃烧buff的实体渲染蓝色火焰效果
     */
    public static class SoulBurnRenderExtension implements EntityRenderExtension {

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

            // 检查是否有灵魂燃烧buff
            int level = buffData.getLevel(RecastingBuffTypes.SOUL_BURN.get(), entity.level());
            if (level <= 0) {
                return;
            }

            // 获取纹理图集
            ModelManager modelManager = Minecraft.getInstance().getModelManager();
            //noinspection deprecation
            TextureAtlas textureAtlas = modelManager.getAtlas(TextureAtlas.LOCATION_BLOCKS);

            // 获取火焰纹理
            TextureAtlasSprite fireSprite0 = textureAtlas.getSprite(RenderStateManage.FIRE_LAYER_0);
            TextureAtlasSprite fireSprite1 = textureAtlas.getSprite(RenderStateManage.FIRE_LAYER_1);

            // 渲染火焰效果（实体位置已经在 poseStack 中正确设置）
            renderEntityOnFire(livingEntity, 0, 0, 0, partialTicks, poseStack, bufferSource, packedLight, fireSprite0, fireSprite1);
        }

        /**
         * 渲染实体上的火焰效果
         */
        private void renderEntityOnFire(
                LivingEntity entity,
                double x,
                double y,
                double z,
                float partialTicks,
                PoseStack poseStack,
                MultiBufferSource bufferSource,
                int packedLight,
                TextureAtlasSprite fireSprite0,
                TextureAtlasSprite fireSprite1
        ) {
            // 使用自定义火焰渲染类型，支持加法混合
            VertexConsumer buffer = bufferSource.getBuffer(RenderStateManage.FIRE_RENDER_TYPE);

            int i = 0;
            // 渲染3层火焰
            for(int re = 0; re < 3; re++) {
                poseStack.pushPose();
                poseStack.translate((float) x, (float) y, (float) z);

                float scale = entity.getBbWidth() * 1.4F;
                poseStack.scale(scale, scale, scale);

                float f1 = 0.5F;
                float f2 = 0.0F;
                float f3 = entity.getBbHeight() / scale;
                // f4 是从实体底部到实体Y坐标的偏移（用于正确放置火焰）
                // 这个偏移是相对于实体底部的，确保火焰从实体底部开始渲染
                float f4 = (float) (entity.getY() - entity.getBoundingBox().minY);

                // 旋转以面向玩家视角
                float playerViewY = Minecraft.getInstance().gameRenderer.getMainCamera().getYRot();
                poseStack.mulPose(Axis.YP.rotationDegrees(-playerViewY));
                poseStack.translate(0.0F, 0.0f, -0.3F + (float) ((int) f3) * 0.02F - re * 0.2f);

                // 缩放后续层
                if (re > 0) {
                    float reScale = 1.0f / (re + 0.25f);
                    poseStack.scale(reScale, 0.75f, reScale);
                }

                Matrix4f matrix = poseStack.last().pose();

                // 蓝色火焰颜色 (R: 0.1, G: 0.0, B: 1.0, A: 1.0)
                int r = 25;  // 0.1 * 255
                int g = 0;
                int b = 255;
                int a = 255;

                float f5 = 0.0F;
                float currentF3 = f3;

                // 构建火焰层
                while (currentF3 > 0.0F) {
                    TextureAtlasSprite currentSprite = i % 2 == 0
                            ? fireSprite0
                            : fireSprite1;
                    float f6 = currentSprite.getU0();
                    float f7 = currentSprite.getV0();
                    float f8 = currentSprite.getU1();
                    float f9 = currentSprite.getV1();

                    // 交替翻转纹理
                    if (i / 2 % 2 == 0) {
                        float f10 = f8;
                        f8 = f6;
                        f6 = f10;
                    }

                    // 构建四边形
                    buffer.vertex(matrix, f1 - f2, 0.0F - f4, f5)
                            .color(r, g, b, a)
                            .uv(f8, f9)
                            .uv2(packedLight)
                            .normal(0, 1, 0)
                            .endVertex();

                    buffer.vertex(matrix, -f1 - f2, 0.0F - f4, f5)
                            .color(r, g, b, a)
                            .uv(f6, f9)
                            .uv2(packedLight)
                            .normal(0, 1, 0)
                            .endVertex();

                    buffer.vertex(matrix, -f1 - f2, 1.4F - f4, f5)
                            .color(r, g, b, a)
                            .uv(f6, f7)
                            .uv2(packedLight)
                            .normal(0, 1, 0)
                            .endVertex();

                    buffer.vertex(matrix, f1 - f2, 1.4F - f4, f5)
                            .color(r, g, b, a)
                            .uv(f8, f7)
                            .uv2(packedLight)
                            .normal(0, 1, 0)
                            .endVertex();

                    currentF3 -= 0.45F;
                    f4 -= 0.45F;
                    f1 *= 0.9F;
                    f5 += 0.03F;
                    ++i;
                }

                poseStack.popPose();
            }
        }

    }
}

