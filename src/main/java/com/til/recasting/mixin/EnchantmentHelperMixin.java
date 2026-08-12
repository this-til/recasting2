package com.til.recasting.mixin;

import com.til.recasting.handler.AttackHelper;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 拔刀剑上的锋利改为每级记录 0.2、上限 1.0，替换原版 {@code 1 + 0.5 * (level - 1)}。
 */
@Mixin(EnchantmentHelper.class)
public abstract class EnchantmentHelperMixin {

    @Inject(method = "getDamageBonus", at = @At("RETURN"), cancellable = true)
    private static void recasting$replaceSlashBladeSharpness(
            ItemStack stack,
            MobType mobType,
            CallbackInfoReturnable<Float> cir
    ) {
        if (!(stack.getItem() instanceof ItemSlashBlade)) {
            return;
        }
        int level = stack.getEnchantmentLevel(Enchantments.SHARPNESS);
        if (level <= 0) {
            return;
        }
        float replaced = cir.getReturnValueF()
                - AttackHelper.vanillaSharpnessDamageBonus(level)
                + AttackHelper.sharpnessDamageBonus(level);
        cir.setReturnValue(replaced);
    }
}
