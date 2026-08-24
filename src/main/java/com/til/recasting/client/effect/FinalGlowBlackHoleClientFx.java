package com.til.recasting.client.effect;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Vector3f;

/**
 * 末辉黑洞客户端终结特效（由实体客户端 tick / remove 触发）。
 * <p>
 * 圆环同心：1 大半径 24 + 3 小半径 8，各自随机环面朝向（大小差靠半径，不靠偏移）。
 * 环半径缩放：寿命进度 x∈[0,1]（周期 = {@link #RING_LIFE} tick）代入 {@code f(x)=1-e^{-x}}，最大为设定半径。
 * 爆炸粒子：相对坍缩期峰值吸积（半径 24、约 46 粒）再大/多约 3 倍的外爆球。
 */
@OnlyIn(Dist.CLIENT)
public final class FinalGlowBlackHoleClientFx {

    private static final int RING_LIFE = 22;
    private static final float LARGE_RADIUS = 24.0f;
    private static final float SMALL_RADIUS = 8.0f;
    private static final float LARGE_RING_WIDTH = 10.0f;
    private static final float SMALL_RING_WIDTH = 5.0f;

    /** 坍缩期峰值吸积半径 24 × 3。 */
    private static final float BLAST_RADIUS = 72.0f;
    /** 峰值约 46 粒 × 3。 */
    private static final int BLAST_COUNT = 138;

    private FinalGlowBlackHoleClientFx() {
    }

    public static void spawnDetonation(Vec3 center, int colorRgb) {
        int color = colorRgb & 0xFFFFFF;

        // 1 大
        spawnOrientedRing(center, LARGE_RADIUS, LARGE_RING_WIDTH, color);
        // 3 小，同心，仅朝向不同
        spawnOrientedRing(center, SMALL_RADIUS, SMALL_RING_WIDTH, color);
        spawnOrientedRing(center, SMALL_RADIUS, SMALL_RING_WIDTH, color);
        spawnOrientedRing(center, SMALL_RADIUS, SMALL_RING_WIDTH, color);

        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }

        level.addParticle(ParticleTypes.EXPLOSION_EMITTER, true, center.x, center.y, center.z, 0.0, 0.0, 0.0);
        for(int e = 0; e < 5; e++) {
            level.addParticle(
                    ParticleTypes.EXPLOSION_EMITTER,
                    true,
                    center.x + (level.random.nextDouble() - 0.5) * 4.0,
                    center.y + (level.random.nextDouble() - 0.5) * 2.0,
                    center.z + (level.random.nextDouble() - 0.5) * 4.0,
                    0.0,
                    0.0,
                    0.0
            );
        }
        for(int i = 0; i < 96; i++) {
            level.addParticle(
                    ParticleTypes.EXPLOSION,
                    true,
                    center.x + (level.random.nextDouble() - 0.5) * 24.0,
                    center.y + (level.random.nextDouble() - 0.5) * 12.0,
                    center.z + (level.random.nextDouble() - 0.5) * 24.0,
                    0.0,
                    0.0,
                    0.0
            );
        }

        spawnBlastParticles(level, center, color);
    }

    private static void spawnBlastParticles(ClientLevel level, Vec3 center, int color) {
        RandomSource random = level.random;
        float r = ((color >> 16) & 255) / 255.0f;
        float g = ((color >> 8) & 255) / 255.0f;
        float b = (color & 255) / 255.0f;
        // 刀色尘 + 强制红色尘（更多）
        DustParticleOptions dust = new DustParticleOptions(new Vector3f(r, g, b), 4.0f);
        DustParticleOptions redDust = new DustParticleOptions(new Vector3f(1.0f, 0.12f, 0.08f), 5.5f);
        DustParticleOptions redDustBig = new DustParticleOptions(new Vector3f(0.95f, 0.05f, 0.02f), 8.0f);
        DustParticleOptions dark = new DustParticleOptions(new Vector3f(0.05f, 0.02f, 0.02f), 6.5f);

        for(int i = 0; i < BLAST_COUNT; i++) {
            Vec3 dir = randomUnit(random);
            double radius = BLAST_RADIUS * (0.15 + random.nextDouble() * 0.85);
            double px = center.x + dir.x * radius;
            double py = center.y + dir.y * radius;
            double pz = center.z + dir.z * radius;
            double speed = 0.35 + random.nextDouble() * 1.1;
            double vx = dir.x * speed;
            double vy = dir.y * speed;
            double vz = dir.z * speed;

            level.addParticle(ParticleTypes.PORTAL, true, px, py, pz, vx, vy, vz);
            level.addParticle(ParticleTypes.END_ROD, true, px, py, pz, vx * 0.45, vy * 0.45, vz * 0.45);
            level.addParticle(dust, true, px, py, pz, 0.0, 0.0, 0.0);
            level.addParticle(redDust, true, px, py, pz, 0.0, 0.0, 0.0);
            if (i % 2 == 0) {
                level.addParticle(redDustBig, true, px, py, pz, 0.0, 0.0, 0.0);
            }
            if (i % 3 == 0) {
                level.addParticle(ParticleTypes.SMOKE, true, px, py, pz, vx * 0.5, vy * 0.5, vz * 0.5);
            }
            if (i % 5 == 0) {
                level.addParticle(dark, true, px, py, pz, 0.0, 0.0, 0.0);
            }
        }

        // 额外红色尘云，集中在中近距
        int redExtra = BLAST_COUNT;
        for(int i = 0; i < redExtra; i++) {
            Vec3 dir = randomUnit(random);
            double radius = BLAST_RADIUS * (0.05 + random.nextDouble() * 0.55);
            double px = center.x + dir.x * radius;
            double py = center.y + dir.y * radius;
            double pz = center.z + dir.z * radius;
            level.addParticle(redDust, true, px, py, pz, 0.0, 0.0, 0.0);
            if (i % 2 == 0) {
                level.addParticle(redDustBig, true, px, py, pz, 0.0, 0.0, 0.0);
            }
        }
    }

    private static void spawnOrientedRing(Vec3 center, float radius, float ringWidth, int color) {
        Vec3[] basis = BurstRingClientEffects.randomPlaneBasis();
        BurstRingClientEffects.add(center, radius, ringWidth, color, RING_LIFE, basis[0], basis[1]);
    }

    private static Vec3 randomUnit(RandomSource random) {
        double u = random.nextDouble();
        double v = random.nextDouble();
        double theta = 2.0 * Math.PI * u;
        double phi = Math.acos(2.0 * v - 1.0);
        double sinPhi = Math.sin(phi);
        return new Vec3(sinPhi * Math.cos(theta), Math.cos(phi), sinPhi * Math.sin(theta));
    }
}
