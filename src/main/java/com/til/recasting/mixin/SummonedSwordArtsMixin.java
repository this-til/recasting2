package com.til.recasting.mixin;

import com.til.recasting.compat.SrelicCompat;
import mods.flammpfeil.slashblade.ability.SummonedSwordArts;
import mods.flammpfeil.slashblade.event.handler.InputCommandEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 非 srelic 刀：屏蔽原版召唤剑输入，改由 SummonedSwordHelper 接管。
 */
@Mixin(value = SummonedSwordArts.class, remap = false)
public abstract class SummonedSwordArtsMixin {

    @Inject(method = "onInputChange", at = @At("HEAD"), cancellable = true)
    private void recasting$cancelVanillaSummonedSword(InputCommandEvent event, CallbackInfo ci) {
        if (SrelicCompat.isSrelicBlade(event.getEntity().getMainHandItem())) {
            return;
        }
        ci.cancel();
    }
}
