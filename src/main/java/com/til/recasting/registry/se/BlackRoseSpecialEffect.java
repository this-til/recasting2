package com.til.recasting.registry.se;

import com.til.recasting.event.AttackAmplifierEvent;
import com.til.recasting.handler.AttackHelper;
import com.til.recasting.registry.RecastingAttackTypes;
import com.til.recasting.util.DamageStructure;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/***
 * 黑色玫瑰
 * 叠加伤害，每 tick 造成伤害，伤害减半
 */
public class BlackRoseSpecialEffect extends ExtendedSpecialEffect {

    float attack = 0.05f;
    float attenuation = 0.75f;
    int attackInterval = 5;

    // 存储每个攻击者对每个目标的累计伤害
    // Map<攻击者, Map<目标, 累计伤害>>
    Map<LivingEntity, Map<LivingEntity, Float>> accumulatedDamageMap = new HashMap<>();

    @SubscribeEvent
    public void onAttackAmplifier(AttackAmplifierEvent event) {
        if (!hasSpecialEffect(event.getSlashBladeState())) {
            return;
        }
        if (event.getAttacker().level().isClientSide()) {
            return;
        }
        if (!(event.getTarget() instanceof LivingEntity target) || !target.isAlive()) {
            return;
        }

        // 检查攻击类型，如果是防止递归攻击类型，不叠加伤害（避免递归）
        if (event.getAttackTypeList().contains(RecastingAttackTypes.NO_RECURSION_ATTACK.get())) {
            return;
        }

        LivingEntity attacker = event.getAttacker();

        // 获取或创建目标伤害映射
        Map<LivingEntity, Float> targetDamageMap = accumulatedDamageMap.computeIfAbsent(attacker, k -> new HashMap<>());

        // 计算本次伤害（基于最终伤害倍率）
        AttributeInstance attribute = attacker.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attribute == null) {
            return;
        }
        float baseDamage = (float) (attribute.getValue() * event.getUltimatelyModifiedRatio());
        baseDamage += event.getExtraDamage();

        // 叠加伤害
        float currentAccumulated = targetDamageMap.getOrDefault(target, 0f);
        float newDamage = baseDamage * attack;
        float totalDamage = currentAccumulated + newDamage;

        targetDamageMap.put(target, totalDamage);
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        // 如果没有攻击者，直接返回
        if (accumulatedDamageMap.isEmpty()) {
            return;
        }

        long currentTime = event.getServer().getTickCount();
        if (currentTime % attackInterval != 0) {
            return;
        }

        // 清理无效的攻击者并统一造成伤害
        Iterator<Map.Entry<LivingEntity, Map<LivingEntity, Float>>> attackerIterator = accumulatedDamageMap.entrySet().iterator();
        while (attackerIterator.hasNext()) {
            Map.Entry<LivingEntity, Map<LivingEntity, Float>> attackerEntry = attackerIterator.next();
            LivingEntity attacker = attackerEntry.getKey();

            // 如果攻击者无效，清除整个条目
            if (attacker == null || !attacker.isAlive() || attacker.level().isClientSide()) {
                attackerIterator.remove();
                continue;
            }

            Map<LivingEntity, Float> targetDamageMap = attackerEntry.getValue();

            if (targetDamageMap == null || targetDamageMap.isEmpty()) {
                continue;
            }

            Iterator<Map.Entry<LivingEntity, Float>> targetIterator = targetDamageMap.entrySet().iterator();
            while (targetIterator.hasNext()) {
                Map.Entry<LivingEntity, Float> targetEntry = targetIterator.next();
                LivingEntity target = targetEntry.getKey();
                Float accumulatedDamage = targetEntry.getValue();

                // 如果目标无效，清除条目
                if (target == null || !target.isAlive() || target.level() != attacker.level()) {
                    targetIterator.remove();
                    continue;
                }

                if (accumulatedDamage == null) {
                    targetIterator.remove();
                    continue;
                }

                // 如果累计伤害小于 0.1，不造成伤害，清除条目
                if (accumulatedDamage < 0.1f) {
                    targetIterator.remove();
                    continue;
                }

                // 使用 AttackHelper 造成伤害，同时使用黑色玫瑰攻击类型和防止递归攻击类型
                // 这样会触发 AttackAmplifierEvent，但由于我们检查了攻击类型，不会递归叠加
                AttackHelper.attack(
                        attacker,
                        target,
                        new DamageStructure(0f, accumulatedDamage),
                        List.of(RecastingAttackTypes.BLACK_ROSE_ATTACK.get(), RecastingAttackTypes.NO_RECURSION_ATTACK.get())
                );

                // 伤害减半
                float newDamage = accumulatedDamage * attenuation;
                if (newDamage < 0.1f) {
                    targetIterator.remove();
                } else {
                    targetEntry.setValue(newDamage);
                }
            }

            // 如果目标映射为空，清除攻击者条目
            if (targetDamageMap.isEmpty()) {
                attackerIterator.remove();
            }
        }
    }

}
