package com.til.recasting.registry.se;

import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.entity.LightningEntity;
import com.til.recasting.event.AttackAmplifierEvent;
import com.til.recasting.handler.CapabilityRegistryHandler;
import com.til.recasting.registry.RecastingAttackTypes;
import com.til.recasting.registry.RecastingBuffTypes;
import com.til.recasting.registry.RecastingEntities;
import com.til.recasting.registry.instance.BuffType;
import com.til.recasting.util.NumberPack;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/***
 * 蓄能
 * 造成伤害后叠加层数，到12层时造成一道闪电攻击目标
 */
public class EnergyStorageSpecialEffect extends ExtendedSpecialEffect {

    NumberPack addLevelPerHit = new NumberPack(1f, 0f); // 每次造成伤害叠加的层数
    NumberPack lightningAttackRatio = new NumberPack(0.2f, 0.1f); // 闪电伤害倍率

    @SubscribeEvent
    public void onAttackAmplifier(AttackAmplifierEvent event) {
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

        // 从事件获取攻击者的刀
        ItemStack blade = event.getItem();
        PropertiesDefinitionExtension properties = getPropertiesDefinitionExtension(blade);
        int level = getLevel(properties);

        // 获取蓄能buff类型（对目标叠加）
        Level world = target.level();
        BuffType energyStorageBuffType = RecastingBuffTypes.ENERGY_STORAGE.get();

        // 获取当前层数（对目标叠加）
        target.getCapability(CapabilityRegistryHandler.BUFF_STACK_DATA).ifPresent(
                buffStackData -> {
                    int currentLevel = buffStackData.getLevel(energyStorageBuffType, world);
                    int maxLevel = energyStorageBuffType.getMaxLevel();

                    // 计算要叠加的层数
                    int addLevel = (int) addLevelPerHit.of(level);
                    int newLevel = Math.min(currentLevel + addLevel, maxLevel);

                    // 设置新层数
                    buffStackData.setLevel(energyStorageBuffType, newLevel, world);

                    // 检查是否达到最大层数（48层）
                    if (newLevel >= maxLevel) {
                        // 重置层数
                        buffStackData.setLevel(energyStorageBuffType, 0, world);

                        // 触发闪电攻击（攻击目标）
                        LivingEntity attacker = event.getAttacker();
                        triggerLightningAttack(attacker, target, blade, event.getSlashBladeState(), properties);
                    }
                }
        );
    }

    private void triggerLightningAttack(LivingEntity attacker, LivingEntity target, ItemStack blade, ISlashBladeState state, PropertiesDefinitionExtension properties) {
        Level world = attacker.level();
        if (world.isClientSide()) {
            return;
        }

        // 获取目标站立位置（地面位置）
        Vec3 targetPos = new Vec3(target.getX(), target.getY(), target.getZ());

        // 计算闪电伤害倍率
        int level = getLevel(properties);
        float attack = lightningAttackRatio.of(level);

        // 创建闪电实体
        LightningEntity lightning = new LightningEntity(
                RecastingEntities.LIGHTNING.get(),
                world,
                attacker
        );

        lightning.setPos(targetPos.x, targetPos.y, targetPos.z);
        lightning.setModifiedRatio(attack);
        lightning.setMaxLifeTime(20);

        // 添加到世界
        world.addFreshEntity(lightning);

    }

}
