package com.til.recasting.client.particle;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
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
 * 可链式配置的通用 Billboard 粒子，自 1.16.5 GlowingFireGlow DefaultParticle 迁移。
 * <p>
 * 支持自定义纹理、尺寸曲线、速度衰减、重力与自转；无纹理时走 {@link #NULL_TEXTURE}。
 */
@OnlyIn(Dist.CLIENT)
public class DefaultParticle extends Particle {

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

    /** 粒子贴图；为 null 时使用 {@link #NULL_TEXTURE}。 */
    @Nullable
    protected ResourceLocation textureName;

    /** 按贴图缓存的 {@link ParticleRenderType}，避免每帧新建。 */
    public static final Map<ResourceLocation, ParticleRenderType> map = new HashMap<>();

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
        this.particleHalfAge = lifetime * 0.5f;
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

    /**
     * 关闭 Forge 视锥剔除；默认粒子包围盒过小，远距会被 frustum 裁掉。
     */
    @Override
    public boolean shouldCull() {
        return false;
    }

    /**
     * 放大渲染用包围盒，避免远距/大尺寸粒子因默认 0.2 盒被裁切。
     * 开启方块碰撞时仍用父类盒，以免碰撞范围异常。
     */
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

        // 位移：可选方块碰撞
        if (enableCollision) {
            Vec3 motion = new Vec3(xd, yd, zd);
            this.move(motion.x, motion.y, motion.z);
        } else {
            this.x += this.xd;
            this.y += this.yd;
            this.z += this.zd;
        }

        // 速度衰减
        if (moveAttenuation != null) {
            this.xd *= moveAttenuation.x;
            this.yd *= moveAttenuation.y;
            this.zd *= moveAttenuation.z;
        }

        // 重力
        if (particleGravity != 0) {
            this.yd -= 0.04D * (double) this.particleGravity;
        }

        this.oldRoll = this.roll;
        this.roll += this.rollSpeed;
    }

    @Override
    public void render(@NotNull VertexConsumer buffer, Camera camera, float partialTick) {
        // 相对相机的插值位置
        Vec3 cameraPos = camera.getPosition();
        Vector3f addPos = new Vector3f(
                (float) (Mth.lerp(partialTick, this.xo, this.x) - cameraPos.x()),
                (float) (Mth.lerp(partialTick, this.yo, this.y) - cameraPos.y()),
                (float) (Mth.lerp(partialTick, this.zo, this.z) - cameraPos.z())
        );

        // Billboard 朝向；有滚转时绕 Z 叠加
        Quaternionf quaternion;
        if (this.roll == 0.0F) {
            quaternion = camera.rotation();
        } else {
            quaternion = new Quaternionf(camera.rotation());
            float f3 = Mth.lerp(partialTick, this.oldRoll, this.roll);
            quaternion.rotateZ(f3);
        }

        // 尺寸曲线：timeLife 为寿命三角波（前半 0→1，后半 1→0）
        float currentSize = size;
        if (sizeChangeType != null) {
            float timeLife = age / particleHalfAge;
            timeLife = timeLife > 1 ? -timeLife + 2 : timeLife;
            switch (sizeChangeType) {
                case SIN -> currentSize = (float) (size * Math.sin(timeLife));
                case SQUARE_SIN -> currentSize = (float) (size * Math.sin(Math.sqrt(timeLife)));
                case COS -> currentSize = (float) (size * Math.cos(timeLife));
                case SQUARE_COS -> currentSize = (float) (size * Math.cos(Math.sqrt(timeLife)));
                case SMOOTH -> currentSize = size * timeLife;
            }
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

        // 满亮度光照，避免环境光压暗发光粒子
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

    /**
     * 按 {@link #textureName} 返回渲染类型；首次遇到的贴图会创建并缓存到 {@link #map}。
     * 使用 SRC_ALPHA / ONE 加法混合，适合发光类粒子。
     */
    @Override
    public @NotNull ParticleRenderType getRenderType() {
        if (textureName == null) {
            return NULL_TEXTURE;
        }
        if (map.containsKey(textureName)) {
            return map.get(textureName);
        }
        ResourceLocation texture = textureName;
        ParticleRenderType particleRenderType = new ParticleRenderType() {
            @Override
            public void begin(BufferBuilder bufferBuilder, TextureManager textureManager) {
                RenderSystem.depthMask(false);
                RenderSystem.enableBlend();
                RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
                RenderSystem.setShader(GameRenderer::getParticleShader);
                RenderSystem.setShaderTexture(0, texture);
                textureManager.getTexture(texture).setFilter(true, false);
                bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
            }

            @Override
            public void end(Tesselator tesselator) {
                tesselator.end();
                Minecraft.getInstance().getTextureManager().getTexture(TextureAtlas.LOCATION_PARTICLES).setFilter(false, false);
                RenderSystem.disableBlend();
                RenderSystem.depthMask(true);
            }

            @Override
            public String toString() {
                return "recasting:" + texture;
            }
        };
        map.put(texture, particleRenderType);
        return particleRenderType;
    }

    /**
     * 无自定义贴图时的回退渲染类型：默认混合、不绑定额外纹理。
     */
    public static final ParticleRenderType NULL_TEXTURE = new ParticleRenderType() {
        @Override
        public void begin(BufferBuilder buffer, TextureManager textureManager) {
            RenderSystem.depthMask(false);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShader(GameRenderer::getParticleShader);
            buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
        }

        @Override
        public void end(Tesselator tess) {
            tess.end();
            RenderSystem.disableBlend();
            RenderSystem.depthMask(true);
        }

        @Override
        public String toString() {
            return "recasting:null_texture";
        }
    };

    /**
     * 生命周期内尺寸相对 {@link #size} 的变化方式。
     * 输入为寿命三角波 {@code timeLife ∈ [0, 1]}（前半升、后半降）。
     */
    public enum SizeChangeType {
        /** {@code size * sin(timeLife)} */
        SIN,
        /** {@code size * sin(√timeLife)}，前半膨胀更快 */
        SQUARE_SIN,
        /** {@code size * cos(timeLife)} */
        COS,
        /** {@code size * cos(√timeLife)} */
        SQUARE_COS,
        /** {@code size * timeLife}，随三角波线性缩放 */
        SMOOTH
    }
}
