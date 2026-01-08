package com.til.recasting.mixin;

import com.til.recasting.registry.SlashArtsRegistry;
import com.til.recasting.registry.SpecialEffectsRegistry;
import mods.flammpfeil.slashblade.registry.specialeffects.SpecialEffect;
import mods.flammpfeil.slashblade.slasharts.SlashArts;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.IForgeRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Objects;

@Mixin(targets = "mods.flammpfeil.slashblade.registry.SlashBladeItems$4", remap = false)
public class SlashBladeItemsMixin$ProudsoulSphere {

    @Inject(method = "appendHoverText", at = @At("HEAD"), cancellable = true, remap = false)
    public void onAppendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> components, @NotNull TooltipFlag flag, CallbackInfo ci) {
        if (stack.getTag() == null) {
            return;
        }

        CompoundTag tag = stack.getTag();

        if (!tag.contains("SpecialAttackType")) {
            return;
        }

        ResourceLocation saLocation = ResourceLocation.tryParse(tag.getString("SpecialAttackType"));

        if (saLocation == null) {
            return;
        }


        // 从注册表中获取 SpecialEffect
        if (!mods.flammpfeil.slashblade.registry.SlashArtsRegistry.REGISTRY.get().containsKey(saLocation)) {
            return;
        }

        SlashArts sa = mods.flammpfeil.slashblade.registry.SlashArtsRegistry.REGISTRY.get().getValue(saLocation);

        if (!(sa instanceof SlashArtsRegistry.ExtendedSlashArts extendedSlashArts)) {
            return;
        }

        components.add(
                Component.translatable(
                        "slashblade.tooltip.slash_art",
                        Component.translatable(sa.getDescriptionId())
                ).withStyle(ChatFormatting.GRAY)
        );

        // 添加特殊效果介绍
        components.add(
                Component.translatable(extendedSlashArts.getDescId())
                        .withStyle(ChatFormatting.DARK_GRAY)
        );

        // 取消原始逻辑
        ci.cancel();
    }
}
