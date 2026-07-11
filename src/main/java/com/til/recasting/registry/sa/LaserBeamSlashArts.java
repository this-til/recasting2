package com.til.recasting.registry.sa;

import com.til.recasting.capability.ITimeRun;
import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.capability.RenderDefinitionExtension;
import com.til.recasting.handler.AttackHelper;
import com.til.recasting.handler.CapabilityRegistryHandler;
import com.til.recasting.handler.EntityPredicateHelper;
import com.til.recasting.handler.PosHelper;
import com.til.recasting.registry.RecastingAttackTypes;
import com.til.recasting.registry.instance.AttackType;
import com.til.recasting.util.DamageStructure;
import lombok.Setter;
import lombok.experimental.Accessors;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 光棱 Slash Arts（红警2 光棱坦克）
 * 头顶发射、自动索敌；射线做方块/实体物理碰撞；从碰撞点散射，仅碰撞到的实体受伤
 */
@Setter
@Accessors(chain = true)
public class LaserBeamSlashArts extends ExtendedSlashArts {

    private static final double FIRE_HEIGHT_OFFSET = 0.5;
    private static final double SCATTER_BOX_X = 15.0;
    private static final double SCATTER_BOX_Y = 5.0;
    private static final double SCATTER_BOX_Z = 15.0;
    private static final float PRISM_PARTICLE_SPACING = 0.25f;

    /** 脉冲次数 */
    int beamCount = 1;
    /** 脉冲间隔 tick */
    int delay = 3;
    float attack = 0.5f;
    /** 散射分光伤害相对主光束 */
    float scatterAttack = 0.15f;
    float range = 24f;
    /** 主碰撞后同时发出的散射条数 */
    int scatterCount = 5;
    /** 散射命中后再发出的二阶散射条数 */
    int secondaryScatterCount = 0;

    @Override
    public void trigger(LivingEntity livingEntity, ItemStack itemStack, ISlashBladeState slashBladeState, RenderDefinitionExtension renderDefinitionExtension, PropertiesDefinitionExtension propertiesDefinitionExtension) {
        if (livingEntity.level().isClientSide()) {
            return;
        }

        float distMul = propertiesDefinitionExtension.attackDistance();
        float finalRange = range * distMul;
        int color = slashBladeState.getColorCode();
        List<AttackType> attackTypes = List.of(RecastingAttackTypes.LASER_ATTACK.get());

        LazyOptional<ITimeRun> timeRunOptional = livingEntity.getCapability(CapabilityRegistryHandler.TIME_RUN);
        timeRunOptional.ifPresent(timeRun -> {
            for (int i = 0; i < beamCount; i++) {
                int pulseIndex = i;
                timeRun.addTimerCell(
                        () -> firePrismPulse(livingEntity, slashBladeState, finalRange, color, attackTypes, pulseIndex),
                        delay * i
                );
            }
        });

        livingEntity.level().playSound(
                null,
                livingEntity.getX(),
                livingEntity.getY(),
                livingEntity.getZ(),
                SoundEvents.BEACON_ACTIVATE,
                SoundSource.PLAYERS,
                0.5F,
                1.7F
        );
    }

    private void firePrismPulse(
            LivingEntity livingEntity,
            ISlashBladeState slashBladeState,
            float finalRange,
            int color,
            List<AttackType> attackTypes,
            int pulseIndex
    ) {
        if (!livingEntity.isAlive() || livingEntity.level().isClientSide()) {
            return;
        }
        if (!(livingEntity.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        Vec3 start = PosHelper.getAboveHead(livingEntity, FIRE_HEIGHT_OFFSET);
        LivingEntity aimed = resolvePrimaryTarget(livingEntity, slashBladeState, finalRange);
        Vec3 aimPoint = aimed != null
                ? aimed.getBoundingBox().getCenter()
                : start.add(livingEntity.getLookAngle().scale(finalRange));

        PosHelper.BeamHit mainHit = fireCollidingBeam(
                livingEntity,
                serverLevel,
                start,
                aimPoint,
                attack,
                attackTypes,
                color
        );

        Set<LivingEntity> exclude = new HashSet<>();
        if (mainHit.entity() != null) {
            exclude.add(mainHit.entity());
        }

        if (scatterCount > 0) {
            fireScatterFrom(
                    livingEntity,
                    serverLevel,
                    mainHit.hitPos(),
                    scatterCount,
                    scatterAttack,
                    secondaryScatterCount,
                    attackTypes,
                    color,
                    exclude
            );
        }

        float pitch = 1.55F + pulseIndex * 0.08F;
        livingEntity.level().playSound(
                null,
                mainHit.hitPos().x,
                mainHit.hitPos().y,
                mainHit.hitPos().z,
                SoundEvents.BEACON_POWER_SELECT,
                SoundSource.PLAYERS,
                0.35F,
                pitch
        );
    }

    private void fireScatterFrom(
            LivingEntity attacker,
            ServerLevel serverLevel,
            Vec3 origin,
            int count,
            float damageRatio,
            int nextScatterCount,
            List<AttackType> attackTypes,
            int color,
            Set<LivingEntity> exclude
    ) {
        List<LivingEntity> targets = findScatterTargets(attacker, origin, exclude, count);
        List<LivingEntity> scatterHits = new ArrayList<>();
        for (LivingEntity target : targets) {
            exclude.add(target);
            PosHelper.BeamHit hit = fireCollidingBeam(
                    attacker,
                    serverLevel,
                    origin,
                    target.getBoundingBox().getCenter(),
                    damageRatio,
                    attackTypes,
                    color
            );
            if (hit.entity() != null) {
                exclude.add(hit.entity());
                if (hit.entity().isAlive()) {
                    scatterHits.add(hit.entity());
                }
            }
        }

        int remaining = count - targets.size();
        RandomSource random = attacker.getRandom();
        for (int i = 0; i < remaining; i++) {
            Vec3 aim = randomPointInScatterBox(origin, random);
            // 空分光同样做物理碰撞，只播特效不结算伤害
            PosHelper.BeamHit hit = PosHelper.castLivingBeam(serverLevel, attacker, origin, aim);
            AttackHelper.spawnPrismAlongSegment(serverLevel, origin, hit.hitPos(), color, PRISM_PARTICLE_SPACING);
        }

        if (nextScatterCount <= 0) {
            return;
        }
        float secondaryDamage = damageRatio * 0.75f;
        for (LivingEntity hit : scatterHits) {
            fireScatterFrom(
                    attacker,
                    serverLevel,
                    hit.getBoundingBox().getCenter(),
                    nextScatterCount,
                    secondaryDamage,
                    0,
                    attackTypes,
                    color,
                    exclude
            );
        }
    }

    /**
     * 向瞄准点发射带物理碰撞的光棱；仅碰撞到的实体受伤
     */
    private static PosHelper.BeamHit fireCollidingBeam(
            LivingEntity attacker,
            ServerLevel serverLevel,
            Vec3 start,
            Vec3 aim,
            float damageRatio,
            List<AttackType> attackTypes,
            int color
    ) {
        PosHelper.BeamHit hit = PosHelper.castLivingBeam(serverLevel, attacker, start, aim);
        AttackHelper.spawnPrismAlongSegment(serverLevel, start, hit.hitPos(), color, PRISM_PARTICLE_SPACING);
        if (hit.entity() != null) {
            AttackHelper.attack(attacker, hit.entity(), new DamageStructure(damageRatio, 0), attackTypes);
        }
        return hit;
    }

    @Nullable
    private static LivingEntity resolvePrimaryTarget(
            LivingEntity attacker,
            ISlashBladeState slashBladeState,
            float maxDistance
    ) {
        Entity locked = slashBladeState.getTargetEntity(attacker.level());
        if (locked instanceof LivingEntity living && living.isAlive() && EntityPredicateHelper.canTarget(attacker, living)) {
            if (attacker.distanceTo(living) <= maxDistance) {
                return living;
            }
        }

        Vec3 start = PosHelper.getAboveHead(attacker, FIRE_HEIGHT_OFFSET);
        Vec3 lookEnd = start.add(attacker.getLookAngle().scale(maxDistance));
        AABB searchBox = new AABB(start, lookEnd).inflate(2.0);
        LivingEntity best = null;
        double bestScore = Double.MAX_VALUE;
        for (LivingEntity candidate : attacker.level().getEntitiesOfClass(
                LivingEntity.class,
                searchBox,
                entity -> EntityPredicateHelper.canTarget(attacker, entity)
        )) {
            Vec3 center = candidate.getBoundingBox().getCenter();
            double toSeg = PosHelper.distancePointToSegment(center, start, lookEnd);
            if (toSeg > 2.0) {
                continue;
            }
            double along = start.distanceToSqr(center);
            if (along > maxDistance * maxDistance) {
                continue;
            }
            if (along < bestScore) {
                bestScore = along;
                best = candidate;
            }
        }
        return best;
    }

    private static List<LivingEntity> findScatterTargets(
            LivingEntity attacker,
            Vec3 origin,
            Set<LivingEntity> exclude,
            int limit
    ) {
        AABB box = AABB.ofSize(origin, SCATTER_BOX_X, SCATTER_BOX_Y, SCATTER_BOX_Z);
        return attacker.level().getEntitiesOfClass(
                        LivingEntity.class,
                        box,
                        entity -> EntityPredicateHelper.canTarget(attacker, entity)
                                && entity.isAlive()
                                && !exclude.contains(entity)
                )
                .stream()
                .sorted(Comparator.comparingDouble(e -> e.getBoundingBox().getCenter().distanceToSqr(origin)))
                .limit(limit)
                .toList();
    }

    private static Vec3 randomPointInScatterBox(Vec3 origin, RandomSource random) {
        double x = (random.nextDouble() - 0.5) * SCATTER_BOX_X;
        double y = (random.nextDouble() - 0.5) * SCATTER_BOX_Y;
        double z = (random.nextDouble() - 0.5) * SCATTER_BOX_Z;
        return origin.add(x, y, z);
    }
}
