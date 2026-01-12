package com.til.recasting.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

/**
 * 默认粒子提供者
 * 负责创建 DefaultParticle 实例
 * 
 * @author til
 */
@OnlyIn(Dist.CLIENT)
public class DefaultParticleProvider implements ParticleProvider<SimpleParticleType> {

    /**
     * 创建粒子实例
     * 
     * @param type 粒子类型
     * @param level 客户端世界
     * @param x X 坐标
     * @param y Y 坐标
     * @param z Z 坐标
     * @param xSpeed X 方向速度
     * @param ySpeed Y 方向速度
     * @param zSpeed Z 方向速度
     * @return 粒子实例，如果创建失败返回 null
     */
    @Nullable
    @Override
    public Particle createParticle(SimpleParticleType type, ClientLevel level, 
                                   double x, double y, double z, 
                                   double xSpeed, double ySpeed, double zSpeed) {
        DefaultParticle particle = new DefaultParticle(level, x, y, z);
        particle.setMove(xSpeed, ySpeed, zSpeed);
        return particle;
    }
}

