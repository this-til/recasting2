package com.til.recasting.registry.sa;

import com.til.recasting.capability.ITimeRun;
import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.capability.RenderDefinitionExtension;
import com.til.recasting.handler.BuffSourceHelper;
import com.til.recasting.handler.CapabilityRegistryHandler;
import com.til.recasting.handler.EntityHelper;
import com.til.recasting.handler.ParticleHelper;
import com.til.recasting.registry.RecastingBuffTypes;
import com.til.recasting.registry.instance.BuffType;
import lombok.Setter;
import lombok.experimental.Accessors;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * 永恒守卫：以自身为中心展开领域，进入范围内的敌人被钉在进入时的绝对坐标，无法移动。
 * 边界粒子环 + 持有 Buff 时的广告牌光圈。
 */
@Setter
@Accessors(chain = true)
public class EternalGuardSlashArts extends ExtendedSlashArts {

    private static final String TIMER_NAME = "eternal_guard";
    private static final String VISUAL_TIMER_NAME = "eternal_guard_visual";

    private int durationSeconds = 25;
    private float radius = 11.0f;
    private int ringSegments = 28;
    private int visualColor = 0x3A6BFF;
    private float ringDustSize = 1.2f;

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

        BuffType buffType = RecastingBuffTypes.ETERNAL_GUARD.get();
        livingEntity.getCapability(CapabilityRegistryHandler.BUFF_STACK_DATA).ifPresent(buffData -> {
            buffData.setLevel(buffType, durationSeconds, level);
            BuffSourceHelper.recordSourceEntity(buffData, buffType, livingEntity, livingEntity);
        });

        Map<UUID, Vec3> absolutePins = new HashMap<>();
        livingEntity.getCapability(CapabilityRegistryHandler.TIME_RUN).ifPresent(timeRun -> {
            timeRun.removeNamedTimerCell(TIMER_NAME);
            timeRun.removeNamedTimerCell(VISUAL_TIMER_NAME);

            timeRun.addNamedTimerCell(
                    TIMER_NAME,
                    new ITimeRun.TimerCell(
                            () -> tickFreeze(livingEntity, absolutePins, timeRun),
                            1,
                            true
                    )
            );
            timeRun.addNamedTimerCell(
                    VISUAL_TIMER_NAME,
                    new ITimeRun.TimerCell(
                            () -> renderBoundaryRing(livingEntity),
                            2,
                            true
                    )
            );
        });

        level.playSound(
                null,
                livingEntity.getX(),
                livingEntity.getY(),
                livingEntity.getZ(),
                SoundEvents.BEACON_ACTIVATE,
                SoundSource.PLAYERS,
                0.7F,
                1.2F
        );
        renderBoundaryRing(livingEntity);
    }

    private void tickFreeze(LivingEntity caster, Map<UUID, Vec3> absolutePins, ITimeRun timeRun) {
        if (!caster.isAlive() || caster.isRemoved() || caster.level().isClientSide()) {
            timeRun.removeNamedTimerCell(TIMER_NAME);
            timeRun.removeNamedTimerCell(VISUAL_TIMER_NAME);
            absolutePins.clear();
            return;
        }

        int buffLevel = caster.getCapability(CapabilityRegistryHandler.BUFF_STACK_DATA)
                .map(data -> data.getLevel(RecastingBuffTypes.ETERNAL_GUARD.get(), caster.level()))
                .orElse(0);
        if (buffLevel <= 0) {
            timeRun.removeNamedTimerCell(TIMER_NAME);
            timeRun.removeNamedTimerCell(VISUAL_TIMER_NAME);
            absolutePins.clear();
            return;
        }

        List<LivingEntity> nearby = EntityHelper.getTargettableLivingEntityWithinAABB(
                caster.level(),
                caster,
                caster.position(),
                radius
        );
        Set<UUID> present = new HashSet<>();
        for(LivingEntity entity : nearby) {
            UUID id = entity.getUUID();
            present.add(id);
            Vec3 pin = absolutePins.computeIfAbsent(id, ignored -> entity.position());
            entity.teleportTo(pin.x, pin.y, pin.z);
            entity.setDeltaMovement(Vec3.ZERO);
            entity.hurtMarked = true;
        }

        absolutePins.entrySet().removeIf(entry -> !present.contains(entry.getKey()));
    }

    private void renderBoundaryRing(LivingEntity caster) {
        if (!(caster.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        if (!caster.isAlive() || caster.isRemoved()) {
            return;
        }

        float red = ((visualColor >> 16) & 0xFF) / 255.0f;
        float green = ((visualColor >> 8) & 0xFF) / 255.0f;
        float blue = (visualColor & 0xFF) / 255.0f;
        DustParticleOptions dust = new DustParticleOptions(new Vector3f(red, green, blue), ringDustSize);
        double footY = caster.getY() + 0.25;
        double waistY = caster.getY() + 1.0;
        double baseAngle = caster.tickCount * (Math.PI / 40.0);

        for(int i = 0; i < ringSegments; i++) {
            double angle = baseAngle + (Math.PI * 2.0) * i / ringSegments;
            double x = caster.getX() + Math.cos(angle) * radius;
            double z = caster.getZ() + Math.sin(angle) * radius;
            ParticleHelper.sendParticlesLongRange(serverLevel, dust, x, footY, z, 1, 0.0, 0.0, 0.0, 0.0);
            if (i % 2 == 0) {
                ParticleHelper.sendParticlesLongRange(
                        serverLevel,
                        ParticleTypes.END_ROD,
                        x,
                        waistY,
                        z,
                        1,
                        0.0,
                        0.0,
                        0.0,
                        0.0
                );
            }
        }
    }
}
