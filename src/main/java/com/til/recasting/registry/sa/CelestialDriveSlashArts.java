package com.til.recasting.registry.sa;

import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.capability.RenderDefinitionExtension;
import com.til.recasting.entity.DriveEntity;
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
 * [回到未来计划]星辰斗转：从自身向球面全方位散射短命剑气。
 */
@Setter
@Accessors(chain = true)
public class CelestialDriveSlashArts extends ExtendedSlashArts {

    private int yawSteps = 8;
    private int pitchSteps = 8;
    private float driveRatio = 0.65f;
    private float driveSeep = 1.8f;
    private int life = 10;
    private int driveColor = 0x3333FF;

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

        RandomSource random = livingEntity.getRandom();
        Vec3 origin = livingEntity.position().add(0.0, livingEntity.getEyeHeight() * 0.5, 0.0);
        float yawStep = 360.0f / yawSteps;
        float pitchStep = 360.0f / pitchSteps;

        for (int i = 0; i < yawSteps; i++) {
            for (int o = 0; o < pitchSteps; o++) {
                Vec3 dir = directionFromYawPitch(i * yawStep, o * pitchStep);
                DriveEntity drive = new DriveEntity(
                        RecastingEntities.DRIVE.get(),
                        level,
                        livingEntity
                );
                drive.setPos(origin.x, origin.y, origin.z);
                drive.setColor(driveColor);
                drive.setModifiedRatio(driveRatio);
                drive.setSeep(driveSeep);
                drive.setMaxLifeTime(life);
                drive.setRepeatedAttack(false);
                drive.setParameter(true);
                drive.setRoll(random.nextInt(361));
                drive.setSize(1.0f);
                // isDistance=true：传入方向向量，不要传世界坐标
                drive.lookAt(dir, true);
                level.addFreshEntity(drive);
            }
        }

        level.playSound(
                null,
                origin.x,
                origin.y,
                origin.z,
                SoundEvents.ENDER_DRAGON_FLAP,
                SoundSource.PLAYERS,
                0.7F,
                1.3F + random.nextFloat() * 0.3F
        );
    }

    private static Vec3 directionFromYawPitch(float yawDeg, float pitchDeg) {
        float yaw = yawDeg * Mth.DEG_TO_RAD;
        float pitch = pitchDeg * Mth.DEG_TO_RAD;
        float cosPitch = Mth.cos(pitch);
        return new Vec3(
                -Mth.sin(yaw) * cosPitch,
                -Mth.sin(pitch),
                Mth.cos(yaw) * cosPitch
        );
    }
}
