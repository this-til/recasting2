package com.til.recasting.registry.sa;

import com.til.recasting.capability.ITimeRun;
import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.capability.RenderDefinitionExtension;
import com.til.recasting.handler.AttackHelper;
import com.til.recasting.handler.CapabilityRegistryHandler;
import com.til.recasting.handler.EntityPredicateHelper;
import com.til.recasting.handler.LightningChainEffectHelper;
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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

/**
 * 闪电链 Slash Arts
 * 对齐光棱发射模式：TIME_RUN 脉冲连射；每发优先命中锁定/视线实体（否则落在看向点），再横跳传导。
 */
@Setter
@Accessors(chain = true)
public class LightningChainSlashArts extends ExtendedSlashArts {

    /** 脉冲次数（对齐光棱 beamCount） */
    int chainCount = 1;
    /** 脉冲间隔 tick（对齐光棱 delay） */
    int delay = 5;
    /** 总跳数（含首击） */
    int maxHops = 6;
    /** 每跳间隔 tick */
    int hopDelay = 2;
    /** 横跳索敌半径 */
    float hopRange = 8f;
    /** 首击伤害倍率 */
    float firstAttack = 0.55f;
    /** 横跳基础倍率（每跳 ×0.9 衰减） */
    float chainAttack = 0.4f;
    /** 无锁定实体时，看向点附近搜首目标半径 */
    float seedRadius = 2.5f;
    /** 范围内未命中目标耗尽后清空名单，允许新一轮跳跃（仍排除上一跳目标） */
    boolean allowRepeatJump = false;

    @Override
    public void trigger(
            LivingEntity livingEntity,
            ItemStack itemStack,
            ISlashBladeState slashBladeState,
            RenderDefinitionExtension renderDefinitionExtension,
            PropertiesDefinitionExtension propertiesDefinitionExtension
    ) {
        if (livingEntity.level().isClientSide()) {
            return;
        }
        if (!(livingEntity.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        int color = slashBladeState.getColorCode();
        List<AttackType> attackTypes = List.of(RecastingAttackTypes.LIGHTNING_ATTACK.get());

        LazyOptional<ITimeRun> timeRunOptional = livingEntity.getCapability(CapabilityRegistryHandler.TIME_RUN);
        timeRunOptional.ifPresent(timeRun -> {
            for (int i = 0; i < chainCount; i++) {
                int pulseIndex = i;
                timeRun.addTimerCell(
                        () -> fireChainPulse(livingEntity, slashBladeState, serverLevel, color, attackTypes, pulseIndex),
                        delay * i
                );
            }
        });

        livingEntity.level().playSound(
                null,
                livingEntity.getX(),
                livingEntity.getY(),
                livingEntity.getZ(),
                SoundEvents.LIGHTNING_BOLT_THUNDER,
                SoundSource.PLAYERS,
                0.25F,
                1.6F
        );
    }

    /**
     * 单次脉冲：发射时刻重新解析看向点/首目标，再启动横跳序列。
     */
    private void fireChainPulse(
            LivingEntity livingEntity,
            ISlashBladeState slashBladeState,
            ServerLevel serverLevel,
            int color,
            List<AttackType> attackTypes,
            int pulseIndex
    ) {
        if (!livingEntity.isAlive() || livingEntity.level().isClientSide()) {
            return;
        }

        Vec3 lookPos = PosHelper.getAttackTargetPosition(livingEntity, slashBladeState);
        LivingEntity seed = resolveSeed(livingEntity, slashBladeState, lookPos, seedRadius);

        Set<LivingEntity> hit = new HashSet<>();
        Vec3 from = livingEntity.getEyePosition(1.0f);
        final Vec3 tip;
        @Nullable LivingEntity lastHit = null;

        if (seed != null && seed.isAlive()) {
            tip = seed.getBoundingBox().getCenter();
            LightningChainEffectHelper.sync(serverLevel, from, tip, color);
            AttackHelper.attack(livingEntity, seed, new DamageStructure(firstAttack, 0), attackTypes);
            hit.add(seed);
            lastHit = seed;
            playThunderSound(serverLevel, tip, pulseIndex);
        } else {
            Vec3 landPos = lookPos;
            LightningChainEffectHelper.sync(serverLevel, from, landPos, color);
            List<LivingEntity> firstHits = AttackHelper.areaAttack(
                    livingEntity,
                    landPos,
                    new DamageStructure(firstAttack, 0),
                    seedRadius,
                    attackTypes,
                    null,
                    null
            );
            hit.addAll(firstHits);
            if (!firstHits.isEmpty()) {
                lastHit = firstHits.get(0);
                tip = lastHit.getBoundingBox().getCenter();
            } else {
                tip = landPos;
            }
            playThunderSound(serverLevel, tip, pulseIndex);
        }

        LazyOptional<ITimeRun> timeRunOptional = livingEntity.getCapability(CapabilityRegistryHandler.TIME_RUN);
        LivingEntity initialLast = lastHit;
        timeRunOptional.ifPresent(timeRun -> {
            Vec3[] tipRef = {tip};
            LivingEntity[] lastRef = {initialLast};
            float decayedRatio = chainAttack;
            for (int hop = 1; hop < maxHops; hop++) {
                int hopTick = hopDelay * hop;
                float ratio = decayedRatio;
                timeRun.addTimerCell(
                        () -> hopStrike(livingEntity, serverLevel, tipRef, lastRef, hit, ratio, color, attackTypes),
                        hopTick
                );
                decayedRatio *= 0.9f;
            }
        });
    }

    private void hopStrike(
            LivingEntity livingEntity,
            ServerLevel serverLevel,
            Vec3[] tipRef,
            LivingEntity[] lastRef,
            Set<LivingEntity> hit,
            float ratio,
            int color,
            List<AttackType> attackTypes
    ) {
        if (!livingEntity.isAlive() || livingEntity.level().isClientSide()) {
            return;
        }
        LivingEntity next = findNext(
                livingEntity,
                tipRef[0],
                hopRange,
                hit,
                lastRef[0],
                allowRepeatJump
        );
        if (next == null) {
            return;
        }
        Vec3 nextPos = next.getBoundingBox().getCenter();
        LightningChainEffectHelper.sync(serverLevel, tipRef[0], nextPos, color);
        AttackHelper.attack(livingEntity, next, new DamageStructure(ratio, 0), attackTypes);
        hit.add(next);
        lastRef[0] = next;
        tipRef[0] = nextPos;
        playImpactSound(serverLevel, nextPos);
    }

    @Nullable
    private static LivingEntity resolveSeed(
            LivingEntity user,
            ISlashBladeState state,
            Vec3 lookPos,
            float radius
    ) {
        Entity locked = state.getTargetEntity(user.level());
        if (locked instanceof LivingEntity living
                && living.isAlive()
                && EntityPredicateHelper.canTarget(user, living)) {
            return living;
        }

        AABB box = AABB.ofSize(lookPos, radius * 2.0, radius * 2.0, radius * 2.0);
        return user.level().getEntitiesOfClass(
                        LivingEntity.class,
                        box,
                        entity -> EntityPredicateHelper.canTarget(user, entity) && entity.isAlive()
                )
                .stream()
                .min(Comparator.comparingDouble(e -> e.distanceToSqr(lookPos)))
                .orElse(null);
    }

    @Nullable
    private static LivingEntity findNext(
            LivingEntity attacker,
            Vec3 origin,
            float range,
            Set<LivingEntity> hit,
            @Nullable LivingEntity lastHit,
            boolean allowRepeat
    ) {
        AABB box = AABB.ofSize(origin, range * 2.0, range * 2.0, range * 2.0);
        LivingEntity unhit = nearestInBox(
                attacker,
                origin,
                box,
                entity -> entity != lastHit && !hit.contains(entity)
        );
        if (unhit != null) {
            return unhit;
        }
        if (!allowRepeat) {
            return null;
        }
        // 范围内可攻击目标均已命中：清空名单后重新跳跃
        hit.clear();
        return nearestInBox(
                attacker,
                origin,
                box,
                entity -> entity != lastHit
        );
    }

    @Nullable
    private static LivingEntity nearestInBox(
            LivingEntity attacker,
            Vec3 origin,
            AABB box,
            java.util.function.            Predicate<LivingEntity> extraFilter
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

    private static void playThunderSound(ServerLevel level, Vec3 pos, int pulseIndex) {
        float pitch = 1.35F + pulseIndex * 0.08F + level.random.nextFloat() * 0.1F;
        level.playSound(
                null,
                pos.x,
                pos.y,
                pos.z,
                SoundEvents.LIGHTNING_BOLT_THUNDER,
                SoundSource.PLAYERS,
                0.35F,
                pitch
        );
        playImpactSound(level, pos);
    }

    private static void playImpactSound(ServerLevel level, Vec3 pos) {
        level.playSound(
                null,
                pos.x,
                pos.y,
                pos.z,
                SoundEvents.LIGHTNING_BOLT_IMPACT,
                SoundSource.PLAYERS,
                0.8F,
                0.9F + level.random.nextFloat() * 0.3F
        );
    }
}
