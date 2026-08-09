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

/**
 * 圆环迸发粒子工厂。
 * <p>
 * {@code xSpeed}=半径，{@code ySpeed}=环宽，{@code zSpeed}=0xRRGGBB 打包色。
 */
@OnlyIn(Dist.CLIENT)
public class BurstRingParticleProvider implements ParticleProvider<SimpleParticleType> {

    @Nullable
    @Override
    public Particle createParticle(
            @NotNull SimpleParticleType type,
            @NotNull ClientLevel level,
            double x,
            double y,
            double z,
            double xSpeed,
            double ySpeed,
            double zSpeed
    ) {
        float radius = (float) Math.max(1.0, xSpeed);
        float ringWidth = (float) Math.max(0.5, ySpeed);
        int rgb = Mth.clamp((int) Math.round(zSpeed), 0, 0xFFFFFF);
        return new BurstRingParticle(level, x, y, z, radius, ringWidth, rgb);
    }
}
