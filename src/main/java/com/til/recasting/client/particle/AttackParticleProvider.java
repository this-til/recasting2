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
 * 攻击命中闪光（{@link DefaultParticle}）。
 * 颜色通过 {@code sendParticles(..., count=0, r, g, b, 1.0)} 的速度通道传入（0~1）。
 */
@OnlyIn(Dist.CLIENT)
public class AttackParticleProvider implements ParticleProvider<SimpleParticleType> {

    public static final ResourceLocation TEXTURE = Recasting.prefix("particle/other/small.png");

    private final float size = 4.5f;
    private final int life = 9;

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
                .setSizeChangeType(DefaultParticle.SizeChangeType.FLASH_SIN)
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
