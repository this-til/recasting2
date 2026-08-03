package com.til.recasting.mixin;

import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.event.RefineSettlementEvent;
import mods.flammpfeil.slashblade.event.handler.RefineHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 三八面体 {@code getEnchantmentValue} 取 {@code TRAPEZOHEDRON_MAX_REFINE}
 *（默认 {@link Integer#MAX_VALUE}）时，原版 {@code level * 10} 会 int 溢出为负，
 * 精炼结算无法正确增加荣耀值。
 */
@Mixin(value = RefineHandler.class, remap = false)
public abstract class RefineHandlerMixin {

    @Inject(method = "getRefineProudsoulCount", at = @At("HEAD"), cancellable = true)
    private void recasting$safeRefineProudsoulCount(
            int level,
            ISlashBladeState state,
            RefineSettlementEvent e2,
            CallbackInfoReturnable<Integer> cir
    ) {
        int delta = e2.getRefineResult() - state.getRefine();
        int perRefine = (int) Math.min(5000L, (long) level * 10L);
        cir.setReturnValue(delta * perRefine);
    }
}
