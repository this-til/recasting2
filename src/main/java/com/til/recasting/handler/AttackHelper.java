package com.til.recasting.handler;

import com.til.recasting.entity.SlashEffectEntity;
import com.til.recasting.event.AttackAmplifierEvent;
import com.til.recasting.event.DoSlashExtendEvent;
import com.til.recasting.registry.RecastingAttackTypes;
import com.til.recasting.registry.RecastingEntities;
import com.til.recasting.registry.instance.AttackType;
import com.til.recasting.util.DamageStructure;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.util.KnockBacks;
import mods.flammpfeil.slashblade.util.VectorHelper;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 攻击助手类
 * 核心攻击流程：创建事件 → 发布事件（由监听器处理加成） → 计算伤害 → 应用效果
 */

public class AttackHelper {

    public static void attack(LivingEntity attacker, Entity target, DamageStructure damageStructure, List<AttackType> attackTypeList) {
        // 判断攻击目标是否可以被攻击
        if (!target.isAttackable()) {
            return;
        }

        ItemStack mainHandItem = attacker.getMainHandItem();

        mainHandItem.getCapability(ItemSlashBlade.BLADESTATE).ifPresent(s -> {
            // 创建攻击放大事件
            AttackAmplifierEvent attackAmplifierEvent = new AttackAmplifierEvent(
                    mainHandItem, s, attacker, target, damageStructure.modifiedRatio(), damageStructure.extraDamage(), attackTypeList,
                    attackTypeList.stream()
                            .map(a -> a.createDamageSource(attacker, target))
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList())
            );

            // 计算暴击状态（用于后续音效和粒子效果）
            boolean isCritical = mods.flammpfeil.slashblade.util.AttackHelper.isCriticalHit(attacker, target);

            // 发布事件，允许其他模组和监听器修改伤害
            MinecraftForge.EVENT_BUS.post(attackAmplifierEvent);

            // 获取其他攻击相关数据
            float knockback = mods.flammpfeil.slashblade.util.AttackHelper.calculateKnockback(attacker);
            mods.flammpfeil.slashblade.util.AttackHelper.FireAspectResult fireAspectResult =
                    mods.flammpfeil.slashblade.util.AttackHelper.handleFireAspect(attacker, target);
            Vec3 originalMotion = target.getDeltaMovement();

            // 计算最终伤害倍率（事件监听器已经添加了各种加成）
            double ultimatelyModifiedRatio = attackAmplifierEvent.getUltimatelyModifiedRatio();
            AttributeInstance attribute = attacker.getAttribute(Attributes.ATTACK_DAMAGE);

            if (attribute == null) {
                return;
            }

            double damage = attribute.getValue() * ultimatelyModifiedRatio;
            damage += attackAmplifierEvent.getExtraDamage();

            // 处理暴击
            //if (attacker instanceof Player player) {
            //    CriticalHitEvent criticalHitEvent = ForgeHooks.getCriticalHit(player, target, isCritical, isCritical
            //            ? 1.5F
            //            : 1.0F);
            //    if (criticalHitEvent != null) {
            //        damage *= criticalHitEvent.getDamageModifier();
            //    }
            //}

            if (damage <= 0) {
                return;
            }

            List<AttackAmplifierEvent.DamageSourceInfo> list = attackAmplifierEvent.getDamageSourceInfoList();
            boolean isAbsolute = attackTypeList.contains(RecastingAttackTypes.ABSOLUTE_ATTACK.get());
            if (list.isEmpty() && !isAbsolute) {
                return;
            }

            float healthBefore = target instanceof LivingEntity livingBefore
                    ? livingBefore.getHealth()
                    : 0f;

            double finalDamage = damage;
            Optional<Boolean> any = list.stream()
                    .map(
                            info -> {
                                target.invulnerableTime = 0;
                                DamageStructure structure = info.damageStructure();
                                boolean hurt = target.hurt(info.damageSource(), (float) (finalDamage * structure.modifiedRatio()) + structure.extraDamage());
                                if (hurt) {
                                    spawnDamageParticlesIfNeeded(target, info);
                                }
                                return hurt;
                            }
                    )
                    .toList()
                    .stream().filter(b -> b).findAny();

            // 绝对伤害：普通 hurt 后生命未减少时 setHealth 补伤，不另开取消正常结算的分支
            boolean absoluteApplied = false;
            if (isAbsolute
                    && target instanceof LivingEntity livingTarget
                    && livingTarget.getHealth() >= healthBefore) {
                float applied = (float) damage;
                AbsoluteHealthChangeGuard.run(() -> {
                    float next = Math.max(0f, livingTarget.getHealth() - applied);
                    livingTarget.setHealth(next);
                });
                absoluteApplied = true;
            }

            if (any.isPresent() || absoluteApplied) {
                mods.flammpfeil.slashblade.util.AttackHelper.applyKnockback(attacker, target, knockback);
                mods.flammpfeil.slashblade.util.AttackHelper.restoreTargetMotionIfNeeded(target, originalMotion);
                mods.flammpfeil.slashblade.util.AttackHelper.playAttackEffects(attacker, target, isCritical);
                mods.flammpfeil.slashblade.util.AttackHelper.handleEnchantmentsAndDurability(attacker, target);
                mods.flammpfeil.slashblade.util.AttackHelper.handlePostAttackEffects(attacker, target, fireAspectResult);
            } else {
                mods.flammpfeil.slashblade.util.AttackHelper.handleFailedAttack(attacker, target, fireAspectResult);
            }

        });

    }

    private static void spawnDamageParticlesIfNeeded(Entity target, AttackAmplifierEvent.DamageSourceInfo info) {
        if (!info.damageSource().is(DamageTypes.FELL_OUT_OF_WORLD)) {
            return;
        }
        if (!(target.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        Vec3 center = target.getBoundingBox().getCenter();
        serverLevel.playSound(null, center.x, center.y, center.z, SoundEvents.ENDER_DRAGON_HURT, SoundSource.PLAYERS, 1.6F, 1.15F);
        ParticleHelper.sendParticlesLongRange(serverLevel, ParticleTypes.PORTAL, center.x, center.y, center.z, 64, 0.3, 0.45, 0.3, 0.32);
        ParticleHelper.sendParticlesLongRange(serverLevel, ParticleTypes.DRAGON_BREATH, center.x, center.y, center.z, 48, 0.4, 0.55, 0.4, 0.22);
        ParticleHelper.sendParticlesLongRange(serverLevel, ParticleTypes.PORTAL, center.x, center.y, center.z, 72, 0.95, 1.15, 0.95, 0.08);
        ParticleHelper.sendParticlesLongRange(serverLevel, ParticleTypes.END_ROD, center.x, center.y, center.z, 18, 0.35, 0.5, 0.35, 0.16);
        ParticleHelper.sendParticlesLongRange(serverLevel, ParticleTypes.ENCHANTED_HIT, center.x, center.y, center.z, 28, 0.45, 0.6, 0.45, 0.28);
        ParticleHelper.sendParticlesLongRange(serverLevel, ParticleTypes.WITCH, center.x, center.y, center.z, 24, 0.5, 0.7, 0.5, 0.12);
    }

    public static SlashEffectEntity doSlash(
            LivingEntity playerIn,
            float roll,
            int colorCode,
            Vec3 centerOffset,
            boolean mute,
            boolean critical,
            DamageStructure damageStructure,
            float attackRange,
            KnockBacks knockback
    ) {

        if (playerIn.level().isClientSide()) {
            return null;
        }

        ItemStack blade = playerIn.getMainHandItem();
        if (!blade.getCapability(ItemSlashBlade.BLADESTATE).isPresent()) {
            return null;
        }

        DoSlashExtendEvent event = new DoSlashExtendEvent(
                blade,
                blade.getCapability(ItemSlashBlade.BLADESTATE).orElseThrow(NullPointerException::new),
                playerIn,
                roll,
                critical,
                damageStructure.modifiedRatio(),
                damageStructure.extraDamage(),
                knockback,
                attackRange,
                centerOffset,
                mute
        );

        if (MinecraftForge.EVENT_BUS.post(event)) {
            return null;
        }
        Vec3 pos = playerIn.position().add(0.0D, (double) playerIn.getEyeHeight() * 0.75D, 0.0D)
                .add(playerIn.getLookAngle().scale(0.3f));

        pos = pos.add(VectorHelper.getVectorForRotation(-90.0F, playerIn.getViewYRot(0)).scale(centerOffset.y))
                .add(VectorHelper.getVectorForRotation(0, playerIn.getViewYRot(0) + 90).scale(centerOffset.z))
                .add(playerIn.getLookAngle().scale(centerOffset.z));

        SlashEffectEntity jc = new SlashEffectEntity(RecastingEntities.SLASH_EFFECT.get(), playerIn.level(), playerIn);
        jc.setPos(pos.x, pos.y, pos.z);
        jc.setRoll(event.getRoll());
        jc.setYRot(playerIn.getYRot());
        jc.setXRot(0);

        jc.setColor(colorCode);

        jc.setMute(event.isMute());
        jc.setCritical(event.isCritical());

        jc.setModifiedRatio(event.getModifiedRatio());
        //noinspection deprecation
        jc.setDamage((float) event.getDamage());

        knockback = event.getKnockback();
        KnockBacks finalKnockback = knockback;
        if (finalKnockback != null) {
            jc.attackActionCallbackPoint.register(finalKnockback.action::accept);

        }

        jc.setSize(event.getAttackRange());

        playerIn.level().addFreshEntity(jc);

        return jc;

    }

    public static List<LivingEntity> areaAttack(LivingEntity playerIn, Vec3 pos, DamageStructure damageStructure, float attackRange, List<AttackType> attackTypeList, @Nullable List<Entity> exclude, @Nullable Consumer<LivingEntity> beforeHit) {
        if (playerIn.level().isClientSide()) {
            return List.of();
        }

        return EntityHelper.getTargettableLivingEntityWithinAABB(
                        playerIn.level(),
                        playerIn,
                        pos,
                        attackRange
                )
                .stream()
                .filter(e -> exclude == null || !exclude.contains(e))
                .peek(e -> {
                    if (beforeHit != null) {
                        beforeHit.accept(e);
                    }
                    doMeleeAttack(playerIn, e, damageStructure, attackTypeList);
                })
                .toList();

    }

    public static void doMeleeAttack(LivingEntity attacker, Entity target, DamageStructure damageStructure, List<AttackType> attackTypeList) {
        target.invulnerableTime = 0;

        attacker.getMainHandItem().getCapability(ItemSlashBlade.BLADESTATE).ifPresent((state) -> {
            try {
                state.setOnClick(true);
                attack(attacker, target, damageStructure, attackTypeList);
            } finally {
                state.setOnClick(false);
            }
        });

        target.invulnerableTime = 0;

        //ArrowReflector.doReflect(target, attacker);
        //TNTExtinguisher.doExtinguishing(target, attacker);
    }

    /**
     * 沿线段粗筛并精筛实体后结算伤害，并在服务端沿线生成尘埃粒子
     *
     * @return 实际命中的实体列表（客户端为空列表）
     */
    public static List<LivingEntity> attackAlongSegment(
            LivingEntity attacker,
            Vec3 start,
            Vec3 end,
            float radius,
            DamageStructure damageStructure,
            List<AttackType> attackTypeList,
            int color
    ) {
        if (attacker.level().isClientSide()) {
            return List.of();
        }

        AABB box = new AABB(start, end).inflate(radius);
        List<LivingEntity> candidates = attacker.level().getEntitiesOfClass(
                LivingEntity.class,
                box,
                entity -> EntityPredicateHelper.canTarget(attacker, entity)
        );

        List<LivingEntity> hits = new ArrayList<>();
        for(LivingEntity target : candidates) {
            Vec3 center = target.getBoundingBox().getCenter();
            if (PosHelper.distancePointToSegment(center, start, end) > radius) {
                continue;
            }
            attack(attacker, target, damageStructure, attackTypeList);
            hits.add(target);
        }

        if (attacker.level() instanceof ServerLevel serverLevel) {
            spawnDustAlongSegment(serverLevel, start, end, color, Math.max(0.25f, radius * 0.5f));
        }
        return hits;
    }

    /**
     * 从眼位沿当前视角方向结算线段伤害
     */
    public static List<LivingEntity> attackAlongLook(
            LivingEntity attacker,
            float range,
            float radius,
            DamageStructure damageStructure,
            List<AttackType> attackTypeList,
            int color
    ) {
        Vec3 start = attacker.getEyePosition();
        Vec3 end = start.add(attacker.getLookAngle().scale(range));
        return attackAlongSegment(attacker, start, end, radius, damageStructure, attackTypeList, color);
    }

    /**
     * 沿线采样生成彩色尘埃粒子
     */
    public static void spawnDustAlongSegment(ServerLevel serverLevel, Vec3 start, Vec3 end, int color, float spacing) {
        Vec3 delta = end.subtract(start);
        double length = delta.length();
        if (length <= 0.0) {
            return;
        }
        int steps = Math.max(1, (int) Math.ceil(length / spacing));
        float r = ((color >> 16) & 0xFF) / 255.0f;
        float g = ((color >> 8) & 0xFF) / 255.0f;
        float b = (color & 0xFF) / 255.0f;
        DustParticleOptions dust = new DustParticleOptions(new Vector3f(r, g, b), 1.0f);
        for(int i = 0; i <= steps; i++) {
            double t = i / (double) steps;
            Vec3 pos = start.lerp(end, t);
            ParticleHelper.sendParticlesLongRange(serverLevel, dust, pos.x, pos.y, pos.z, 1, 0.0, 0.0, 0.0, 0.0);
        }
    }
}
