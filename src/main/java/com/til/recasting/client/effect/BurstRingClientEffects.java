package com.til.recasting.client.effect;

import net.minecraft.client.Minecraft;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * 末辉终结圆环客户端缓冲（任意朝向环带，由 RenderLevel 阶段绘制）。
 */
@OnlyIn(Dist.CLIENT)
public final class BurstRingClientEffects {

    private static final List<Ring> RINGS = new ArrayList<>();
    private static final List<Ring> RINGS_VIEW = Collections.unmodifiableList(RINGS);

    private BurstRingClientEffects() {
    }

    public static void add(Vec3 center, float radius, float ringWidth, int color, int lifeTicks) {
        Vec3[] basis = randomPlaneBasis();
        add(center, radius, ringWidth, color, lifeTicks, basis[0], basis[1]);
    }

    public static void add(Vec3 center, float radius, float ringWidth, int color, int lifeTicks, Vec3 axisU, Vec3 axisV) {
        long now = Minecraft.getInstance().level == null
                ? 0L
                : Minecraft.getInstance().level.getGameTime();
        RINGS.add(new Ring(
                center,
                radius,
                ringWidth,
                color,
                Math.max(1, lifeTicks),
                now,
                axisU,
                axisV
        ));
    }

    public static void tick() {
        if (Minecraft.getInstance().level == null) {
            RINGS.clear();
            return;
        }
        long now = Minecraft.getInstance().level.getGameTime();
        Iterator<Ring> iterator = RINGS.iterator();
        while (iterator.hasNext()) {
            Ring ring = iterator.next();
            if (now - ring.startGameTime() >= ring.lifeTicks()) {
                iterator.remove();
            }
        }
    }

    public static List<Ring> snapshot() {
        return RINGS_VIEW;
    }

    /**
     * 随机环面正交基（U/V）。
     */
    public static Vec3[] randomPlaneBasis() {
        RandomSource random = Minecraft.getInstance().level == null
                ? RandomSource.create()
                : Minecraft.getInstance().level.getRandom();
        Vec3 normal = randomUnit(random);
        Vec3 ref = Math.abs(normal.y) < 0.9 ? new Vec3(0.0, 1.0, 0.0) : new Vec3(1.0, 0.0, 0.0);
        Vec3 axisU = normal.cross(ref).normalize();
        Vec3 axisV = normal.cross(axisU).normalize();
        return new Vec3[]{axisU, axisV};
    }

    private static Vec3 randomUnit(RandomSource random) {
        double u = random.nextDouble();
        double v = random.nextDouble();
        double theta = Math.PI * 2.0 * u;
        double phi = Math.acos(2.0 * v - 1.0);
        double sinPhi = Math.sin(phi);
        return new Vec3(sinPhi * Math.cos(theta), Math.cos(phi), sinPhi * Math.sin(theta));
    }

    public record Ring(
            Vec3 center,
            float radius,
            float ringWidth,
            int color,
            int lifeTicks,
            long startGameTime,
            Vec3 axisU,
            Vec3 axisV
    ) {

        public float progress(long gameTime, float partialTick) {
            float age = (float) (gameTime - startGameTime) + partialTick;
            float life = lifeTicks;
            if (life <= 0.0f) {
                return 1.0f;
            }
            return Math.min(1.0f, Math.max(0.0f, age / life));
        }

        public float alpha(long gameTime, float partialTick) {
            float t = progress(gameTime, partialTick);
            if (t <= 0.2f) {
                return t / 0.2f;
            }
            return 1.0f - (t - 0.2f) / 0.8f;
        }

        /**
         * 半径按 {@code f(x)=1-e^{-x}} 从 0 涨到设定值；{@code x} 为寿命进度（age/lifeTicks∈[0,1]）。
         * 除以 {@code f(1)} 使周期结束时恰好等于设定半径。
         */
        public float currentRadius(long gameTime, float partialTick) {
            float x = progress(gameTime, partialTick);
            float fx = (float) (1.0 - Math.exp(-x));
            float f1 = (float) (1.0 - Math.exp(-1.0));
            return radius * (fx / f1);
        }
    }
}
