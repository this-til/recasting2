package com.til.recasting.util;

import com.til.recasting.event.AttackAmplifierEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

/**
 * @Author: til
 * @Description: 创建伤害源接口
 */
@FunctionalInterface
public interface ICreateDamageSource {

    /**
     * 创建伤害源
     *
     * @param attacker 攻击者
     * @param target   目标
     * @return 伤害源信息
     */
    AttackAmplifierEvent.DamageSourceInfo createDamageSource(LivingEntity attacker, Entity target);

}
