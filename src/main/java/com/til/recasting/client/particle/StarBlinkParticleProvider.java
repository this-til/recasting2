package com.til.recasting.client.particle;

import com.til.recasting.Recasting;
import com.til.recasting.util.NumberPack;
import com.til.recasting.util.RandomUtil;
import net.minecraft.client.Minecraft;
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
import java.util.Random;

/**
 * 星闪满层触发粒子：主闪光 + 散射小粒子。
 * 颜色通过 {@code sendParticles(..., count=0, r, g, b, 1.0)} 的速度通道传入（0~1）。
 */
@OnlyIn(Dist.CLIENT)
public class StarBlinkParticleProvider implements ParticleProvider<SimpleParticleType> {

    public static final ResourceLocation TEXTURE = Recasting.prefix("particle/star_blink.png");

    private final float size = 4.5f;
    private final int life = 9;
    private final int smallNumber = 3;
    private final NumberPack smallMove = new NumberPack(0, 0.15f);
    private final NumberPack smallLife = new NumberPack(9, 27);
    private final NumberPack smallSize = new NumberPack(1, 0.5f);
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

        DefaultParticle main = new DefaultParticle(level, x, y, z);
        main.setSize(size)
                .setSizeChangeType(DefaultParticle.SizeChangeType.SQUARE_SIN)
                .setParticleCollide(false)
                .setLifeTime(life)
                .setColor(color)
                .setTextureName(TEXTURE);

        spawnSmallParticles(level, x, y, z, color);
        return main;
    }

    private void spawnSmallParticles(ClientLevel level, double x, double y, double z, Color color) {
        for (int i = 0; i < smallNumber; i++) {
            var move = RandomUtil.nextVector3dOnCircles(random, 1.0)
                    .scale(smallMove.of(random.nextFloat()));

            DefaultParticle particle = new DefaultParticle(level, x, y, z);
            particle.setMove(move.x, move.y, move.z)
                    .setLifeTime((int) smallLife.of(random.nextFloat()))
                    .setColor(color)
                    .setSize((float) smallSize.of(random.nextFloat()))
                    .setSizeChangeType(DefaultParticle.SizeChangeType.SQUARE_SIN)
                    .setParticleCollide(false)
                    .setTextureName(TEXTURE);

            Minecraft.getInstance().particleEngine.add(particle);
        }
    }

    private static Color resolveColor(double r, double g, double b) {
        return new Color(
                Mth.clamp(Mth.floor(r * 255.0 + 0.5), 0, 255),
                Mth.clamp(Mth.floor(g * 255.0 + 0.5), 0, 255),
                Mth.clamp(Mth.floor(b * 255.0 + 0.5), 0, 255)
        );
    }
}
