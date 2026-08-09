package com.til.recasting.client.particle;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

/**
 * 水平圆环迸发粒子：一个实例绘制一整圈 XZ 平面环带。
 * <p>
 * 参数由 {@code sendParticles(..., count=0, radius, ringWidth, colorPacked, 1.0)} 速度通道传入。
 */
@OnlyIn(Dist.CLIENT)
public class BurstRingParticle extends Particle {

    private static final float EXPAND_FACTOR = 1.15f;

    private final float baseRadius;
    private final float ringWidth;
    private final float red;
    private final float green;
    private final float blue;
    private final ParticleRenderType renderType;

    public BurstRingParticle(
            ClientLevel level,
            double x,
            double y,
            double z,
            float radius,
            float ringWidth,
            int rgb
    ) {
        super(level, x, y, z);
        this.baseRadius = Math.max(1.0f, radius);
        this.ringWidth = Math.max(0.5f, ringWidth);
        this.red = ((rgb >> 16) & 255) / 255.0f;
        this.green = ((rgb >> 8) & 255) / 255.0f;
        this.blue = (rgb & 255) / 255.0f;
        this.lifetime = 18;
        this.hasPhysics = false;
        this.renderType = createRenderType();
        float bound = this.baseRadius * EXPAND_FACTOR + this.ringWidth;
        setBoundingBox(new AABB(x - bound, y - 2.0, z - bound, x + bound, y + 2.0, z + bound));
    }

    private ParticleRenderType createRenderType() {
        return new ParticleRenderType() {
            @Override
            public void begin(BufferBuilder buffer, TextureManager textureManager) {
                RenderSystem.disableCull();
                RenderSystem.enableBlend();
                RenderSystem.depthMask(false);
                RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
                RenderSystem.setShader(GameRenderer::getRendertypeLightningShader);
                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
                buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
            }

            @Override
            public void end(Tesselator tesselator) {
                tesselator.end();
                RenderSystem.depthMask(true);
                RenderSystem.defaultBlendFunc();
                RenderSystem.disableBlend();
                RenderSystem.enableCull();
            }

            @Override
            public String toString() {
                return "recasting:burst_ring";
            }
        };
    }

    @Override
    public void render(@NotNull VertexConsumer buffer, Camera camera, float partialTick) {
        float ageProgress = (this.age + partialTick) / (float) this.lifetime;
        float expand = Mth.lerp(ageProgress, 1.0f, EXPAND_FACTOR);
        float alpha = ageProgress < 0.25f
                ? ageProgress / 0.25f
                : 1.0f - (ageProgress - 0.25f) / 0.75f;
        alpha = Mth.clamp(alpha, 0.0f, 1.0f);

        float radius = baseRadius * expand;
        float halfW = ringWidth * 0.5f;
        float inner = Math.max(0.1f, radius - halfW);
        float outer = radius + halfW;
        int segments = Math.max(64, (int) (radius * 1.25f));

        Vec3 cam = camera.getPosition();
        double cx = this.x - cam.x;
        double cy = this.y - cam.y;
        double cz = this.z - cam.z;

        for(int i = 0; i < segments; i++) {
            float a0 = (float) (Math.PI * 2.0 * i / segments);
            float a1 = (float) (Math.PI * 2.0 * (i + 1) / segments);
            float c0 = Mth.cos(a0);
            float s0 = Mth.sin(a0);
            float c1 = Mth.cos(a1);
            float s1 = Mth.sin(a1);

            // 内外环四边形（水平）
            vertex(buffer, cx + outer * c0, cy, cz + outer * s0, alpha);
            vertex(buffer, cx + inner * c0, cy, cz + inner * s0, alpha);
            vertex(buffer, cx + inner * c1, cy, cz + inner * s1, alpha);
            vertex(buffer, cx + outer * c1, cy, cz + outer * s1, alpha);
        }
    }

    private void vertex(VertexConsumer buffer, double x, double y, double z, float alpha) {
        buffer.vertex(x, y, z).color(red, green, blue, alpha).endVertex();
    }

    @Override
    public void tick() {
        if (this.age++ >= this.lifetime) {
            this.remove();
        }
    }

    @Override
    public int getLightColor(float partialTick) {
        return 0xF000F0;
    }

    @Override
    public boolean shouldCull() {
        return false;
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return renderType;
    }
}
