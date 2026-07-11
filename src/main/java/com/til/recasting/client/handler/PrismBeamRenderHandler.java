package com.til.recasting.client.handler;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.til.recasting.Recasting;
import com.til.recasting.client.effect.PrismBeamClientEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix4f;

import java.util.List;

/**
 * 光棱线段自定义渲染（相机朝向的发光四边形带）
 */
@Mod.EventBusSubscriber(modid = Recasting.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class PrismBeamRenderHandler {

    private static final float CORE_HALF_WIDTH = 0.035f;
    private static final float SHEATH_HALF_WIDTH = 0.11f;

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
        MultiBufferSource.BufferSource bufferSource = minecraft.renderBuffers().bufferSource();
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.lightning());
        Matrix4f matrix = poseStack.last().pose();

        Vec3 camera = event.getCamera().getPosition();
        long gameTime = minecraft.level.getGameTime();
        float partialTick = event.getPartialTick();

        for (PrismBeamClientEffects.Beam beam : beams) {
            float alpha = beam.alpha(gameTime, partialTick);
            if (alpha <= 0.01f) {
                continue;
            }
            int color = beam.color();
            float r = ((color >> 16) & 0xFF) / 255.0f;
            float g = ((color >> 8) & 0xFF) / 255.0f;
            float b = (color & 0xFF) / 255.0f;

            drawBeamQuad(
                    consumer,
                    matrix,
                    camera,
                    beam.start(),
                    beam.end(),
                    SHEATH_HALF_WIDTH,
                    r,
                    g,
                    b,
                    alpha * 0.55f
            );
            drawBeamQuad(
                    consumer,
                    matrix,
                    camera,
                    beam.start(),
                    beam.end(),
                    CORE_HALF_WIDTH,
                    1.0f,
                    0.95f,
                    0.55f,
                    alpha
            );
        }

        bufferSource.endBatch(RenderType.lightning());
    }

    private static void drawBeamQuad(
            VertexConsumer consumer,
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

        put(consumer, matrix, camera, start.subtract(side), r, g, b, a);
        put(consumer, matrix, camera, start.add(side), r, g, b, a);
        put(consumer, matrix, camera, end.add(side), r, g, b, a);
        put(consumer, matrix, camera, end.subtract(side), r, g, b, a);
    }

    private static void put(
            VertexConsumer consumer,
            Matrix4f matrix,
            Vec3 camera,
            Vec3 pos,
            float r,
            float g,
            float b,
            float a
    ) {
        consumer.vertex(
                        matrix,
                        (float) (pos.x - camera.x),
                        (float) (pos.y - camera.y),
                        (float) (pos.z - camera.z)
                )
                .color(r, g, b, a)
                .endVertex();
    }
}
