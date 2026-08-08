package com.til.recasting.registry.sa;

import com.til.recasting.capability.ITimeRun;
import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.capability.RenderDefinitionExtension;
import com.til.recasting.constant.R;
import com.til.recasting.entity.JudgementCutEntity;
import com.til.recasting.entity.TrackingSummondSwordEntity;
import com.til.recasting.handler.CapabilityRegistryHandler;
import com.til.recasting.handler.PosHelper;
import com.til.recasting.registry.RecastingEntities;
import lombok.Setter;
import lombok.experimental.Accessors;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * 禁锢：将锁定目标钉在次元斩中心，并持续环射追踪幻影剑。
 */
@Setter
@Accessors(chain = true)
public class ImprisonmentSlashArts extends ExtendedSlashArts {

    private static final String TIMER_NAME = "imprisonment";

    private float centerRatio = 0.25f;
    private float bladeRatio = 0.25f;
    private int durationTicks = 300;
    private int spawnInterval = 2;
    private float shellRadius = 8.0f;
    private int bladeLife = 35;
    private int bladeInterval = 20;
    private float judgementCutSize = 2.0f;

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

        Vec3 center = PosHelper.getEntityAimPosition(target);
        JudgementCutEntity jc = new JudgementCutEntity(
                RecastingEntities.JUDGEMENT_CUT.get(),
                level,
                livingEntity
        );
        jc.setPos(center.x, center.y, center.z);
        jc.setColor(slashBladeState.getColorCode());
        jc.setModifiedRatio(centerRatio);
        jc.setSize(judgementCutSize);
        jc.setMaxLifeTime(durationTicks);
        jc.setRepeatedAttack(true);
        jc.setAttackInterval(4);
        level.addFreshEntity(jc);

        level.playSound(
                null,
                center.x,
                center.y,
                center.z,
                SoundEvents.ENDERMAN_TELEPORT,
                SoundSource.PLAYERS,
                0.6F,
                0.8F / (livingEntity.getRandom().nextFloat() * 0.4F + 0.8F)
        );

        livingEntity.getCapability(CapabilityRegistryHandler.TIME_RUN).ifPresent(timeRun -> {
            timeRun.removeNamedTimerCell(TIMER_NAME);
            int[] left = {durationTicks};
            timeRun.addNamedTimerCell(
                    TIMER_NAME,
                    new ITimeRun.TimerCell(
                            () -> tickImprisonment(livingEntity, target, jc, left, timeRun, slashBladeState.getColorCode()),
                            1,
                            true
                    )
            );
        });
    }

    private void tickImprisonment(
            LivingEntity caster,
            LivingEntity target,
            JudgementCutEntity jc,
            int[] left,
            ITimeRun timeRun,
            int color
    ) {
        if (!caster.isAlive()
                || caster.isRemoved()
                || !target.isAlive()
                || target.isRemoved()
                || !jc.isAlive()
                || jc.isRemoved()
                || left[0] <= 0) {
            timeRun.removeNamedTimerCell(TIMER_NAME);
            return;
        }

        left[0]--;
        double pinY = jc.getY() - target.getBbHeight() * 0.5;
        target.teleportTo(jc.getX(), pinY, jc.getZ());
        target.setDeltaMovement(Vec3.ZERO);
        target.hurtMarked = true;

        if (left[0] % spawnInterval != 0) {
            return;
        }

        Level level = caster.level();
        RandomSource random = caster.getRandom();
        Vec3 offset = PosHelper.getRandomVectorInCircle(random, shellRadius);
        Vec3 spawnPos = jc.position().add(offset);

        TrackingSummondSwordEntity blade = new TrackingSummondSwordEntity(
                RecastingEntities.TRACKING_SUMMOND_SWORD.get(),
                level,
                caster
        );
        blade.setPos(spawnPos.x, spawnPos.y, spawnPos.z);
        blade.setModel(R.Models.Special.imprisonment$obj);
        blade.setTexture(R.Models.Special.imprisonment$png);
        blade.setColor(color);
        blade.setModifiedRatio(bladeRatio);
        blade.setInterval(bladeInterval);
        blade.setMaxLifeTime(bladeLife);
        blade.setStartDelay(0);
        blade.setIgnoringBlock(true);
        blade.setTargetEntity(target);
        blade.lookAt(PosHelper.getEntityAimPosition(target), false);
        level.addFreshEntity(blade);
    }
}
