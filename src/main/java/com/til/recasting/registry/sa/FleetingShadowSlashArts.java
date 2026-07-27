package com.til.recasting.registry.sa;

import com.til.recasting.capability.ITimeRun;
import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.capability.RenderDefinitionExtension;
import com.til.recasting.handler.AttackHelper;
import com.til.recasting.handler.CapabilityRegistryHandler;
import com.til.recasting.handler.EntityHelper;
import com.til.recasting.handler.MathHelper;
import com.til.recasting.handler.ParticleHelper;
import com.til.recasting.util.DamageStructure;
import lombok.Setter;
import lombok.experimental.Accessors;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.util.KnockBacks;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.LazyOptional;

import java.util.List;

/**
 * 掠影 Slash Arts
 */
@Setter
@Accessors(chain = true)
public class FleetingShadowSlashArts extends ExtendedSlashArts {

    private int jumpCount = 18;
    private int jumpInterval = 3;
    private int slashTicksPerJump = 3;
    private int slashesPerTick = 1;
    private float searchRange = 32.0f;
    private float healAmount = 1.0f;
    private float slashHit = 0.18f;
    private float slashOffset = 3.0f;
    private final float targetSideDistance = 2.0f;
    private final float trackingMaxTurnSpeed = 15.0f;
    private final float trackingSmoothness = 0.3f;
    private final float trackingPredictionFactor = 0.5f;

    @Override
    public void trigger(
            LivingEntity livingEntity,
            ItemStack itemStack,
            ISlashBladeState slashBladeState,
            RenderDefinitionExtension renderDefinitionExtension,
            PropertiesDefinitionExtension propertiesDefinitionExtension
    ) {
        LazyOptional<ITimeRun> timeRunOptional = livingEntity.getCapability(CapabilityRegistryHandler.TIME_RUN);
        Vec3 origin = livingEntity.position();

        timeRunOptional.ifPresent(timeRun -> {
            for(int i = 0; i < jumpCount; i++) {
                timeRun.addTimerCell(
                        () -> jumpAndQueueSlashes(livingEntity, slashBladeState, propertiesDefinitionExtension, timeRun),
                        (i + 1) * jumpInterval
                );
            }

            timeRun.addTimerCell(
                    () -> returnToOrigin(livingEntity, origin),
                    jumpCount * jumpInterval + slashTicksPerJump + 1
            );
        });
    }

    private void jumpAndQueueSlashes(
            LivingEntity livingEntity,
            ISlashBladeState slashBladeState,
            PropertiesDefinitionExtension propertiesDefinitionExtension,
            ITimeRun timeRun
    ) {
        if (!livingEntity.isAlive() || livingEntity.level().isClientSide()) {
            return;
        }

        Level level = livingEntity.level();
        List<LivingEntity> targets = EntityHelper.getTargettableLivingEntityWithinAABB(
                level,
                livingEntity,
                livingEntity.position(),
                searchRange
        );

        if (targets.isEmpty()) {
            return;
        }

        LivingEntity target = targets.get(livingEntity.getRandom().nextInt(targets.size()));
        Vec3 destination = calculateTrackingSidePosition(livingEntity, target);

        livingEntity.teleportTo(destination.x, destination.y, destination.z);
        lookAtTarget(livingEntity, target);
        livingEntity.setDeltaMovement(Vec3.ZERO);
        livingEntity.fallDistance = 0.0f;
        livingEntity.heal(healAmount);

        playTeleportEffect(level, destination);

        for(int tick = 0; tick < slashTicksPerJump; tick++) {
            for(int slash = 0; slash < slashesPerTick; slash++) {
                timeRun.addTimerCell(
                        () -> slashOnce(livingEntity, slashBladeState, propertiesDefinitionExtension),
                        tick + 1
                );
            }
        }
    }

    private void returnToOrigin(LivingEntity livingEntity, Vec3 origin) {
        if (!livingEntity.isAlive() || livingEntity.level().isClientSide()) {
            return;
        }

        livingEntity.teleportTo(origin.x, origin.y, origin.z);
        livingEntity.setDeltaMovement(Vec3.ZERO);
        livingEntity.fallDistance = 0.0f;
        playTeleportEffect(livingEntity.level(), origin);
    }

    private void playTeleportEffect(Level level, Vec3 pos) {
        level.playSound(
                null,
                pos.x,
                pos.y,
                pos.z,
                SoundEvents.ENDERMAN_TELEPORT,
                SoundSource.PLAYERS,
                0.7F,
                1.0F
        );

        if (level instanceof ServerLevel serverLevel) {
            ParticleHelper.sendParticlesLongRange(
                    serverLevel,
                    ParticleTypes.PORTAL,
                    pos.x,
                    pos.y + 1.0,
                    pos.z,
                    48,
                    0.35,
                    0.75,
                    0.35,
                    0.08
            );
        }
    }

    private void slashOnce(
            LivingEntity livingEntity,
            ISlashBladeState slashBladeState,
            PropertiesDefinitionExtension propertiesDefinitionExtension
    ) {
        if (!livingEntity.isAlive() || livingEntity.level().isClientSide()) {
            return;
        }

        AttackHelper.doSlash(
                livingEntity,
                livingEntity.getRandom().nextFloat() * 360.0f,
                slashBladeState.getColorCode(),
                createRandomSlashOffset(livingEntity),
                false,
                true,
                new DamageStructure(slashHit, 0),
                propertiesDefinitionExtension.attackDistance(),
                KnockBacks.cancel
        );
    }

    private Vec3 calculateTrackingSidePosition(LivingEntity livingEntity, Entity target) {
        Vec3 targetPos = MathHelper.predictEntityCenterPosition(livingEntity, target, trackingPredictionFactor);
        Vec3 desiredDirection = targetPos.subtract(livingEntity.position());

        if (desiredDirection.lengthSqr() < 0.001) {
            desiredDirection = livingEntity.getLookAngle();
        } else {
            desiredDirection = desiredDirection.normalize();
        }

        Vec3 currentVelocity = livingEntity.getDeltaMovement();
        Vec3 currentDirection = currentVelocity.lengthSqr() > 0.001
                ? currentVelocity.normalize()
                : livingEntity.getLookAngle().normalize();

        Vec3 approachDirection = MathHelper.smoothDirection(
                currentDirection,
                desiredDirection,
                trackingMaxTurnSpeed,
                trackingSmoothness
        );

        return targetPos.subtract(approachDirection.scale(targetSideDistance));
    }

    private void lookAtTarget(LivingEntity livingEntity, LivingEntity target) {
        Vec3 lookPos = target.position().add(0, target.getEyeHeight() * 0.5, 0);
        Vec3 direction = lookPos.subtract(livingEntity.getEyePosition());

        if (direction.lengthSqr() < 0.001) {
            return;
        }

        double horizontalDistance = Math.sqrt(direction.x * direction.x + direction.z * direction.z);
        float yaw = (float) (Math.atan2(direction.z, direction.x) * 180.0 / Math.PI) - 90.0f;
        float pitch = (float) -(Math.atan2(direction.y, horizontalDistance) * 180.0 / Math.PI);

        livingEntity.setYRot(yaw);
        livingEntity.setYHeadRot(yaw);
        livingEntity.setYBodyRot(yaw);
        livingEntity.setXRot(pitch);
    }

    private Vec3 createRandomSlashOffset(LivingEntity livingEntity) {
        return new Vec3(
                livingEntity.getRandom().nextFloat() - 0.5f,
                livingEntity.getRandom().nextFloat() - 0.5f,
                0
        ).scale(slashOffset);
    }
}
