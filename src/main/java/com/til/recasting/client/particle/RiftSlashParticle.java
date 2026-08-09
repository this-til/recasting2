package com.til.recasting.client.particle;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.til.recasting.client.RecastingShaderHandler;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.awt.*;

/**
 * 裂隙斩粒子：对齐 FantasyDesire {@code BladeRiftParticle} 的 SDF HDR + ONE/ONE 渲染。
 */
@OnlyIn(Dist.CLIENT)
public class RiftSlashParticle extends Particle {

    /**
     * SDF 轴向半长（与 FD 默认一致）
     */
    private static final float SDF_RIFT_LENGTH = 0.9f;

    private final Vec3 start;
    private final Vec3 end;
    private final float riftLength;
    private final float riftWidth;
    private final int coreColor;
    private final int energyColor;
    private final ParticleRenderType renderType;

    /**
     * @param worldHalfLength 世界空间半长（中心沿 roll 方向延伸）
     * @param visualWidth     视觉宽度，映射到 SDF {@code RiftWidth}
     * @param slashRoll       绕视线的滚转（弧度），生成时冻结为世界轴
     */
    public RiftSlashParticle(
            ClientLevel level,
            double x, double y, double z,
            float worldHalfLength,
            float visualWidth,
            int lifetime,
            float slashRoll,
            Color color
    ) {
        super(level, x, y, z);
        this.lifetime = Math.max(1, lifetime);
        this.hasPhysics = false;
        this.riftLength = SDF_RIFT_LENGTH;
        // FD 常用 0.03；按旧 billboard 宽度比例映射，夹到合理区间
        this.riftWidth = Mth.clamp(visualWidth * 0.08f, 0.018f, 0.06f);

        int rgb = (color.getRed() << 16) | (color.getGreen() << 8) | color.getBlue();
        this.coreColor = rgb;
        this.energyColor = brightenRgb(rgb, 1.12f);

        Vec3 center = new Vec3(x, y, z);
        Vec3 axis = resolveAxis(slashRoll);
        float half = Math.max(0.15f, worldHalfLength);
        this.start = center.subtract(axis.scale(half));
        this.end = center.add(axis.scale(half));

        this.renderType = createRenderType();
        double inflate = Math.max(0.25, start.distanceTo(end) * riftWidth / riftLength * 28.0);
        setBoundingBox(new AABB(start, end).inflate(inflate));
    }

    private static Vec3 resolveAxis(float slashRoll) {
        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        Quaternionf quaternion = new Quaternionf(camera.rotation());
        quaternion.rotateZ(slashRoll);
        Vector3f local = new Vector3f(1.0f, 0.0f, 0.0f);
        local.rotate(quaternion);
        double len = Math.sqrt(local.x() * local.x() + local.y() * local.y() + local.z() * local.z());
        if (len < 1.0E-8) {
            return new Vec3(1.0, 0.0, 0.0);
        }
        return new Vec3(local.x() / len, local.y() / len, local.z() / len);
    }

    private static int brightenRgb(int rgb, float factor) {
        int r = Mth.clamp(Mth.floor(((rgb >> 16) & 255) * factor), 0, 255);
        int g = Mth.clamp(Mth.floor(((rgb >> 8) & 255) * factor), 0, 255);
        int b = Mth.clamp(Mth.floor((rgb & 255) * factor), 0, 255);
        return (r << 16) | (g << 8) | b;
    }

    private ParticleRenderType createRenderType() {
        return new ParticleRenderType() {
            @Override
            public void begin(BufferBuilder buffer, TextureManager textures) {
                ShaderInstance shader = RecastingShaderHandler.getBladeRift();
                if (shader != null) {
                    RenderSystem.disableCull();
                    RenderSystem.enableBlend();
                    RenderSystem.depthMask(false);
                    // FantasyDesire BladeRift：发射通道用 ONE/ONE 累加 HDR 晕
                    RenderSystem.blendFuncSeparate(
                            GlStateManager.SourceFactor.ONE,
                            GlStateManager.DestFactor.ONE,
                            GlStateManager.SourceFactor.ONE,
                            GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA
                    );
                    RenderSystem.setShader(() -> shader);
                    float partial = Minecraft.getInstance().getFrameTime();
                    float progress = Math.min(1f, Math.max(0f, (age + partial) / (float) lifetime));
                    setUniform(shader, "Progress", progress);
                    setUniform(shader, "FlowTime", (age + partial) * 0.05f);
                    setUniform(shader, "RiftLength", riftLength);
                    setUniform(shader, "RiftWidth", riftWidth);
                    setColorUniform(shader, "CoreColor", coreColor);
                    setColorUniform(shader, "EnergyColor", energyColor);
                }
                buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
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
                return "recasting:blade_rift/" + System.identityHashCode(RiftSlashParticle.this);
            }
        };
    }

    private static void setUniform(ShaderInstance shader, String name, float value) {
        if (shader.getUniform(name) != null) {
            shader.getUniform(name).set(value);
        }
    }

    private static void setColorUniform(ShaderInstance shader, String name, int color) {
        if (shader.getUniform(name) != null) {
            shader.getUniform(name).set(
                    ((color >> 16) & 255) / 255f,
                    ((color >> 8) & 255) / 255f,
                    (color & 255) / 255f
            );
        }
    }

    @Override
    public void render(@NotNull VertexConsumer out, Camera camera, float partialTick) {
        if (RecastingShaderHandler.getBladeRift() == null) {
            return;
        }
        Vec3 axis = end.subtract(start);
        if (axis.lengthSqr() < 1.0E-8) {
            return;
        }
        double axisLength = axis.length();
        Vec3 forward = axis.scale(1.0 / axisLength);
        Vec3 center = start.add(end).scale(0.5);
        Vec3 view = camera.getPosition().subtract(center);
        Vec3 side = forward.cross(view.normalize());
        if (side.lengthSqr() < 1.0E-8) {
            side = forward.cross(new Vec3(0, 1, 0));
        }
        if (side.lengthSqr() < 1.0E-8) {
            side = forward.cross(new Vec3(1, 0, 0));
        }

        float axialMargin = Math.max(riftWidth * 24f, riftLength * 0.22f);
        float uvY = riftWidth * 28f;
        double worldPerLocalX = axisLength / (2.0 * riftLength);
        double worldPerLocalY = axisLength / riftLength;
        double halfWidth = Math.max(0.08, worldPerLocalY * uvY);
        side = side.normalize().scale(halfWidth);
        Vec3 tipMargin = forward.scale(worldPerLocalX * axialMargin);
        Vec3 meshStart = start.subtract(tipMargin);
        Vec3 meshEnd = end.add(tipMargin);
        Vec3 cam = camera.getPosition();
        Vec3 a = meshStart.subtract(cam).add(side);
        Vec3 b = meshStart.subtract(cam).subtract(side);
        Vec3 c = meshEnd.subtract(cam).subtract(side);
        Vec3 d = meshEnd.subtract(cam).add(side);
        vertex(out, a, -riftLength - axialMargin, uvY);
        vertex(out, b, -riftLength - axialMargin, -uvY);
        vertex(out, c, riftLength + axialMargin, -uvY);
        vertex(out, d, riftLength + axialMargin, uvY);
    }

    private static void vertex(VertexConsumer out, Vec3 p, float u, float v) {
        out.vertex(p.x, p.y, p.z).color(1f, 1f, 1f, 1f).uv(u, v).endVertex();
    }

    @Override
    public void tick() {
        if (age++ >= lifetime) {
            remove();
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
