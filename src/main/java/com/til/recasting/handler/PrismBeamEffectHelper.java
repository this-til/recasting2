package com.til.recasting.handler;

import com.til.recasting.network.PrismBeamMessage;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * 光棱特效：线段同步（不含伤害逻辑）。
 */
public final class PrismBeamEffectHelper {

    /**
     * 光棱线段默认可见时长（tick）
     */
    public static final int DEFAULT_LIFE_TICKS = 6;

    private PrismBeamEffectHelper() {
    }

    /**
     * 同步光棱线段至附近客户端。
     *
     * @param lifeTicks 客户端线段可见持续时间
     */
    public static void sync(ServerLevel serverLevel, Vec3 start, Vec3 end, int color, int lifeTicks) {
        if (start.distanceToSqr(end) <= 1.0E-8) {
            return;
        }
        Vec3 mid = start.add(end).scale(0.5);
        double range = Math.max(64.0, start.distanceTo(end) + 32.0);
        PacketDistributor.sendToPlayersNear(
                serverLevel,
                null,
                mid.x,
                mid.y,
                mid.z,
                range,
                new PrismBeamMessage(start, end, color, lifeTicks)
        );
    }
}
