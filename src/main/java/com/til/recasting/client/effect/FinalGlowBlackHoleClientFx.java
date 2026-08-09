package com.til.recasting.client.effect;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * 末辉黑洞客户端终结特效（由实体客户端 tick / remove 触发）。
 * <p>
 * 圆环同心：1 大半径 128 + 3 小半径 48，各自随机环面朝向（大小差靠半径，不靠偏移）。
 */
@OnlyIn(Dist.CLIENT)
public final class FinalGlowBlackHoleClientFx {

    private static final int RING_LIFE = 22;
    private static final float LARGE_RADIUS = 24.0f;
    private static final float SMALL_RADIUS = 8.0f;
    private static final float LARGE_RING_WIDTH = 10.0f;
    private static final float SMALL_RING_WIDTH = 5.0f;

    private FinalGlowBlackHoleClientFx() {
    }

    public static void spawnDetonation(Vec3 center, int colorRgb) {
        int color = colorRgb & 0xFFFFFF;

        // 1 大
        spawnOrientedRing(center, LARGE_RADIUS, LARGE_RING_WIDTH, color);
        // 3 小，同心，仅朝向不同 —— 半径 48 vs 128，屏幕上直径差约 2.7 倍
        spawnOrientedRing(center, SMALL_RADIUS, SMALL_RING_WIDTH, color);
        spawnOrientedRing(center, SMALL_RADIUS, SMALL_RING_WIDTH, color);
        spawnOrientedRing(center, SMALL_RADIUS, SMALL_RING_WIDTH, color);

        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }
        level.addParticle(ParticleTypes.EXPLOSION_EMITTER, true, center.x, center.y, center.z, 0.0, 0.0, 0.0);
        for(int i = 0; i < 24; i++) {
            level.addParticle(
                    ParticleTypes.EXPLOSION,
                    true,
                    center.x + (level.random.nextDouble() - 0.5) * 6.0,
                    center.y + (level.random.nextDouble() - 0.5) * 3.0,
                    center.z + (level.random.nextDouble() - 0.5) * 6.0,
                    0.0,
                    0.0,
                    0.0
            );
        }
    }

    private static void spawnOrientedRing(Vec3 center, float radius, float ringWidth, int color) {
        Vec3[] basis = BurstRingClientEffects.randomPlaneBasis();
        BurstRingClientEffects.add(center, radius, ringWidth, color, RING_LIFE, basis[0], basis[1]);
    }
}
