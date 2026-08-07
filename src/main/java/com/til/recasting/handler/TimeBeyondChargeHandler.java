package com.til.recasting.handler;

import com.til.recasting.Config;
import com.til.recasting.Recasting;
import com.til.recasting.network.NetworkManager;
import com.til.recasting.network.TimeBeyondAccelMessage;
import com.til.recasting.registry.SlashArtsRegistry;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.event.SlashBladeEvent;
import mods.flammpfeil.slashblade.slasharts.SlashArts;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 时之彼端：SA 满蓄（有特效）后推进日夜并可选加速周围实体；通知维度内客户端每帧平滑加速。
 */
@Mod.EventBusSubscriber(modid = Recasting.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class TimeBeyondChargeHandler {

    public static final int TIME_MULTIPLIER = 32;
    private static final int EXTRA_TICKS = TIME_MULTIPLIER - 1;
    private static final int MAX_CHARGE_TICKS = 12 * 20;
    private static final double ACCEL_RANGE = 32.0;

    private static final ThreadLocal<Boolean> ACCELERATING = ThreadLocal.withInitial(() -> false);
    private static final Map<UUID, Long> CHARGE_START = new ConcurrentHashMap<>();
    private static final Set<UUID> ACTIVE_ACCEL = ConcurrentHashMap.newKeySet();

    private TimeBeyondChargeHandler() {
    }

    @SubscribeEvent
    public static void onCharge(SlashBladeEvent.ChargeActionEvent event) {
        if (Boolean.TRUE.equals(ACCELERATING.get())) {
            return;
        }

        LivingEntity user = event.getEntityLiving();
        if (user.level().isClientSide()) {
            return;
        }
        if (!isTimeBeyondSa(event.getSlashBladeState())) {
            return;
        }

        int fullChargeTicks = event.getSlashBladeState().getFullChargeTicks(user);
        // 满蓄出现传送门粒子后才开始加速
        if (event.getChargeTicks() < fullChargeTicks) {
            return;
        }

        if (!(user.level() instanceof ServerLevel level)) {
            return;
        }

        long now = level.getGameTime();
        CHARGE_START.putIfAbsent(user.getUUID(), now);

        boolean newlyActive = ACTIVE_ACCEL.add(user.getUUID());
        level.setDayTime(level.getDayTime() + EXTRA_TICKS);
        if (newlyActive && countActiveInLevel(level) == 1) {
            broadcast(level, true);
        }

        if (!Config.isTimeBeyondEntityTickAccel()) {
            return;
        }

        AABB area = user.getBoundingBox().inflate(ACCEL_RANGE);
        List<Entity> entities = level.getEntities(null, area);

        ACCELERATING.set(true);
        try {
            for (Entity entity : entities) {
                if (!entity.isAlive()) {
                    continue;
                }
                for (int i = 0; i < EXTRA_TICKS; i++) {
                    if (!entity.isAlive()) {
                        break;
                    }
                    entity.tick();
                }
            }
        } finally {
            ACCELERATING.set(false);
        }
    }

    @SubscribeEvent
    public static void onStopUsing(LivingEntityUseItemEvent.Stop event) {
        stopAccel(event.getEntity());
    }

    @SubscribeEvent
    public static void onFinishUsing(LivingEntityUseItemEvent.Finish event) {
        stopAccel(event.getEntity());
    }

    @SubscribeEvent
    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            stopAccel(serverPlayer);
            CHARGE_START.remove(serverPlayer.getUUID());
        }
    }

    /**
     * 读取并清除蓄力进度（0~1），按满蓄加速起点起的真实 gameTime 计算。
     */
    public static float consumeProgress(LivingEntity livingEntity) {
        stopAccel(livingEntity);
        Long start = CHARGE_START.remove(livingEntity.getUUID());
        if (start == null) {
            return 0.0f;
        }
        long elapsed = livingEntity.level().getGameTime() - start;
        return Mth.clamp(elapsed / (float) MAX_CHARGE_TICKS, 0.0f, 1.0f);
    }

    public static void clear(LivingEntity livingEntity) {
        stopAccel(livingEntity);
        CHARGE_START.remove(livingEntity.getUUID());
    }

    private static void stopAccel(LivingEntity livingEntity) {
        if (livingEntity.level().isClientSide()) {
            return;
        }
        if (!ACTIVE_ACCEL.remove(livingEntity.getUUID())) {
            return;
        }
        if (!(livingEntity.level() instanceof ServerLevel level)) {
            return;
        }
        if (countActiveInLevel(level) == 0) {
            broadcast(level, false);
        }
    }

    private static int countActiveInLevel(ServerLevel level) {
        int count = 0;
        for (UUID uuid : ACTIVE_ACCEL) {
            if (level.getEntity(uuid) != null) {
                count++;
            }
        }
        return count;
    }

    private static void broadcast(ServerLevel level, boolean active) {
        TimeBeyondAccelMessage message = new TimeBeyondAccelMessage(
                active,
                TIME_MULTIPLIER,
                level.getDayTime()
        );
        NetworkManager.INSTANCE.send(
                PacketDistributor.DIMENSION.with(level::dimension),
                message
        );
    }

    private static boolean isTimeBeyondSa(ISlashBladeState state) {
        SlashArts arts = state.getSlashArts();
        return arts == SlashArtsRegistry.TIME_BEYOND.get();
    }
}
