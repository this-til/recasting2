package com.til.recasting.client.handler;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public final class CameraFacingBeamRenderer {

    private static final double MIN_LENGTH_SQUARED = 1.0E-8;
    private static final BufferBuilder BUFFER = new BufferBuilder(256 * 1024);

    /**
     * 线段混合模式。
     * <p>
     * {@link #ADDITIVE} 适合亮色发光；暗色在加法混合下几乎不可见，应改用 {@link #TRANSLUCENT}。
     */
    public enum BlendMode {
        /** SRC_ALPHA, ONE — 越亮越显，黑色无贡献 */
        ADDITIVE,
        /** SRC_ALPHA, ONE_MINUS_SRC_ALPHA — 可正确显示黑色/暗色 */
        TRANSLUCENT
    }

    private CameraFacingBeamRenderer() {
    }

    /** 默认加法混合，兼容既有亮色光束调用 */
    public static void begin() {
        begin(BlendMode.ADDITIVE);
    }

    public static void begin(BlendMode blendMode) {
        flushIfBuilding();
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);
        RenderSystem.enableBlend();
        if (blendMode == BlendMode.TRANSLUCENT) {
            RenderSystem.blendFunc(
                    GlStateManager.SourceFactor.SRC_ALPHA,
                    GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA
            );
        } else {
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        }
        RenderSystem.setShader(GameRenderer::getRendertypeLightningShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        BUFFER.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
    }

    public static void end() {
        flushIfBuilding();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.depthMask(true);
    }

    public static void drawPolyline(
            Matrix4f matrix,
            Vec3 camera,
            Vec3[] points,
            float halfWidth,
            float red,
            float green,
            float blue,
            float alpha
    ) {
        for (int i = 0; i < points.length - 1; i++) {
            drawQuad(matrix, camera, points[i], points[i + 1], halfWidth, red, green, blue, alpha);
        }
    }

    public static void drawQuad(
            Matrix4f matrix,
            Vec3 camera,
            Vec3 start,
            Vec3 end,
            float halfWidth,
            float red,
            float green,
            float blue,
            float alpha
    ) {
        Vec3 delta = end.subtract(start);
        double lengthSquared = delta.lengthSqr();
        if (lengthSquared <= MIN_LENGTH_SQUARED) {
            return;
        }

        Vec3 direction = delta.scale(1.0 / Math.sqrt(lengthSquared));
        Vec3 midpoint = start.add(end).scale(0.5);
        Vec3 side = direction.cross(camera.subtract(midpoint));
        if (side.lengthSqr() <= MIN_LENGTH_SQUARED) {
            side = direction.cross(new Vec3(0.0, 1.0, 0.0));
            if (side.lengthSqr() <= MIN_LENGTH_SQUARED) {
                side = direction.cross(new Vec3(1.0, 0.0, 0.0));
            }
        }
        side = side.normalize().scale(halfWidth);

        putVertex(matrix, camera, start.subtract(side), red, green, blue, alpha);
        putVertex(matrix, camera, start.add(side), red, green, blue, alpha);
        putVertex(matrix, camera, end.add(side), red, green, blue, alpha);
        putVertex(matrix, camera, end.subtract(side), red, green, blue, alpha);
    }

    private static void flushIfBuilding() {
        if (BUFFER.building()) {
            BufferUploader.drawWithShader(BUFFER.end());
        }
    }

    private static void putVertex(
            Matrix4f matrix,
            Vec3 camera,
            Vec3 position,
            float red,
            float green,
            float blue,
            float alpha
    ) {
        BUFFER.vertex(
                        matrix,
                        (float) (position.x - camera.x),
                        (float) (position.y - camera.y),
                        (float) (position.z - camera.z)
                )
                .color(red, green, blue, alpha)
                .endVertex();
    }
}
