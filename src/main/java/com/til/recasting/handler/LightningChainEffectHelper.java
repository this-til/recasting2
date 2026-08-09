package com.til.recasting.handler;

import com.til.recasting.network.LightningChainMessage;
import com.til.recasting.network.NetworkManager;
import com.til.recasting.registry.RecastingParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;

/**
 * 闪电链特效：折线同步 + 落点电云命中（不含伤害逻辑）
 */
public final class LightningChainEffectHelper {

    /**
     * 折线默认可见时长（tick）
     */
    public static final int DEFAULT_LIFE_TICKS = 8;

    /**
     * 特效同步半径（格），对齐 {@link ParticleHelper#sendParticlesLongRange}，避免远距横跳丢包
     */
    private static final double SYNC_RANGE = 512.0;

    private LightningChainEffectHelper() {
    }

    /**
     * 同步闪电链折线，并在落点播放电云命中粒子
     */
    public static void sync(ServerLevel serverLevel, Vec3 start, Vec3 end, int color) {
        sync(serverLevel, start, end, color, serverLevel.random.nextLong(), DEFAULT_LIFE_TICKS);
    }

    public static void sync(ServerLevel serverLevel, Vec3 start, Vec3 end, int color, long seed, int lifeTicks) {
        if (start.distanceToSqr(end) <= 1.0E-8) {
            return;
        }

        LightningChainMessage message = new LightningChainMessage(start, end, color, seed, lifeTicks);
        double rangeSqr = SYNC_RANGE * SYNC_RANGE;
        for(ServerPlayer player : serverLevel.players()) {
            // 以端点判定：横跳远离施法者时中点可能很远，仅用 mid+64 会丢包
            if (player.distanceToSqr(start) <= rangeSqr || player.distanceToSqr(end) <= rangeSqr) {
                NetworkManager.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
            }
        }

        spawnHitParticles(serverLevel, end, color);
    }

    /**
     * 命中电云：刀色 {@code LIGHTNING_HIT}（柔和云团 + flashlight 电火花）
     */
    public static void spawnHitParticles(ServerLevel serverLevel, Vec3 end, int color) {
        float r = ((color >> 16) & 0xFF) / 255.0f;
        float g = ((color >> 8) & 0xFF) / 255.0f;
        float b = (color & 0xFF) / 255.0f;

        // count=0：颜色走速度通道（与光棱命中同约定）
        ParticleHelper.sendParticlesLongRange(
                serverLevel,
                RecastingParticleTypes.LIGHTNING_HIT.get(),
                end.x,
                end.y,
                end.z,
                0,
                r,
                g,
                b,
                1.0
        );
    }
}
