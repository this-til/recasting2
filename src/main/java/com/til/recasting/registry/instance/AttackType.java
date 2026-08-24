package com.til.recasting.registry.instance;

import com.til.recasting.event.AttackAmplifierEvent;
import com.til.recasting.registry.RecastingAttackTypes;
import com.til.recasting.util.DamageStructure;
import com.til.recasting.util.ICreateDamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.Objects;

public class AttackType implements ICreateDamageSource {

    private final ICreateDamageSource iCreateDamageSource;

    public AttackType() {
        this.iCreateDamageSource = (attacker, target) -> new AttackAmplifierEvent.DamageSourceInfo(
                attacker instanceof Player
                        ? attacker.damageSources().playerAttack((Player) attacker)
                        : attacker.damageSources().mobAttack(attacker),
                new DamageStructure(1.0f, 0)
        );
    }

    public AttackType(ICreateDamageSource iCreateDamageSource) {
        this.iCreateDamageSource = iCreateDamageSource;
    }

    @Override
    public AttackAmplifierEvent.DamageSourceInfo createDamageSource(LivingEntity attacker, Entity target) {
        return iCreateDamageSource.createDamageSource(attacker, target);
    }

    @Override
    public String toString() {
        return Objects.requireNonNull(RecastingAttackTypes.REGISTRY.getKey(this)).toString();
    }
}
