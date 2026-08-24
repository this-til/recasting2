package com.til.recasting.registry.se;

import com.til.recasting.entity.JudgementCutEntity;
import com.til.recasting.util.NumberPack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

/***
 * 旋风
 * 你的次元斩将允许造成重复的伤害
 */
public class WhirlwindSpecialEffect extends ExtendedSpecialEffect {

    NumberPack attackInterval = new NumberPack(9, -1);

    @SubscribeEvent
    public void onEvent(EntityJoinLevelEvent event) {
        JudgementCutContext context = resolveJudgementCutContext(event);
        if (context == null) {
            return;
        }
        JudgementCutEntity jc = context.judgementCut();

        // 设置次元斩允许重复攻击
        jc.setRepeatedAttack(true);

        float v = attackInterval.of(context.effectLevel());

        if (jc.getAttackInterval() > v) {
            jc.setAttackInterval((int) v);
        }
    }

}
