package com.til.recasting.handler;

import com.til.recasting.Recasting;
import com.til.recasting.registry.SlashArtsRegistry;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.event.SlashBladeEvent;
import mods.flammpfeil.slashblade.slasharts.SlashArts;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 时之彼端：蓄力期间推进日夜并加速周围实体 tick，同时按真实 gameTime 记录蓄力进度。
 */
@Mod.EventBusSubscriber(modid = Recasting.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class TimeBeyondChargeHandler {

    private static final int MAX_CHARGE_TICKS = 12 * 20;
    private static final int EXTRA_TICKS = 15;
    private static final double ACCEL_RANGE = 32.0;
    private static final ThreadLocal<Boolean> ACCELERATING = ThreadLocal.withInitial(() -> false);
    private static final Map<UUID, Long> CHARGE_START = new ConcurrentHashMap<>();

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

        long now = user.level().getGameTime();
        if (event.getChargeTicks() <= 1) {
            CHARGE_START.put(user.getUUID(), now);
        } else {
            CHARGE_START.putIfAbsent(user.getUUID(), now);
        }

        if (!(user.level() instanceof ServerLevel level)) {
            return;
        }

        level.setDayTime(level.getDayTime() + EXTRA_TICKS);

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

    /**
     * 读取并清除蓄力进度（0~1），按真实服务端 gameTime 相对蓄力起点计算。
     */
    public static float consumeProgress(LivingEntity livingEntity) {
        Long start = CHARGE_START.remove(livingEntity.getUUID());
        if (start == null) {
            return 0.0f;
        }
        long elapsed = livingEntity.level().getGameTime() - start;
        return Mth.clamp(elapsed / (float) MAX_CHARGE_TICKS, 0.0f, 1.0f);
    }

    public static void clear(LivingEntity livingEntity) {
        CHARGE_START.remove(livingEntity.getUUID());
    }

    private static boolean isTimeBeyondSa(ISlashBladeState state) {
        SlashArts arts = state.getSlashArts();
        return arts == SlashArtsRegistry.TIME_BEYOND.get();
    }
}
