package com.til.recasting.registry.se;

import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.event.AttackAmplifierEvent;
import com.til.recasting.handler.CapabilityRegistryHandler;
import com.til.recasting.util.NumberPack;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/***
 * 吸血转化
 * 将攻击伤害的一部分转化为生命恢复
 */
public class LifeStealSpecialEffect extends ExtendedSpecialEffect {

    NumberPack healRatio = new NumberPack(0f, 0.01f); // 伤害转化比例

    @SubscribeEvent
    public void onEvent(AttackAmplifierEvent event) {
        LivingEntity target = resolveServerLivingTarget(event);
        if (target == null) {
            return;
        }

        LivingEntity attacker = event.getAttacker();

        // 计算预期伤害（基于最终伤害倍率）
        AttributeInstance attribute = attacker.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attribute == null) {
            return;
        }

        PropertiesDefinitionExtension properties = getPropertiesDefinitionExtension(event.getItem());
        int level = getLevel(properties);

        // 提前计算并保存需要的值
        double ultimatelyModifiedRatio = event.getUltimatelyModifiedRatio();
        float extraDamage = event.getExtraDamage();
        float currentHealRatio = healRatio.of(level);

        // 延迟执行，确保攻击已经完成并造成实际伤害
        attacker.getCapability(CapabilityRegistryHandler.TIME_RUN).ifPresent(
                timeRun -> timeRun.addTimerCell(
                        () -> {
                            Level worldIn = attacker.level();
                            if (worldIn.isClientSide()) {
                                return;
                            }

                            // 重新获取属性（可能在延迟期间发生变化）
                            AttributeInstance currentAttribute = attacker.getAttribute(Attributes.ATTACK_DAMAGE);
                            if (currentAttribute == null) {
                                return;
                            }

                            // 计算预期伤害
                            float expectedDamage = (float) (currentAttribute.getValue() * ultimatelyModifiedRatio);
                            expectedDamage += extraDamage;

                            // 恢复生命值（基于预期伤害的比例）
                            float healAmount = expectedDamage * currentHealRatio;
                            if (healAmount > 0) {
                                attacker.heal(healAmount);
                            }
                        },
                        1 // 延迟1 tick，确保伤害已经应用
                )
        );
    }

}
