package com.til.recasting.handler;

import com.til.recasting.constant.RecastingLanguageKeys;
import com.til.recasting.registry.se.ExtendedSpecialEffect;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public final class SpecialEffectTooltipHelper {

    private SpecialEffectTooltipHelper() {
    }

    public static Component createEffectLine(
            ExtendedSpecialEffect specialEffect,
            Component name,
            Component level
    ) {
        ChatFormatting style = specialEffect.isSpecial()
                ? ChatFormatting.LIGHT_PURPLE
                : ChatFormatting.GRAY;
        Component line = Component.translatable(
                "slashblade.tooltip.special_effect",
                name.copy().withStyle(style),
                level
        ).withStyle(style);
        if (!specialEffect.isSpecial()) {
            return line;
        }
        return line.copy()
                .append(Component.literal(" "))
                .append(Component.translatable(RecastingLanguageKeys.TOOLTIP_SPECIAL_SE_BADGE)
                        .withStyle(ChatFormatting.LIGHT_PURPLE));
    }

    public static Component createDescription(ExtendedSpecialEffect specialEffect) {
        return Component.translatable(specialEffect.getDescId())
                .withStyle(ChatFormatting.DARK_GRAY);
    }
}
