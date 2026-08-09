package com.til.recasting.client.handler;

import com.til.recasting.Recasting;
import com.til.recasting.client.registry.EntityRenderExtensionRegistry;
import com.til.recasting.client.renderer.entity.DriveEntityRenderer;
import com.til.recasting.client.renderer.entity.JudgementCutEntityRenderer;
import com.til.recasting.client.renderer.entity.LightningEntityRenderer;
import com.til.recasting.client.renderer.entity.MatrixEntityRender;
import com.til.recasting.client.renderer.entity.SlashEffectEntityRenderer;
import com.til.recasting.client.renderer.entity.StarfallArrayEntityRender;
import com.til.recasting.client.renderer.entity.StellarRotationEntityRender;
import com.til.recasting.client.renderer.entity.SummondSwordEntityRenderer;
import com.til.recasting.client.renderer.entity.TrackingSummondSwordEntityRenderer;
import com.til.recasting.registry.RecastingEntities;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * 客户端渲染器注册事件处理器
 */
@Mod.EventBusSubscriber(modid = Recasting.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientRenderHandler {

    /**
     * 注册实体渲染器
     */
    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        // 注册次元斩实体渲染器
        event.registerEntityRenderer(RecastingEntities.JUDGEMENT_CUT.get(), JudgementCutEntityRenderer::new);
        
        // 注册闪电实体渲染器
        event.registerEntityRenderer(RecastingEntities.LIGHTNING.get(), LightningEntityRenderer::new);
        
        // 注册召唤剑实体渲染器
        event.registerEntityRenderer(RecastingEntities.SUMMOND_SWORD.get(), SummondSwordEntityRenderer::new);
        
        // 注册螺旋剑实体渲染器（使用相同的渲染器）
        event.registerEntityRenderer(RecastingEntities.SUMMOND_SPIRAL_SWORD.get(), SummondSwordEntityRenderer::new);

        // 注册追踪幻影剑（sb 组 + 绕刀身旋转）
        event.registerEntityRenderer(RecastingEntities.TRACKING_SUMMOND_SWORD.get(), TrackingSummondSwordEntityRenderer::new);
        
        // 注册斩击特效实体渲染器
        event.registerEntityRenderer(RecastingEntities.SLASH_EFFECT.get(), SlashEffectEntityRenderer::new);
        
        // 注册 Drive 实体渲染器
        event.registerEntityRenderer(RecastingEntities.DRIVE.get(), DriveEntityRenderer::new);
        
        // 注册星旋斩实体渲染器
        event.registerEntityRenderer(RecastingEntities.STELLAR_ROTATION.get(), StellarRotationEntityRender::new);
        
        // 注册穷观阵实体渲染器
        event.registerEntityRenderer(RecastingEntities.MATRIX.get(), MatrixEntityRender::new);

        // 注册群星坠落阵实体渲染器
        event.registerEntityRenderer(RecastingEntities.STARFALL_ARRAY.get(), StarfallArrayEntityRender::new);
    }
}

