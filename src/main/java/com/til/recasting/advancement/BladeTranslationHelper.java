package com.til.recasting.advancement;

import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

/**
 * 从拔刀剑 translationKey 解析命名刀 ResourceLocation。
 */
public final class BladeTranslationHelper {

    private BladeTranslationHelper() {
    }

    public static String itemDescriptionId(ResourceLocation bladeId) {
        return Util.makeDescriptionId("item", bladeId);
    }

    @Nullable
    public static ResourceLocation tryParseBladeId(@Nullable String translationKey) {
        if (translationKey == null || translationKey.isEmpty()) {
            return null;
        }

        String[] parts = translationKey.split("\\.");

        if (parts.length >= 3 && "item".equals(parts[0])) {
            return joinPath(parts[1], parts, 2);
        }

        if (parts.length >= 4 && "slashblade".equals(parts[0]) && "name".equals(parts[1])) {
            return joinPath(parts[2], parts, 3);
        }

        return null;
    }

    private static ResourceLocation joinPath(String namespace, String[] parts, int pathStart) {
        StringBuilder pathBuilder = new StringBuilder();
        for(int i = pathStart; i < parts.length; i++) {
            if (i > pathStart) {
                pathBuilder.append('/');
            }
            pathBuilder.append(parts[i]);
        }
        return ResourceLocation.fromNamespaceAndPath(namespace, pathBuilder.toString());
    }
}
