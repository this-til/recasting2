package com.til.recasting.registry.instance;

import com.til.recasting.event.AttackAmplifierEvent;
import com.til.recasting.registry.RecastingAttackTypes;
import com.til.recasting.util.DamageStructure;
import com.til.recasting.util.ICreateDamageSource;
import lombok.AllArgsConstructor;
import mods.flammpfeil.slashblade.registry.SlashArtsRegistry;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Objects;

/**
 * &#064;Author:  til
 */
public class AttackType implements ICreateDamageSource {

    final ICreateDamageSource iCreateDamageSource;

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


    public String toString() {
        return Objects.requireNonNull((RecastingAttackTypes.REGISTRY.get()).getKey(this)).toString();
    }


}

