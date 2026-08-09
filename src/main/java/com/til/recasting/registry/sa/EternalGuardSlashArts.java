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
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.*;

/**
 * 永恒守卫：以自身为中心展开领域，进入范围内的敌人被钉在进入时的绝对坐标，无法移动；
 * 同时清除领域内非释放者发出的弹射物。边界粒子环绕施法者；光圈 Buff 挂在被禁锢目标身上。
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

        Map<UUID, Vec3> absolutePins = new HashMap<>();
        Map<UUID, LivingEntity> pinnedTargets = new HashMap<>();
        int[] ticksLeft = {durationSeconds * 20};

        livingEntity.getCapability(CapabilityRegistryHandler.TIME_RUN).ifPresent(timeRun -> {
            timeRun.removeNamedTimerCell(TIMER_NAME);
            timeRun.removeNamedTimerCell(VISUAL_TIMER_NAME);

            timeRun.addNamedTimerCell(
                    TIMER_NAME,
                    new ITimeRun.TimerCell(
                            () -> tickFreeze(livingEntity, absolutePins, pinnedTargets, ticksLeft, timeRun),
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

    private void tickFreeze(
            LivingEntity caster,
            Map<UUID, Vec3> absolutePins,
            Map<UUID, LivingEntity> pinnedTargets,
            int[] ticksLeft,
            ITimeRun timeRun
    ) {
        if (!caster.isAlive() || caster.isRemoved() || caster.level().isClientSide() || ticksLeft[0] <= 0) {
            endDomain(timeRun, absolutePins, pinnedTargets);
            return;
        }

        ticksLeft[0]--;
        int displayLevel = Math.max(1, (ticksLeft[0] + 19) / 20);
        BuffType buffType = RecastingBuffTypes.ETERNAL_GUARD.get();

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
            pinnedTargets.put(id, entity);
            entity.teleportTo(pin.x, pin.y, pin.z);
            entity.setDeltaMovement(Vec3.ZERO);
            entity.hurtMarked = true;
            mountGuardBuff(entity, caster, buffType, displayLevel);
        }

        Iterator<Map.Entry<UUID, LivingEntity>> iterator = pinnedTargets.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, LivingEntity> entry = iterator.next();
            if (present.contains(entry.getKey())) {
                continue;
            }
            clearGuardBuff(entry.getValue(), buffType);
            absolutePins.remove(entry.getKey());
            iterator.remove();
        }

        discardForeignProjectiles(caster);
    }

    /**
     * 清除领域内非本 SA 释放者发出的弹射物。
     */
    private void discardForeignProjectiles(LivingEntity caster) {
        Level level = caster.level();
        AABB area = new AABB(caster.position(), caster.position()).inflate(radius);
        for(Projectile projectile : level.getEntitiesOfClass(Projectile.class, area)) {
            if (caster.equals(projectile.getOwner())) {
                continue;
            }
            projectile.discard();
        }
    }

    private void endDomain(
            ITimeRun timeRun,
            Map<UUID, Vec3> absolutePins,
            Map<UUID, LivingEntity> pinnedTargets
    ) {
        timeRun.removeNamedTimerCell(TIMER_NAME);
        timeRun.removeNamedTimerCell(VISUAL_TIMER_NAME);
        BuffType buffType = RecastingBuffTypes.ETERNAL_GUARD.get();
        for(LivingEntity target : pinnedTargets.values()) {
            clearGuardBuff(target, buffType);
        }
        absolutePins.clear();
        pinnedTargets.clear();
    }

    private void mountGuardBuff(LivingEntity target, LivingEntity caster, BuffType buffType, int displayLevel) {
        target.getCapability(CapabilityRegistryHandler.BUFF_STACK_DATA).ifPresent(data -> {
            data.setLevel(buffType, displayLevel, target.level());
            BuffSourceHelper.recordSourceEntity(data, buffType, target, caster);
        });
    }

    private void clearGuardBuff(LivingEntity target, BuffType buffType) {
        if (target == null || target.isRemoved()) {
            return;
        }
        target.getCapability(CapabilityRegistryHandler.BUFF_STACK_DATA).ifPresent(data ->
                data.setLevel(buffType, 0, target.level())
        );
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
