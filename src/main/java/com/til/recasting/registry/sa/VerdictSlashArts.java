package com.til.recasting.registry.sa;

import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.capability.RenderDefinitionExtension;
import com.til.recasting.entity.DriveEntity;
import com.til.recasting.entity.SummondSwordEntity;
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
 * [回到未来计划]断罪：发射放大剑气，命中后在目标周围刷出与视线同向的幻影剑。
 */
@Setter
@Accessors(chain = true)
public class VerdictSlashArts extends ExtendedSlashArts {

    private float driveRatio = 0.75f;
    private float driveSeep = 0.25f;
    private int driveLife = 300;
    private float driveSize = 5.0f;
    private float driveRoll = 90.0f;
    private int followBladeCount = 12;
    private float followBladeRatio = 0.375f;
    private int followBladeLife = 100;
    private float followSpawnRange = 7.0f;

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

        int color = slashBladeState.getColorCode();
        Vec3 origin = livingEntity.position().add(0.0, livingEntity.getEyeHeight() * 0.5, 0.0);
        Vec3 look = livingEntity.getLookAngle();

        DriveEntity drive = new DriveEntity(
                RecastingEntities.DRIVE.get(),
                level,
                livingEntity
        );
        drive.setPos(origin.x, origin.y, origin.z);
        drive.setModifiedRatio(driveRatio);
        drive.setSeep(driveSeep);
        drive.setMaxLifeTime(driveLife);
        drive.setRoll(driveRoll);
        drive.setSize(driveSize);
        drive.setColor(color);
        drive.setRepeatedAttack(false);
        drive.setParameter(true);
        drive.lookAt(look, true);

        drive.attackActionCallbackPoint.register(hit -> spawnFollowBlades(drive, livingEntity, hit, color));

        level.addFreshEntity(drive);

        level.playSound(
                null,
                origin.x,
                origin.y,
                origin.z,
                SoundEvents.PLAYER_ATTACK_CRIT,
                SoundSource.PLAYERS,
                0.9f,
                0.65f
        );
    }

    private void spawnFollowBlades(
            DriveEntity drive,
            LivingEntity caster,
            LivingEntity target,
            int color
    ) {
        Level level = drive.level();
        if (level.isClientSide()) {
            return;
        }
        if (caster == null || !caster.isAlive() || target == null || !target.isAlive()) {
            return;
        }

        RandomSource random = level.getRandom();
        Vec3 look = caster.getLookAngle();
        for (int i = 0; i < followBladeCount; i++) {
            SummondSwordEntity blade = new SummondSwordEntity(
                    RecastingEntities.SUMMOND_SWORD.get(),
                    level,
                    caster
            );
            double ox = (random.nextDouble() * 2.0 - 1.0) * followSpawnRange;
            double oy = (random.nextDouble() * 2.0 - 1.0) * followSpawnRange;
            double oz = (random.nextDouble() * 2.0 - 1.0) * followSpawnRange;
            blade.setPos(target.getX() + ox, target.getY() + oy + target.getBbHeight() * 0.5, target.getZ() + oz);
            blade.setModifiedRatio(followBladeRatio);
            blade.setMaxLifeTime(followBladeLife);
            blade.setColor(color);
            // 朝向与玩家视线方向一致，而非射向目标点
            blade.lookAt(look, true);
            level.addFreshEntity(blade);
        }
    }
}
