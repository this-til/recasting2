package com.til.recasting.registry.sa;

import com.til.recasting.capability.IBuffStackData;
import com.til.recasting.capability.ITimeRun;
import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.capability.RenderDefinitionExtension;
import com.til.recasting.handler.BuffSourceHelper;
import com.til.recasting.registry.RecastingAttachments;
import com.til.recasting.handler.EntityHelper;
import com.til.recasting.handler.ParticleHelper;
import com.til.recasting.registry.RecastingBuffTypes;
import lombok.Getter;
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
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 永恒守卫：以自身为中心展开领域，进入范围内的敌人被钉在进入时的绝对坐标，无法移动；
 * 同时清除领域内非释放者发出的弹射物。边界粒子环绕施法者。
 * 施术者挂 {@link RecastingBuffTypes#ETERNAL_GUARD_SELF}（物品栏条）；目标挂 {@link RecastingBuffTypes#ETERNAL_GUARD}（名称标签 / 光圈）。
 */
@Getter
@Setter
@Accessors(chain = true)
public class EternalGuardSlashArts extends ExtendedSlashArts {

    private static final String TIMER_NAME = "eternal_guard";
    private static final String VISUAL_TIMER_NAME = "eternal_guard_visual";

    private int durationTicks = 25 * 20;
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
        AtomicInteger ticksLeft = new AtomicInteger(durationTicks);

        ITimeRun timeRun = RecastingAttachments.timeRun(livingEntity);
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
            AtomicInteger ticksLeft,
            ITimeRun timeRun
    ) {
        if (!caster.isAlive() || caster.isRemoved() || caster.level().isClientSide() || ticksLeft.get() <= 0) {
            endDomain(caster, timeRun, absolutePins, pinnedTargets);
            return;
        }

        ticksLeft.decrementAndGet();
        int displayLevel = Math.max(1, ticksLeft.get());
        mountSelfBuff(caster, displayLevel);

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
            mountTargetBuff(entity, caster, displayLevel);
        }

        Iterator<Map.Entry<UUID, LivingEntity>> iterator = pinnedTargets.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, LivingEntity> entry = iterator.next();
            if (present.contains(entry.getKey())) {
                continue;
            }
            clearTargetBuff(entry.getValue());
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
            LivingEntity caster,
            ITimeRun timeRun,
            Map<UUID, Vec3> absolutePins,
            Map<UUID, LivingEntity> pinnedTargets
    ) {
        timeRun.removeNamedTimerCell(TIMER_NAME);
        timeRun.removeNamedTimerCell(VISUAL_TIMER_NAME);
        clearSelfBuff(caster);
        for(LivingEntity target : pinnedTargets.values()) {
            clearTargetBuff(target);
        }
        absolutePins.clear();
        pinnedTargets.clear();
    }

    private void mountSelfBuff(LivingEntity caster, int displayLevel) {
        IBuffStackData data = RecastingAttachments.buffStackData(caster);
        data.setLevel(RecastingBuffTypes.ETERNAL_GUARD_SELF.get(), displayLevel, caster.level());
        BuffSourceHelper.recordSourceEntity(data, RecastingBuffTypes.ETERNAL_GUARD_SELF.get(), caster, caster);
    }

    private void clearSelfBuff(LivingEntity caster) {
        if (caster == null || caster.isRemoved()) {
            return;
        }
        RecastingAttachments.buffStackData(caster)
                .setLevel(RecastingBuffTypes.ETERNAL_GUARD_SELF.get(), 0, caster.level());
    }

    private void mountTargetBuff(LivingEntity target, LivingEntity caster, int displayLevel) {
        IBuffStackData data = RecastingAttachments.buffStackData(target);
        data.setLevel(RecastingBuffTypes.ETERNAL_GUARD.get(), displayLevel, target.level());
        BuffSourceHelper.recordSourceEntity(data, RecastingBuffTypes.ETERNAL_GUARD.get(), target, caster);
    }

    private void clearTargetBuff(LivingEntity target) {
        if (target == null || target.isRemoved()) {
            return;
        }
        RecastingAttachments.buffStackData(target)
                .setLevel(RecastingBuffTypes.ETERNAL_GUARD.get(), 0, target.level());
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
