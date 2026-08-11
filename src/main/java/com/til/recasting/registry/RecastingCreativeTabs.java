package com.til.recasting.registry;

import com.til.recasting.Recasting;
import com.til.recasting.constant.RecastingLanguageKeys;
import com.til.recasting.constant.RecastingSlashBladeCreativeOrder;
import com.til.recasting.constant.RecastingSlashBladeKeys;
import com.til.recasting.handler.SlashBladeRegistryHelper;
import mods.flammpfeil.slashblade.registry.SlashBladeItems;
import mods.flammpfeil.slashblade.registry.slashblade.SlashBladeDefinition;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

/**
 * Recasting 模组创造栏 Tab。
 */
public final class RecastingCreativeTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Recasting.MODID);

    public static final RegistryObject<CreativeModeTab> RECASTING = CREATIVE_MODE_TABS.register(
            "recasting",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable(RecastingLanguageKeys.ITEM_GROUP))
                    .icon(RecastingCreativeTabs::coolMintIcon)
                    .displayItems(RecastingCreativeTabs::fillOrderedBlades)
                    .build()
    );

    private RecastingCreativeTabs() {
    }

    private static ItemStack coolMintIcon() {
        return SlashBladeRegistryHelper.getDefinition(RecastingSlashBladeKeys.COOL_MINT)
                .map(SlashBladeDefinition::getBlade)
                .filter(stack -> !stack.isEmpty())
                .orElseGet(() -> new ItemStack(SlashBladeItems.SLASHBLADE.get()));
    }

    private static void fillOrderedBlades(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output) {
        var lookup = parameters.holders().lookupOrThrow(SlashBladeDefinition.REGISTRY_KEY);
        for (ResourceKey<SlashBladeDefinition> key : RecastingSlashBladeCreativeOrder.CREATIVE_TAB_ORDER) {
            lookup.get(key).ifPresent(holder -> {
                ItemStack blade = holder.value().getBlade();
                if (!blade.isEmpty()) {
                    output.accept(blade);
                }
            });
        }
    }
}
