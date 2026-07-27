package com.til.recasting.handler;

import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.registry.se.ExtendedSpecialEffect;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.registry.specialeffects.SpecialEffect;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class BladeSpecialEffectHelper {

    public record EffectEntry(ResourceLocation id, int level) {
    }

    private record BladeContext(
            ISlashBladeState state,
            PropertiesDefinitionExtension properties
    ) {
    }

    private BladeSpecialEffectHelper() {
    }

    public static Optional<EffectEntry> findFirstSpecialEffect(ItemStack blade) {
        BladeContext context = resolveBladeContext(blade);
        if (context == null) {
            return Optional.empty();
        }

        for (ResourceLocation effectId : context.state().getSpecialEffects()) {
            if (!isSpecialExtendedEffect(effectId)) {
                continue;
            }
            int level = context.properties().getExtendedSpecialLevels(effectId);
            if (level > 0) {
                return Optional.of(new EffectEntry(effectId, level));
            }
        }
        return Optional.empty();
    }

    public static int countActiveNormalExtendedEffects(ItemStack blade) {
        BladeContext context = resolveBladeContext(blade);
        if (context == null) {
            return 0;
        }

        int count = 0;
        for (ResourceLocation effectId : context.state().getSpecialEffects()) {
            SpecialEffect effect = getSpecialEffect(effectId);
            if (!(effect instanceof ExtendedSpecialEffect extendedEffect) || extendedEffect.isSpecial()) {
                continue;
            }
            if (context.properties().getExtendedSpecialLevels(effectId) > 0) {
                count++;
            }
        }
        return count;
    }

    public static boolean isSpecialExtendedEffect(ResourceLocation effectId) {
        SpecialEffect effect = getSpecialEffect(effectId);
        return effect instanceof ExtendedSpecialEffect extendedEffect
                && extendedEffect.isSpecial();
    }

    public static void removeSpecialEffectsExcept(
            ISlashBladeState state,
            PropertiesDefinitionExtension properties,
            @Nullable ResourceLocation keep
    ) {
        List<ResourceLocation> removals = new ArrayList<>();
        for (ResourceLocation effectId : state.getSpecialEffects()) {
            if (effectId.equals(keep)) {
                continue;
            }
            if (isSpecialExtendedEffect(effectId)) {
                removals.add(effectId);
            }
        }

        for (ResourceLocation effectId : removals) {
            state.removeSpecialEffect(effectId);
            properties.setExtendedSpecialLevels(effectId, 0);
        }
    }

    @Nullable
    private static BladeContext resolveBladeContext(ItemStack blade) {
        if (!(blade.getItem() instanceof ItemSlashBlade)) {
            return null;
        }

        ISlashBladeState state = blade.getCapability(ItemSlashBlade.BLADESTATE).orElse(null);
        PropertiesDefinitionExtension properties = blade.getCapability(
                CapabilityRegistryHandler.PROPERTIES_DEFINITION_EXTENSION
        ).orElse(null);
        if (state == null || properties == null) {
            return null;
        }
        return new BladeContext(state, properties);
    }

    @Nullable
    private static SpecialEffect getSpecialEffect(ResourceLocation effectId) {
        return mods.flammpfeil.slashblade.registry.SpecialEffectsRegistry.REGISTRY.get()
                .getValue(effectId);
    }
}
