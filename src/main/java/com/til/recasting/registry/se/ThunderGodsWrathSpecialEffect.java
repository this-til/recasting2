package com.til.recasting.registry.se;

import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.entity.LightningEntity;
import com.til.recasting.registry.RecastingEntities;
import com.til.recasting.util.NumberPack;
import mods.flammpfeil.slashblade.capability.slashblade.BladeStateAccess;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

/***
 * 雷神之怒
 * 击杀敌人时，在死亡位置召唤闪电
 */
public class ThunderGodsWrathSpecialEffect extends ExtendedSpecialEffect {

    NumberPack attackRatio = new NumberPack(0.2f, 0.1f);

    @SubscribeEvent
    public void onEvent(LivingDeathEvent event) {
        // 只在服务端执行
        if (event.getEntity().level().isClientSide()) {
            return;
        }

        // 获取击杀者
        net.minecraft.world.entity.Entity killer = event.getSource().getEntity();
        if (!(killer instanceof LivingEntity attacker)) {
            return;
        }

        // 检查击杀者是否持有刀
        ItemStack blade = attacker.getMainHandItem();
        if (blade.isEmpty()) {
            return;
        }

        // 检查是否拥有此特效
        BladeStateAccess.of(blade).ifPresent(state -> {
            if (!hasSpecialEffect(state)) {
                return;
            }

            PropertiesDefinitionExtension properties = getPropertiesDefinitionExtension(blade);
            int level = getLevel(properties);

            // 获取死亡位置
            LivingEntity victim = event.getEntity();
            Vec3 deathPos = victim.position();

            // 计算伤害倍率和爆炸范围
            float attack = attackRatio.of(level);

            // 在死亡位置创建强力闪电
            LightningEntity lightning = new LightningEntity(
                    RecastingEntities.LIGHTNING.get(),
                    victim.level(),
                    attacker
            );

            lightning.setPos(deathPos.x, deathPos.y, deathPos.z);
            lightning.setModifiedRatio(attack);


            // 添加到世界
            victim.level().addFreshEntity(lightning);

        });
    }

}
