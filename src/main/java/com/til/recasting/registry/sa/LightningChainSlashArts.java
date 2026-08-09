package com.til.recasting.registry.sa;

import com.til.recasting.capability.ITimeRun;
import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.capability.RenderDefinitionExtension;
import com.til.recasting.handler.*;
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

/**
 * 闪电链 Slash Arts
 * 对齐光棱发射模式：TIME_RUN 脉冲连射；每发优先命中锁定/视线实体（否则落在看向点），再横跳传导。
 */
@Setter
@Accessors(chain = true)
public class LightningChainSlashArts extends ExtendedSlashArts {

    /**
     * 脉冲次数（对齐光棱 beamCount）
     */
    int chainCount = 1;
    /**
     * 脉冲间隔 tick（对齐光棱 delay）
     */
    int delay = 5;
    /**
     * 总跳数（含首击）
     */
    int maxHops = 6;
    /**
     * 每跳间隔 tick
     */
    int hopDelay = 2;
    /**
     * 横跳索敌半径
     */
    float hopRange = 8f;
    /**
     * 首击伤害倍率
     */
    float firstAttack = 0.55f;
    /**
     * 横跳基础倍率（每跳 ×0.9 衰减）
     */
    float chainAttack = 0.4f;
    /**
     * 无锁定实体时，看向点附近搜首目标半径
     */
    float seedRadius = 2.5f;
    /**
     * 范围内未命中目标耗尽后清空名单，允许新一轮跳跃（仍排除上一跳目标）
     */
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
            for(int i = 0; i < chainCount; i++) {
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
        Vec3 from = PosHelper.getAboveHead(livingEntity, 0.5);
        final Vec3 tip;
        @Nullable LivingEntity lastHit = null;

        if (seed != null && seed.isAlive()) {
            tip = seed.getBoundingBox().getCenter();
            LightningChainEffectHelper.sync(serverLevel, from, tip, color);
            AttackHelper.attack(livingEntity, seed, new DamageStructure(firstAttack, 0), attackTypes);
            hit.add(seed);
            lastHit = seed;
            LightningChainHelper.playThunderSound(serverLevel, tip, pulseIndex);
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
            LightningChainHelper.playThunderSound(serverLevel, tip, pulseIndex);
        }

        LightningChainHelper.startHopSequence(
                livingEntity, tip, lastHit, serverLevel, color,
                maxHops - 1, hopDelay, hopRange, chainAttack, attackTypes, allowRepeatJump
        );
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
}
