package com.til.recasting.mixin;

import com.til.recasting.handler.SlashBladeRegistryHelper;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mods.flammpfeil.slashblade.capability.slashblade.BladeStateAccess;
import mods.flammpfeil.slashblade.compat.jei.JEICompat;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 混入 JEICompat.syncSlashBlade：贴图/模型为空时从本模组刀定义回填。
 */
@Mixin(value = JEICompat.class, remap = false)
public class JEICompatMixin {

    @Inject(method = "syncSlashBlade", at = @At("HEAD"), remap = false)
    private static void recasting$fillMissingRender(
            ItemStack stack,
            UidContext context,
            CallbackInfoReturnable<String> cir
    ) {
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

            ResourceLocation bladeName = tryParseResourceLocationFromTranslationKey(translationKey);
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

    private static ResourceLocation tryParseResourceLocationFromTranslationKey(String translationKey) {
        String[] parts = translationKey.split("\\.");

        if (parts.length >= 3 && "item".equals(parts[0])) {
            String namespace = parts[1];
            StringBuilder pathBuilder = new StringBuilder();
            for (int i = 2; i < parts.length; i++) {
                if (i > 2) {
                    pathBuilder.append("/");
                }
                pathBuilder.append(parts[i]);
            }
            return ResourceLocation.fromNamespaceAndPath(namespace, pathBuilder.toString());
        }

        if (parts.length >= 4 && "slashblade".equals(parts[0]) && "name".equals(parts[1])) {
            String namespace = parts[2];
            StringBuilder pathBuilder = new StringBuilder();
            for (int i = 3; i < parts.length; i++) {
                if (i > 3) {
                    pathBuilder.append("/");
                }
                pathBuilder.append(parts[i]);
            }
            return ResourceLocation.fromNamespaceAndPath(namespace, pathBuilder.toString());
        }

        return null;
    }
}
