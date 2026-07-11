package com.til.recasting.registry.se;

import com.til.recasting.entity.JudgementCutEntity;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import com.til.recasting.util.NumberPack;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/***
 * 旋风
 * 你的次元斩将允许造成重复的伤害
 */
public class WhirlwindSpecialEffect extends ExtendedSpecialEffect {

    NumberPack attackInterval = new NumberPack(9, -1);

    @SubscribeEvent
    public void onEvent(EntityJoinLevelEvent event) {
        // 只在服务端执行
        if (event.getLevel().isClientSide()) {
            return;
        }

        // 检查是否是 JudgementCutEntity
        if (!(event.getEntity() instanceof JudgementCutEntity jc)) {
            return;
        }

        // 获取创建者
        LivingEntity shooter = jc.getShooter();
        if (shooter == null) {
            return;
        }

        // 检查是否拥有此特效
        ItemStack blade = shooter.getMainHandItem();
        if (blade.isEmpty()) {
            return;
        }

        blade.getCapability(ItemSlashBlade.BLADESTATE).ifPresent(state -> {
            if (!hasSpecialEffect(state)) {
                return;
            }

            // 设置次元斩允许重复攻击
            jc.setRepeatedAttack(true);

            float v = attackInterval.of(getLevel(getPropertiesDefinitionExtension(blade)));

            if (jc.getAttackInterval() > v) {
                jc.setAttackInterval((int) v);
            }
        });
    }

}
