package com.til.recasting.registry.se;

import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.entity.JudgementCutEntity;
import com.til.recasting.entity.SummondSwordEntity;
import com.til.recasting.registry.RecastingEntities;
import com.til.recasting.util.NumberPack;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/***
 * 风暴
 * 触发审判时，召唤幻影剑进行攻击
 */
public class StormSpecialEffect extends ExtendedSpecialEffect {

    NumberPack attackRatio = new NumberPack(0f, 0.05f);
    NumberPack number = new NumberPack(1f, 1f);

    @SubscribeEvent
    public void onEvent(EntityJoinLevelEvent event) {
        JudgementCutContext context = resolveJudgementCutContext(event);
        if (context == null) {
            return;
        }
        JudgementCutEntity jc = context.judgementCut();
        var shooter = context.shooter();
        var state = context.state();
        int level = context.effectLevel();

            // 获取次元斩位置
            Vec3 pos = jc.position();

            // 生成幻影剑数量
            int n = (int) number.of(level);
            float attack = attackRatio.of(level);

            for(int i = 0; i < n; i++) {
                SummondSwordEntity summondSword = new SummondSwordEntity(
                        RecastingEntities.SUMMOND_SWORD.get(),
                        event.getLevel(),
                        shooter
                );

                summondSword.lookAt(pos, false);
                summondSword.setColor(state.getColorCode());
                summondSword.setModifiedRatio(attack);
                summondSword.setStartDelay(shooter.getRandom().nextInt(10));

                // 添加到世界
                event.getLevel().addFreshEntity(summondSword);
            }

            // 播放音效
            event.getEntity().playSound(
                    SoundEvents.CHORUS_FRUIT_TELEPORT,
                    0.2F,
                    1.45F
            );
    }

}
