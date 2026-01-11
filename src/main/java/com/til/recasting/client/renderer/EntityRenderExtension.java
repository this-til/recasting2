package com.til.recasting.client.renderer;

import com.mojang.authlib.minecraft.client.MinecraftClient;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.til.recasting.capability.IBuffStackData;
import com.til.recasting.client.registry.EntityRenderExtensionRegistry;
import com.til.recasting.entity.StandardizationAttackEntity;
import com.til.recasting.entity.SummondSwordEntity;
import com.til.recasting.handler.CapabilityRegistryHandler;
import com.til.recasting.registry.SpecialEffectsRegistry;
import com.til.recasting.registry.instance.BuffType;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.client.renderer.model.BladeModelManager;
import mods.flammpfeil.slashblade.client.renderer.model.obj.WavefrontObject;
import mods.flammpfeil.slashblade.client.renderer.util.BladeRenderState;
import mods.flammpfeil.slashblade.client.renderer.util.MSAutoCloser;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.function.Supplier;

/**
 * 实体渲染扩展接口
 * 允许为实体添加额外的渲染逻辑
 */
@FunctionalInterface
public interface EntityRenderExtension {

    /**
     * 渲染实体的额外内容
     *
     * @param entity       要渲染的实体
     * @param partialTicks 部分刻度（用于平滑动画）
     * @param poseStack    矩阵栈
     * @param bufferSource 缓冲区源
     * @param packedLight  打包的光照值
     */
    void render(Entity entity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight);


    /**
     * 渲染优先级（数值越小越先渲染）
     * 默认为0
     *
     * @return 优先级
     */
    default int getPriority() {
        return 0;
    }

    class BuffLevelRender implements EntityRenderExtension {
        public static final ResourceLocation defaultTexture = ResourceLocation.fromNamespaceAndPath(SlashBlade.MODID, "model/util/ss.png");

        ResourceLocation modelLocation;
        ResourceLocation textureLocation;
        Supplier<BuffType> buffTypeSupplier;
        int color;

        int renderOffset;
        static int renderOffsetGlobalCounter = 0;

        public BuffLevelRender(ResourceLocation modelLocation, ResourceLocation resourceLocation, int color, Supplier<BuffType> buffTypeSupplier) {
            this.modelLocation = modelLocation;
            this.textureLocation = resourceLocation;
            this.buffTypeSupplier = buffTypeSupplier;
            this.color = color;

            renderOffset = renderOffsetGlobalCounter++;
        }

        public BuffLevelRender(ResourceLocation modelLocation, int color, Supplier<BuffType> buffTypeSupplier) {
            this(modelLocation, defaultTexture, color, buffTypeSupplier);
        }

        @Override
        public void render(Entity entity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {


            LazyOptional<IBuffStackData> capability = entity.getCapability(CapabilityRegistryHandler.BUFF_STACK_DATA);

            if (!capability.isPresent()) {
                return;
            }

            //noinspection DataFlowIssue
            IBuffStackData iBuffStackData = capability.orElse(null);

            BuffType buffType = buffTypeSupplier.get();
            int level = iBuffStackData.getLevel(buffType, Minecraft.getInstance().level);

            if (level <= 0) {
                return;
            }

            try (MSAutoCloser msac = MSAutoCloser.pushMatrix(poseStack)) {

                poseStack.translate(0, renderOffset * 0.01 + 0.1, 0);


                poseStack.mulPose(Axis.YP.rotationDegrees(-Mth.lerp(partialTicks, entity.yRotO, entity.getYRot())));
                float scale = 0.0075f;
                poseStack.scale(scale, scale, scale);

                WavefrontObject model = BladeModelManager.getInstance().getModel(modelLocation);

                for(int layer = level; layer > 0; layer--) {
                    BladeRenderState.setCol(color);
                    BladeRenderState.renderOverrided(ItemStack.EMPTY, model, "l" + layer, textureLocation, poseStack, bufferSource, packedLight, RenderStateManage::mackLuminous, true);
                }

            }

        }


    }

}

