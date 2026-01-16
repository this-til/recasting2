package com.til.recasting.client.particle;

import com.til.recasting.util.NumberPack;
import com.til.recasting.util.RandomUtil;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.Random;

/**
 * 攻击小粒子提供者
 * 对应原来的 ATTACK_SMALL_PARTICLE_CLIENT
 * 
 * @author til
 */
@OnlyIn(Dist.CLIENT)
public class AttackSmallParticleProvider implements ParticleProvider<SimpleParticleType> {
    
    /**
     * 粒子数量
     */
    private final int number;
    
    /**
     * 移动范围
     */
    private final NumberPack move;
    
    /**
     * 生命周期范围
     */
    private final NumberPack life;
    
    /**
     * 大小范围
     */
    private final NumberPack size;
    
    /**
     * 随机数生成器
     */
    private final Random random = new Random();
    
    /**
     * 默认颜色（白色）
     */
    private static final Color DEFAULT_COLOR = new Color(255, 255, 255, 255);
    
    public AttackSmallParticleProvider() {
        this(3, new NumberPack(0, 0.15f), new NumberPack(9, 27), new NumberPack(1, 0.5f));
    }
    
    public AttackSmallParticleProvider(int number, NumberPack move, NumberPack life, NumberPack size) {
        this.number = number;
        this.move = move;
        this.life = life;
        this.size = size;
    }
    
    @Nullable
    @Override
    public Particle createParticle(SimpleParticleType type, ClientLevel level, 
                                   double x, double y, double z, 
                                   double xSpeed, double ySpeed, double zSpeed) {
        // 这个方法用于单个粒子的创建，但我们需要创建多个粒子
        // 所以这个方法主要用于注册，实际创建通过 createSmallParticles 方法
        return createSmallParticles(level, x, y, z, DEFAULT_COLOR, null);
    }
    
    /**
     * 创建多个小粒子
     * 
     * @param level 客户端世界
     * @param x X 坐标
     * @param y Y 坐标
     * @param z Z 坐标
     * @param color 颜色
     * @param resourceLocation 纹理资源位置（可选）
     * @return 返回第一个创建的粒子（用于兼容 ParticleProvider 接口）
     */
    @Nullable
    public Particle createSmallParticles(ClientLevel level, double x, double y, double z, 
                                        @Nullable Color color, @Nullable ResourceLocation resourceLocation) {
        Color particleColor = color != null ? color : DEFAULT_COLOR;
        double density = 1.0; // 密度，可以根据需要调整
        
        DefaultParticle firstParticle = null;
        
        for (int i = 0; i < number * density; i++) {
            net.minecraft.world.phys.Vec3 moveVec = RandomUtil.nextVector3dOnCircles(random, 1.0)
                    .scale(move.of(random.nextFloat()));
            
            DefaultParticle particle = new DefaultParticle(level, x, y, z);
            particle.setMove(moveVec.x, moveVec.y, moveVec.z)
                    .setLifeTime((int) life.of(random.nextFloat()))
                    .setColor(particleColor)
                    .setSize((float) size.of(random.nextFloat()))
                    .setSizeChangeType(DefaultParticle.SizeChangeType.SQUARE_SIN)
                    .setParticleCollide(false);
            
            // 添加到世界中
            if (firstParticle == null) {
                firstParticle = particle;
            }
        }
        
        return firstParticle;
    }
}

