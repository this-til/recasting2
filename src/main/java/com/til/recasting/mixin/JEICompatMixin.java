package com.til.recasting.mixin;

import com.til.recasting.advancement.BladeTranslationHelper;
import com.til.recasting.handler.SlashBladeRegistryHelper;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mods.flammpfeil.slashblade.capability.slashblade.BladeStateAccess;
import mods.flammpfeil.slashblade.compat.jei.SlashBladeSubtypeInterpreter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * JEI 展示配方刀时，{@link mods.flammpfeil.slashblade.recipe.RequestDefinition#initItemStack}
 * 只写入 translationKey / 数值门槛，不含 model、texture。
 * <p>
 * 1.20 在 {@code JEICompat.syncSlashBlade}（子类型解释器）上回填；
 * 1.21 SlashBlade 改走 {@link SlashBladeSubtypeInterpreter}，故改注入此处。
 */
@Mixin(value = SlashBladeSubtypeInterpreter.class, remap = false)
public class JEICompatMixin {

    @Inject(method = "getSubtypeData", at = @At("HEAD"), remap = false)
    private void recasting$fillMissingRenderOnSubtypeData(
            ItemStack ingredient,
            UidContext context,
            CallbackInfoReturnable<Object> cir
    ) {
        recasting$fillMissingRender(ingredient);
    }

    @Inject(method = "getStringName", at = @At("HEAD"), remap = false)
    private void recasting$fillMissingRenderOnStringName(
            ItemStack itemStack,
            CallbackInfoReturnable<String> cir
    ) {
        recasting$fillMissingRender(itemStack);
    }

    @Unique
    private static void recasting$fillMissingRender(ItemStack stack) {
        BladeStateAccess.of(stack).ifPresent(state -> {
            boolean textureEmpty = state.getTexture().isEmpty();
            boolean modelEmpty = state.getModel().isEmpty();
            if (!textureEmpty && !modelEmpty) {
                return;
            }

            String translationKey = state.getTranslationKey();
            if (translationKey == null || translationKey.isEmpty()) {
                return;
            }

            ResourceLocation bladeName = BladeTranslationHelper.tryParseBladeId(translationKey);
            if (bladeName == null) {
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
