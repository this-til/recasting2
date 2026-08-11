package com.til.recasting.registry;

import com.til.recasting.Recasting;
import com.til.recasting.constant.RecastingLanguageKeys;
import com.til.recasting.constant.RecastingSlashBladeCreativeOrder;
import com.til.recasting.constant.RecastingSlashBladeKeys;
import com.til.recasting.handler.CapabilityRegistryHandler;
import com.til.recasting.handler.SlashBladeRegistryHelper;
import com.til.recasting.registry.se.ExtendedSpecialEffect;
import mods.flammpfeil.slashblade.registry.SlashBladeItems;
import mods.flammpfeil.slashblade.registry.slashblade.SlashBladeDefinition;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.Objects;
import java.util.stream.IntStream;

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
                    .displayItems(RecastingCreativeTabs::fillDisplayItems)
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

    private static void fillDisplayItems(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output) {
        fillOrderedBlades(parameters, output);
        fillRegisteredItems(output);
        fillSpecialEffectCrystals(output);
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

    private static void fillRegisteredItems(CreativeModeTab.Output output) {
        output.accept(RecastingItems.PROUD_SOUL_BAG.get());
        output.accept(RecastingItems.MATTER_BALL.get());
        RecastingItems.getAllItems().stream()
                .map(RegistryObject::get)
                .forEach(output::accept);
    }

    private static void fillSpecialEffectCrystals(CreativeModeTab.Output output) {
        mods.flammpfeil.slashblade.registry.SpecialEffectsRegistry.REGISTRY.get().getValues().stream()
                .filter(specialEffect -> specialEffect instanceof ExtendedSpecialEffect)
                .map(specialEffect -> (ExtendedSpecialEffect) specialEffect)
                .flatMap(specialEffect -> {
                    int startLevel = specialEffect.isSpecial() ? 1 : 0;
                    return IntStream.range(startLevel, specialEffect.getMaxLevel() + 1)
                            .mapToObj(level -> createSpecialEffectCrystal(specialEffect, level));
                })
                .forEach(output::accept);
    }

    private static ItemStack createSpecialEffectCrystal(ExtendedSpecialEffect specialEffect, int level) {
        ItemStack itemStack = new ItemStack(RecastingItems.SE_CRYSTAL.get());
        itemStack.getCapability(CapabilityRegistryHandler.SE_CRYSTAL_DATA).ifPresent(data -> {
            ResourceLocation specialEffectKey = Objects.requireNonNull(
                    mods.flammpfeil.slashblade.registry.SpecialEffectsRegistry.REGISTRY.get().getKey(specialEffect)
            );
            data.setSpecialEffectType(specialEffectKey);
            data.setSpecialEffectLevel(level);
        });
        return itemStack;
    }
}
