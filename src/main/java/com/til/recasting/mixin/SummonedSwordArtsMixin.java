package com.til.recasting.mixin;

import com.til.recasting.handler.SlashBladeItemHelper;
import mods.flammpfeil.slashblade.ability.SummonedSwordArts;
import mods.flammpfeil.slashblade.event.handler.InputCommandEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 规则命中时屏蔽原版召唤剑输入，改由 {@link com.til.recasting.handler.SummonedSwordHelper} 接管。
 */
@Mixin(value = SummonedSwordArts.class)
public abstract class SummonedSwordArtsMixin {

    @Inject(method = "onInputChange", at = @At("HEAD"), cancellable = true, remap = false)
    private void recasting$cancelVanillaSummonedSword(InputCommandEvent event, CallbackInfo ci) {
        if (!SlashBladeItemHelper.matchesReplaceRule(event.getEntity().getMainHandItem())) {
            return;
        }
        ci.cancel();
    }
}
