package com.til.recasting.mixin;

import com.til.recasting.handler.AttackHelper;
import com.til.recasting.handler.MathHelper;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 拔刀剑上的锋利改为每级 0.2、上限 1.0，替换原版 {@code 1 + 0.5 * (level - 1)}。
 */
@Mixin(EnchantmentHelper.class)
public abstract class EnchantmentHelperMixin {

    @Inject(method = "modifyDamage", at = @At("RETURN"), cancellable = true)
    private static void recasting$replaceSlashBladeSharpness(
            ServerLevel level,
            ItemStack stack,
            Entity target,
            DamageSource damageSource,
            float damage,
            CallbackInfoReturnable<Float> cir
    ) {
        if (!(stack.getItem() instanceof ItemSlashBlade)) {
            return;
        }
        int sharpnessLevel = MathHelper.getEnchantmentLevel(stack, Enchantments.SHARPNESS);
        if (sharpnessLevel <= 0) {
            return;
        }
        float replaced = cir.getReturnValueF()
                - AttackHelper.vanillaSharpnessDamageBonus(sharpnessLevel)
                + AttackHelper.sharpnessDamageBonus(sharpnessLevel);
        cir.setReturnValue(replaced);
    }
}
