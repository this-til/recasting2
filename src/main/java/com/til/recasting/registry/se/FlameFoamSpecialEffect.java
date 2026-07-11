package com.til.recasting.registry.se;

import com.til.recasting.event.AttackAmplifierEvent;
import com.til.recasting.handler.CapabilityRegistryHandler;
import com.til.recasting.registry.RecastingAttackTypes;
import com.til.recasting.registry.RecastingBuffTypes;
import com.til.recasting.registry.instance.AttackType;
import com.til.recasting.registry.instance.BuffType;
import com.til.recasting.util.DamageStructure;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/***
 * 燃沫
 * 攻击处于灵魂燃烧的目标时，使其额外受到当前生命1%的额外伤害，并有概率增加一层灵魂燃烧
 */
public class FlameFoamSpecialEffect extends ExtendedSpecialEffect {

    float healthDamageRatio = 0.01f; // 额外伤害比例（当前生命值的1%）
    float addSoulBurnProbability = 0.1f; // 增加灵魂燃烧的概率

    @SubscribeEvent
    public void onEvent(AttackAmplifierEvent event) {
        // 检查攻击者是否拥有此特效
        if (!hasSpecialEffect(event.getSlashBladeState())) {
            return;
        }

        // 只在服务端执行
        if (event.getAttacker().level().isClientSide()) {
            return;
        }

        // 检查目标是否是生物实体且存活
        if (!(event.getTarget() instanceof LivingEntity target) || !target.isAlive()) {
            return;
        }

        // 排除递归攻击
        if (event.getAttackTypeList().contains(RecastingAttackTypes.NO_RECURSION_ATTACK.get())) {
            return;
        }

        // 检查目标是否有灵魂燃烧buff
        Level world = target.level();
        BuffType soulBurnBuffType = RecastingBuffTypes.SOUL_BURN.get();

        target.getCapability(CapabilityRegistryHandler.BUFF_STACK_DATA).ifPresent(
                buffStackData -> {
                    int currentSoulBurnLevel = buffStackData.getLevel(soulBurnBuffType, world);
                    if (currentSoulBurnLevel <= 0) {
                        return;
                    }

                    // 计算目标当前生命值的1%作为额外伤害
                    float currentHealth = target.getHealth();
                    float extraDamage = currentHealth * healthDamageRatio;

                    // 创建魔法伤害源，使用 extraDamage 添加固定数值的额外伤害
                    AttackType magicAttackType = RecastingAttackTypes.SUMMOND_SWORD_ATTACK.get();
                    AttackAmplifierEvent.DamageSourceInfo damageSourceInfo = magicAttackType.createDamageSource(event.getAttacker(), target);

                    if (damageSourceInfo != null) {
                        // 使用 addDamageSourceInfo 添加额外的魔法伤害（固定数值）
                        event.addDamageSourceInfo(
                                damageSourceInfo.damageSource(),
                                new DamageStructure(0f, extraDamage)
                        );
                    }

                    if (event.getAttacker().getRandom().nextFloat() < addSoulBurnProbability) {
                        int maxLevel = soulBurnBuffType.getMaxLevel();
                        int newLevel = Math.min(currentSoulBurnLevel + 1, maxLevel);
                        buffStackData.setLevel(soulBurnBuffType, newLevel, world);
                    }
                }
        );
    }

}
