package com.til.recasting.client.particle;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 可链式配置的通用 Billboard 粒子。
 * <p>
 * 独立 {@link ParticleRenderType} 通道 + 自有 {@link BufferBuilder} 批绘。
 * <p>
 * 兼容假人（dummmmmmy）伤害数字：其 {@code ParticleRenderType.CUSTOM} 在粒子阶段用 Font +
 * {@code renderBuffers().bufferSource().endBatch()}，会弄脏 lightmap / 纹理单元 / 混合状态；
 * Forge 又将自定义 RenderType 排在 CUSTOM 之后，导致同帧后续粒子全灭（同类问题见 Forge#6706）。
 * 本类在 {@code begin} 中完整恢复渲染状态后再开批。
 */
@OnlyIn(Dist.CLIENT)
public class DefaultParticle extends Particle {

    /** 本模组粒子批绘专用 Buffer，与引擎 Tesselator / 共享 BufferSource 隔离。 */
    private static final BufferBuilder BATCH_BUFFER = new BufferBuilder(512 * 1024);

    /** 当前批正在写入的 Buffer；仅在对应 {@link ParticleRenderType#begin}～{@code end} 期间非 null。 */
    @Nullable
    protected static BufferBuilder activeBatch;

    /** 生命周期一半的 tick 数，供尺寸曲线将寿命映射为 0→1→0 的三角波。 */
    protected float particleHalfAge;

    /** 每 tick 对速度分量的乘积衰减；为 null 时不衰减。 */
    @Nullable
    protected Vec3 moveAttenuation;

    /** 重力系数；非 0 时每 tick 对 {@code yd} 施加 {@code -0.04 * particleGravity}。 */
    protected float particleGravity;

    /** 基础渲染半宽（四边形边长的一半）。 */
    protected float size = 1;

    /** 生命周期内尺寸变化曲线；为 null 时保持 {@link #size} 不变。 */
    protected SizeChangeType sizeChangeType;

    /** 当前绕视线轴的滚转角（弧度）。 */
    protected float roll;

    /** 上一 tick 的滚转角，用于渲染插值。 */
    protected float oldRoll;

    /** 每 tick 滚转增量（弧度）。 */
    protected float rollSpeed;

    /** 为 true 时走 {@link #move} 做方块碰撞；否则直接累加坐标。 */
    protected boolean enableCollision = false;

    /** 粒子贴图；为 null 时走 {@link #NULL_TEXTURE} 通道。 */
    @Nullable
    protected ResourceLocation textureName;

    /**
     * 是否使用加法混合。亮色发光应为 true；暗色/黑色必须为 false，否则几乎不可见。
     */
    protected boolean additiveBlend = true;

    /** 按贴图缓存的加法批绘通道，同贴图粒子共用一个 {@link ParticleRenderType}。 */
    public static final Map<ResourceLocation, ParticleRenderType> map = new HashMap<>();

    /** 按贴图缓存的半透明批绘通道（SRC_ALPHA, ONE_MINUS_SRC_ALPHA）。 */
    public static final Map<ResourceLocation, ParticleRenderType> translucentMap = new HashMap<>();

    public DefaultParticle(ClientLevel level, double x, double y, double z) {
        super(level, x, y, z);
        this.x = x;
        this.y = y;
        this.z = z;
        this.xo = x;
        this.yo = y;
        this.zo = z;
    }

    public DefaultParticle setSize(float size) {
        this.size = size;
        this.setSize(size, size);
        return this;
    }

    /**
     * 设置生命周期，并同步更新 {@link #particleHalfAge}。
     */
    public DefaultParticle setLifeTime(int lifetime) {
        this.lifetime = lifetime;
        this.particleHalfAge = Math.max(lifetime * 0.5f, 0.5f);
        return this;
    }

    public DefaultParticle setMove(double motionX, double motionY, double motionZ) {
        this.xd = motionX;
        this.yd = motionY;
        this.zd = motionZ;
        return this;
    }

    /**
     * 设置速度衰减向量；各分量在每 tick 末乘到对应速度上。
     */
    public DefaultParticle setMoveAttenuation(Vec3 moveAttenuation) {
        this.moveAttenuation = moveAttenuation;
        return this;
    }

    public DefaultParticle setPosition(double x, double y, double z) {
        this.xo = x;
        this.yo = y;
        this.zo = z;
        this.x = x;
        this.y = y;
        this.z = z;
        this.setSize(size, size);
        return this;
    }

    public DefaultParticle setColor(Color color) {
        this.rCol = color.getRed() / 255f;
        this.gCol = color.getGreen() / 255f;
        this.bCol = color.getBlue() / 255f;
        this.alpha = color.getAlpha() / 255f;
        return this;
    }

    public DefaultParticle setParticleGravity(float particleGravity) {
        this.particleGravity = particleGravity;
        return this;
    }

    public DefaultParticle setSizeChangeType(SizeChangeType sizeChangeType) {
        this.sizeChangeType = sizeChangeType;
        return this;
    }

    public DefaultParticle setParticleCollide(boolean particleCollide) {
        this.enableCollision = particleCollide;
        return this;
    }

    public DefaultParticle setRollSpeed(float rollSpeed) {
        this.rollSpeed = rollSpeed;
        return this;
    }

    public DefaultParticle setTextureName(@Nullable ResourceLocation textureName) {
        this.textureName = textureName;
        return this;
    }

    public DefaultParticle setAdditiveBlend(boolean additiveBlend) {
        this.additiveBlend = additiveBlend;
        return this;
    }

    @Override
    public boolean shouldCull() {
        return false;
    }

    @Override
    public @NotNull AABB getBoundingBox() {
        if (enableCollision) {
            return super.getBoundingBox();
        }
        double extent = Math.max(this.size * 2.0, 16.0);
        return new AABB(
                this.x - extent,
                this.y - extent,
                this.z - extent,
                this.x + extent,
                this.y + extent,
                this.z + extent
        );
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

        if (enableCollision) {
            Vec3 motion = new Vec3(xd, yd, zd);
            this.move(motion.x, motion.y, motion.z);
        } else {
            this.x += this.xd;
            this.y += this.yd;
            this.z += this.zd;
        }

        if (moveAttenuation != null) {
            this.xd *= moveAttenuation.x;
            this.yd *= moveAttenuation.y;
            this.zd *= moveAttenuation.z;
        }

        if (particleGravity != 0) {
            this.yd -= 0.04D * (double) this.particleGravity;
        }

        this.oldRoll = this.roll;
        this.roll += this.rollSpeed;
    }

    @Override
    public void render(@NotNull VertexConsumer ignored, Camera camera, float partialTick) {
        BufferBuilder buffer = activeBatch;
        if (buffer == null) {
            return;
        }

        float currentSize = resolveCurrentSize();
        if (currentSize <= 1.0E-4f || this.alpha <= 1.0E-4f) {
            return;
        }

        Vec3 cameraPos = camera.getPosition();
        Vector3f addPos = new Vector3f(
                (float) (Mth.lerp(partialTick, this.xo, this.x) - cameraPos.x()),
                (float) (Mth.lerp(partialTick, this.yo, this.y) - cameraPos.y()),
                (float) (Mth.lerp(partialTick, this.zo, this.z) - cameraPos.z())
        );

        Quaternionf quaternion;
        if (this.roll == 0.0F) {
            quaternion = camera.rotation();
        } else {
            quaternion = new Quaternionf(camera.rotation());
            float f3 = Mth.lerp(partialTick, this.oldRoll, this.roll);
            quaternion.rotateZ(f3);
        }

        Vector3f[] vertices = new Vector3f[]{
                new Vector3f(-1.0F, -1.0F, 0.0F),
                new Vector3f(-1.0F, 1.0F, 0.0F),
                new Vector3f(1.0F, 1.0F, 0.0F),
                new Vector3f(1.0F, -1.0F, 0.0F)
        };
        for (int i = 0; i < 4; ++i) {
            Vector3f vertex = vertices[i];
            vertex.rotate(quaternion);
            vertex.mul(currentSize);
            vertex.add(addPos);
        }

        int combined = 15 << 20 | 15 << 4;
        buffer.vertex(vertices[0].x(), vertices[0].y(), vertices[0].z())
                .uv(0, 0)
                .color(this.rCol, this.gCol, this.bCol, this.alpha)
                .uv2(combined)
                .endVertex();
        buffer.vertex(vertices[1].x(), vertices[1].y(), vertices[1].z())
                .uv(0, 1)
                .color(this.rCol, this.gCol, this.bCol, this.alpha)
                .uv2(combined)
                .endVertex();
        buffer.vertex(vertices[2].x(), vertices[2].y(), vertices[2].z())
                .uv(1, 1)
                .color(this.rCol, this.gCol, this.bCol, this.alpha)
                .uv2(combined)
                .endVertex();
        buffer.vertex(vertices[3].x(), vertices[3].y(), vertices[3].z())
                .uv(1, 0)
                .color(this.rCol, this.gCol, this.bCol, this.alpha)
                .uv2(combined)
                .endVertex();
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        if (textureName == null) {
            return additiveBlend ? NULL_TEXTURE : NULL_TEXTURE_TRANSLUCENT;
        }
        Map<ResourceLocation, ParticleRenderType> cache = additiveBlend ? map : translucentMap;
        ParticleRenderType cached = cache.get(textureName);
        if (cached != null) {
            return cached;
        }
        ResourceLocation texture = textureName;
        String prefix = additiveBlend ? "recasting:default_particle:" : "recasting:default_particle_alpha:";
        ParticleRenderType type = createBatchType(prefix + texture, texture, additiveBlend);
        cache.put(texture, type);
        return type;
    }

    /**
     * 无贴图时的独立批绘通道（显式绑粒子图集）。
     */
    public static final ParticleRenderType NULL_TEXTURE =
            createBatchType("recasting:default_particle:null", TextureAtlas.LOCATION_PARTICLES, true);

    public static final ParticleRenderType NULL_TEXTURE_TRANSLUCENT =
            createBatchType("recasting:default_particle_alpha:null", TextureAtlas.LOCATION_PARTICLES, false);

    private static ParticleRenderType createBatchType(String name, ResourceLocation texture, boolean additive) {
        return new ParticleRenderType() {
            @Override
            public void begin(BufferBuilder engineBuffer, TextureManager textureManager) {
                restoreParticlePipelineAfterCustom();
                RenderSystem.depthMask(false);
                RenderSystem.enableBlend();
                if (additive) {
                    RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
                } else {
                    RenderSystem.blendFunc(
                            GlStateManager.SourceFactor.SRC_ALPHA,
                            GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA
                    );
                }
                RenderSystem.setShader(GameRenderer::getParticleShader);
                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
                RenderSystem.setShaderTexture(0, texture);
                textureManager.getTexture(texture).setFilter(true, false);
                if (BATCH_BUFFER.building()) {
                    BufferUploader.drawWithShader(BATCH_BUFFER.end());
                }
                BATCH_BUFFER.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
                activeBatch = BATCH_BUFFER;
            }

            @Override
            public void end(Tesselator ignored) {
                activeBatch = null;
                if (BATCH_BUFFER.building()) {
                    BufferUploader.drawWithShader(BATCH_BUFFER.end());
                }
                Minecraft.getInstance().getTextureManager().getTexture(TextureAtlas.LOCATION_PARTICLES).setFilter(false, false);
                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
                RenderSystem.disableBlend();
                RenderSystem.depthMask(true);
                RenderSystem.defaultBlendFunc();
                // 供同帧后续自定义粒子类型继续使用 lightmap
                Minecraft.getInstance().gameRenderer.lightTexture().turnOnLightLayer();
            }

            @Override
            public String toString() {
                return name;
            }
        };
    }

    /**
     * 假人伤害数字在 CUSTOM 阶段 endBatch 后，lightmap / 活动纹理单元往往已被拆掉；
     * 粒子着色器仍采样 lightmap，未恢复则整批不可见。
     */
    private static void restoreParticlePipelineAfterCustom() {
        Minecraft.getInstance().gameRenderer.lightTexture().turnOnLightLayer();
        RenderSystem.activeTexture(org.lwjgl.opengl.GL13.GL_TEXTURE2);
        RenderSystem.activeTexture(org.lwjgl.opengl.GL13.GL_TEXTURE0);
        RenderSystem.enableDepthTest();
    }

    private float resolveCurrentSize() {
        float currentSize = size;
        if (sizeChangeType == null) {
            return currentSize;
        }
        float half = particleHalfAge <= 0.0f ? 0.5f : particleHalfAge;
        float timeLife = age / half;
        timeLife = timeLife > 1 ? -timeLife + 2 : timeLife;
        timeLife = Mth.clamp(timeLife, 0.0f, 1.0f);
        return switch (sizeChangeType) {
            case SIN -> (float) (size * Math.sin(timeLife * Math.PI * 0.5));
            case SQUARE_SIN -> (float) (size * Math.sin(Math.sqrt(timeLife) * Math.PI * 0.5));
            case FLASH_SIN -> (float) (size * Math.sin(Math.pow(timeLife, 0.25) * Math.PI * 0.5));
            case COS -> (float) (size * Math.cos(timeLife * Math.PI * 0.5));
            case SQUARE_COS -> (float) (size * Math.cos(Math.sqrt(timeLife) * Math.PI * 0.5));
            case FLASH_COS -> (float) (size * Math.cos(Math.pow(timeLife, 0.25) * Math.PI * 0.5));
            case SMOOTH -> size * timeLife;
            case FLASH -> (float) (size * Math.pow(timeLife, 0.25));
        };
    }

    /**
     * 生命周期内尺寸相对 {@link #size} 的变化方式。
     * 输入为寿命三角波 {@code timeLife ∈ [0, 1]}（前半升、后半降）。
     */
    public enum SizeChangeType {
        /** {@code size * sin(π/2 * timeLife)} */
        SIN,
        /** {@code size * sin(π/2 * √timeLife)}，前半膨胀更快 */
        SQUARE_SIN,
        /** {@code size * sin(π/2 * ∜timeLife)}，胀缩极快，贴合闪光 */
        FLASH_SIN,
        /** {@code size * cos(π/2 * timeLife)} */
        COS,
        /** {@code size * cos(π/2 * √timeLife)} */
        SQUARE_COS,
        /** {@code size * cos(π/2 * ∜timeLife)}，衰减极快 */
        FLASH_COS,
        /** {@code size * timeLife}，随三角波线性缩放 */
        SMOOTH,
        /** {@code size * ∜timeLife}，胀缩极快，无 sin 压缩 */
        FLASH
    }
}
