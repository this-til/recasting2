package com.til.recasting.client.particle;

import com.til.recasting.constant.R;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

/**
 * 红尘滚滚飞行拖尾：对齐旧模组 {@code YAO_UPDATE} / {@code ModParticle}。
 */
@OnlyIn(Dist.CLIENT)
public class MortalDustTrailParticleProvider implements ParticleProvider<SimpleParticleType> {

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
        // Pos.getRandomPos(0.07, 0.07, 0.07)
        double mx = random.nextBoolean() ? random.nextDouble() * 0.07 : -(random.nextDouble() * 0.07);
        double my = random.nextBoolean() ? random.nextDouble() * 0.07 : -(random.nextDouble() * 0.07);
        double mz = random.nextBoolean() ? random.nextDouble() * 0.07 : -(random.nextDouble() * 0.07);

        DefaultParticle particle = new DefaultParticle(level, x, y, z);
        particle.setMove(mx, my, mz)
                .setSize(0.75f)
                .setSizeChangeType(DefaultParticle.SizeChangeType.SMOOTH)
                .setLifeTime(20)
                .setParticleGravity(0.0f)
                .setParticleCollide(false)
                .setColor(YAO_COLOR)
                .setTextureName(TEXTURE)
                .setAdditiveBlend(true);
        return particle;
    }
}
