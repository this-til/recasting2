package com.til.recasting.handler;

import mods.flammpfeil.slashblade.SlashBladeConfig;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ItemFrame;

import javax.annotation.Nullable;

public class EntityPredicateHelper {
    public static boolean canTarget(@Nullable Entity attacker, Entity target) {

        boolean friendlyFire = SlashBladeConfig.PVP_ENABLE.get();


        if (target == null) {
            return false;
        }
        if (attacker == target) {
            return false;
        }
        if (target.isSpectator()) {
            return false;
        }
        if (!target.isAlive()) {
            return false;
        }

        if (target.isInvulnerable()) {
            return false;
        }

        if (attacker == null) {
            return true;
        }

        if (target instanceof ItemFrame) {
            return false;
        }

        if (attacker instanceof LivingEntity livingAttacker && target instanceof LivingEntity livingTarget) {
            if (!livingAttacker.canAttack(livingTarget)) {
                return false;
            }
        }

        if (!friendlyFire && attacker.isAlliedTo(target)) {
            return false;
        }

        return true;

    }
}
