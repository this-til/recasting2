package com.til.recasting.handler;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 粒子同步辅助。
 */
public final class ParticleHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(ParticleHelper.class);
    private static final RandomSource RANDOM = RandomSource.create();

    private ParticleHelper() {
    }

    /**
     * 向维度内玩家发送粒子；使用远距同步，避免默认距离截断。
     */
    public static <T extends ParticleOptions> void sendParticlesLongRange(
            ServerLevel serverLevel,
            T type,
            double x,
            double y,
            double z,
            int count,
            double xOffset,
            double yOffset,
            double zOffset,
            double speed
    ) {
        for (ServerPlayer player : serverLevel.players()) {
            serverLevel.sendParticles(player, type, true, x, y, z, count, xOffset, yOffset, zOffset, speed);
        }
    }

    public static void spawnParticle(
            Level level,
            ParticleOptions type,
            double posX,
            double posY,
            double posZ,
            int particleCount,
            double xOffset,
            double yOffset,
            double zOffset,
            double speed
    ) {
        for (int i = 0; i < particleCount; i++) {
            double d1 = RANDOM.nextGaussian() * xOffset;
            double d3 = RANDOM.nextGaussian() * yOffset;
            double d5 = RANDOM.nextGaussian() * zOffset;
            double d6 = RANDOM.nextGaussian() * speed;
            double d7 = RANDOM.nextGaussian() * speed;
            double d8 = RANDOM.nextGaussian() * speed;

            try {
                level.addParticle(
                        type,
                        true,
                        posX + d1,
                        posY + d3,
                        posZ + d5,
                        d6,
                        d7,
                        d8
                );
            } catch (Throwable throwable) {
                LOGGER.warn("Could not spawn particle effect {}", type);
                return;
            }
        }
    }

    public static void spawnBlockParticle(Entity user, Vec3 targetPos, Vec3 normal, float fallFactor) {
        Vec3 blockPos = targetPos.add(normal.normalize().scale(0.5F));
        BlockPos pos = new BlockPos(Mth.floor(blockPos.x()), Mth.floor(blockPos.y()), Mth.floor(blockPos.z()));
        BlockState state = user.level().getBlockState(pos);
        float f = (float) Math.ceil(fallFactor);
        if (state.isAir()) {
            return;
        }
        double d0 = Math.min(0.2F + f / 15.0F, 2.5F);
        int i = (int) (150.0F * d0);
        BlockParticleOption blockParticleOption = new BlockParticleOption(ParticleTypes.BLOCK, state);
        spawnParticle(user.level(), blockParticleOption, targetPos.x, targetPos.y, targetPos.z, i, 0, 0, 0, 0.15F);
    }
}
