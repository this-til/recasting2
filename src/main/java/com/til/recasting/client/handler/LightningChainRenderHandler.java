package com.til.recasting.client.handler;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.til.recasting.Recasting;
import com.til.recasting.client.effect.LightningChainClientEffects;
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
import java.util.Random;

/**
 * 闪电链折线自定义渲染（沿 start→end 分段抖动的发光四边形带）。
 * <p>
 * 使用自有 {@link BufferBuilder} 批绘，避免与假人伤害数字等对共享 BufferSource 的干扰。
 */
@Mod.EventBusSubscriber(modid = Recasting.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class LightningChainRenderHandler {

    private static final int SEGMENT_COUNT = 8;
    private static final float CORE_HALF_WIDTH = 0.04f;
    private static final float SHEATH_HALF_WIDTH = 0.14f;
    private static final float BRANCH_OFFSET_SCALE = 0.35f;

    private static final BufferBuilder BATCH_BUFFER = new BufferBuilder(256 * 1024);

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        LightningChainClientEffects.tick();
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
        List<LightningChainClientEffects.Bolt> bolts = LightningChainClientEffects.snapshot();
        if (bolts.isEmpty()) {
            return;
        }

        PoseStack poseStack = event.getPoseStack();
        Matrix4f matrix = poseStack.last().pose();
        Vec3 camera = event.getCamera().getPosition();
        long gameTime = minecraft.level.getGameTime();
        float partialTick = event.getPartialTick();

        beginBatch();
        for (LightningChainClientEffects.Bolt bolt : bolts) {
            float alpha = bolt.alpha(gameTime, partialTick);
            if (alpha <= 0.01f) {
                continue;
            }
            int color = bolt.color();
            float r = ((color >> 16) & 0xFF) / 255.0f;
            float g = ((color >> 8) & 0xFF) / 255.0f;
            float b = (color & 0xFF) / 255.0f;

            Vec3[] points = buildBoltPoints(bolt.start(), bolt.end(), bolt.seed());
            drawPolyline(matrix, camera, points, SHEATH_HALF_WIDTH, r, g, b, alpha * 0.5f);
            drawPolyline(matrix, camera, points, CORE_HALF_WIDTH, 0.85f, 0.95f, 1.0f, alpha);

            // 短分支
            Random random = new Random(bolt.seed() ^ 0x5DEECE66DL);
            for (int i = 2; i < points.length - 2; i += 2) {
                if (random.nextInt(3) != 0) {
                    continue;
                }
                Vec3 mid = points[i];
                Vec3 dir = points[i + 1].subtract(points[i - 1]);
                if (dir.lengthSqr() <= 1.0E-8) {
                    continue;
                }
                dir = dir.normalize();
                Vec3 side = orthogonal(dir, random).scale(0.6 + random.nextDouble() * 0.8);
                Vec3 branchEnd = mid.add(side);
                drawBeamQuad(matrix, camera, mid, branchEnd, SHEATH_HALF_WIDTH * 0.6f, r, g, b, alpha * 0.35f);
                drawBeamQuad(matrix, camera, mid, branchEnd, CORE_HALF_WIDTH * 0.6f, 0.85f, 0.95f, 1.0f, alpha * 0.7f);
            }
        }
        endBatch();
    }

    private static Vec3[] buildBoltPoints(Vec3 start, Vec3 end, long seed) {
        Vec3 delta = end.subtract(start);
        double lengthSqr = delta.lengthSqr();
        if (lengthSqr <= 1.0E-8) {
            return new Vec3[]{start, end};
        }
        Vec3 dir = delta.scale(1.0 / Math.sqrt(lengthSqr));
        Vec3 orthoA = orthogonal(dir, new Random(seed));
        Vec3 orthoB = dir.cross(orthoA).normalize();

        Random random = new Random(seed);
        Vec3[] points = new Vec3[SEGMENT_COUNT + 1];
        points[0] = start;
        points[SEGMENT_COUNT] = end;

        double length = Math.sqrt(lengthSqr);
        float jitter = (float) Math.min(1.2, length * 0.08) * BRANCH_OFFSET_SCALE;
        for (int i = 1; i < SEGMENT_COUNT; i++) {
            float t = i / (float) SEGMENT_COUNT;
            Vec3 base = start.add(delta.scale(t));
            float ox = (random.nextFloat() - 0.5f) * 2.0f * jitter;
            float oy = (random.nextFloat() - 0.5f) * 2.0f * jitter;
            points[i] = base.add(orthoA.scale(ox)).add(orthoB.scale(oy));
        }
        return points;
    }

    private static Vec3 orthogonal(Vec3 dir, Random random) {
        Vec3 side = dir.cross(new Vec3(0.0, 1.0, 0.0));
        if (side.lengthSqr() <= 1.0E-8) {
            side = dir.cross(new Vec3(1.0, 0.0, 0.0));
        }
        side = side.normalize();
        // 轻微旋转，避免所有分支共面
        double angle = random.nextDouble() * Math.PI * 2.0;
        Vec3 bitangent = dir.cross(side).normalize();
        return side.scale(Math.cos(angle)).add(bitangent.scale(Math.sin(angle))).normalize();
    }

    private static void drawPolyline(
            Matrix4f matrix,
            Vec3 camera,
            Vec3[] points,
            float halfWidth,
            float r,
            float g,
            float b,
            float a
    ) {
        for (int i = 0; i < points.length - 1; i++) {
            drawBeamQuad(matrix, camera, points[i], points[i + 1], halfWidth, r, g, b, a);
        }
    }

    private static void beginBatch() {
        if (BATCH_BUFFER.building()) {
            BufferUploader.drawWithShader(BATCH_BUFFER.end());
        }
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
