package com.til.recasting.constant;

import com.til.recasting.Recasting;
import mods.flammpfeil.slashblade.registry.slashblade.SlashBladeDefinition;
import net.minecraft.resources.ResourceKey;

/**
 * Recasting SlashBlade 定义的稳定注册 key（按需扩充）。
 */
public final class RecastingSlashBladeKeys {

    public static final ResourceKey<SlashBladeDefinition> DHARMA_STICK_LAMBDA = ResourceKey.create(
            SlashBladeDefinition.REGISTRY_KEY,
            Recasting.prefix("slashblade/dharma_stick_lambda")
    );

    private RecastingSlashBladeKeys() {
    }
}
