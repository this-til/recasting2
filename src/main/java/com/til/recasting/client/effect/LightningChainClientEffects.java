package com.til.recasting.client.effect;

import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * 客户端闪电链折线特效缓冲
 */
@OnlyIn(Dist.CLIENT)
public final class LightningChainClientEffects {

    private static final List<Bolt> BOLTS = new ArrayList<>();
    private static final List<Bolt> BOLTS_VIEW = Collections.unmodifiableList(BOLTS);

    private LightningChainClientEffects() {
    }

    public static void add(Vec3 start, Vec3 end, int color, long seed, int lifeTicks) {
        long now = Minecraft.getInstance().level == null
                ? 0L
                : Minecraft.getInstance().level.getGameTime();
        BOLTS.add(new Bolt(start, end, color, seed, Math.max(1, lifeTicks), now, buildBoltPoints(start, end, seed)));
    }

    public static void tick() {
        if (Minecraft.getInstance().level == null) {
            BOLTS.clear();
            return;
        }
        long now = Minecraft.getInstance().level.getGameTime();
        Iterator<Bolt> iterator = BOLTS.iterator();
        while (iterator.hasNext()) {
            Bolt bolt = iterator.next();
            if (now - bolt.startGameTime() >= bolt.lifeTicks()) {
                iterator.remove();
            }
        }
    }

    public static List<Bolt> snapshot() {
        return BOLTS_VIEW;
    }

    private static Vec3[] buildBoltPoints(Vec3 start, Vec3 end, long seed) {
        Vec3 delta = end.subtract(start);
        double lengthSqr = delta.lengthSqr();
        if (lengthSqr <= 1.0E-8) {
            return new Vec3[]{start, end};
        }

        double invLength = 1.0 / Math.sqrt(lengthSqr);
        Vec3 dir = delta.scale(invLength);
        Vec3 orthoA = orthogonal(dir, mixSeed(seed, 0));
        Vec3 orthoB = dir.cross(orthoA).normalize();
        Vec3[] points = new Vec3[9];
        points[0] = start;
        points[8] = end;

        double length = Math.sqrt(lengthSqr);
        float jitter = (float) Math.min(1.2, length * 0.08) * 0.35f;
        for (int i = 1; i < 8; i++) {
            float t = i / 8.0f;
            Vec3 base = start.add(delta.scale(t));
            double ox = signedUnit(seed, i * 2L) * jitter;
            double oy = signedUnit(seed, i * 2L + 1L) * jitter;
            points[i] = base.add(orthoA.scale(ox)).add(orthoB.scale(oy));
        }
        return points;
    }

    private static Vec3 orthogonal(Vec3 dir, long seed) {
        Vec3 side = dir.cross(new Vec3(0.0, 1.0, 0.0));
        if (side.lengthSqr() <= 1.0E-8) {
            side = dir.cross(new Vec3(1.0, 0.0, 0.0));
        }
        side = side.normalize();
        double angle = unit(seed) * Math.PI * 2.0;
        Vec3 bitangent = dir.cross(side).normalize();
        return side.scale(Math.cos(angle)).add(bitangent.scale(Math.sin(angle))).normalize();
    }

    private static double signedUnit(long seed, long salt) {
        return unit(mixSeed(seed, salt)) * 2.0 - 1.0;
    }

    private static double unit(long seed) {
        long mixed = mixSeed(seed, 0x9E3779B97F4A7C15L);
        return ((mixed >>> 11) & ((1L << 53) - 1)) / (double) (1L << 53);
    }

    private static long mixSeed(long seed, long salt) {
        long z = seed + salt + 0x9E3779B97F4A7C15L;
        z = (z ^ (z >>> 30)) * 0xBF58476D1CE4E5B9L;
        z = (z ^ (z >>> 27)) * 0x94D049BB133111EBL;
        return z ^ (z >>> 31);
    }

    public record Bolt(Vec3 start, Vec3 end, int color, long seed, int lifeTicks, long startGameTime, Vec3[] points) {

        public float alpha(long gameTime, float partialTick) {
            float age = (float) (gameTime - startGameTime) + partialTick;
            float life = lifeTicks;
            if (life <= 0.0f) {
                return 0.0f;
            }
            float t = age / life;
            if (t <= 0.0f) {
                return 1.0f;
            }
            if (t >= 1.0f) {
                return 0.0f;
            }
            if (t < 0.25f) {
                return 1.0f;
            }
            return 1.0f - (t - 0.25f) / 0.75f;
        }
    }
}
