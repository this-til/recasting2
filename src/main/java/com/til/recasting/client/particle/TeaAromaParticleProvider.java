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
import java.util.Random;

/**
 * 茶韵延迟释放：BladeRift 式 SDF 裂隙斩（单道主裂隙）。
 * 颜色通过 {@code sendParticles(..., count=0, r, g, b, 1.0)} 的速度通道传入（0~1）。
 */
@OnlyIn(Dist.CLIENT)
public class TeaAromaParticleProvider implements ParticleProvider<SimpleParticleType> {

    private static final int RIFT_LIFE = 24;

    private final Random random = new Random();

    @Nullable
    @Override
    public Particle createParticle(
            @NotNull SimpleParticleType type,
            @NotNull ClientLevel level,
            double x, double y, double z,
            double xSpeed, double ySpeed, double zSpeed
    ) {
        Color color = resolveColor(xSpeed, ySpeed, zSpeed);
        Color bright = brighten(color, 1.25f);
        float baseAngle = random.nextFloat() * (float) Math.PI;

        return new RiftSlashParticle(
                level, x, y, z,
                8.4f + random.nextFloat() * 1.6f,
                0.44f + random.nextFloat() * 0.16f,
                RIFT_LIFE,
                baseAngle + (random.nextFloat() - 0.5f) * 0.2f,
                withAlpha(bright, 240)
        );
    }

    private static Color resolveColor(double r, double g, double b) {
        return new Color(
                Mth.clamp(Mth.floor(r * 255.0 + 0.5), 0, 255),
                Mth.clamp(Mth.floor(g * 255.0 + 0.5), 0, 255),
                Mth.clamp(Mth.floor(b * 255.0 + 0.5), 0, 255)
        );
    }

    private static Color brighten(Color color, float factor) {
        return new Color(
                Mth.clamp(Mth.floor(color.getRed() * factor), 0, 255),
                Mth.clamp(Mth.floor(color.getGreen() * factor), 0, 255),
                Mth.clamp(Mth.floor(color.getBlue() * factor), 0, 255)
        );
    }

    private static Color withAlpha(Color color, int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), Mth.clamp(alpha, 0, 255));
    }
}
