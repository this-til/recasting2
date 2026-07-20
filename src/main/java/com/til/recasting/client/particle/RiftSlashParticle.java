package com.til.recasting.client.particle;

import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.awt.*;

/**
 * 裂隙斩粒子：细长裂隙快速撕开，随后宽度按指数收束（愈合）。
 */
@OnlyIn(Dist.CLIENT)
public class RiftSlashParticle extends DefaultParticle {

    private final float length;
    private final float maxWidth;
    private final float baseAlpha;
    /** 寿命前段用于撕开的比例 */
    private final float openRatio;
    /** 愈合指数衰减强度，越大收束越快 */
    private final float healDecay;

    public RiftSlashParticle(
            ClientLevel level,
            double x, double y, double z,
            float length,
            float maxWidth,
            int lifetime,
            float openRatio,
            float slashRoll,
            Color color,
            ResourceLocation texture
    ) {
        super(level, x, y, z);
        this.length = length;
        this.maxWidth = maxWidth;
        this.openRatio = Mth.clamp(openRatio, 0.05f, 0.45f);
        this.healDecay = 4.5f;
        this.baseAlpha = color.getAlpha() / 255f;
        setLifeTime(lifetime);
        setColor(color);
        setTextureName(texture);
        setParticleCollide(false);
        setSizeChangeType(null);
        this.roll = slashRoll;
        this.oldRoll = slashRoll;
        this.setSize(length, maxWidth);
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        if (this.age++ >= this.lifetime) {
            this.remove();
            return;
        }

        float t = this.age / (float) Math.max(1, this.lifetime);
        float widthFactor = widthFactor(t);
        this.alpha = this.baseAlpha * Mth.clamp(0.25f + widthFactor * 0.75f, 0.0f, 1.0f);
    }

    @Override
    public void render(@NotNull com.mojang.blaze3d.vertex.VertexConsumer ignored, Camera camera, float partialTick) {
        BufferBuilder buffer = activeBatch;
        if (buffer == null) {
            return;
        }

        float ageF = this.age + partialTick;
        float t = ageF / (float) Math.max(1, this.lifetime);
        float lengthNow = this.length * lengthFactor(t);
        float widthNow = this.maxWidth * widthFactor(t);
        if (lengthNow <= 1.0E-4f || widthNow <= 1.0E-4f || this.alpha <= 1.0E-4f) {
            return;
        }

        Vec3 cameraPos = camera.getPosition();
        Vector3f addPos = new Vector3f(
                (float) (Mth.lerp(partialTick, this.xo, this.x) - cameraPos.x()),
                (float) (Mth.lerp(partialTick, this.yo, this.y) - cameraPos.y()),
                (float) (Mth.lerp(partialTick, this.zo, this.z) - cameraPos.z())
        );

        Quaternionf quaternion = new Quaternionf(camera.rotation());
        float rollNow = Mth.lerp(partialTick, this.oldRoll, this.roll);
        quaternion.rotateZ(rollNow);

        Vector3f[] vertices = new Vector3f[]{
                new Vector3f(-lengthNow, -widthNow, 0.0F),
                new Vector3f(-lengthNow, widthNow, 0.0F),
                new Vector3f(lengthNow, widthNow, 0.0F),
                new Vector3f(lengthNow, -widthNow, 0.0F)
        };
        for (int i = 0; i < 4; ++i) {
            Vector3f vertex = vertices[i];
            vertex.rotate(quaternion);
            vertex.add(addPos);
        }

        float a = this.alpha;
        int combined = 15 << 20 | 15 << 4;
        buffer.vertex(vertices[0].x(), vertices[0].y(), vertices[0].z())
                .uv(0, 0).color(this.rCol, this.gCol, this.bCol, a).uv2(combined).endVertex();
        buffer.vertex(vertices[1].x(), vertices[1].y(), vertices[1].z())
                .uv(0, 1).color(this.rCol, this.gCol, this.bCol, a).uv2(combined).endVertex();
        buffer.vertex(vertices[2].x(), vertices[2].y(), vertices[2].z())
                .uv(1, 1).color(this.rCol, this.gCol, this.bCol, a).uv2(combined).endVertex();
        buffer.vertex(vertices[3].x(), vertices[3].y(), vertices[3].z())
                .uv(1, 0).color(this.rCol, this.gCol, this.bCol, a).uv2(combined).endVertex();
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return super.getRenderType();
    }

    /**
     * 长度：极快拉满，愈合阶段几乎不缩，只靠宽度收束。
     */
    private float lengthFactor(float t) {
        float open = this.openRatio;
        if (t <= open) {
            float u = t / open;
            return 1.0f - (1.0f - u) * (1.0f - u);
        }
        return 1.0f;
    }

    /**
     * 宽度：快速撕开后按指数衰减收束。
     */
    private float widthFactor(float t) {
        float open = this.openRatio;
        if (t <= open) {
            float u = t / open;
            return 1.0f - (1.0f - u) * (1.0f - u) * (1.0f - u);
        }
        float u = (t - open) / (1.0f - open);
        return (float) Math.exp(-this.healDecay * u);
    }
}
