package com.til.recasting.mixin;

import com.til.recasting.registry.se.ExtendedSpecialEffect;
import mods.flammpfeil.slashblade.registry.specialeffects.SpecialEffect;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(targets = "mods.flammpfeil.slashblade.registry.SlashBladeItems$5")
public class SlashBladeItemsMixin$ProudsoulCrystal {

    @Inject(method = "appendHoverText", at = @At("HEAD"), cancellable = true)
    public void onAppendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> components, @NotNull TooltipFlag flag, CallbackInfo ci) {
        if (stack.getTag() == null) {
            return;
        }

        CompoundTag tag = stack.getTag();

        if (!tag.contains("SpecialEffectType")) {
            return;
        }

        ResourceLocation seLocation = ResourceLocation.tryParse(tag.getString("SpecialEffectType"));

        if (seLocation == null) {
            return;
        }


        // 从注册表中获取 SpecialEffect
        if (!mods.flammpfeil.slashblade.registry.SpecialEffectsRegistry.REGISTRY.get().containsKey(seLocation)) {
            return;
        }

        SpecialEffect se = mods.flammpfeil.slashblade.registry.SpecialEffectsRegistry.REGISTRY.get().getValue(seLocation);

        if (!(se instanceof ExtendedSpecialEffect extendedSE)) {
            return;
        }

        // 获取等级和最大等级
        int currentLevel = tag.getInt("SpecialEffectTypeLevel");
        int maxLevel = extendedSE.getMaxLevel();

        Component nameComponent = Component.translatable(se.getDescriptionId())
                .withStyle(extendedSE.isSpecial() ? ChatFormatting.LIGHT_PURPLE : ChatFormatting.GRAY);
        Component line = Component.translatable(
                "slashblade.tooltip.special_effect",
                nameComponent,
                currentLevel + "/" + maxLevel
        ).withStyle(extendedSE.isSpecial() ? ChatFormatting.LIGHT_PURPLE : ChatFormatting.GRAY);
        if (extendedSE.isSpecial()) {
            line = line.copy()
                    .append(Component.literal(" "))
                    .append(Component.translatable("recasting.tooltip.special_se.badge")
                            .withStyle(ChatFormatting.LIGHT_PURPLE));
        }
        components.add(line);

        // 添加特殊效果介绍
        components.add(
                Component.translatable(extendedSE.getDescId())
                        .withStyle(ChatFormatting.DARK_GRAY)
        );

        // 取消原始逻辑
        ci.cancel();
    }
}
