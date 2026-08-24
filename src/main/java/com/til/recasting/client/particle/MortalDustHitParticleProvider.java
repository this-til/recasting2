package com.til.recasting.client.particle;

import com.til.recasting.constant.R;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

/**
 * 红尘滚滚命中喷泉：对齐旧模组 {@code YAO_ATTECK} / {@code yaoAtteck}（单次调用生成一颗）。
 */
@OnlyIn(Dist.CLIENT)
public class MortalDustHitParticleProvider implements ParticleProvider<SimpleParticleType> {

    public static final ResourceLocation TEXTURE = R.Particle.Other.modparticle$png;
    private static final Color YAO_COLOR = new Color(255, 128, 64);

    @Nullable
    @Override
    public Particle createParticle(
            @NotNull SimpleParticleType type,
            @NotNull ClientLevel level,
            double x, double y, double z,
            double xSpeed, double ySpeed, double zSpeed
    ) {
        RandomSource random = level.random;
        // Pos.getRandomPos(1.55, 1.55, 1.55)，竖直分量翻正并 +0.21
        double mx = random.nextBoolean()
                ? random.nextDouble() * 1.55
                : -(random.nextDouble() * 1.55);
        double my = random.nextBoolean()
                ? random.nextDouble() * 1.55
                : -(random.nextDouble() * 1.55);
        double mz = random.nextBoolean()
                ? random.nextDouble() * 1.55
                : -(random.nextDouble() * 1.55);
        my = my < 0.0
                ? -my + 0.21
                : my + 0.21;

        float scale = random.nextFloat() * 3.25f + 0.22f;
        int life = random.nextInt(70) + 20;
        float gravity = random.nextFloat() * 1.254f + 0.45f;

        DefaultParticle particle = new DefaultParticle(level, x, y, z);
        particle.setMove(mx, my, mz)
                .setSize(scale)
                .setSizeChangeType(DefaultParticle.SizeChangeType.SMOOTH)
                .setLifeTime(life)
                .setParticleGravity(gravity)
                .setParticleCollide(false)
                .setColor(YAO_COLOR)
                .setTextureName(TEXTURE)
                .setAdditiveBlend(true);
        return particle;
    }
}
