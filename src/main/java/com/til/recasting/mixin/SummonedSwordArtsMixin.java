package com.til.recasting.mixin;

import com.til.recasting.Config;
import mods.flammpfeil.slashblade.ability.SummonedSwordArts;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * Mixin 用于修改召唤剑实体的基础伤害值
 * 将 powerLevel（力量附魔等级）替换为配置的基础伤害倍率
 */
@Mixin(value = SummonedSwordArts.class, remap = false)
public abstract class SummonedSwordArtsMixin {

    /**
     * 修改 onInputChange 方法中的 powerLevel 变量
     */
    @ModifyVariable(
            method = "onInputChange",
            at = @At("STORE"),
            remap = false,
            name = "powerLevel")
    private int modifyPowerLevel(int powerLevel) {
        //return (int) (Config.summonedSwordBaseDamage * 100);
        return 1;
    }
}
