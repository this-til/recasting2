package com.til.recasting.generated;

import com.til.recasting.constant.SlashBladeDefinitions;
import mods.flammpfeil.slashblade.registry.slashblade.SlashBladeDefinition;
import net.minecraft.data.worldgen.BootstapContext;

public final class RecastingSlashBladeBuiltInRegistry {
    private RecastingSlashBladeBuiltInRegistry() {
    }

    public static void registerAll(BootstapContext<SlashBladeDefinition> bootstrap) {
        SlashBladeDefinitions.registerAll(bootstrap);
    }
}

