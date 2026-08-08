package com.til.recasting.registry.sa;

import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.capability.RenderDefinitionExtension;
import com.til.recasting.entity.DriveEntity;
import com.til.recasting.entity.SummondSwordEntity;
import com.til.recasting.handler.EntityHelper;
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

import java.util.List;

/**
 * 青茫熳天摇：射出普通幻影剑，消失时扩散剑气；索敌对齐星流。
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
    private int bladeLifeMin = 10;
    private int bladeLifeMax = 40;
    private float bladeSize = 4.0f;
    private float seekRange = 12.0f;

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

        Vec3 attackPos = PosHelper.getAttackTargetPosition(livingEntity, slashBladeState);
        List<LivingEntity> entityList = EntityHelper.getTargettableLivingEntityWithinAABB(
                level,
                livingEntity,
                attackPos,
                seekRange
        );

        RandomSource random = livingEntity.getRandom();
        int color = slashBladeState.getColorCode();
        int lifeSpan = Math.max(0, bladeLifeMax - bladeLifeMin);

        for (int i = 0; i < cloudCount; i++) {
            SummondSwordEntity cloud = new SummondSwordEntity(
                    RecastingEntities.SUMMOND_SWORD.get(),
                    level,
                    livingEntity
            );
            cloud.setColor(color);
            cloud.setModifiedRatio(bladeRatio);
            cloud.setSize(bladeSize);
            cloud.setMaxLifeTime(bladeLifeMin + random.nextInt(lifeSpan + 1));
            cloud.setStartDelay(10 + random.nextInt(10));
            cloud.setIgnoringBlock(true);
            cloud.setRoll(random.nextInt(361));

            Vec3 targetPos;
            if (!entityList.isEmpty()) {
                Entity target = entityList.get(random.nextInt(entityList.size()));
                targetPos = new Vec3(
                        target.getX(),
                        target.getY() + target.getEyeHeight() * 0.5,
                        target.getZ()
                );
            } else {
                targetPos = attackPos;
            }
            cloud.lookAt(targetPos, false);

            cloud.endCallbackPoint.register(() -> spawnDriveBurst(cloud, livingEntity, color));

            level.addFreshEntity(cloud);
        }

        level.playSound(
                null,
                livingEntity.getX(),
                livingEntity.getY(),
                livingEntity.getZ(),
                SoundEvents.CHORUS_FRUIT_TELEPORT,
                SoundSource.PLAYERS,
                0.2F,
                1.45F
        );
    }

    private void spawnDriveBurst(SummondSwordEntity cloud, LivingEntity caster, int color) {
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
