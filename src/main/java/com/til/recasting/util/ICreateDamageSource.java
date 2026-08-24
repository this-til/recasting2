package com.til.recasting.util;

import com.til.recasting.event.AttackAmplifierEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

@FunctionalInterface
public interface ICreateDamageSource {

    /**
     * 创建伤害源信息；返回 {@code null} 表示该 AttackType 仅作标记、不产生独立伤害源。
     */
    AttackAmplifierEvent.DamageSourceInfo createDamageSource(LivingEntity attacker, Entity target);
}
