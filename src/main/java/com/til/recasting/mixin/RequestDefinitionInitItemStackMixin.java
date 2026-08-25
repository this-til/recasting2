package com.til.recasting.mixin;

import com.til.recasting.handler.SlashBladeRegistryHelper;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.capability.slashblade.BladeStateAccess;
import mods.flammpfeil.slashblade.recipe.RequestDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 配方请求栈（JEI 合成格展示用）在 init 时只有 translationKey / 门槛数值；
 * 从刀定义回填 model、texture，避免 GUI 自定义渲染无模型可用。
 */
@Mixin(value = RequestDefinition.class, remap = false)
public class RequestDefinitionInitItemStackMixin {

    @Inject(method = "initItemStack", at = @At("RETURN"), remap = false)
    private void recasting$fillMissingRender(ItemStack blade, CallbackInfo ci) {
        RequestDefinition self = (RequestDefinition) (Object) this;
        ResourceLocation bladeName = self.name();
        if (bladeName == null || bladeName.equals(SlashBlade.prefix("none"))) {
            return;
        }

        BladeStateAccess.of(blade).ifPresent(state -> {
            boolean textureEmpty = state.getTexture().isEmpty();
            boolean modelEmpty = state.getModel().isEmpty();
            if (!textureEmpty && !modelEmpty) {
                return;
            }

            SlashBladeRegistryHelper.getDefinition(bladeName).ifPresent(definition -> {
                if (textureEmpty) {
                    ResourceLocation defTexture = definition.getRenderDefinition().getTextureName();
                    if (defTexture != null) {
                        state.setTexture(defTexture);
                    }
                }
                if (modelEmpty) {
                    ResourceLocation defModel = definition.getRenderDefinition().getModelName();
                    if (defModel != null) {
                        state.setModel(defModel);
                    }
                }
            });
        });
    }
}
