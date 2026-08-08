package com.til.recasting.registry.sa;

import com.til.recasting.capability.ITimeRun;
import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.capability.RenderDefinitionExtension;
import com.til.recasting.handler.BuffSourceHelper;
import com.til.recasting.handler.CapabilityRegistryHandler;
import com.til.recasting.handler.EntityHelper;
import com.til.recasting.registry.RecastingBuffTypes;
import com.til.recasting.registry.instance.BuffType;
import lombok.Setter;
import lombok.experimental.Accessors;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * 永恒守卫：以自身为中心展开领域，进入范围内的敌人被钉在进入时的绝对坐标，无法移动。
 * 视觉由持有 {@code ETERNAL_GUARD} Buff 的实体广告牌光圈渲染。
 */
@Setter
@Accessors(chain = true)
public class EternalGuardSlashArts extends ExtendedSlashArts {

    private static final String TIMER_NAME = "eternal_guard";

    private int durationSeconds = 25;
    private float radius = 11.0f;

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
            timeRun.addNamedTimerCell(
                    TIMER_NAME,
                    new ITimeRun.TimerCell(
                            () -> tickFreeze(livingEntity, absolutePins, timeRun),
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
                SoundEvents.BEACON_ACTIVATE,
                SoundSource.PLAYERS,
                0.7F,
                1.2F
        );
    }

    private void tickFreeze(LivingEntity caster, Map<UUID, Vec3> absolutePins, ITimeRun timeRun) {
        if (!caster.isAlive() || caster.isRemoved() || caster.level().isClientSide()) {
            timeRun.removeNamedTimerCell(TIMER_NAME);
            absolutePins.clear();
            return;
        }

        int buffLevel = caster.getCapability(CapabilityRegistryHandler.BUFF_STACK_DATA)
                .map(data -> data.getLevel(RecastingBuffTypes.ETERNAL_GUARD.get(), caster.level()))
                .orElse(0);
        if (buffLevel <= 0) {
            timeRun.removeNamedTimerCell(TIMER_NAME);
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
        for (LivingEntity entity : nearby) {
            UUID id = entity.getUUID();
            present.add(id);
            Vec3 pin = absolutePins.computeIfAbsent(id, ignored -> entity.position());
            entity.teleportTo(pin.x, pin.y, pin.z);
            entity.setDeltaMovement(Vec3.ZERO);
            entity.hurtMarked = true;
        }

        Iterator<Map.Entry<UUID, Vec3>> iterator = absolutePins.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, Vec3> entry = iterator.next();
            if (!present.contains(entry.getKey())) {
                iterator.remove();
            }
        }
    }
}
