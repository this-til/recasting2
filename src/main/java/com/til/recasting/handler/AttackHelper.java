package com.til.recasting.handler;

import com.til.recasting.entity.LightningEntity;
import com.til.recasting.entity.SlashEffectEntity;
import com.til.recasting.event.AttackAmplifierEvent;
import com.til.recasting.event.DoSlashExtendEvent;
import com.til.recasting.registry.RecastingEntities;
import com.til.recasting.registry.instance.AttackType;
import com.til.recasting.util.DamageStructure;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.util.KnockBacks;
import mods.flammpfeil.slashblade.util.VectorHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;

import javax.annotation.Nullable;
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
                            .filter(Objects::nonNull).collect(Collectors.toList())
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
            if (list.isEmpty()) {
                return;
            }

            double finalDamage = damage;
            Optional<Boolean> any = list.stream()
                    .map(
                            info -> {
                                target.invulnerableTime = 0;
                                return target.hurt(info.damageSource(), (float) (finalDamage * info.damage()));
                            }
                    )
                    .toList()
                    .stream().filter(b -> b).findAny();

            if (any.isPresent()) {
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
}
