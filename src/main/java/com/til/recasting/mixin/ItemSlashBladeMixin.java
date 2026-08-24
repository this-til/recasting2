package com.til.recasting.mixin;

import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.constant.RecastingLanguageKeys;
import com.til.recasting.handler.FeEnergyHelper;
import com.til.recasting.handler.SpecialEffectTooltipHelper;
import com.til.recasting.registry.RecastingDataComponents;
import com.til.recasting.registry.se.ExtendedSpecialEffect;
import mods.flammpfeil.slashblade.capability.slashblade.BladeStateAccess;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.item.SwordType;
import mods.flammpfeil.slashblade.registry.specialeffects.SpecialEffect;
import mods.flammpfeil.slashblade.slasharts.SlashArts;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.EnumSet;
import java.util.List;

/**
 * 混入 ItemSlashBlade 的 appendSpecialEffects 方法，支持 ExtendedSpecialEffect 等级显示与描述 tooltip。
 */
@Mixin(value = ItemSlashBlade.class)
public class ItemSlashBladeMixin {

    @Overwrite(remap = false)
    @OnlyIn(Dist.CLIENT)
    public void appendSpecialEffects(List<Component> tooltip, @NotNull ISlashBladeState s) {
    }

    @Inject(method = "appendSlashArt", at = @At("RETURN"), remap = false)
    @OnlyIn(Dist.CLIENT)
    public void appendSlashArtDescription(ItemStack stack, List<Component> tooltip, @NotNull ISlashBladeState s, CallbackInfo ci) {
        if (!Screen.hasShiftDown() || !showsSlashArt(stack)) {
            return;
        }
        appendSlashArtDesc(tooltip, s.getSlashArts());
    }

    @Inject(method = "appendHoverText", at = @At("RETURN"))
    @OnlyIn(Dist.CLIENT)
    public void appendSpecialEffectHoverText(ItemStack stack, Level worldIn, @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn, CallbackInfo ci) {
        FeEnergyHelper.appendTooltip(stack, tooltip);

        PropertiesDefinitionExtension extension = stack.get(RecastingDataComponents.PROPERTIES_DEFINITION_EXTENSION.get());
        if (extension != null) {
            EnumSet<SwordType> swordTypes = SwordType.from(stack);
            if (swordTypes.contains(SwordType.BEWITCHED)
                    && stack.getEnchantmentLevel(Enchantments.POWER) > 0) {
                boolean tracking = extension.trackingPhantomBlade();
                tooltip.add(Component.translatable(
                        tracking
                                ? RecastingLanguageKeys.TOOLTIP_PHANTOM_BLADE_TRACKING
                                : RecastingLanguageKeys.TOOLTIP_PHANTOM_BLADE_NORMAL
                ).withStyle(tracking
                        ? ChatFormatting.AQUA
                        : ChatFormatting.GRAY));
            }
        }

        BladeStateAccess.of(stack).ifPresent(state -> {
            boolean showDescription = Screen.hasShiftDown();
            if (!state.getSpecialEffects().isEmpty()) {
                appendSpecialEffectLines(stack, tooltip, state, showDescription);
            }
            if (!showDescription && hasAnyDescription(stack, state)) {
                tooltip.add(Component.translatable(RecastingLanguageKeys.TOOLTIP_SA_SE_DESC_HINT)
                        .withStyle(ChatFormatting.GRAY));
            }
        });
    }

    @Unique
    private static void appendSpecialEffectLines(
            ItemStack stack,
            List<Component> tooltip,
            ISlashBladeState s,
            boolean showDescription
    ) {
        Player player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        PropertiesDefinitionExtension properties = stack.get(RecastingDataComponents.PROPERTIES_DEFINITION_EXTENSION.get());
        if (properties == null) {
            return;
        }

        for (ResourceLocation specialEffectResourceLocation : s.getSpecialEffects()) {
            SpecialEffect specialEffect = mods.flammpfeil.slashblade.registry.SpecialEffectsRegistry.REGISTRY.get(specialEffectResourceLocation);
            if (specialEffect == null) {
                continue;
            }

            boolean showingLevel = SpecialEffect.getRequestLevel(specialEffectResourceLocation) > 0;
            Component nameComponent;
            Component valueComponent;
            if (specialEffect instanceof ExtendedSpecialEffect extendedSpecialEffect) {
                nameComponent = SpecialEffect.getDescription(specialEffectResourceLocation)
                        .copy()
                        .withStyle(extendedSpecialEffect.isSpecial()
                                ? ChatFormatting.LIGHT_PURPLE
                                : ChatFormatting.GRAY);
                valueComponent = Component.literal(
                        properties.getExtendedSpecialLevels(specialEffectResourceLocation) + "/" + extendedSpecialEffect.getMaxLevel()
                ).withStyle(ChatFormatting.LIGHT_PURPLE);
                tooltip.add(SpecialEffectTooltipHelper.createEffectLine(
                        extendedSpecialEffect,
                        nameComponent,
                        valueComponent
                ));
            } else {
                valueComponent = Component.literal(
                                showingLevel
                                        ? String.valueOf(SpecialEffect.getRequestLevel(specialEffectResourceLocation))
                                        : ""
                        )
                        .withStyle(
                                SpecialEffect.isEffective(
                                        specialEffectResourceLocation,
                                        player.experienceLevel
                                )
                                        ? ChatFormatting.RED
                                        : ChatFormatting.DARK_GRAY
                        );
                nameComponent = SpecialEffect.getDescription(specialEffectResourceLocation)
                        .copy()
                        .withStyle(ChatFormatting.GRAY);
                tooltip.add(
                        Component.translatable(
                                        "slashblade.tooltip.special_effect",
                                        nameComponent,
                                        valueComponent
                                )
                                .withStyle(ChatFormatting.GRAY));
            }

            if (showDescription) {
                appendDescLine(tooltip, specialEffect.getDescriptionId());
            }
        }
    }

    @Unique
    private static boolean hasAnyDescription(ItemStack stack, ISlashBladeState state) {
        if (showsSlashArt(stack) && hasSlashArtDescription(state.getSlashArts())) {
            return true;
        }
        for (ResourceLocation specialEffectResourceLocation : state.getSpecialEffects()) {
            SpecialEffect specialEffect = mods.flammpfeil.slashblade.registry.SpecialEffectsRegistry.REGISTRY.get(specialEffectResourceLocation);
            if (hasSpecialEffectDescription(specialEffect)) {
                return true;
            }
        }
        return false;
    }

    @Unique
    private static boolean showsSlashArt(ItemStack stack) {
        EnumSet<SwordType> swordType = SwordType.from(stack);
        return swordType.contains(SwordType.BEWITCHED) && !swordType.contains(SwordType.SEALED);
    }

    @Unique
    private static boolean hasSlashArtDescription(@Nullable SlashArts slashArts) {
        return slashArts != null && I18n.exists(slashArts.getDescriptionId() + ".desc");
    }

    @Unique
    private static boolean hasSpecialEffectDescription(@Nullable SpecialEffect specialEffect) {
        return specialEffect != null && I18n.exists(specialEffect.getDescriptionId() + ".desc");
    }

    @Unique
    private static void appendSlashArtDesc(List<Component> tooltip, @Nullable SlashArts slashArts) {
        if (slashArts == null) {
            return;
        }
        appendDescLine(tooltip, slashArts.getDescriptionId());
    }

    @Unique
    private static void appendDescLine(List<Component> tooltip, String descriptionId) {
        String descKey = descriptionId + ".desc";
        if (!I18n.exists(descKey)) {
            return;
        }
        tooltip.add(Component.translatable(descKey).withStyle(ChatFormatting.DARK_GRAY));
    }
}
