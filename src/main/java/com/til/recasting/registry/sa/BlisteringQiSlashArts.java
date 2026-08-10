package com.til.recasting.registry.sa;

import com.til.recasting.capability.ITimeRun;
import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.capability.RenderDefinitionExtension;
import com.til.recasting.entity.SummondSwordEntity;
import com.til.recasting.handler.CapabilityRegistryHandler;
import com.til.recasting.handler.ParticleHelper;
import com.til.recasting.handler.PosHelper;
import com.til.recasting.registry.RecastingEntities;
import lombok.Setter;
import lombok.experimental.Accessors;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * [回到未来计划]暴烈剑气：持续刷出普通幻影剑，指向锁定/视线目标；命中仅播小型爆炸特效。
 */
@Setter
@Accessors(chain = true)
public class BlisteringQiSlashArts extends ExtendedSlashArts {

    private static final String TIMER_NAME = "blistering_qi";
    private static final int COLOR_BLUE = 0x0000FF;
    private static final int COLOR_RED = 0xFF0000;

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

        AtomicInteger ticksLeft = new AtomicInteger(durationTicks);

        livingEntity.getCapability(CapabilityRegistryHandler.TIME_RUN).ifPresent(timeRun -> {
            timeRun.removeNamedTimerCell(TIMER_NAME);
            timeRun.addNamedTimerCell(
                    TIMER_NAME,
                    new ITimeRun.TimerCell(
                            () -> tickSpawnBlades(livingEntity, slashBladeState, timeRun, ticksLeft),
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
            ISlashBladeState slashBladeState,
            ITimeRun timeRun,
            AtomicInteger ticksLeft
    ) {
        if (!caster.isAlive() || ticksLeft.decrementAndGet() < 0) {
            timeRun.removeNamedTimerCell(TIMER_NAME);
            return;
        }

        Level level = caster.level();
        if (level.isClientSide()) {
            return;
        }

        Vec3 aim = PosHelper.getAttackTargetPosition(caster, slashBladeState);

        for(int i = 0; i < bladesPerTick; i++) {
            SummondSwordEntity blade = new SummondSwordEntity(
                    RecastingEntities.SUMMOND_SWORD.get(),
                    level,
                    caster
            );
            blade.setStartDelay(0);
            blade.setModifiedRatio(bladeRatio);
            blade.setMaxLifeTime(bladeLife);
            blade.setBreakDelay(breakDelay);
            blade.setIgnoringBlock(true);
            blade.setColor((i % 2 == 0)
                    ? COLOR_BLUE
                    : COLOR_RED);
            blade.lookAt(aim, false);

            blade.attackActionCallbackPoint.register(hit -> spawnBurstVfx(blade));

            level.addFreshEntity(blade);
        }
    }

    private static void spawnBurstVfx(SummondSwordEntity blade) {
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
        ParticleHelper.sendParticlesLongRange(
                serverLevel,
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
}
