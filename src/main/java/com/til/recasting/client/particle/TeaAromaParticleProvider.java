package com.til.recasting.client.particle;

import net.minecraft.client.Minecraft;
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
 * 茶韵延迟释放：BladeRift 式 SDF 裂隙斩。
 * 一道大裂隙为主，若干小裂隙交叉；大裂隙寿命 = 小裂隙 × 1.5。
 * 颜色通过 {@code sendParticles(..., count=0, r, g, b, 1.0)} 的速度通道传入（0~1）。
 */
@OnlyIn(Dist.CLIENT)
public class TeaAromaParticleProvider implements ParticleProvider<SimpleParticleType> {

    private static final int SMALL_RIFT_COUNT = 4;
    private static final int SMALL_RIFT_LIFE = 16;
    private static final float LARGE_LIFE_SCALE = 1.5f;

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

        int smallLife = SMALL_RIFT_LIFE;
        int largeLife = Math.max(1, Mth.ceil(smallLife * LARGE_LIFE_SCALE));
        float baseAngle = random.nextFloat() * (float) Math.PI;

        spawnSmallRifts(level, x, y, z, color, bright, baseAngle, smallLife);

        // 主粒子：一道大裂隙代替原中心核
        return new RiftSlashParticle(
                level, x, y, z,
                8.4f + random.nextFloat() * 1.6f,
                0.44f + random.nextFloat() * 0.16f,
                largeLife,
                baseAngle + (random.nextFloat() - 0.5f) * 0.2f,
                withAlpha(bright, 240)
        );
    }

    private void spawnSmallRifts(
            ClientLevel level,
            double x, double y, double z,
            Color color,
            Color bright,
            float baseAngle,
            int smallLife
    ) {
        for (int i = 0; i < SMALL_RIFT_COUNT; i++) {
            float roll = baseAngle + (float) Math.PI * (i + 0.5f) / SMALL_RIFT_COUNT
                    + (random.nextFloat() - 0.5f) * 0.4f;
            float length = 2.0f + random.nextFloat() * 1.2f;
            float width = 0.07f + random.nextFloat() * 0.1f;
            int life = smallLife + random.nextInt(5);
            Color slashColor = i % 2 == 0 ? bright : color;

            Minecraft.getInstance().particleEngine.add(new RiftSlashParticle(
                    level, x, y, z,
                    length,
                    width,
                    life,
                    roll,
                    withAlpha(slashColor, 220)
            ));
        }
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
