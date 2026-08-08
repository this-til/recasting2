package com.til.recasting.registry.sa;

import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.capability.RenderDefinitionExtension;
import com.til.recasting.entity.DriveEntity;
import com.til.recasting.entity.TrackingSummondSwordEntity;
import com.til.recasting.handler.PosHelper;
import com.til.recasting.registry.RecastingEntities;
import lombok.Setter;
import lombok.experimental.Accessors;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * 青茫熳天摇：环绕施法者射出追踪飞剑，消失时扩散剑气。
 */
@Setter
@Accessors(chain = true)
public class AzureHazeSlashArts extends ExtendedSlashArts {

    private int cloudCount = 35;
    private float bladeRatio = 0.5f;
    private float driveRatio = 1.5f;
    private int driveCount = 7;
    private int driveLife = 55;
    private float driveSpeed = 0.23f;
    private int bladeLife = 100;
    private int bladeInterval = 15;
    private float spawnSpread = 8.0f;

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

        Entity lock = slashBladeState.getTargetEntity(level);
        if (!(lock instanceof LivingEntity target) || !target.isAlive() || target.isRemoved()) {
            return;
        }

        RandomSource random = livingEntity.getRandom();
        int color = slashBladeState.getColorCode();

        for (int i = 0; i < cloudCount; i++) {
            double rx = random.nextDouble();
            double ry = random.nextDouble();
            double rz = random.nextDouble();
            double xSpeed = random.nextGaussian() * 0.02;
            double ySpeed = random.nextGaussian() * 0.02;
            double zSpeed = random.nextGaussian() * 0.02;
            double width = livingEntity.getBbWidth();
            Vec3 spawnPos = new Vec3(
                    livingEntity.getX() + ((rx * 2.0 - 1.0) * width - xSpeed * 10.0) * spawnSpread,
                    livingEntity.getY() + ((ry * 2.0 - 1.0) * width - ySpeed * 10.0) * spawnSpread,
                    livingEntity.getZ() + ((rz * 2.0 - 1.0) * width - zSpeed * 10.0) * spawnSpread
            );

            TrackingSummondSwordEntity cloud = new TrackingSummondSwordEntity(
                    RecastingEntities.TRACKING_SUMMOND_SWORD.get(),
                    level,
                    livingEntity
            );
            cloud.setPos(spawnPos.x, spawnPos.y, spawnPos.z);
            cloud.setColor(color);
            cloud.setModifiedRatio(bladeRatio);
            cloud.setInterval(bladeInterval);
            cloud.setMaxLifeTime(bladeLife);
            cloud.setStartDelay(random.nextInt(8));
            cloud.setIgnoringBlock(true);
            cloud.setRoll(random.nextInt(361));
            cloud.setTargetEntity(target);
            cloud.lookAt(PosHelper.getEntityAimPosition(target), false);

            cloud.endCallbackPoint.register(() -> spawnDriveBurst(cloud, livingEntity, color));

            level.addFreshEntity(cloud);
        }

        level.playSound(
                null,
                livingEntity.getX(),
                livingEntity.getY(),
                livingEntity.getZ(),
                SoundEvents.TRIDENT_THROW,
                SoundSource.PLAYERS,
                0.8F,
                0.6F + random.nextFloat() * 0.4F
        );
    }

    private void spawnDriveBurst(TrackingSummondSwordEntity cloud, LivingEntity caster, int color) {
        Level level = cloud.level();
        if (level.isClientSide()) {
            return;
        }
        if (caster == null || !caster.isAlive()) {
            return;
        }

        RandomSource random = level.getRandom();
        double originY = cloud.getY() + cloud.getBbHeight() * 0.5;
        for (int d = 0; d < driveCount; d++) {
            DriveEntity drive = new DriveEntity(
                    RecastingEntities.DRIVE.get(),
                    level,
                    caster
            );
            drive.setPos(cloud.getX(), originY, cloud.getZ());
            drive.setColor(color);
            drive.setModifiedRatio(driveRatio);
            drive.setMaxLifeTime(driveLife);
            drive.setRoll(random.nextInt(361));
            drive.setSeep(driveSpeed);
            drive.setSize(1.2f);
            drive.lookAt(randomDirection(random), true);
            level.addFreshEntity(drive);
        }
    }

    private static Vec3 randomDirection(RandomSource random) {
        float yaw = random.nextFloat() * 360.0f;
        float pitch = random.nextFloat() * 360.0f;
        float yawRad = yaw * Mth.DEG_TO_RAD;
        float pitchRad = pitch * Mth.DEG_TO_RAD;
        float cosPitch = Mth.cos(pitchRad);
        return new Vec3(
                -Mth.sin(yawRad) * cosPitch,
                -Mth.sin(pitchRad),
                Mth.cos(yawRad) * cosPitch
        );
    }
}
