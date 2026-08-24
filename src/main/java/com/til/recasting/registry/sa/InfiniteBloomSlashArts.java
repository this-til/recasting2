package com.til.recasting.registry.sa;

import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.capability.RenderDefinitionExtension;
import com.til.recasting.entity.DriveEntity;
import com.til.recasting.entity.JudgementCutEntity;
import com.til.recasting.handler.AttackHelper;
import com.til.recasting.handler.EntityHelper;
import com.til.recasting.handler.PosHelper;
import com.til.recasting.registry.RecastingAttackTypes;
import com.til.recasting.registry.RecastingEntities;
import com.til.recasting.registry.instance.AttackType;
import com.til.recasting.util.DamageStructure;
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

import org.jetbrains.annotations.Nullable;
import java.util.List;

/**
 * [回到未来计划]无限开花决：锁定优先、视锥索敌；中心次元斩 + 螺旋环剑气，有目标时追加近距盒伤。
 */
@Setter
@Accessors(chain = true)
public class InfiniteBloomSlashArts extends ExtendedSlashArts {

    private float meleeRatio = 1.0f;
    private float centreRatio = 0.5f;
    private float driveRatio = 0.15f;
    private float areaRange = 2.5f;
    private int driveCount = 36;
    private int judgementLifeMinTicks = 40;
    private int judgementLifeMaxTicks = 80;
    private int driveLifeMinTicks = 40;
    private int driveLifeMaxTicks = 80;
    private float driveSeepMin = 0.05f;
    private float driveSeepMax = 0.35f;
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

        LivingEntity target = resolveLockThenCone(livingEntity, slashBladeState, level);
        Vec3 center = target != null
                ? PosHelper.getEntityAimPosition(target)
                : PosHelper.getAttackTargetPosition(livingEntity, slashBladeState);
        int color = slashBladeState.getColorCode();
        List<AttackType> meleeTypes = List.of(RecastingAttackTypes.SLASH_EFFECT_ATTACK.get());

        if (target != null) {
            AttackHelper.areaAttack(
                    livingEntity,
                    center,
                    new DamageStructure(meleeRatio, 0.0f),
                    areaRange,
                    meleeTypes,
                    null,
                    null
            );
        }

        RandomSource random = livingEntity.getRandom();
        int jcLifeTicks = judgementLifeMinTicks + random.nextInt(Math.max(1, judgementLifeMaxTicks - judgementLifeMinTicks + 1));

        JudgementCutEntity jc = new JudgementCutEntity(
                RecastingEntities.JUDGEMENT_CUT.get(),
                level,
                livingEntity
        );
        jc.setPos(center.x, center.y, center.z);
        jc.setColor(color);
        jc.setModifiedRatio(centreRatio);
        jc.setMaxLifeTime(jcLifeTicks);
        level.addFreshEntity(jc);

        spawnSpiralDrives(livingEntity, level, center, color, random);

        level.playSound(
                null,
                center.x,
                center.y,
                center.z,
                SoundEvents.ENDERMAN_TELEPORT,
                SoundSource.PLAYERS,
                0.5F,
                0.8F / (random.nextFloat() * 0.4F + 0.8F)
        );
    }

    private void spawnSpiralDrives(
            LivingEntity caster,
            Level level,
            Vec3 center,
            int color,
            RandomSource random
    ) {
        Vec3 ringCenter = center.add(0.0, -0.5, 0.0);
        float yawStep = 360.0f / driveCount;
        float offsetYaw = random.nextFloat() * 180.0f;

        for(int i = 0; i < driveCount; i++) {
            float yaw = yawStep * i;
            float yawWithOffset = yaw + offsetYaw;
            float pitch = -30.0f * Mth.cos((yaw - 60.0f) * Mth.DEG_TO_RAD);

            double dx = Mth.cos(yawWithOffset * Mth.DEG_TO_RAD);
            double dy = 0.7 * Mth.sin((yaw - 60.0f) * Mth.DEG_TO_RAD);
            double dz = Mth.sin(yawWithOffset * Mth.DEG_TO_RAD);

            float roll = (float) (90.0 - 30.0 * Mth.cos((yaw + 30.0f) * Mth.DEG_TO_RAD));
            float seep = driveSeepMin + random.nextFloat() * (driveSeepMax - driveSeepMin);
            int driveLifeTicks = driveLifeMinTicks + random.nextInt(Math.max(1, driveLifeMaxTicks - driveLifeMinTicks + 1));

            DriveEntity drive = new DriveEntity(
                    RecastingEntities.DRIVE.get(),
                    level,
                    caster
            );
            drive.setPos(ringCenter.x - dx, ringCenter.y - dy, ringCenter.z - dz);
            drive.setColor(color);
            drive.setModifiedRatio(driveRatio);
            drive.setSeep(seep);
            drive.setMaxLifeTime(driveLifeTicks);
            drive.setRoll(roll + 90.0f);
            drive.setSize(1.0f);
            drive.setAttackInterval(5);
            drive.setRepeatedAttack(false);
            drive.setParameter(true);
            drive.setSpeedScalePerTick(speedScalePerTick);
            drive.lookAt(directionFromYawPitch(yawWithOffset, pitch), true);
            level.addFreshEntity(drive);
        }
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

    @Nullable
    private static LivingEntity resolveLockThenCone(
            LivingEntity caster,
            ISlashBladeState slashBladeState,
            Level level
    ) {
        Entity lock = slashBladeState.getTargetEntity(level);
        if (lock instanceof LivingEntity locked && locked.isAlive() && !locked.isRemoved()) {
            return locked;
        }
        return EntityHelper.selectClosestInViewCone(caster).orElse(null);
    }
}
