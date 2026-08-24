package com.til.recasting.client;

import com.til.recasting.Recasting;
import com.til.recasting.registry.RecastingEntities;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

/**
 * P2.5：空 Renderer 占位，避免生成实体时客户端崩溃；完整渲染待 P5。
 */
@EventBusSubscriber(modid = Recasting.MODID, value = Dist.CLIENT)
public final class ClientEntityRenderers {

    private ClientEntityRenderers() {
    }

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(RecastingEntities.LIGHTNING.get(), EmptyEntityRenderer::new);
        event.registerEntityRenderer(RecastingEntities.JUDGEMENT_CUT.get(), EmptyEntityRenderer::new);
        event.registerEntityRenderer(RecastingEntities.SUMMOND_SWORD.get(), EmptyEntityRenderer::new);
        event.registerEntityRenderer(RecastingEntities.SUMMOND_SPIRAL_SWORD.get(), EmptyEntityRenderer::new);
        event.registerEntityRenderer(RecastingEntities.TRACKING_SUMMOND_SWORD.get(), EmptyEntityRenderer::new);
        event.registerEntityRenderer(RecastingEntities.SLASH_EFFECT.get(), EmptyEntityRenderer::new);
        event.registerEntityRenderer(RecastingEntities.DRIVE.get(), EmptyEntityRenderer::new);
        event.registerEntityRenderer(RecastingEntities.STELLAR_ROTATION.get(), EmptyEntityRenderer::new);
        event.registerEntityRenderer(RecastingEntities.MATRIX.get(), EmptyEntityRenderer::new);
        event.registerEntityRenderer(RecastingEntities.STARFALL_ARRAY.get(), EmptyEntityRenderer::new);
        event.registerEntityRenderer(RecastingEntities.FINAL_GLOW_BLACK_HOLE.get(), EmptyEntityRenderer::new);
    }

    private static final class EmptyEntityRenderer extends EntityRenderer<Entity> {

        private EmptyEntityRenderer(EntityRendererProvider.Context context) {
            super(context);
        }

        @Override
        public ResourceLocation getTextureLocation(Entity entity) {
            return Recasting.prefix("textures/entity/empty.png");
        }
    }
}
