package com.til.recasting.registry.se;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 屠巫血咒
 * 手持时参与阶梯增伤与致命抵挡；优先级由 {@link com.til.recasting.handler.EmperorLineSeHelper} 结算。
 */
@Getter
@Setter
@Accessors(chain = true)
public class TuWuBloodCurseSpecialEffect extends ExtendedSpecialEffect implements EmperorLineStats {

    int lineGrade = 0;
    float damageAmplifier = 0.33f;
    int proudPerDamage = 200;
    int maxProudPerHit = 5000;
    int protectThreshold = 5000;
}
