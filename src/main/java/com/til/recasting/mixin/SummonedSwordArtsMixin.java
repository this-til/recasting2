package com.til.recasting.mixin;

import com.til.recasting.compat.SrelicCompat;
import mods.flammpfeil.slashblade.ability.SummonedSwordArts;
import mods.flammpfeil.slashblade.event.handler.InputCommandEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 非 srelic 刀：屏蔽原版召唤剑输入，改由 {@link com.til.recasting.handler.SummonedSwordHelper} 接管。
 * srelic 刀：放行原版方法体，供 srelic 在 ordinal=1 处注入 Drive。
 */
@Mixin(value = SummonedSwordArts.class)
public abstract class SummonedSwordArtsMixin {

    @Inject(method = "onInputChange", at = @At("HEAD"), cancellable = true, remap = false)
    private void recasting$cancelVanillaSummonedSword(InputCommandEvent event, CallbackInfo ci) {
        if (SrelicCompat.isSrelicBlade(event.getEntity().getMainHandItem())) {
            return;
        }
        ci.cancel();
    }
}
