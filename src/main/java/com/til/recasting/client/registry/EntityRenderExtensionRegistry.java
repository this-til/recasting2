package com.til.recasting.client.registry;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.til.recasting.Recasting;
import com.til.recasting.capability.IBuffStackData;
import com.til.recasting.client.renderer.BuffLevelTextRenderer;
import com.til.recasting.client.renderer.EntityRenderExtension;
import com.til.recasting.client.renderer.RenderStateManage;
import mods.flammpfeil.slashblade.client.renderer.util.MSAutoCloser;
import com.til.recasting.constant.R;
import com.til.recasting.entity.SummondSwordEntity;
import com.til.recasting.handler.CapabilityRegistryHandler;
import com.til.recasting.registry.RecastingBuffTypes;
import com.til.recasting.registry.instance.BuffType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
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

    public static final RegistryObject<EntityRenderExtension> CALCULUS = ENTITY_RENDER_EXTENSIONS.register(
            "calculus",
            () -> new EntityRenderExtension.BuffLevelRender(
                    R.Models.Mark.calculus$obj,
                    new Color(255, 255, 255).getRGB(),
                    RecastingBuffTypes.CALCULUS
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
     * 断灭渲染扩展
     * 为拥有断灭buff的实体渲染层级效果
     */
    public static final RegistryObject<EntityRenderExtension> ANNIHILATION = ENTITY_RENDER_EXTENSIONS.register(
            "annihilation",
            () -> new EntityRenderExtension.BuffLevelRender(
                    R.Models.Mark.annihilation$obj,
                    new Color(28, 50, 112).getRGB(),
                    RecastingBuffTypes.ANNIHILATION
            )
    );

    /**
     * 灵魂燃烧渲染扩展
     * 为拥有灵魂燃烧buff的实体渲染蓝色火焰效果
     */
    public static final RegistryObject<EntityRenderExtension> SOUL_BURN = ENTITY_RENDER_EXTENSIONS.register(
            "soul_burn",
            () -> new BuffFireRenderExtension(RecastingBuffTypes.SOUL_BURN, new Color(25, 0, 255))
    );

    /**
     * 光子灼痕渲染扩展
     * 复用灵魂燃烧火焰渲染，使用青色
     */
    public static final RegistryObject<EntityRenderExtension> PHOTON_SCAR = ENTITY_RENDER_EXTENSIONS.register(
            "photon_scar",
            () -> new BuffFireRenderExtension(RecastingBuffTypes.PHOTON_BURN, new Color(80, 220, 255))
    );

    /**
     * 翠火渲染扩展
     * 为拥有翠火buff的实体渲染绿色火焰效果
     */
    public static final RegistryObject<EntityRenderExtension> JADE_FIRE = ENTITY_RENDER_EXTENSIONS.register(
            "jade_fire",
            () -> new BuffFireRenderExtension(RecastingBuffTypes.JADE_FIRE, new Color(72, 220, 120))
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
     * 按 Buff 类型渲染实体火焰效果，颜色可配置
     */
    public static class BuffFireRenderExtension implements EntityRenderExtension {

        private final RegistryObject<BuffType> buffType;
        private final Color color;

        public BuffFireRenderExtension(RegistryObject<BuffType> buffType, Color color) {
            this.buffType = buffType;
            this.color = color;
        }

        @Override
        public void render(Entity entity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
            if (!(entity instanceof LivingEntity livingEntity)) {
                return;
            }

            LazyOptional<IBuffStackData> capability = entity.getCapability(CapabilityRegistryHandler.BUFF_STACK_DATA);
            if (!capability.isPresent()) {
                return;
            }

            //noinspection DataFlowIssue
            IBuffStackData buffData = capability.orElse(null);

            int level = buffData.getLevel(buffType.get(), entity.level());
            if (level <= 0) {
                return;
            }

            ModelManager modelManager = Minecraft.getInstance().getModelManager();
            //noinspection deprecation
            TextureAtlas textureAtlas = modelManager.getAtlas(TextureAtlas.LOCATION_BLOCKS);

            TextureAtlasSprite fireSprite0 = textureAtlas.getSprite(RenderStateManage.FIRE_LAYER_0);
            TextureAtlasSprite fireSprite1 = textureAtlas.getSprite(RenderStateManage.FIRE_LAYER_1);

            renderEntityOnFire(livingEntity, 0, 0, 0, partialTicks, poseStack, bufferSource, packedLight, fireSprite0, fireSprite1);
        }

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
            VertexConsumer buffer = bufferSource.getBuffer(RenderStateManage.FIRE_RENDER_TYPE);

            int i = 0;
            for(int re = 0; re < 3; re++) {
                try (MSAutoCloser msac = MSAutoCloser.pushMatrix(poseStack)) {
                    poseStack.translate((float) x, (float) y, (float) z);

                    float scale = entity.getBbWidth() * 1.4F;
                    poseStack.scale(scale, scale, scale);

                    float f1 = 0.5F;
                    float f2 = 0.0F;
                    float f3 = entity.getBbHeight() / scale;
                    float f4 = (float) (entity.getY() - entity.getBoundingBox().minY);

                    float playerViewY = Minecraft.getInstance().gameRenderer.getMainCamera().getYRot();
                    poseStack.mulPose(Axis.YP.rotationDegrees(-playerViewY));
                    poseStack.translate(0.0F, 0.0f, -0.3F + (float) ((int) f3) * 0.02F - re * 0.2f);

                    if (re > 0) {
                        float reScale = 1.0f / (re + 0.25f);
                        poseStack.scale(reScale, 0.75f, reScale);
                    }

                    Matrix4f matrix = poseStack.last().pose();

                    int r = color.getRed();
                    int g = color.getGreen();
                    int b = color.getBlue();
                    int a = color.getAlpha();

                    float f5 = 0.0F;
                    float currentF3 = f3;

                    while (currentF3 > 0.0F) {
                        TextureAtlasSprite currentSprite = i % 2 == 0
                                ? fireSprite0
                                : fireSprite1;
                        float f6 = currentSprite.getU0();
                        float f7 = currentSprite.getV0();
                        float f8 = currentSprite.getU1();
                        float f9 = currentSprite.getV1();

                        if (i / 2 % 2 == 0) {
                            float f10 = f8;
                            f8 = f6;
                            f6 = f10;
                        }

                        buffer.vertex(matrix, f1 - f2, 0.0F - f4, f5)
                                .color(r, g, b, a)
                                .uv(f8, f9)
                                .overlayCoords(OverlayTexture.NO_OVERLAY)
                                .uv2(packedLight)
                                .normal(0, 1, 0)
                                .endVertex();

                        buffer.vertex(matrix, -f1 - f2, 0.0F - f4, f5)
                                .color(r, g, b, a)
                                .uv(f6, f9)
                                .overlayCoords(OverlayTexture.NO_OVERLAY)
                                .uv2(packedLight)
                                .normal(0, 1, 0)
                                .endVertex();

                        buffer.vertex(matrix, -f1 - f2, 1.4F - f4, f5)
                                .color(r, g, b, a)
                                .uv(f6, f7)
                                .overlayCoords(OverlayTexture.NO_OVERLAY)
                                .uv2(packedLight)
                                .normal(0, 1, 0)
                                .endVertex();

                        buffer.vertex(matrix, f1 - f2, 1.4F - f4, f5)
                                .color(r, g, b, a)
                                .uv(f8, f7)
                                .overlayCoords(OverlayTexture.NO_OVERLAY)
                                .uv2(packedLight)
                                .normal(0, 1, 0)
                                .endVertex();

                        currentF3 -= 0.45F;
                        f4 -= 0.45F;
                        f1 *= 0.9F;
                        f5 += 0.03F;
                        ++i;
                    }
                }
            }
        }

    }
}
