package com.til.recasting.mixin;

import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.SlashBladeCreativeGroup;
import mods.flammpfeil.slashblade.registry.slashblade.SlashBladeDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * SlashBladeCreativeGroup.fillBlades 会无条件塞入全部命名刀；
 * 按 creativeGroup 过滤，仅保留指向拔刀剑创造栏的定义。
 */
@Mixin(value = SlashBladeCreativeGroup.class, remap = false)
public abstract class SlashBladeCreativeGroupMixin {

    @Inject(method = "fillBlades", at = @At("HEAD"), cancellable = true)
    private static void recasting$fillBladesByCreativeGroup(
            CreativeModeTab.ItemDisplayParameters parameters,
            CreativeModeTab.Output output,
            CallbackInfo callbackInfo
    ) {
        ResourceLocation slashBladeTab = SlashBladeCreativeGroup.SLASHBLADE_GROUP.getId();
        SlashBlade.getSlashBladeDefinitionRegistry(parameters.holders())
                .listElements()
                .sorted(SlashBladeDefinition.COMPARATOR)
                .forEach(entry -> {
                    SlashBladeDefinition definition = entry.value();
                    if (!slashBladeTab.equals(definition.getCreativeGroup())) {
                        return;
                    }
                    ItemStack blade = definition.getBlade();
                    if (!blade.isEmpty()) {
                        output.accept(blade);
                    }
                });
        callbackInfo.cancel();
    }
}
