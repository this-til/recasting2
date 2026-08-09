package com.til.recasting.registry.sa;

import com.til.recasting.capability.ITimeRun;
import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.capability.RenderDefinitionExtension;
import com.til.recasting.entity.TrackingSummondSwordEntity;
import com.til.recasting.handler.AttackHelper;
import com.til.recasting.handler.CapabilityRegistryHandler;
import com.til.recasting.handler.EntityHelper;
import com.til.recasting.handler.PosHelper;
import com.til.recasting.registry.RecastingAttackTypes;
import com.til.recasting.registry.RecastingEntities;
import com.til.recasting.util.DamageStructure;
import lombok.Setter;
import lombok.experimental.Accessors;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;

/**
 * [回到未来计划]暴烈剑气：锁定优先、视锥索敌；持续刷出追踪幻影剑，命中仅播小型爆炸特效。
 */
@Setter
@Accessors(chain = true)
public class BlisteringQiSlashArts extends ExtendedSlashArts {

    private static final String TIMER_NAME = "blistering_qi";
    private static final int COLOR_BLUE = 0x0000FF;
    private static final int COLOR_RED = 0xFF0000;

    private float meleeRatio = 1.0f;
    private float bladeRatio = 0.12f;
    private int durationTicks = 100;
    private int bladesPerTick = 2;
    private int bladeLife = 120;
    private int breakDelay = 10;

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
        if (target == null) {
            return;
        }

        AttackHelper.attack(
                livingEntity,
                target,
                new DamageStructure(meleeRatio, 0.0f),
                List.of(RecastingAttackTypes.SLASH_EFFECT_ATTACK.get())
        );

        int[] ticksLeft = {durationTicks};
        LivingEntity[] lockedTarget = {target};

        livingEntity.getCapability(CapabilityRegistryHandler.TIME_RUN).ifPresent(timeRun -> {
            timeRun.removeNamedTimerCell(TIMER_NAME);
            timeRun.addNamedTimerCell(
                    TIMER_NAME,
                    new ITimeRun.TimerCell(
                            () -> tickSpawnBlades(livingEntity, timeRun, ticksLeft, lockedTarget),
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
                SoundEvents.PLAYER_ATTACK_SWEEP,
                SoundSource.PLAYERS,
                0.8F,
                0.7F + livingEntity.getRandom().nextFloat() * 0.3F
        );
    }

    private void tickSpawnBlades(
            LivingEntity caster,
            ITimeRun timeRun,
            int[] ticksLeft,
            LivingEntity[] lockedTarget
    ) {
        if (!caster.isAlive() || --ticksLeft[0] < 0) {
            timeRun.removeNamedTimerCell(TIMER_NAME);
            return;
        }

        Level level = caster.level();
        if (level.isClientSide()) {
            return;
        }

        LivingEntity target = lockedTarget[0];
        if (target == null || !target.isAlive() || target.isRemoved()) {
            LivingEntity retarget = EntityHelper.selectClosestInViewCone(caster).orElse(null);
            if (retarget == null) {
                timeRun.removeNamedTimerCell(TIMER_NAME);
                return;
            }
            lockedTarget[0] = retarget;
            target = retarget;
        }

        RandomSource random = caster.getRandom();
        Vec3 aim = PosHelper.getEntityAimPosition(target);

        for (int i = 0; i < bladesPerTick; i++) {
            TrackingSummondSwordEntity blade = new TrackingSummondSwordEntity(
                    RecastingEntities.TRACKING_SUMMOND_SWORD.get(),
                    level,
                    caster
            );
            blade.setInterval(0);
            blade.setStartDelay(0);
            blade.setModifiedRatio(bladeRatio);
            blade.setMaxLifeTime(bladeLife);
            blade.setBreakDelay(breakDelay);
            blade.setIgnoringBlock(true);
            blade.setColor((i % 2 == 0) ? COLOR_BLUE : COLOR_RED);
            blade.setTargetEntity(target);
            blade.lookAt(aim, false);

            blade.attackActionCallbackPoint.register(hit -> spawnBurstVfx(blade));

            level.addFreshEntity(blade);
        }
    }

    private static void spawnBurstVfx(TrackingSummondSwordEntity blade) {
        if (!(blade.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        Vec3 pos = blade.position();
        serverLevel.playSound(
                null,
                pos.x,
                pos.y,
                pos.z,
                SoundEvents.GENERIC_EXPLODE,
                SoundSource.PLAYERS,
                0.25F,
                1.2F + blade.level().getRandom().nextFloat() * 0.3F
        );
        serverLevel.sendParticles(
                ParticleTypes.EXPLOSION,
                pos.x,
                pos.y,
                pos.z,
                2,
                0.15,
                0.15,
                0.15,
                0.05
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
