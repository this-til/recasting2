package com.til.recasting.client.handler;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.til.recasting.Recasting;
import com.til.recasting.client.effect.PrismBeamClientEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix4f;

import java.util.List;

/**
 * 光棱线段自定义渲染（相机朝向的发光四边形带）。
 * <p>
 * 使用自有 {@link BufferBuilder} 批绘，不写入 {@code renderBuffers().bufferSource()}，
 * 避免与假人伤害数字等对共享 BufferSource 的 {@code endBatch} 互相干扰。
 */
@Mod.EventBusSubscriber(modid = Recasting.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class PrismBeamRenderHandler {

    private static final float CORE_HALF_WIDTH = 0.035f;
    private static final float SHEATH_HALF_WIDTH = 0.11f;

    /** 光棱批绘专用 Buffer（POSITION_COLOR，对齐 {@code RenderType.lightning()}）。 */
    private static final BufferBuilder BATCH_BUFFER = new BufferBuilder(256 * 1024);

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
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
        float partialTick = event.getPartialTick();

        beginBatch();
        for (PrismBeamClientEffects.Beam beam : beams) {
            float alpha = beam.alpha(gameTime, partialTick);
            if (alpha <= 0.01f) {
                continue;
            }
            int color = beam.color();
            float r = ((color >> 16) & 0xFF) / 255.0f;
            float g = ((color >> 8) & 0xFF) / 255.0f;
            float b = (color & 0xFF) / 255.0f;

            drawBeamQuad(matrix, camera, beam.start(), beam.end(), SHEATH_HALF_WIDTH, r, g, b, alpha * 0.55f);
            drawBeamQuad(matrix, camera, beam.start(), beam.end(), CORE_HALF_WIDTH, 1.0f, 0.95f, 0.55f, alpha);
        }
        endBatch();
    }

    private static void beginBatch() {
        if (BATCH_BUFFER.building()) {
            BufferUploader.drawWithShader(BATCH_BUFFER.end());
        }
        // 对齐 RenderType.lightning()：lightning shader + 加法混合
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        RenderSystem.setShader(GameRenderer::getRendertypeLightningShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        BATCH_BUFFER.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
    }

    private static void endBatch() {
        if (BATCH_BUFFER.building()) {
            BufferUploader.drawWithShader(BATCH_BUFFER.end());
        }
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.depthMask(true);
    }

    private static void drawBeamQuad(
            Matrix4f matrix,
            Vec3 camera,
            Vec3 start,
            Vec3 end,
            float halfWidth,
            float r,
            float g,
            float b,
            float a
    ) {
        Vec3 delta = end.subtract(start);
        double lengthSqr = delta.lengthSqr();
        if (lengthSqr <= 1.0E-8) {
            return;
        }
        Vec3 dir = delta.scale(1.0 / Math.sqrt(lengthSqr));
        Vec3 mid = start.add(end).scale(0.5);
        Vec3 toCam = camera.subtract(mid);
        Vec3 side = dir.cross(toCam);
        if (side.lengthSqr() <= 1.0E-8) {
            side = dir.cross(new Vec3(0.0, 1.0, 0.0));
            if (side.lengthSqr() <= 1.0E-8) {
                side = dir.cross(new Vec3(1.0, 0.0, 0.0));
            }
        }
        side = side.normalize().scale(halfWidth);

        put(matrix, camera, start.subtract(side), r, g, b, a);
        put(matrix, camera, start.add(side), r, g, b, a);
        put(matrix, camera, end.add(side), r, g, b, a);
        put(matrix, camera, end.subtract(side), r, g, b, a);
    }

    private static void put(
            Matrix4f matrix,
            Vec3 camera,
            Vec3 pos,
            float r,
            float g,
            float b,
            float a
    ) {
        BATCH_BUFFER.vertex(
                        matrix,
                        (float) (pos.x - camera.x),
                        (float) (pos.y - camera.y),
                        (float) (pos.z - camera.z)
                )
                .color(r, g, b, a)
                .endVertex();
    }
}
