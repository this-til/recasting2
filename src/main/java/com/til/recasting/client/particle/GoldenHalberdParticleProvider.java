package com.til.recasting.client.particle;

import com.til.recasting.Recasting;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

/**
 * 金戈满层引爆粒子（环状冲击）。
 * 颜色通过 {@code sendParticles(..., count=0, r, g, b, 1.0)} 的速度通道传入（0~1）。
 */
@OnlyIn(Dist.CLIENT)
public class GoldenHalberdParticleProvider implements ParticleProvider<SimpleParticleType> {

    public static final ResourceLocation TEXTURE = Recasting.prefix("particle/other/lizi-18.png");

    private final float size = 3.5f;
    private final int life = 12;

    @Nullable
    @Override
    public Particle createParticle(
            @NotNull SimpleParticleType type,
            @NotNull ClientLevel level,
            double x, double y, double z,
            double xSpeed, double ySpeed, double zSpeed
    ) {
        DefaultParticle particle = new DefaultParticle(level, x, y, z);
        particle.setSize(size)
                .setSizeChangeType(DefaultParticle.SizeChangeType.SQUARE_SIN)
                .setParticleCollide(false)
                .setLifeTime(life)
                .setColor(resolveColor(xSpeed, ySpeed, zSpeed))
                .setTextureName(TEXTURE);
        return particle;
    }

    private static Color resolveColor(double r, double g, double b) {
        return new Color(
                Mth.clamp(Mth.floor(r * 255.0 + 0.5), 0, 255),
                Mth.clamp(Mth.floor(g * 255.0 + 0.5), 0, 255),
                Mth.clamp(Mth.floor(b * 255.0 + 0.5), 0, 255)
        );
    }
}
