package com.til.recasting.mixin;

import com.til.recasting.registry.RecastingAttackTypes;
import com.til.recasting.util.DamageStructure;
import mods.flammpfeil.slashblade.util.AttackHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.List;

/**
 * Mixin 用于重写 AttackHelper.calculateTotalDamage 方法
 * 修改伤害计算公式为: base * (comboRatio * (other1 + other2))
 */
@Mixin(value = AttackHelper.class)
public abstract class AttackHelperMixin {

    /**
     * @author til
     * @reason 修改伤害计算公式，基础伤害和附加伤害分开计算
     */
    @Overwrite(remap = false)
    public static void attack(LivingEntity attacker, Entity target, float modifiedRatio) {
        com.til.recasting.handler.AttackHelper.attack(attacker, target, new DamageStructure(modifiedRatio, 0), List.of(RecastingAttackTypes.SLASH_EFFECT_ATTACK.get()));
    }
}

