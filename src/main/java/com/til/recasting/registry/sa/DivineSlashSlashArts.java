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
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * [回到未来计划]神斩：沿视线发射多道随机色、递减体型的剑气。
 */
@Setter
@Accessors(chain = true)
public class DivineSlashSlashArts extends ExtendedSlashArts {

    private int driveCount = 16;
    private float driveRatio = 0.15f;
    private float driveSeep = 0.15f;
    private int life = 200;
    private float speedScalePerTick = 1.05f;

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
        Vec3 look = livingEntity.getLookAngle();

        for(int i = 0; i < driveCount; i++) {
            DriveEntity drive = new DriveEntity(
                    RecastingEntities.DRIVE.get(),
                    level,
                    livingEntity
            );
            drive.setPos(origin.x, origin.y, origin.z);
            drive.setModifiedRatio(driveRatio);
            drive.setSeep(driveSeep);
            drive.setMaxLifeTime(life);
            drive.setAttackInterval(5);
            drive.setRepeatedAttack(false);
            drive.setRoll(random.nextInt(361));
            drive.setColor(randomRgb(random));
            drive.setSize((driveCount - i) * (random.nextFloat() * 0.75f + 1.25f) / 4.0f);
            drive.setSpeedScalePerTick(speedScalePerTick);
            drive.setParameter(true);
            drive.lookAt(look, true);
            level.addFreshEntity(drive);
        }

        level.playSound(
                null,
                origin.x,
                origin.y,
                origin.z,
                SoundEvents.PLAYER_ATTACK_SWEEP,
                SoundSource.PLAYERS,
                0.8f,
                0.7f + random.nextFloat() * 0.3f
        );
    }

    private static int randomRgb(RandomSource random) {
        int r = random.nextInt(256);
        int g = random.nextInt(256);
        int b = random.nextInt(256);
        return (r << 16) | (g << 8) | b;
    }
}
