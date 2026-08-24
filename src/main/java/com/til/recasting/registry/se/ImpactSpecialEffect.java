package com.til.recasting.registry.se;

import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.entity.SummondSwordEntity;
import com.til.recasting.event.AttackAmplifierEvent;
import com.til.recasting.registry.RecastingAttachments;
import com.til.recasting.registry.RecastingAttackTypes;
import com.til.recasting.registry.RecastingEntities;
import com.til.recasting.util.NumberPack;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;

/***
 * 冲击
 * 造成伤害有几率召唤幻影剑造成瞬间伤害
 */
public class ImpactSpecialEffect extends ExtendedSpecialEffect {

    NumberPack probability = new NumberPack(0f, 0.05f);
    NumberPack attackRatio = new NumberPack(0f, 0.1f);


    @SubscribeEvent
    public void onEvent(AttackAmplifierEvent event) {
        LivingEntity target = resolveServerLivingTarget(event);
        if (target == null) {
            return;
        }

        if (event.getAttackTypeList().contains(RecastingAttackTypes.NO_RECURSION_ATTACK.get())) {
            return;
        }

        PropertiesDefinitionExtension properties = getPropertiesDefinitionExtension(event.getItem());
        int level = getLevel(properties);

        // 概率检查
        if (event.getAttacker().getRandom().nextFloat() >= probability.of(level)) {
            return;
        }


        // 延迟执行，确保攻击已经完成
        RecastingAttachments.timeRun(event.getAttacker()).addTimerCell(
                () -> {
                    Level worldIn = event.getAttacker().level();
                    if (worldIn.isClientSide()) {
                        return;
                    }

                    // 检查目标是否仍然存活
                    if (!target.isAlive()) {
                        return;
                    }

                    // 获取目标位置
                    Vec3 pos = target.position().add(0, target.getEyeHeight() * 0.5, 0);

                    // 创建幻影剑
                    SummondSwordEntity summondSword = new SummondSwordEntity(
                            RecastingEntities.SUMMOND_SWORD.get(),
                            worldIn,
                            event.getAttacker()
                    );

                    // 设置位置和旋转
                    summondSword.setPos(pos.x, pos.y, pos.z);
                    summondSword.setYRot(event.getAttacker().getRandom().nextFloat() * 360);
                    summondSword.setXRot(event.getAttacker().getRandom().nextFloat() * 360);

                    // 设置颜色
                    summondSword.setColor(event.getSlashBladeState().getColorCode());

                    // 设置伤害倍率
                    summondSword.setModifiedRatio(attackRatio.of(level));

                    // 设置最大生命时间
                    summondSword.setMaxLifeTime(40);

                    // 添加到世界
                    worldIn.addFreshEntity(summondSword);

                    // 立即攻击目标
                    summondSword.onHitEntity(target, SummondSwordEntity.SummondAttackType.HIT);
                },
                0
        );
    }

}
