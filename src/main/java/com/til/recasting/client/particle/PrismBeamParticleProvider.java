package com.til.recasting.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

/**
 * 光棱射线粒子：使用 {@link DefaultParticle}（{@link DefaultParticle#shouldCull()} 为 false）。
 * 颜色通过 {@code sendParticles(..., count=0, r, g, b, 1.0)} 的速度通道传入（0~1）。
 */
@OnlyIn(Dist.CLIENT)
public class PrismBeamParticleProvider implements ParticleProvider<SimpleParticleType> {

    private static final float SIZE = 0.45f;
    private static final int LIFE = 6;

    @Nullable
    @Override
    public Particle createParticle(
            @NotNull SimpleParticleType type,
            @NotNull ClientLevel level,
            double x, double y, double z,
            double xSpeed, double ySpeed, double zSpeed
    ) {
        Color color = new Color(
                Mth.clamp(Mth.floor(xSpeed * 255.0 + 0.5), 0, 255),
                Mth.clamp(Mth.floor(ySpeed * 255.0 + 0.5), 0, 255),
                Mth.clamp(Mth.floor(zSpeed * 255.0 + 0.5), 0, 255),
                220
        );

        return new DefaultParticle(level, x, y, z)
                .setSize(SIZE)
                .setSizeChangeType(DefaultParticle.SizeChangeType.SQUARE_SIN)
                .setParticleCollide(false)
                .setLifeTime(LIFE)
                .setColor(color)
                .setTextureName(null);
    }
}
