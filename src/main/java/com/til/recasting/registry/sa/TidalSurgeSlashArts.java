package com.til.recasting.registry.sa;

import com.til.recasting.capability.ITimeRun;
import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.capability.RenderDefinitionExtension;
import com.til.recasting.entity.DriveEntity;
import com.til.recasting.handler.CapabilityRegistryHandler;
import com.til.recasting.registry.RecastingEntities;
import lombok.Setter;
import lombok.experimental.Accessors;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * [回到未来计划]撼海潮涌：对齐旧版玩家余韵 —— 持续 20 tick，每 tick 水平喷出 5～8 道剑气。
 */
@Setter
@Accessors(chain = true)
public class TidalSurgeSlashArts extends ExtendedSlashArts {

    private static final String TIMER_NAME = "tidal_surge";

    private int durationTicks = 20;
    private int driveCount = 5;
    private float driveRatio = 0.15f;
    private int driveLife = 80;
    private float driveSeep = 0.1f;
    private int driveColor = 0x0000CD;

    @Override
    public void trigger(
            LivingEntity livingEntity,
            ItemStack itemStack,
            ISlashBladeState slashBladeState,
            RenderDefinitionExtension renderDefinitionExtension,
            PropertiesDefinitionExtension propertiesDefinitionExtension
    ) {
        Level level = livingEntity.level();
        if (level.isClientSide()) {
            return;
        }

        int[] ticksLeft = {durationTicks};

        livingEntity.getCapability(CapabilityRegistryHandler.TIME_RUN).ifPresent(timeRun -> {
            timeRun.removeNamedTimerCell(TIMER_NAME);
            timeRun.addNamedTimerCell(
                    TIMER_NAME,
                    new ITimeRun.TimerCell(
                            () -> tickDriveRain(livingEntity, timeRun, ticksLeft),
                            1,
                            true
                    )
            );
        });

        level.playSound(
                null,
                livingEntity.getX(),
                livingEntity.getY(),
                livingEntity.getZ(),
                SoundEvents.GENERIC_SPLASH,
                SoundSource.PLAYERS,
                0.8F,
                0.7F + livingEntity.getRandom().nextFloat() * 0.3F
        );
    }

    private void tickDriveRain(LivingEntity caster, ITimeRun timeRun, int[] ticksLeft) {
        if (!caster.isAlive() || --ticksLeft[0] < 0) {
            timeRun.removeNamedTimerCell(TIMER_NAME);
            return;
        }

        Level level = caster.level();
        if (level.isClientSide()) {
            return;
        }

        RandomSource random = caster.getRandom();

        double originY = caster.getY() + caster.getEyeHeight() * 0.5;

        for(int i = 0; i < driveCount; i++) {
            DriveEntity drive = new DriveEntity(
                    RecastingEntities.DRIVE.get(),
                    level,
                    caster
            );
            drive.setPos(caster.getX(), originY, caster.getZ());
            drive.setColor(driveColor);
            drive.setModifiedRatio(driveRatio);
            drive.setMaxLifeTime(driveLife);
            drive.setSeep(driveSeep);
            drive.setSize(1.0f);
            drive.setRepeatedAttack(false);
            // 水平锁定 + 旧 RoundaboutDrive 每 tick ×1.05
            drive.setSpeedScalePerTick(1.05f);
            // 旧版：yaw = playerYaw + rand*360，pitch = 0
            drive.lookAt(horizontalDirection(caster.getYRot(), random), true);
            level.addFreshEntity(drive);
        }
    }

    private static Vec3 horizontalDirection(float playerYawDeg, RandomSource random) {
        float yawRad = (playerYawDeg + random.nextFloat() * 360.0f) * Mth.DEG_TO_RAD;
        return new Vec3(-Mth.sin(yawRad), 0.0, Mth.cos(yawRad));
    }
}
