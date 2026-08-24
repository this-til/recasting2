package com.til.recasting.registry.sa;

import com.til.recasting.Config;
import com.til.recasting.Recasting;
import com.til.recasting.capability.IBuffStackData;
import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.capability.RenderDefinitionExtension;
import com.til.recasting.entity.DriveEntity;
import com.til.recasting.handler.CapabilityRegistryHandler;
import com.til.recasting.network.NetworkManager;
import com.til.recasting.network.TimeBeyondAccelMessage;
import com.til.recasting.registry.RecastingBuffTypes;
import com.til.recasting.registry.RecastingEntities;
import com.til.recasting.registry.SlashArtsRegistry;
import lombok.Setter;
import lombok.experimental.Accessors;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.event.SlashBladeEvent;
import mods.flammpfeil.slashblade.slasharts.SlashArts;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 时之彼端：蓄力加速时间后释放裂岚式十字斩（Drive 尾杀），威力随蓄力时长提升。
 * SA 满蓄（有特效）后推进日夜并可选加速周围实体；通知维度内客户端每帧平滑加速。
 * 蓄力进度写入 {@link RecastingBuffTypes#TIME_BEYOND_CHARGE}（层=已蓄力 tick）。
 */
@Mod.EventBusSubscriber(modid = Recasting.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
@Setter
@Accessors(chain = true)
public class TimeBeyondSlashArts extends ExtendedSlashArts {

    public static final int TIME_MULTIPLIER = 32;
    public static final int MAX_CHARGE_TICKS = 12 * 20;

    private static final String KEY_CHARGE_START = "ChargeStartGameTime";
    private static final int EXTRA_TICKS = TIME_MULTIPLIER - 1;
    private static final double ACCEL_RANGE = 32.0;

    private static final ThreadLocal<Boolean> ACCELERATING = ThreadLocal.withInitial(() -> false);
    private static final Set<UUID> ACTIVE_ACCEL = ConcurrentHashMap.newKeySet();

    private float attackMin = 0.1f;
    private float attackMax = 3.0f;
    private float crossSize = 3.5f;
    private int driveLife = 10;
    private float driveSpeed = 4.5f;

    @Override
    public void trigger(
            LivingEntity livingEntity,
            ItemStack itemStack,
            ISlashBladeState slashBladeState,
            RenderDefinitionExtension renderDefinitionExtension,
            PropertiesDefinitionExtension propertiesDefinitionExtension
    ) {
        if (livingEntity.level().isClientSide()) {
            return;
        }

        float progress = consumeProgress(livingEntity);
        float ratio = Mth.lerp(progress, attackMin, attackMax);
        RandomSource random = livingEntity.getRandom();
        float roll = random.nextFloat() * 360.0f;
        spawnCrossSlash(livingEntity, slashBladeState, roll, ratio);
        spawnCrossSlash(livingEntity, slashBladeState, roll + 90.0f, ratio);
    }

    private void spawnCrossSlash(
            LivingEntity livingEntity,
            ISlashBladeState slashBladeState,
            float roll,
            float attackRatio
    ) {
        DriveEntity driveEntity = new DriveEntity(
                RecastingEntities.DRIVE.get(),
                livingEntity.level(),
                livingEntity
        );

        Vec3 pos = livingEntity.position()
                .add(0.0D, livingEntity.getEyeHeight() * 0.75D, 0.0D)
                .add(livingEntity.getLookAngle().scale(0.3f));
        driveEntity.setPos(pos.x, pos.y, pos.z);
        driveEntity.setColor(slashBladeState.getColorCode());
        driveEntity.setModifiedRatio(attackRatio);
        driveEntity.setMaxLifeTime(driveLife);
        driveEntity.setSize(crossSize);
        driveEntity.setRoll(roll);
        driveEntity.setSeep(driveSpeed);
        driveEntity.lookAt(livingEntity.getLookAngle(), true);

        livingEntity.level().addFreshEntity(driveEntity);
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
        updateChargeBuff(user, now);

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
            for(Entity entity : entities) {
                if (!entity.isAlive()) {
                    continue;
                }
                for(int i = 0; i < EXTRA_TICKS; i++) {
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
            clearChargeBuff(serverPlayer);
        }
    }

    /**
     * 读取并清除蓄力进度（0~1），按 {@link RecastingBuffTypes#TIME_BEYOND_CHARGE} 层数计算。
     */
    public static float consumeProgress(LivingEntity livingEntity) {
        stopAccel(livingEntity);
        IBuffStackData buffStackData = livingEntity.getCapability(CapabilityRegistryHandler.BUFF_STACK_DATA).orElse(null);
        if (buffStackData == null) {
            return 0.0f;
        }
        int level = buffStackData.getLevel(RecastingBuffTypes.TIME_BEYOND_CHARGE.get(), livingEntity.level());
        clearChargeBuff(livingEntity);
        return Mth.clamp(level / (float) MAX_CHARGE_TICKS, 0.0f, 1.0f);
    }

    private static void updateChargeBuff(LivingEntity user, long now) {
        user.getCapability(CapabilityRegistryHandler.BUFF_STACK_DATA).ifPresent(data -> {
            CompoundTag customData = data.getOrCreateCustomData(RecastingBuffTypes.TIME_BEYOND_CHARGE.get(), user.level());
            if (!customData.contains(KEY_CHARGE_START)) {
                customData.putLong(KEY_CHARGE_START, now);
            }
            long start = customData.getLong(KEY_CHARGE_START);
            int elapsed = (int) Mth.clamp(now - start, 0L, MAX_CHARGE_TICKS);
            data.setLevel(RecastingBuffTypes.TIME_BEYOND_CHARGE.get(), elapsed, user.level());
        });
    }

    private static void clearChargeBuff(LivingEntity livingEntity) {
        if (livingEntity.level().isClientSide()) {
            return;
        }
        livingEntity.getCapability(CapabilityRegistryHandler.BUFF_STACK_DATA).ifPresent(data -> {
            IBuffStackData.BuffEntry entry = data.getEntry(RecastingBuffTypes.TIME_BEYOND_CHARGE.get());
            if (entry != null && entry.getCustomData() != null) {
                entry.getCustomData().remove(KEY_CHARGE_START);
            }
            data.setLevel(RecastingBuffTypes.TIME_BEYOND_CHARGE.get(), 0, livingEntity.level());
        });
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
        for(UUID uuid : ACTIVE_ACCEL) {
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
