package com.til.recasting.mixin;

import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.event.RefineSettlementEvent;
import mods.flammpfeil.slashblade.event.handler.RefineHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 精炼（锻造）相关修正：
 * <ul>
 *   <li>三八面体 {@code getEnchantmentValue} 取 {@code TRAPEZOHEDRON_MAX_REFINE}
 *       （默认 {@link Integer#MAX_VALUE}）时，原版 {@code level * 10} 会 int 溢出为负，
 *       精炼结算无法正确增加荣耀值。</li>
 *   <li>铁砧精炼经验消耗固定为 1 级：逐颗材料费用置 0，避免堆叠时按经验提前截断；
 *       最终 {@code setCost} 固定为 1。</li>
 * </ul>
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

    @ModifyArg(
            method = "onAnvilUpdateEvent",
            at = @At(
                    value = "INVOKE",
                    target = "Lmods/flammpfeil/slashblade/event/RefineProgressEvent;<init>("
                            + "Lnet/minecraft/world/item/ItemStack;"
                            + "Lmods/flammpfeil/slashblade/capability/slashblade/ISlashBladeState;"
                            + "IIII"
                            + "Lnet/minecraftforge/event/AnvilUpdateEvent;"
                            + ")V"
            ),
            index = 3
    )
    private int recasting$zeroPerMaterialLevelCost(int levelCost) {
        return 0;
    }

    @ModifyArg(
            method = "onAnvilUpdateEvent",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraftforge/event/AnvilUpdateEvent;setCost(I)V"
            ),
            index = 0
    )
    private int recasting$forceRefineCostOne(int cost) {
        return 1;
    }
}
