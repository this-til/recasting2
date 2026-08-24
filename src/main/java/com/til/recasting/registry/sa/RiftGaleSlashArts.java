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
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

@Setter
@Accessors(chain = true)
public class RiftGaleSlashArts extends ExtendedSlashArts {

    private int driveCount = 20;
    private int driveDurationTicks = 20;
    private float driveAttack = 0.1f;
    private float driveMinSize = 0.6f;
    private float driveSizeRange = 0.4f;
    private int driveLifeTicks = 10;
    private float driveSpeed = 4.5f;
    private float crossAttack = 1.35f;
    private float crossSize = 3.5f;
    private int crossDelayTicks = 2;

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

        livingEntity.getCapability(CapabilityRegistryHandler.TIME_RUN)
                .resolve()
                .ifPresentOrElse(
                        timeRun -> scheduleSequence(timeRun, livingEntity, slashBladeState),
                        () -> spawnSequenceImmediately(livingEntity, slashBladeState)
                );

        livingEntity.playSound(
                SoundEvents.CHORUS_FRUIT_TELEPORT,
                0.2F,
                1.45F
        );
    }

    private void scheduleSequence(
            ITimeRun timeRun,
            LivingEntity livingEntity,
            ISlashBladeState slashBladeState
    ) {
        RandomSource random = livingEntity.getRandom();
        for(int i = 0; i < driveCount; i++) {
            int delayTicks = random.nextInt(driveDurationTicks);
            timeRun.addTimerCell(
                    () -> spawnDrive(livingEntity.level(), livingEntity, slashBladeState, livingEntity.getRandom()),
                    delayTicks
            );
        }
        timeRun.addTimerCell(
                () -> spawnCross(livingEntity, slashBladeState, livingEntity.getRandom()),
                driveDurationTicks + crossDelayTicks
        );
    }

    private void spawnSequenceImmediately(
            LivingEntity livingEntity,
            ISlashBladeState slashBladeState
    ) {
        RandomSource random = livingEntity.getRandom();
        for(int i = 0; i < driveCount; i++) {
            spawnDrive(livingEntity.level(), livingEntity, slashBladeState, random);
        }
        spawnCross(livingEntity, slashBladeState, random);
    }

    private void spawnDrive(
            Level worldIn,
            LivingEntity livingEntity,
            ISlashBladeState slashBladeState,
            RandomSource random
    ) {
        DriveEntity driveEntity = new DriveEntity(
                RecastingEntities.DRIVE.get(),
                worldIn,
                livingEntity
        );

        Vec3 pos = livingEntity.position()
                .add(0.0D, livingEntity.getEyeHeight() * 0.75D, 0.0D)
                .add(livingEntity.getLookAngle().scale(0.3f));
        driveEntity.setPos(pos.x, pos.y, pos.z);
        driveEntity.setColor(slashBladeState.getColorCode());
        driveEntity.setModifiedRatio(driveAttack);
        driveEntity.setMaxLifeTime(driveLifeTicks);
        driveEntity.setSize(driveMinSize + random.nextFloat() * driveSizeRange);
        driveEntity.setRoll(random.nextFloat() * 360.0f);
        driveEntity.setSeep(driveSpeed);
        driveEntity.lookAt(livingEntity.getLookAngle(), true);

        worldIn.addFreshEntity(driveEntity);
    }

    private void spawnCross(
            LivingEntity livingEntity,
            ISlashBladeState slashBladeState,
            RandomSource random
    ) {
        float roll = random.nextFloat() * 360.0f;
        spawnCrossSlash(livingEntity, slashBladeState, roll);
        spawnCrossSlash(livingEntity, slashBladeState, roll + 90.0f);
    }

    private void spawnCrossSlash(
            LivingEntity livingEntity,
            ISlashBladeState slashBladeState,
            float roll
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
        driveEntity.setModifiedRatio(crossAttack);
        driveEntity.setMaxLifeTime(driveLifeTicks);
        driveEntity.setSize(crossSize);
        driveEntity.setRoll(roll);
        driveEntity.setSeep(driveSpeed);
        driveEntity.lookAt(livingEntity.getLookAngle(), true);

        livingEntity.level().addFreshEntity(driveEntity);
    }
}
