package com.til.recasting.mixin;

import com.til.recasting.handler.SlashBladeRegistryHelper;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mods.flammpfeil.slashblade.compat.jei.JEICompat;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 混入 JEICompat 的 syncSlashBlade 方法
 * 当贴图和模型为空时，从 SlashBladeRegistryHelper 查找注册项并设置参数
 */
@Mixin(value = JEICompat.class, remap = false)
public class JEICompatMixin {

    /**
     * 在 syncSlashBlade 方法之后注入
     * 检查贴图和模型是否为空，如果为空则从注册表中查找并设置
     */
    @Inject(method = "syncSlashBlade", at = @At("HEAD"), remap = false)
    private static void onSyncSlashBlade(ItemStack stack, UidContext context, CallbackInfoReturnable<String> cir) {
        stack.getCapability(ItemSlashBlade.BLADESTATE).ifPresent((cap) -> {
            // 检查是否有 bladeState NBT 数据
            if (stack.getOrCreateTag().contains("bladeState")) {
                cap.deserializeNBT(stack.getOrCreateTag().getCompound("bladeState"));
            }

            // 检查贴图和模型是否为空
            boolean textureEmpty = cap.getTexture().isEmpty();
            boolean modelEmpty = cap.getModel().isEmpty();

            if (!textureEmpty && !modelEmpty) {
                return;
            }

            // 从注册表中查找定义
            String translationKey = cap.getTranslationKey();
            if (translationKey.isEmpty()) {
                return;
            }

            // 尝试从 translationKey 推断 ResourceLocation
            ResourceLocation bladeName = tryParseResourceLocationFromTranslationKey(translationKey);

            if (bladeName == null) {
                return;
            }

            SlashBladeRegistryHelper.getDefinition(bladeName).ifPresent(definition -> {
                // 设置贴图
                if (textureEmpty) {
                    ResourceLocation defTexture = definition.getRenderDefinition().getTextureName();
                    if (defTexture != null) {
                        cap.setTexture(defTexture);
                    }
                }

                // 设置模型
                if (modelEmpty) {
                    ResourceLocation defModel = definition.getRenderDefinition().getModelName();
                    if (defModel != null) {
                        cap.setModel(defModel);
                    }
                }

                // 重新序列化到 NBT
                stack.getOrCreateTag().put("bladeState", cap.serializeNBT());
            });
        });
    }

    /**
     * 从 translationKey 尝试解析 ResourceLocation
     * translationKey 格式通常是 "item.{namespace}.{path}" 或 "slashblade.name.{namespace}.{path}"
     */
    private static ResourceLocation tryParseResourceLocationFromTranslationKey(String translationKey) {
        if (translationKey == null || translationKey.isEmpty()) {
            return null;
        }

        // 尝试解析 translationKey
        // 格式1: "item.{namespace}.{path}"
        // 格式2: "slashblade.name.{namespace}.{path}"
        String[] parts = translationKey.split("\\.");

        // 检查格式1: item.namespace.path
        if (parts.length >= 3 && "item".equals(parts[0])) {
            String namespace = parts[1];
            StringBuilder pathBuilder = new StringBuilder();
            for(int i = 2; i < parts.length; i++) {
                if (i > 2) {
                    pathBuilder.append("/");
                }
                pathBuilder.append(parts[i]);
            }
            String path = pathBuilder.toString();
            return ResourceLocation.fromNamespaceAndPath(namespace, path);
        }

        // 检查格式2: slashblade.name.namespace.path
        if (parts.length >= 4 && "slashblade".equals(parts[0]) && "name".equals(parts[1])) {
            String namespace = parts[2];
            StringBuilder pathBuilder = new StringBuilder();
            for(int i = 3; i < parts.length; i++) {
                if (i > 3) {
                    pathBuilder.append("/");
                }
                pathBuilder.append(parts[i]);
            }
            String path = pathBuilder.toString();
            return ResourceLocation.fromNamespaceAndPath(namespace, path);
        }

        return null;
    }
}

