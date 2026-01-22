package com.til.recasting.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

/**
 * 攻击粒子提供者
 * 对应原来的 ATTACK_PARTICLE_CLIENT
 *
 * @author til
 */
@OnlyIn(Dist.CLIENT)
public class AttackParticleProvider implements ParticleProvider<SimpleParticleType> {

    /**
     * 粒子大小
     */
    private final float size = 4.5f;

    /**
     * 粒子生命周期
     */
    private final int life = 9;

    @Nullable
    @Override
    public Particle createParticle(
            @NotNull SimpleParticleType type, @NotNull ClientLevel level,
            double x, double y, double z,
            double xSpeed, double ySpeed, double zSpeed
    ) {
        DefaultParticle particle = new DefaultParticle(level, x, y, z);

        particle.setSize(size)
                .setSizeChangeType(DefaultParticle.SizeChangeType.SQUARE_SIN)
                .setParticleCollide(false)
                .setLifeTime(life);

        return particle;
    }
}


