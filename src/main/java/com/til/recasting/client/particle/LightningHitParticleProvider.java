package com.til.recasting.client.particle;

import com.til.recasting.constant.R;
import com.til.recasting.util.RandomUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.Random;

/**
 * 闪电链命中：一小片电云（柔和云团 + 闪烁电火花）。
 * 颜色通过 {@code sendParticles(..., count=0, r, g, b, 1.0)} 的速度通道传入（0~1）。
 * 暗色走半透明混合，避免加法混合下黑色不可见。
 */
@OnlyIn(Dist.CLIENT)
public class LightningHitParticleProvider implements ParticleProvider<SimpleParticleType> {

    public static final ResourceLocation CLOUD_TEXTURE = R.Particle.Other.shimmer$png;
    public static final ResourceLocation SPARK_TEXTURE = R.Particle.Other.flashlight$png;

    private static final int CLOUD_COUNT = 5;
    private static final int SPARK_COUNT = 7;
    private static final float CLOUD_SPREAD = 0.35f;
    private static final float DARK_LUMINANCE_THRESHOLD = 0.18f;
    private static final Vec3 DRIFT_ATTENUATION = new Vec3(0.88, 0.88, 0.88);

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
        boolean dark = isDark(color);
        Color bright = dark
                ? liftDark(color)
                : brighten(color, 1.35f);
        Color soft = withAlpha(color, dark
                ? 200
                : 140);

        spawnCloudPuffs(level, x, y, z, soft, bright, dark);
        spawnSparks(level, x, y, z, bright, color, dark);

        DefaultParticle core = new DefaultParticle(level, x, y, z);
        core.setSize(2.2f + random.nextFloat() * 0.4f)
                .setSizeChangeType(DefaultParticle.SizeChangeType.SQUARE_SIN)
                .setParticleCollide(false)
                .setLifeTime(10 + random.nextInt(4))
                .setColor(withAlpha(bright, dark
                        ? 220
                        : 200))
                .setTextureName(CLOUD_TEXTURE)
                .setAdditiveBlend(!dark)
                .setRollSpeed((random.nextFloat() - 0.5f) * 0.15f);
        return core;
    }

    private void spawnCloudPuffs(
            ClientLevel level,
            double x, double y, double z,
            Color soft,
            Color bright,
            boolean dark
    ) {
        for(int i = 0; i < CLOUD_COUNT; i++) {
            Vec3 offset = RandomUtil.nextVector3dOnCircles(random, 1.0)
                    .scale(CLOUD_SPREAD * (0.35 + random.nextDouble() * 0.65));
            Vec3 drift = RandomUtil.nextVector3dOnCircles(random, 1.0)
                    .scale(0.01 + random.nextDouble() * 0.025);

            DefaultParticle puff = new DefaultParticle(
                    level,
                    x + offset.x,
                    y + offset.y,
                    z + offset.z
            );
            puff.setMove(drift.x, drift.y, drift.z)
                    .setMoveAttenuation(DRIFT_ATTENUATION)
                    .setSize(1.1f + random.nextFloat() * 0.9f)
                    .setSizeChangeType(DefaultParticle.SizeChangeType.SQUARE_SIN)
                    .setParticleCollide(false)
                    .setLifeTime(8 + random.nextInt(6))
                    .setColor(i % 2 == 0
                            ? soft
                            : withAlpha(bright, dark
                                    ? 200
                                    : 160))
                    .setTextureName(CLOUD_TEXTURE)
                    .setAdditiveBlend(!dark)
                    .setRollSpeed((random.nextFloat() - 0.5f) * 0.2f);

            Minecraft.getInstance().particleEngine.add(puff);
        }
    }

    private void spawnSparks(
            ClientLevel level,
            double x, double y, double z,
            Color bright,
            Color color,
            boolean dark
    ) {
        for(int i = 0; i < SPARK_COUNT; i++) {
            Vec3 offset = RandomUtil.nextVector3dOnCircles(random, 1.0)
                    .scale(CLOUD_SPREAD * (0.2 + random.nextDouble() * 0.9));
            Vec3 crackle = RandomUtil.nextVector3dOnCircles(random, 1.0)
                    .scale(0.02 + random.nextDouble() * 0.05);

            DefaultParticle spark = new DefaultParticle(
                    level,
                    x + offset.x,
                    y + offset.y,
                    z + offset.z
            );
            spark.setMove(crackle.x, crackle.y, crackle.z)
                    .setMoveAttenuation(DRIFT_ATTENUATION)
                    .setSize(0.55f + random.nextFloat() * 0.75f)
                    .setSizeChangeType(DefaultParticle.SizeChangeType.FLASH_SIN)
                    .setParticleCollide(false)
                    .setLifeTime(4 + random.nextInt(5))
                    .setColor(withAlpha(i % 2 == 0
                            ? bright
                            : color, 230))
                    .setTextureName(SPARK_TEXTURE)
                    .setAdditiveBlend(!dark);

            Minecraft.getInstance().particleEngine.add(spark);
        }
    }

    private static boolean isDark(Color color) {
        float r = color.getRed() / 255.0f;
        float g = color.getGreen() / 255.0f;
        float b = color.getBlue() / 255.0f;
        float luminance = 0.2126f * r + 0.7152f * g + 0.0722f * b;
        return luminance < DARK_LUMINANCE_THRESHOLD;
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
                Mth.clamp(Mth.floor(color.getBlue() * factor), 0, 255),
                color.getAlpha()
        );
    }

    /**
     * 暗色略提亮，便于核/火花与纯黑鞘区分，不退回高亮加法色
     */
    private static Color liftDark(Color color) {
        return new Color(
                Mth.clamp(color.getRed() + 28, 0, 255),
                Mth.clamp(color.getGreen() + 28, 0, 255),
                Mth.clamp(color.getBlue() + 32, 0, 255),
                color.getAlpha()
        );
    }

    private static Color withAlpha(Color color, int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), Mth.clamp(alpha, 0, 255));
    }
}
