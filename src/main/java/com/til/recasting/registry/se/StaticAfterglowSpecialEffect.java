package com.til.recasting.registry.se;

import com.til.recasting.event.AttackAmplifierEvent;
import com.til.recasting.handler.AttackHelper;
import com.til.recasting.handler.CapabilityRegistryHandler;
import com.til.recasting.handler.EntityPredicateHelper;
import com.til.recasting.handler.LightningChainEffectHelper;
import com.til.recasting.registry.RecastingAttackTypes;
import com.til.recasting.registry.RecastingBuffTypes;
import com.til.recasting.registry.instance.AttackType;
import com.til.recasting.registry.instance.BuffType;
import com.til.recasting.util.DamageStructure;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import lombok.Setter;
import lombok.experimental.Accessors;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.event.SlashBladeEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 静电余韵
 * 释放 SA 后的一段时间内，造成伤害时附带雷电附加伤害（带内置冷却），
 * 造成雷电伤害时有概率附带闪电链 L1（每个起始目标有独立冷却）。
 * <p>
 * 持续与冷却均通过 Buff 层数（剩余 tick）+ decayInterval=1 自动衰减计时。
 */
@Setter
@Accessors(chain = true)
public class StaticAfterglowSpecialEffect extends ExtendedSpecialEffect {

    /** SA 触发后的增益持续时间（tick），默认 10s / lambda 15s */
    private int durationTick = 200;

    /** 雷电附加伤害倍率，默认 0.3 / lambda 0.4 */
    private float lightningDamageRatio = 0.3f;

    /** 附加雷电伤害的内置冷却（tick），默认 0.2s / lambda 0.15s */
    private int damageCooldownTick = 4;

    /** 触发闪电链的概率，默认 10% / lambda 15% */
    private float chainChance = 0.10f;

    /** 闪电链起始目标的内置冷却（tick），默认 0.7s / lambda 0.5s */
    private int chainCooldownTick = 14;

    @SubscribeEvent
    public void onSATrigger(SlashBladeEvent.ChargeActionEvent event) {
        if (!hasSpecialEffect(event.getSlashBladeState())) {
            return;
        }

        LivingEntity user = event.getEntityLiving();
        if (user.level().isClientSide()) {
            return;
        }

        if (event.getType() == mods.flammpfeil.slashblade.slasharts.SlashArts.ArtsType.Fail) {
            return;
        }

        BuffType afterglowBuff = RecastingBuffTypes.STATIC_AFTERGLOW.get();
        Level level = user.level();
        user.getCapability(CapabilityRegistryHandler.BUFF_STACK_DATA).ifPresent(
                buffStackData -> buffStackData.setLevel(afterglowBuff, durationTick, level)
        );
    }

    @SubscribeEvent
    public void onAttackAmplifier(AttackAmplifierEvent event) {
        if (!hasSpecialEffect(event.getSlashBladeState())) {
            return;
        }

        LivingEntity attacker = event.getAttacker();
        Level level = attacker.level();
        if (level.isClientSide()) {
            return;
        }

        if (event.getAttackTypeList().contains(RecastingAttackTypes.NO_RECURSION_ATTACK.get())) {
            return;
        }

        if (!(event.getTarget() instanceof LivingEntity target) || !target.isAlive()) {
            return;
        }

        BuffType afterglowBuff = RecastingBuffTypes.STATIC_AFTERGLOW.get();
        AtomicBoolean hasAfterglow = new AtomicBoolean(false);
        attacker.getCapability(CapabilityRegistryHandler.BUFF_STACK_DATA).ifPresent(buffStackData -> {
            if (buffStackData.getLevel(afterglowBuff, level) > 0) {
                hasAfterglow.set(true);
            }
        });

        if (!hasAfterglow.get()) {
            return;
        }

        ISlashBladeState state = event.getSlashBladeState();
        int color = state.getColorCode();

        // 附加雷电伤害（层数 > 0 表示冷却中）
        BuffType damageCdBuff = RecastingBuffTypes.STATIC_AFTERGLOW_DAMAGE_CD.get();
        attacker.getCapability(CapabilityRegistryHandler.BUFF_STACK_DATA).ifPresent(attackerBuff -> {
            if (attackerBuff.getLevel(damageCdBuff, level) > 0) {
                return;
            }

            AttackType lightningAttackType = RecastingAttackTypes.LIGHTNING_ATTACK.get();
            AttackAmplifierEvent.DamageSourceInfo damageSourceInfo = lightningAttackType.createDamageSource(attacker, target);
            if (damageSourceInfo == null) {
                return;
            }

            attackerBuff.setLevel(damageCdBuff, damageCooldownTick, level);
            event.addDamageSourceInfo(
                    damageSourceInfo.damageSource(),
                    new DamageStructure(lightningDamageRatio, 0f)
            );

            if (level instanceof ServerLevel serverLevel) {
                Vec3 targetPos = target.getBoundingBox().getCenter();
                LightningChainEffectHelper.spawnHitParticles(serverLevel, targetPos, color);
            }
        });

        // 造成雷电伤害时概率触发闪电链 L1
        if (!event.getAttackTypeList().contains(RecastingAttackTypes.LIGHTNING_ATTACK.get())) {
            return;
        }
        if (level.getRandom().nextFloat() >= chainChance) {
            return;
        }

        BuffType chainCdBuff = RecastingBuffTypes.STATIC_AFTERGLOW_CHAIN_CD.get();
        target.getCapability(CapabilityRegistryHandler.BUFF_STACK_DATA).ifPresent(targetBuff -> {
            if (targetBuff.getLevel(chainCdBuff, level) > 0) {
                return;
            }
            targetBuff.setLevel(chainCdBuff, chainCooldownTick, level);

            if (level instanceof ServerLevel serverLevel) {
                triggerMiniLightningChain(attacker, target, serverLevel, color);
            }
        });
    }

    /**
     * 触发闪电链 L1：从起始目标横跳传导（6 跳，8 格范围）
     */
    private void triggerMiniLightningChain(LivingEntity attacker, LivingEntity startTarget, ServerLevel serverLevel, int color) {
        List<AttackType> attackTypes = List.of(
                RecastingAttackTypes.LIGHTNING_ATTACK.get(),
                RecastingAttackTypes.NO_RECURSION_ATTACK.get()
        );
        int maxHops = 6;
        float hopRange = 8f;
        float chainAttack = 0.4f;

        Vec3 from = startTarget.getBoundingBox().getCenter();

        attacker.getCapability(CapabilityRegistryHandler.TIME_RUN).ifPresent(timeRun -> {
            AtomicReference<Vec3> tipRef = new AtomicReference<>(from);
            AtomicReference<LivingEntity> lastRef = new AtomicReference<>(startTarget);
            Set<LivingEntity> hit = new HashSet<>();
            hit.add(startTarget);

            float decayedRatio = chainAttack;
            for (int hop = 0; hop < maxHops; hop++) {
                int hopTick = 2 * (hop + 1);
                float ratio = decayedRatio;
                timeRun.addTimerCell(() -> {
                    if (!attacker.isAlive() || attacker.level().isClientSide()) {
                        return;
                    }
                    LivingEntity next = findNearest(attacker, tipRef.get(), hopRange, hit, lastRef.get());
                    if (next == null) {
                        return;
                    }
                    Vec3 nextPos = next.getBoundingBox().getCenter();
                    LightningChainEffectHelper.sync(serverLevel, tipRef.get(), nextPos, color);
                    AttackHelper.attack(attacker, next, new DamageStructure(ratio, 0), attackTypes);
                    hit.add(next);
                    lastRef.set(next);
                    tipRef.set(nextPos);
                }, hopTick);
                decayedRatio *= 0.9f;
            }
        });
    }

    @Nullable
    private static LivingEntity findNearest(
            LivingEntity attacker,
            Vec3 origin,
            float range,
            Set<LivingEntity> hit,
            LivingEntity lastHit
    ) {
        AABB box = AABB.ofSize(origin, range * 2.0, range * 2.0, range * 2.0);
        return attacker.level().getEntitiesOfClass(
                        LivingEntity.class,
                        box,
                        entity -> EntityPredicateHelper.canTarget(attacker, entity)
                                && entity.isAlive()
                                && entity != lastHit
                                && !hit.contains(entity)
                )
                .stream()
                .min(Comparator.comparingDouble(e -> e.getBoundingBox().getCenter().distanceToSqr(origin)))
                .orElse(null);
    }
}
