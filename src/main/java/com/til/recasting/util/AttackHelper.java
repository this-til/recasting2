package com.til.recasting.util;

import com.til.recasting.event.AttackAmplifierEvent;
import com.til.recasting.registry.instance.AttackType;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.CriticalHitEvent;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 攻击助手类
 * 核心攻击流程：创建事件 → 发布事件（由监听器处理加成） → 计算伤害 → 应用效果
 */

public class AttackHelper {

    public static void attack(LivingEntity attacker, Entity target, float modifiedRatio, float extraDamage, List<AttackType> attackTypeList) {
        // 判断攻击目标是否可以被攻击
        if (!target.isAttackable() || target.skipAttackInteraction(attacker)) {
            return;
        }

        ItemStack mainHandItem = attacker.getMainHandItem();

        mainHandItem.getCapability(ItemSlashBlade.BLADESTATE).ifPresent(s -> {
            // 创建攻击放大事件
            AttackAmplifierEvent attackAmplifierEvent = new AttackAmplifierEvent(
                    mainHandItem, s, attacker, target, modifiedRatio, extraDamage, attackTypeList,
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
            if (attacker instanceof Player player) {
                CriticalHitEvent criticalHitEvent = ForgeHooks.getCriticalHit(player, target, isCritical, isCritical
                        ? 1.5F
                        : 1.0F);
                if (criticalHitEvent != null) {
                    damage *= criticalHitEvent.getDamageModifier();
                }
            }

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
}
