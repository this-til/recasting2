package com.til.recasting.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import mods.flammpfeil.slashblade.client.renderer.layers.LayerMainBlade;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * SlashBlade 会把快捷栏 0 号刀当作备用鞘刀渲染；取消该行为，只保留手中 / 副手刀。
 */
@Mixin(value = LayerMainBlade.class, remap = false)
public abstract class LayerMainBladeMixin {

    @Inject(method = "renderHotbarItem", at = @At("HEAD"), cancellable = true)
    private void recasting$skipHotbarSlot0Standby(
            PoseStack matrixStack,
            MultiBufferSource bufferIn,
            int lightIn,
            LivingEntity entity,
            CallbackInfo ci
    ) {
        ci.cancel();
    }
}
