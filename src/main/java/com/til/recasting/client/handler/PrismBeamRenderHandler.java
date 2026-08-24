package com.til.recasting.client.handler;

import com.mojang.blaze3d.vertex.PoseStack;
import com.til.recasting.Recasting;
import com.til.recasting.client.effect.PrismBeamClientEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.bus.api.SubscribeEvent;
import org.joml.Matrix4f;

import java.util.List;

/**
 * 光棱线段自定义渲染（相机朝向的发光四边形带）。
 * <p>
 * 使用发光线段公共渲染器的专用批绘 Buffer，不写入 {@code renderBuffers().bufferSource()}，
 * 避免与假人伤害数字等对共享 BufferSource 的 {@code endBatch} 互相干扰。
 */
@EventBusSubscriber(modid = Recasting.MODID, value = Dist.CLIENT)
public class PrismBeamRenderHandler {

    private static final float CORE_HALF_WIDTH = 0.035f;
    private static final float SHEATH_HALF_WIDTH = 0.11f;

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        PrismBeamClientEffects.tick();
    }

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) {
            return;
        }
        List<PrismBeamClientEffects.Beam> beams = PrismBeamClientEffects.snapshot();
        if (beams.isEmpty()) {
            return;
        }

        PoseStack poseStack = event.getPoseStack();
        Matrix4f matrix = poseStack.last().pose();
        Vec3 camera = event.getCamera().getPosition();
        long gameTime = minecraft.level.getGameTime();
        float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(true);

        CameraFacingBeamRenderer.begin();
        for(PrismBeamClientEffects.Beam beam : beams) {
            float alpha = beam.alpha(gameTime, partialTick);
            if (alpha <= 0.01f) {
                continue;
            }
            int color = beam.color();
            float r = ((color >> 16) & 0xFF) / 255.0f;
            float g = ((color >> 8) & 0xFF) / 255.0f;
            float b = (color & 0xFF) / 255.0f;

            CameraFacingBeamRenderer.drawQuad(
                    matrix, camera, beam.start(), beam.end(),
                    SHEATH_HALF_WIDTH, r, g, b, alpha * 0.55f
            );
            CameraFacingBeamRenderer.drawQuad(
                    matrix, camera, beam.start(), beam.end(),
                    CORE_HALF_WIDTH, 1.0f, 0.95f, 0.55f, alpha
            );
        }
        CameraFacingBeamRenderer.end();
    }
}
