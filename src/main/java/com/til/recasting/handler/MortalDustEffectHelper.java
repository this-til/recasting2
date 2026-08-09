package com.til.recasting.handler;

import com.til.recasting.registry.RecastingParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;

/**
 * 红尘滚滚粒子：严格对齐旧 {@code YAO_UPDATE} / {@code YAO_ATTECK}。
 */
public final class MortalDustEffectHelper {

    private MortalDustEffectHelper() {
    }

    /**
     * 飞行拖尾：每 tick 入队 2 次（与旧 EntityYao 服务端循环一致）。
     */
    public static void spawnTrail(ServerLevel serverLevel, Vec3 pos) {
        for(int i = 0; i < 2; i++) {
            ParticleHelper.sendParticlesLongRange(
                    serverLevel,
                    RecastingParticleTypes.MORTAL_DUST_TRAIL.get(),
                    pos.x,
                    pos.y,
                    pos.z,
                    1,
                    0.0,
                    0.0,
                    0.0,
                    0.0
            );
        }
    }

    /**
     * 命中喷泉：同位置连发 45 颗（参数在客户端 Provider 内随机）。
     */
    public static void spawnHitBurst(ServerLevel serverLevel, Vec3 pos) {
        ParticleHelper.sendParticlesLongRange(
                serverLevel,
                RecastingParticleTypes.MORTAL_DUST_HIT.get(),
                pos.x,
                pos.y,
                pos.z,
                45,
                0.0,
                0.0,
                0.0,
                0.0
        );
    }
}
