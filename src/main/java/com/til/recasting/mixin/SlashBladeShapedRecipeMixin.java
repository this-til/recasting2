package com.til.recasting.mixin;

import com.til.recasting.handler.BladeSpecialEffectInheritanceHelper;
import mods.flammpfeil.slashblade.recipe.SlashBladeShapedRecipe;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 刀配方合成完成后，按规则继承或保留特殊 SE。
 */
@Mixin(value = SlashBladeShapedRecipe.class, remap = false)
public abstract class SlashBladeShapedRecipeMixin {

    @Shadow
    public abstract net.minecraft.resources.ResourceLocation getOutputBlade();

    @Inject(
            method = "assemble(Lnet/minecraft/world/inventory/CraftingContainer;Lnet/minecraft/core/RegistryAccess;)Lnet/minecraft/world/item/ItemStack;",
            at = @At("RETURN"),
            require = 0,
            remap = false
    )
    private void recasting$inheritSpecialEffect(
            CraftingContainer container,
            RegistryAccess access,
            CallbackInfoReturnable<ItemStack> cir
    ) {
        BladeSpecialEffectInheritanceHelper.apply(
                cir.getReturnValue(),
                container,
                access,
                getOutputBlade()
        );
    }

    @Inject(
            method = "assemble(Lnet/minecraft/world/Container;Lnet/minecraft/core/RegistryAccess;)Lnet/minecraft/world/item/ItemStack;",
            at = @At("RETURN"),
            require = 0,
            remap = false
    )
    private void recasting$inheritSpecialEffectBridge(
            Container container,
            RegistryAccess access,
            CallbackInfoReturnable<ItemStack> cir
    ) {
        BladeSpecialEffectInheritanceHelper.apply(
                cir.getReturnValue(),
                container,
                access,
                getOutputBlade()
        );
    }
}
