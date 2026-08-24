package com.til.recasting.client.handler;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.til.recasting.Recasting;
import com.til.recasting.client.effect.BurstRingClientEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.event.TickEvent;
import net.neoforged.bus.api.SubscribeEvent;
import org.joml.Matrix4f;

import java.util.List;

/**
 * 末辉终结圆环：任意朝向环带；启用深度测试，使环与地形/实体正确遮挡。
 */
@EventBusSubscriber(modid = Recasting.MODID, value = Dist.CLIENT)
public class BurstRingRenderHandler {

    private static final BufferBuilder BUFFER = new BufferBuilder(2 * 1024 * 1024);

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        BurstRingClientEffects.tick();
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
        List<BurstRingClientEffects.Ring> rings = BurstRingClientEffects.snapshot();
        if (rings.isEmpty()) {
            return;
        }

        PoseStack poseStack = event.getPoseStack();
        Matrix4f matrix = poseStack.last().pose();
        Vec3 camera = event.getCamera().getPosition();
        long gameTime = minecraft.level.getGameTime();
        float partialTick = event.getPartialTick();

        beginBatch();
        for(BurstRingClientEffects.Ring ring : rings) {
            float alpha = ring.alpha(gameTime, partialTick);
            if (alpha <= 0.01f) {
                continue;
            }
            float radius = ring.currentRadius(gameTime, partialTick);
            float halfW = Math.max(1.25f, ring.ringWidth() * 0.5f);
            int color = ring.color();
            float r = ((color >> 16) & 0xFF) / 255.0f;
            float g = ((color >> 8) & 0xFF) / 255.0f;
            float b = (color & 0xFF) / 255.0f;

            drawAnnulus(matrix, camera, ring, radius, halfW, r, g, b, alpha * 0.7f);
            drawAnnulus(matrix, camera, ring, radius, halfW * 0.4f, 1.0f, 0.92f, 0.88f, alpha);
        }
        endBatch();
    }

    private static void beginBatch() {
        if (BUFFER.building()) {
            BufferUploader.drawWithShader(BUFFER.end());
        }
        // 深度测试开启：被地形/实体遮挡；不写深度避免半透明环互相挖洞
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.disableCull();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        RenderSystem.setShader(GameRenderer::getRendertypeLightningShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        BUFFER.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
    }

    private static void endBatch() {
        if (BUFFER.building()) {
            BufferUploader.drawWithShader(BUFFER.end());
        }
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
    }

    private static void drawAnnulus(
            Matrix4f matrix,
            Vec3 camera,
            BurstRingClientEffects.Ring ring,
            float radius,
            float halfWidth,
            float red,
            float green,
            float blue,
            float alpha
    ) {
        float inner = Math.max(0.1f, radius - halfWidth);
        float outer = radius + halfWidth;
        int segments = Math.max(96, (int) (radius * 1.5f));
        Vec3 center = ring.center();
        Vec3 axisU = ring.axisU();
        Vec3 axisV = ring.axisV();

        for(int i = 0; i < segments; i++) {
            float a0 = (float) (Math.PI * 2.0 * i / segments);
            float a1 = (float) (Math.PI * 2.0 * (i + 1) / segments);
            float c0 = Mth.cos(a0);
            float s0 = Mth.sin(a0);
            float c1 = Mth.cos(a1);
            float s1 = Mth.sin(a1);

            put(matrix, camera, pointOnRing(center, axisU, axisV, outer, c0, s0), red, green, blue, alpha);
            put(matrix, camera, pointOnRing(center, axisU, axisV, inner, c0, s0), red, green, blue, alpha);
            put(matrix, camera, pointOnRing(center, axisU, axisV, inner, c1, s1), red, green, blue, alpha);
            put(matrix, camera, pointOnRing(center, axisU, axisV, outer, c1, s1), red, green, blue, alpha);
        }
    }

    private static Vec3 pointOnRing(Vec3 center, Vec3 axisU, Vec3 axisV, float radius, float cos, float sin) {
        return center.add(axisU.scale(radius * cos)).add(axisV.scale(radius * sin));
    }

    private static void put(
            Matrix4f matrix,
            Vec3 camera,
            Vec3 position,
            float r,
            float g,
            float b,
            float a
    ) {
        BUFFER.vertex(
                        matrix,
                        (float) (position.x - camera.x),
                        (float) (position.y - camera.y),
                        (float) (position.z - camera.z)
                )
                .color(r, g, b, a)
                .endVertex();
    }
}
