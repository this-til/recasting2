package com.til.recasting.registry.se;

import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.event.AttackAmplifierEvent;
import com.til.recasting.registry.RecastingAttachments;
import com.til.recasting.registry.RecastingAttackTypes;
import com.til.recasting.registry.RecastingBuffTypes;
import com.til.recasting.util.DamageStructure;
import com.til.recasting.util.NumberPack;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;

/***
 * 雷云
 * 目标受到雷电伤害后获得雷光buff，持有雷光的实体受到伤害后附加相当于原伤害10%等级的附加闪电伤害
 */
public class ThunderCloudSpecialEffect extends ExtendedSpecialEffect {

    NumberPack thunderLightLevelPerHit = new NumberPack(0.5f, 0.5f); // 每次受到雷电伤害获得的雷光层数
    float damageRatio = 0.1f;

    @SubscribeEvent
    public void onAttackAmplifier(AttackAmplifierEvent event) {
        LivingEntity target = resolveServerLivingTarget(event);
        if (target == null) {
            return;
        }

        // 排除递归攻击
        if (event.getAttackTypeList().contains(RecastingAttackTypes.NO_RECURSION_ATTACK.get())) {
            return;
        }

        // 检查是否是闪电攻击
        if (!event.getAttackTypeList().contains(RecastingAttackTypes.LIGHTNING_ATTACK.get())) {
            return;
        }

        // 给目标添加雷光buff
        Level world = target.level();

        ItemStack blade = event.getItem();
        PropertiesDefinitionExtension properties = getPropertiesDefinitionExtension(blade);

        var buffStackData = RecastingAttachments.buffStackData(target);
        int currentLevel = buffStackData.getLevel(RecastingBuffTypes.THUNDER_LIGHT.get(), world);
        int maxLevel = RecastingBuffTypes.THUNDER_LIGHT.get().getMaxLevel();
        int newLevel = Math.min(currentLevel + (int) thunderLightLevelPerHit.of(getLevel(properties)), maxLevel);
        buffStackData.setLevel(RecastingBuffTypes.THUNDER_LIGHT.get(), newLevel, world);
    }

    @SubscribeEvent
    public void onAttackAmplifierForThunderLight(AttackAmplifierEvent event) {

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

        // 检查目标（受害者）是否有雷光buff
        Level world = target.level();

        var buffStackData = RecastingAttachments.buffStackData(target);
        int thunderLightLevel = buffStackData.getLevel(RecastingBuffTypes.THUNDER_LIGHT.get(), world);
        if (thunderLightLevel <= 0) {
            return;
        }

        // 计算附加闪电伤害比例：10% + 5% * SE等级
        float damageRatioValue = damageRatio;

        // 创建闪电伤害源，使用 modifiedRatio 基于 finalDamage 计算额外伤害
        AttackAmplifierEvent.DamageSourceInfo damageSourceInfo =
                RecastingAttackTypes.LIGHTNING_ATTACK.get().createDamageSource(event.getAttacker(), target);

        if (damageSourceInfo != null) {
            // 使用 addDamageSourceInfo 添加额外的闪电伤害（基于伤害倍率）
            event.addDamageSourceInfo(
                    damageSourceInfo.damageSource(),
                    new DamageStructure(damageRatioValue, 0f)
            );
        }
    }

}
