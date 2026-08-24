package com.til.recasting.handler;

import com.til.recasting.registry.RecastingAttachments;
import com.til.recasting.registry.instance.AttackType;
import com.til.recasting.util.DamageStructure;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

/**
 * 闪电链传导共用逻辑：索敌、横跳序列、音效。
 */
public final class LightningChainHelper {

    private LightningChainHelper() {
    }

    /**
     * 从给定起点启动横跳传导序列（不含首击伤害，仅传导后续跳）。
     */
    public static void startHopSequence(
            LivingEntity attacker,
            Vec3 startPos,
            @Nullable LivingEntity startTarget,
            ServerLevel serverLevel,
            int color,
            int maxHops,
            int hopDelay,
            float hopRange,
            float baseRatio,
            List<AttackType> attackTypes,
            boolean allowRepeat
    ) {
        Set<LivingEntity> hit = new HashSet<>();
        if (startTarget != null) {
            hit.add(startTarget);
        }

        var timeRun = RecastingAttachments.timeRun(attacker);
        AtomicReference<Vec3> tipRef = new AtomicReference<>(startPos);
        AtomicReference<LivingEntity> lastRef = new AtomicReference<>(startTarget);
        float decayed = baseRatio;
        for(int hop = 0; hop < maxHops; hop++) {
            int hopTick = hopDelay * (hop + 1);
            float ratio = decayed;
            timeRun.addTimerCell(() -> {
                if (!attacker.isAlive() || attacker.level().isClientSide()) {
                    return;
                }
                LivingEntity next = findNext(attacker, tipRef.get(), hopRange, hit, lastRef.get(), allowRepeat);
                if (next == null) {
                    return;
                }
                Vec3 nextPos = next.getBoundingBox().getCenter();
                LightningChainEffectHelper.sync(serverLevel, tipRef.get(), nextPos, color);
                AttackHelper.attack(attacker, next, new DamageStructure(ratio, 0), attackTypes);
                hit.add(next);
                lastRef.set(next);
                tipRef.set(nextPos);
                playImpactSound(serverLevel, nextPos);
            }, hopTick);
            decayed *= 0.9f;
        }
    }

    /**
     * 从攻击者到 seed 发射首道闪电链特效 + 首击伤害，然后启动横跳传导。
     */
    public static void fireChainFromSeed(
            LivingEntity attacker,
            LivingEntity seed,
            ServerLevel serverLevel,
            int color,
            int maxHops,
            int hopDelay,
            float hopRange,
            float firstRatio,
            float chainRatio,
            List<AttackType> attackTypes,
            boolean allowRepeat
    ) {
        Vec3 from = PosHelper.getAboveHead(attacker, 0.5);
        Vec3 seedPos = seed.getBoundingBox().getCenter();
        LightningChainEffectHelper.sync(serverLevel, from, seedPos, color);
        AttackHelper.attack(attacker, seed, new DamageStructure(firstRatio, 0), attackTypes);
        playThunderSound(serverLevel, seedPos, 0);

        startHopSequence(attacker, seedPos, seed, serverLevel, color,
                maxHops, hopDelay, hopRange, chainRatio, attackTypes, allowRepeat);
    }

    @Nullable
    public static LivingEntity findNext(
            LivingEntity attacker,
            Vec3 origin,
            float range,
            Set<LivingEntity> hit,
            @Nullable LivingEntity lastHit,
            boolean allowRepeat
    ) {
        AABB box = AABB.ofSize(origin, range * 2.0, range * 2.0, range * 2.0);
        LivingEntity unhit = nearestInBox(attacker, origin, box,
                entity -> entity != lastHit && !hit.contains(entity));
        if (unhit != null) {
            return unhit;
        }
        if (!allowRepeat) {
            return null;
        }
        hit.clear();
        return nearestInBox(attacker, origin, box, entity -> entity != lastHit);
    }

    @Nullable
    public static LivingEntity nearestInBox(
            LivingEntity attacker,
            Vec3 origin,
            AABB box,
            Predicate<LivingEntity> extraFilter
    ) {
        return attacker.level().getEntitiesOfClass(
                        LivingEntity.class,
                        box,
                        entity -> EntityPredicateHelper.canTarget(attacker, entity)
                                && entity.isAlive()
                                && extraFilter.test(entity)
                )
                .stream()
                .min(Comparator.comparingDouble(e -> e.getBoundingBox().getCenter().distanceToSqr(origin)))
                .orElse(null);
    }

    public static void playThunderSound(ServerLevel level, Vec3 pos, int pulseIndex) {
        float pitch = 1.35F + pulseIndex * 0.08F + level.random.nextFloat() * 0.1F;
        level.playSound(null, pos.x, pos.y, pos.z,
                SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.PLAYERS, 0.35F, pitch);
        playImpactSound(level, pos);
    }

    public static void playImpactSound(ServerLevel level, Vec3 pos) {
        level.playSound(null, pos.x, pos.y, pos.z,
                SoundEvents.LIGHTNING_BOLT_IMPACT, SoundSource.PLAYERS,
                0.8F, 0.9F + level.random.nextFloat() * 0.3F);
    }
}
