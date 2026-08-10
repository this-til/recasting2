package com.til.recasting.registry.sa;

import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.capability.RenderDefinitionExtension;
import com.til.recasting.entity.SummondSwordEntity;
import com.til.recasting.entity.TrackingSummondSwordEntity;
import com.til.recasting.handler.*;
import com.til.recasting.registry.RecastingAttackTypes;
import com.til.recasting.registry.RecastingBuffTypes;
import com.til.recasting.registry.RecastingEntities;
import com.til.recasting.registry.RecastingParticleTypes;
import com.til.recasting.registry.instance.AttackType;
import com.til.recasting.util.DamageStructure;
import lombok.Setter;
import lombok.experimental.Accessors;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * [回到未来计划]红尘滚滚：玩家周围随机落下追踪幻影剑，命中叠红尘并溅射，橙尘拖尾与命中喷泉。
 */
@Setter
@Accessors(chain = true)
public class MortalDustSlashArts extends ExtendedSlashArts {

    private int bladeCount = 16;
    private float spawnRangeXZ = 16.0f;
    private float spawnYMin = 2.0f;
    private float spawnYMax = 16.0f;
    private int startDelayMin = 5;
    private int startDelayMax = 25;
    private float bladeRatio = 0.42f;
    private float splashRange = 12.0f;
    private int bladeLife = 300;
    private int breakDelay = 10;
    private int stackAtCenter = 10;
    private int stackAtEdge = 1;

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
        List<AttackType> attackTypes = List.of(RecastingAttackTypes.SUMMOND_SWORD_ATTACK.get());
        RandomSource random = livingEntity.getRandom();
        double originX = livingEntity.getX();
        double originY = livingEntity.getY();
        double originZ = livingEntity.getZ();
        float ySpan = spawnYMax - spawnYMin;

        for(int i = 0; i < bladeCount; i++) {
            TrackingSummondSwordEntity blade = new TrackingSummondSwordEntity(
                    RecastingEntities.TRACKING_SUMMOND_SWORD.get(),
                    level,
                    livingEntity
            );
            double x = originX + (random.nextDouble() * 2.0 - 1.0) * spawnRangeXZ;
            double y = originY + spawnYMin + random.nextDouble() * ySpan;
            double z = originZ + (random.nextDouble() * 2.0 - 1.0) * spawnRangeXZ;
            blade.setPos(x, y, z);
            blade.setDeltaMovement(Vec3.ZERO);
            blade.setColor(color);
            blade.setModifiedRatio(bladeRatio);
            blade.setStartDelay(startDelayMin + random.nextInt(startDelayMax - startDelayMin + 1));
            blade.setInterval(0);
            blade.setMaxLifeTime(bladeLife);
            blade.setBreakDelay(breakDelay);
            blade.setIgnoringBlock(true);
            blade.setSize(0f);
            blade.setMute(true);
            blade.lookAt(PosHelper.getAttackTargetPosition(livingEntity, slashBladeState), false);

            blade.tickCallbackPoint.register(() -> {
                if (!(blade.level() instanceof ServerLevel serverLevel)) {
                    return;
                }
                SummondSwordEntity.ActionType action = blade.getActionType();
                if (action != SummondSwordEntity.ActionType.PREPARE && action != SummondSwordEntity.ActionType.FLYING) {
                    return;
                }
                spawnTrail(serverLevel, blade.position());
            });

            blade.attackActionCallbackPoint.register(hit -> {
                if (!(hit instanceof LivingEntity livingHit)) {
                    return;
                }
                LivingEntity shooter = blade.getShooter();
                if (shooter == null || !shooter.isAlive()) {
                    return;
                }

                Vec3 splashCenter = livingHit.getBoundingBox().getCenter();
                applyMortalDustByDistance(shooter, splashCenter);

                AttackHelper.areaAttack(
                        shooter,
                        splashCenter,
                        new DamageStructure(bladeRatio, 0.0f),
                        splashRange,
                        attackTypes,
                        List.of(livingHit),
                        null
                );

                if (blade.level() instanceof ServerLevel serverLevel) {
                    spawnHitBurst(serverLevel, blade.position());
                }
            });

            level.addFreshEntity(blade);
        }

        level.playSound(
                null,
                livingEntity.getX(),
                livingEntity.getY(),
                livingEntity.getZ(),
                SoundEvents.BLAZE_SHOOT,
                SoundSource.PLAYERS,
                0.55F,
                0.65F + livingEntity.getRandom().nextFloat() * 0.2F
        );
    }

    private static void spawnTrail(ServerLevel serverLevel, Vec3 pos) {
        for(int i = 0; i < 2; i++) {
            ParticleHelper.sendParticlesLongRange(
                    serverLevel,
                    RecastingParticleTypes.MORTAL_DUST_TRAIL.get(),
                    pos.x,
                    pos.y,
                    pos.z,
                    1,
                    0.0,
                    0.0,
                    0.0,
                    0.0
            );
        }
    }

    private static void spawnHitBurst(ServerLevel serverLevel, Vec3 pos) {
        ParticleHelper.sendParticlesLongRange(
                serverLevel,
                RecastingParticleTypes.MORTAL_DUST_HIT.get(),
                pos.x,
                pos.y,
                pos.z,
                45,
                0.0,
                0.0,
                0.0,
                0.0
        );
    }

    private void applyMortalDustByDistance(LivingEntity shooter, Vec3 splashCenter) {
        List<LivingEntity> targets = EntityHelper.getTargettableLivingEntityWithinAABB(
                shooter.level(),
                shooter,
                splashCenter,
                splashRange
        );
        for(LivingEntity target : targets) {
            int stacks = stacksByDistance(splashCenter, target);
            if (stacks <= 0) {
                continue;
            }
            addMortalDustStacks(shooter, target, stacks);
        }
    }

    private int stacksByDistance(Vec3 splashCenter, LivingEntity target) {
        double dist = target.getBoundingBox().getCenter().distanceTo(splashCenter);
        float t = MathHelper.clamp((float) (dist / splashRange), 0.0f, 1.0f);
        return Math.max(stackAtEdge, Math.round(MathHelper.lerp(t, (float) stackAtCenter, (float) stackAtEdge)));
    }

    private void addMortalDustStacks(LivingEntity shooter, LivingEntity target, int addStacks) {
        target.getCapability(CapabilityRegistryHandler.BUFF_STACK_DATA).ifPresent(data -> {
            int current = data.getLevel(RecastingBuffTypes.MORTAL_DUST.get(), target.level());
            data.setLevel(RecastingBuffTypes.MORTAL_DUST.get(), current + addStacks, target.level());
            BuffSourceHelper.recordSourceEntity(data, RecastingBuffTypes.MORTAL_DUST.get(), target, shooter);
        });
    }
}
