package com.til.recasting.client.handler;

import com.til.recasting.Recasting;
import com.til.recasting.client.renderer.entity.DriveEntityRenderer;
import com.til.recasting.client.renderer.entity.FinalGlowBlackHoleEntityRender;
import com.til.recasting.client.renderer.entity.JudgementCutEntityRenderer;
import com.til.recasting.client.renderer.entity.LightningEntityRenderer;
import com.til.recasting.client.renderer.entity.MatrixEntityRender;
import com.til.recasting.client.renderer.entity.SlashEffectEntityRenderer;
import com.til.recasting.client.renderer.entity.StellarRotationEntityRender;
import com.til.recasting.client.renderer.entity.StarfallArrayEntityRender;
import com.til.recasting.client.renderer.entity.SummondSwordEntityRenderer;
import com.til.recasting.client.renderer.entity.TrackingSummondSwordEntityRenderer;
import com.til.recasting.registry.RecastingEntities;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

/**
 * 客户端实体渲染器注册。
 */
@EventBusSubscriber(modid = Recasting.MODID, value = Dist.CLIENT)
public final class ClientRenderHandler {

    private ClientRenderHandler() {
    }

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(RecastingEntities.JUDGEMENT_CUT.get(), JudgementCutEntityRenderer::new);
        event.registerEntityRenderer(RecastingEntities.LIGHTNING.get(), LightningEntityRenderer::new);
        event.registerEntityRenderer(RecastingEntities.SUMMOND_SWORD.get(), SummondSwordEntityRenderer::new);
        event.registerEntityRenderer(RecastingEntities.SUMMOND_SPIRAL_SWORD.get(), SummondSwordEntityRenderer::new);
        event.registerEntityRenderer(RecastingEntities.TRACKING_SUMMOND_SWORD.get(), TrackingSummondSwordEntityRenderer::new);
        event.registerEntityRenderer(RecastingEntities.SLASH_EFFECT.get(), SlashEffectEntityRenderer::new);
        event.registerEntityRenderer(RecastingEntities.DRIVE.get(), DriveEntityRenderer::new);
        event.registerEntityRenderer(RecastingEntities.STELLAR_ROTATION.get(), StellarRotationEntityRender::new);
        event.registerEntityRenderer(RecastingEntities.MATRIX.get(), MatrixEntityRender::new);
        event.registerEntityRenderer(RecastingEntities.STARFALL_ARRAY.get(), StarfallArrayEntityRender::new);
        event.registerEntityRenderer(RecastingEntities.FINAL_GLOW_BLACK_HOLE.get(), FinalGlowBlackHoleEntityRender::new);
    }
}
