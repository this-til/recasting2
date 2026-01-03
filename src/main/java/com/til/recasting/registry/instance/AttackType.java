package com.til.recasting.registry.instance;

import com.til.recasting.event.AttackAmplifierEvent;
import com.til.recasting.util.ICreateDamageSource;
import lombok.AllArgsConstructor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

/**
 * &#064;Author:  til
 */
@AllArgsConstructor
public class AttackType implements ICreateDamageSource {

    final ICreateDamageSource iCreateDamageSource;

    public AttackType() {
        this.iCreateDamageSource = (attacker, target) -> new AttackAmplifierEvent.DamageSourceInfo(
                attacker instanceof Player
                        ? attacker.damageSources().playerAttack((Player) attacker)
                        : attacker.damageSources().mobAttack(attacker),
                1.0f
        );
    }

    @Override
    public AttackAmplifierEvent.DamageSourceInfo createDamageSource(LivingEntity attacker, Entity target) {
        return iCreateDamageSource.createDamageSource(attacker, target);
    }

}

