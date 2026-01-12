package com.til.recasting.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.awt.*;
import java.util.stream.Stream;

/**
 * 默认粒子类 - 从 1.16.5 迁移到 1.20.1
 * 支持自定义纹理、大小、颜色、旋转等效果
 * 
 * @author til
 */
@OnlyIn(Dist.CLIENT)
public class DefaultParticle extends Particle {

    /**
     * 生命周期的一半（用于大小变化计算）
     */
    protected float particleHalfAge;

    /**
     * 运动衰减系数
     */
    @Nullable
    protected Vec3 moveAttenuation;

    /**
     * 重力系数
     */
    protected float particleGravity;

    /**
     * 粒子大小
     */
    protected float size = 1;

    /**
     * 粒子大小变化类型
     */
    protected SizeChangeType sizeChangeType;

    /**
     * 粒子的旋转角度（弧度）
     */
    protected float roll;

    /**
     * 上一刻的旋转角度
     */
    protected float oldRoll;

    /**
     * 旋转速度（弧度/tick）
     */
    protected float rollSpeed;

    /**
     * 是否启用碰撞
     */
    protected boolean enableCollision = false;

    /**
     * 粒子渲染类型（用于自定义纹理）
     */
    @Nullable
    protected ParticleRenderType customRenderType;

    public DefaultParticle(ClientLevel level, double x, double y, double z) {
        super(level, x, y, z);
        this.x = x;
        this.y = y;
        this.z = z;
        this.xo = x;
        this.yo = y;
        this.zo = z;
    }

    /**
     * 设置大小
     */
    public DefaultParticle setSize(float size) {
        this.size = size;
        this.setSize(size, size);
        return this;
    }

    /**
     * 设置生命周期
     */
    public DefaultParticle setLifeTime(int lifetime) {
        this.lifetime = lifetime;
        this.particleHalfAge = lifetime * 0.5f;
        return this;
    }

    /**
     * 设置运动速度
     */
    public DefaultParticle setMove(double motionX, double motionY, double motionZ) {
        this.xd = motionX;
        this.yd = motionY;
        this.zd = motionZ;
        return this;
    }

    /**
     * 设置移动衰减
     */
    public DefaultParticle setMoveAttenuation(Vec3 moveAttenuation) {
        this.moveAttenuation = moveAttenuation;
        return this;
    }

    /**
     * 设置位置
     */
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

    /**
     * 设置颜色
     */
    public DefaultParticle setColor(Color color) {
        this.rCol = color.getRed() / 255f;
        this.gCol = color.getGreen() / 255f;
        this.bCol = color.getBlue() / 255f;
        this.alpha = color.getAlpha() / 255f;
        return this;
    }

    /**
     * 设置重力
     */
    public DefaultParticle setParticleGravity(float particleGravity) {
        this.particleGravity = particleGravity;
        return this;
    }

    /**
     * 设置大小变化类型
     */
    public DefaultParticle setSizeChangeType(SizeChangeType sizeChangeType) {
        this.sizeChangeType = sizeChangeType;
        return this;
    }

    /**
     * 设置是否启用碰撞
     */
    public DefaultParticle setParticleCollide(boolean particleCollide) {
        this.enableCollision = particleCollide;
        return this;
    }

    /**
     * 设置旋转速度
     */
    public DefaultParticle setRollSpeed(float rollSpeed) {
        this.rollSpeed = rollSpeed;
        return this;
    }

    /**
     * 设置自定义渲染类型
     */
    public DefaultParticle setCustomRenderType(@Nullable ParticleRenderType renderType) {
        this.customRenderType = renderType;
        return this;
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

        // 处理碰撞
        if (enableCollision) {
            Vec3 motion = new Vec3(xd, yd, zd);
            this.move(motion.x, motion.y, motion.z);
        } else {
            this.x += this.xd;
            this.y += this.yd;
            this.z += this.zd;
        }

        // 应用运动衰减
        if (moveAttenuation != null) {
            this.xd *= moveAttenuation.x;
            this.yd *= moveAttenuation.y;
            this.zd *= moveAttenuation.z;
        }

        // 应用重力
        if (particleGravity != 0) {
            this.yd -= 0.04D * (double) this.particleGravity;
        }

        // 更新旋转
        this.oldRoll = this.roll;
        this.roll += this.rollSpeed;
    }

    @Override
    public void render(@NotNull VertexConsumer consumer, Camera camera, float partialTick) {
        Vec3 cameraPos = camera.getPosition();
        
        // 计算插值后的位置
        float x = (float) (Mth.lerp(partialTick, this.xo, this.x) - cameraPos.x());
        float y = (float) (Mth.lerp(partialTick, this.yo, this.y) - cameraPos.y());
        float z = (float) (Mth.lerp(partialTick, this.zo, this.z) - cameraPos.z());

        // 获取四元数旋转
        Quaternionf quaternion;
        if (this.roll == 0.0F) {
            quaternion = camera.rotation();
        } else {
            quaternion = new Quaternionf(camera.rotation());
            float interpolatedRoll = Mth.lerp(partialTick, this.oldRoll, this.roll);
            quaternion.rotateZ(interpolatedRoll);
        }

        // 计算当前大小
        float currentSize = size;
        if (sizeChangeType != null) {
            float timeLife = age / particleHalfAge;
            timeLife = timeLife > 1 ? -timeLife + 2 : timeLife;
            switch (sizeChangeType) {
                case SIN:
                    currentSize = (float) (size * Math.sin(timeLife * Math.PI / 2));
                    break;
                case SQUARE_SIN:
                    currentSize = (float) (size * Math.sin(Math.sqrt(timeLife) * Math.PI / 2));
                    break;
                case COS:
                    currentSize = (float) (size * Math.cos((1 - timeLife) * Math.PI / 2));
                    break;
                case SQUARE_COS:
                    currentSize = (float) (size * Math.cos(Math.sqrt(1 - timeLife) * Math.PI / 2));
                    break;
                case SMOOTH:
                    currentSize = size * timeLife;
                    break;
            }
        }

        // 创建四个顶点
        Vector3f[] vertices = new Vector3f[]{
                new Vector3f(-1.0F, -1.0F, 0.0F),
                new Vector3f(-1.0F, 1.0F, 0.0F),
                new Vector3f(1.0F, 1.0F, 0.0F),
                new Vector3f(1.0F, -1.0F, 0.0F)
        };

        // 应用旋转和缩放
        for (int i = 0; i < 4; ++i) {
            Vector3f vertex = vertices[i];
            vertex.rotate(quaternion);
            vertex.mul(currentSize);
            vertex.add(x, y, z);
        }

        // 光照值（全亮）
        int light = 15728880; // 等同于 15 << 20 | 15 << 4

        // UV 坐标
        float u0 = 0.0F;
        float u1 = 1.0F;
        float v0 = 0.0F;
        float v1 = 1.0F;

        // 渲染四个顶点
        consumer.vertex(vertices[0].x(), vertices[0].y(), vertices[0].z())
                .uv(u1, v1)
                .color(this.rCol, this.gCol, this.bCol, this.alpha)
                .uv2(light)
                .endVertex();
        
        consumer.vertex(vertices[1].x(), vertices[1].y(), vertices[1].z())
                .uv(u1, v0)
                .color(this.rCol, this.gCol, this.bCol, this.alpha)
                .uv2(light)
                .endVertex();
        
        consumer.vertex(vertices[2].x(), vertices[2].y(), vertices[2].z())
                .uv(u0, v0)
                .color(this.rCol, this.gCol, this.bCol, this.alpha)
                .uv2(light)
                .endVertex();
        
        consumer.vertex(vertices[3].x(), vertices[3].y(), vertices[3].z())
                .uv(u0, v1)
                .color(this.rCol, this.gCol, this.bCol, this.alpha)
                .uv2(light)
                .endVertex();
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        // 如果设置了自定义渲染类型，使用自定义的
        if (customRenderType != null) {
            return customRenderType;
        }
        // 否则使用半透明渲染类型（适合大多数发光粒子）
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    /**
     * 粒子大小变化类型枚举
     */
    public enum SizeChangeType {
        /**
         * 正弦变化
         */
        SIN,
        
        /**
         * 平方根正弦变化（更缓和）
         */
        SQUARE_SIN,
        
        /**
         * 余弦变化
         */
        COS,
        
        /**
         * 平方根余弦变化（更缓和）
         */
        SQUARE_COS,
        
        /**
         * 线性平滑变化
         */
        SMOOTH
    }
}

