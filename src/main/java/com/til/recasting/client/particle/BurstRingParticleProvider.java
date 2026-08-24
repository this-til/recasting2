package com.til.recasting.client.particle;

import com.til.recasting.client.effect.BurstRingClientEffects;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 圆环迸发：粒子包只作同步载体，实际绘制走 {@link BurstRingClientEffects}。
 * <p>
 * {@code xSpeed}=半径，{@code ySpeed}=环宽，{@code zSpeed}=0xRRGGBB。
 */
@OnlyIn(Dist.CLIENT)
public class BurstRingParticleProvider implements ParticleProvider<SimpleParticleType> {

    private static final int RING_LIFE_TICKS = 18;

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
        BurstRingClientEffects.add(new Vec3(x, y, z), radius, ringWidth, rgb, RING_LIFE_TICKS);
        return new MarkerParticle(level, x, y, z);
    }

    /**
     * 仅占位，不绘制。
     */
    private static final class MarkerParticle extends Particle {

        private MarkerParticle(ClientLevel level, double x, double y, double z) {
            super(level, x, y, z);
            this.lifetime = 1;
            this.hasPhysics = false;
        }

        @Override
        public void tick() {
            this.remove();
        }

        @Override
        public void render(
                @NotNull com.mojang.blaze3d.vertex.VertexConsumer buffer,
                net.minecraft.client.Camera camera,
                float partialTick
        ) {
        }

        @Override
        public @NotNull ParticleRenderType getRenderType() {
            return ParticleRenderType.NO_RENDER;
        }
    }
}
