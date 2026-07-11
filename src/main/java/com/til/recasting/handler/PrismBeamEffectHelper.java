package com.til.recasting.handler;

import com.til.recasting.network.NetworkManager;
import com.til.recasting.network.PrismBeamMessage;
import com.til.recasting.registry.RecastingParticleTypes;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;
import org.joml.Vector3f;

/**
 * 光棱特效：线段同步 + 命中点粒子（不含伤害逻辑）
 * <p>
 * 光棱 SA 与灼痕短光束共用本入口，保证命中粒子一致。
 */
public final class PrismBeamEffectHelper {

    /** 光棱线段默认可见时长（tick） */
    public static final int DEFAULT_LIFE_TICKS = 6;

    private PrismBeamEffectHelper() {
    }

    /**
     * 同步光棱线段，并在落点播放命中粒子（刀色 DefaultParticle 高闪 + 白金芯/刀色尘埃）
     *
     * @param lifeTicks 客户端线段可见持续时间
     */
    public static void sync(ServerLevel serverLevel, Vec3 start, Vec3 end, int color, int lifeTicks) {
        if (start.distanceToSqr(end) <= 1.0E-8) {
            return;
        }
        Vec3 mid = start.add(end).scale(0.5);
        double range = Math.max(64.0, start.distanceTo(end) + 32.0);
        NetworkManager.INSTANCE.send(
                PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(
                        mid.x,
                        mid.y,
                        mid.z,
                        range,
                        serverLevel.dimension()
                )),
                new PrismBeamMessage(start, end, color, lifeTicks)
        );

        spawnHitParticles(serverLevel, end, color);
    }

    /**
     * 光棱命中点粒子：刀色 {@code DefaultParticle} 高闪 + 白金芯 + 刀色散射尘埃
     */
    public static void spawnHitParticles(ServerLevel serverLevel, Vec3 end, int color) {
        float r = ((color >> 16) & 0xFF) / 255.0f;
        float g = ((color >> 8) & 0xFF) / 255.0f;
        float b = (color & 0xFF) / 255.0f;
        DustParticleOptions core = new DustParticleOptions(new Vector3f(1.0f, 0.95f, 0.55f), 1.35f);
        DustParticleOptions sheath = new DustParticleOptions(new Vector3f(r, g, b), 1.0f);

        // 外层高闪：DefaultParticle，颜色走速度通道（与星闪同约定）
        ParticleHelper.sendParticlesLongRange(
                serverLevel,
                RecastingParticleTypes.DEFAULT_PARTICLE.get(),
                end.x,
                end.y,
                end.z,
                0,
                r,
                g,
                b,
                1.0
        );
        ParticleHelper.sendParticlesLongRange(serverLevel, core, end.x, end.y, end.z, 6, 0.12, 0.12, 0.12, 0.0);
        ParticleHelper.sendParticlesLongRange(serverLevel, sheath, end.x, end.y, end.z, 10, 0.2, 0.2, 0.2, 0.0);
    }
}
