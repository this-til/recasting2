package com.til.recasting.client.particle;

import com.til.recasting.constant.R;
import com.til.recasting.util.NumberPack;
import com.til.recasting.util.RandomUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.Random;

/**
 * 星闪满层触发粒子：主闪光 + 散射小粒子。
 * 主闪光与每个小粒子均在客户端各自随机高饱和色。
 */
@OnlyIn(Dist.CLIENT)
public class StarBlinkParticleProvider implements ParticleProvider<SimpleParticleType> {

    public static final ResourceLocation TEXTURE = R.Particle.starBlink$png;

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
        DefaultParticle main = new DefaultParticle(level, x, y, z);
        main.setSize(size)
                .setSizeChangeType(DefaultParticle.SizeChangeType.SQUARE_SIN)
                .setParticleCollide(false)
                .setLifeTime(life)
                .setColor(randomVividColor())
                .setTextureName(TEXTURE);

        spawnSmallParticles(level, x, y, z);
        return main;
    }

    private void spawnSmallParticles(ClientLevel level, double x, double y, double z) {
        for(int i = 0; i < smallNumber; i++) {
            var move = RandomUtil.nextVector3dOnCircles(random, 1.0)
                    .scale(smallMove.of(random.nextFloat()));

            DefaultParticle particle = new DefaultParticle(level, x, y, z);
            particle.setMove(move.x, move.y, move.z)
                    .setLifeTime((int) smallLife.of(random.nextFloat()))
                    .setColor(randomVividColor())
                    .setSize((float) smallSize.of(random.nextFloat()))
                    .setSizeChangeType(DefaultParticle.SizeChangeType.SQUARE_SIN)
                    .setParticleCollide(false)
                    .setTextureName(TEXTURE);

            Minecraft.getInstance().particleEngine.add(particle);
        }
    }

    private Color randomVividColor() {
        float hue = random.nextFloat();
        float saturation = 0.7f + random.nextFloat() * 0.3f;
        float brightness = 0.6f + random.nextFloat() * 0.4f;
        return new Color(Color.HSBtoRGB(hue, saturation, brightness));
    }
}
