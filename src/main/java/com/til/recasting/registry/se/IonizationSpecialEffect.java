package com.til.recasting.registry.se;

import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.event.AttackAmplifierEvent;
import com.til.recasting.registry.RecastingAttachments;
import com.til.recasting.registry.RecastingAttackTypes;
import com.til.recasting.registry.RecastingBuffTypes;
import com.til.recasting.util.NumberPack;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;

/***
 * 电离
 * 受到闪电伤害时叠加电离buff，每层提供1%全伤害增伤，最高64层，高等级叠层更快
 */
public class IonizationSpecialEffect extends ExtendedSpecialEffect {

    // 每次受到闪电伤害叠加的层数
    NumberPack addLevelPerHit = new NumberPack(0f, 1f);

    @SubscribeEvent
    public void onAttackAmplifier(AttackAmplifierEvent event) {
        LivingEntity target = resolveServerLivingTarget(event);
        if (target == null) {
            return;
        }


        // 从事件获取攻击者的刀
        ItemStack blade = event.getItem();

        PropertiesDefinitionExtension properties = getPropertiesDefinitionExtension(blade);
        int level = getLevel(properties);

        // 获取电离buff类型
        Level world = target.level();

        // 检查是否是闪电攻击，如果是则叠加buff
        if (event.getAttackTypeList().contains(RecastingAttackTypes.LIGHTNING_ATTACK.get()) && !event.getAttackTypeList().contains(RecastingAttackTypes.NO_RECURSION_ATTACK.get())) {
            var buffStackData = RecastingAttachments.buffStackData(target);
            int currentLevel = buffStackData.getLevel(RecastingBuffTypes.IONIZATION.get(), world);
            int maxLevel = RecastingBuffTypes.IONIZATION.get().getMaxLevel();

            // 计算要叠加的层数（高等级叠层更快）
            int addLevel = (int) addLevelPerHit.of(level);
            int newLevel = Math.min(currentLevel + addLevel, maxLevel);

            // 设置新层数
            buffStackData.setLevel(RecastingBuffTypes.IONIZATION.get(), newLevel, world);
        }

        // 应用增伤：根据目标的电离buff层数提供全伤害增伤（对所有攻击类型都生效）
        var buffStackData = RecastingAttachments.buffStackData(target);
        int ionizationLevel = buffStackData.getLevel(RecastingBuffTypes.IONIZATION.get(), world);
        if (ionizationLevel > 0) {
            // 每层提供1%全伤害增伤
            float damageBonus = ionizationLevel * 0.01f;
            event.addModifiedRatioAmplifier(damageBonus);
        }
    }

}
