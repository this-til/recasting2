package com.til.recasting.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.til.recasting.capability.IBuffStackData;
import com.til.recasting.registry.RecastingAttachments;
import com.til.recasting.registry.instance.BuffType;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.client.renderer.model.BladeModelManager;
import mods.flammpfeil.slashblade.client.renderer.model.obj.WavefrontObject;
import mods.flammpfeil.slashblade.client.renderer.util.BladeRenderState;
import mods.flammpfeil.slashblade.client.renderer.util.MSAutoCloser;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

/**
 * 实体渲染扩展接口，允许为实体添加额外的渲染逻辑。
 */
@FunctionalInterface
public interface EntityRenderExtension {

    void render(Entity entity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight);

    default int getPriority() {
        return 0;
    }

    class BuffLevelRender implements EntityRenderExtension {
        public static final ResourceLocation defaultTexture = ResourceLocation.fromNamespaceAndPath(SlashBlade.MODID, "model/util/ss.png");

        ResourceLocation modelLocation;
        ResourceLocation textureLocation;
        Supplier<? extends BuffType> buffTypeSupplier;
        int color;

        int renderOffset;
        static int renderOffsetGlobalCounter = 0;

        public BuffLevelRender(ResourceLocation modelLocation, ResourceLocation resourceLocation, int color, Supplier<? extends BuffType> buffTypeSupplier) {
            this.modelLocation = modelLocation;
            this.textureLocation = resourceLocation;
            this.buffTypeSupplier = buffTypeSupplier;
            this.color = color;

            renderOffset = renderOffsetGlobalCounter++;
        }

        public BuffLevelRender(ResourceLocation modelLocation, int color, Supplier<? extends BuffType> buffTypeSupplier) {
            this(modelLocation, defaultTexture, color, buffTypeSupplier);
        }

        @Override
        public void render(Entity entity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
            IBuffStackData buffStackData = RecastingAttachments.buffStackData(entity);
            BuffType buffType = buffTypeSupplier.get();
            int level = buffStackData.getLevel(buffType, entity.level());

            if (level <= 0) {
                return;
            }

            try (MSAutoCloser msac = MSAutoCloser.pushMatrix(poseStack)) {
                poseStack.translate(0, renderOffset * 0.01 + 0.1, 0);
                poseStack.mulPose(Axis.YP.rotationDegrees(-Mth.lerp(partialTicks, entity.yRotO, entity.getYRot())));
                float scale = 0.0075f;
                poseStack.scale(scale, scale, scale);

                WavefrontObject model = BladeModelManager.getInstance().getModel(modelLocation);

                for (int layer = level; layer > 0; layer--) {
                    BladeRenderState.setCol(color);
                    BladeRenderState.renderOverrided(
                            ItemStack.EMPTY,
                            model,
                            "l" + layer,
                            textureLocation,
                            poseStack,
                            bufferSource,
                            BladeRenderState.MAX_LIGHT,
                            RenderStateManage::mackLuminous,
                            true
                    );
                }
            }
        }
    }
}
