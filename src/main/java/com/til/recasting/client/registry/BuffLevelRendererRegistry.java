package com.til.recasting.client.registry;

import com.til.recasting.Recasting;
import com.til.recasting.client.registry.instance.BuffLevelRenderConfig;
import com.til.recasting.constant.RecastingLanguageKeys;
import com.til.recasting.registry.RecastingBuffTypes;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryBuilder;

/**
 * Buff层数渲染器注册表，用于标记哪些 BuffType 需要在实体名称上方显示层数。
 */
public final class BuffLevelRendererRegistry {

    public static final ResourceKey<Registry<BuffLevelRenderConfig>> BUFF_LEVEL_RENDER_CONFIG_REGISTRY_KEY =
            ResourceKey.createRegistryKey(Recasting.prefix("buff_level_render_config"));

    public static final DeferredRegister<BuffLevelRenderConfig> BUFF_LEVEL_RENDER_CONFIGS =
            DeferredRegister.create(BUFF_LEVEL_RENDER_CONFIG_REGISTRY_KEY, Recasting.MODID);

    public static final Registry<BuffLevelRenderConfig> REGISTRY =
            new RegistryBuilder<>(BUFF_LEVEL_RENDER_CONFIG_REGISTRY_KEY).sync(true).create();

    public static final DeferredHolder<BuffLevelRenderConfig, BuffLevelRenderConfig> SOUL_BURN = BUFF_LEVEL_RENDER_CONFIGS.register(
            "soul_burn",
            () -> new BuffLevelRenderConfig(RecastingBuffTypes.SOUL_BURN, RecastingLanguageKeys.BUFF_SOUL_BURN)
    );

    public static final DeferredHolder<BuffLevelRenderConfig, BuffLevelRenderConfig> FRAGMENT = BUFF_LEVEL_RENDER_CONFIGS.register(
            "sword_momentum",
            () -> new BuffLevelRenderConfig(RecastingBuffTypes.FRAGMENT, RecastingLanguageKeys.BUFF_FRAGMENT)
    );

    public static final DeferredHolder<BuffLevelRenderConfig, BuffLevelRenderConfig> IONIZATION = BUFF_LEVEL_RENDER_CONFIGS.register(
            "ionization",
            () -> new BuffLevelRenderConfig(RecastingBuffTypes.IONIZATION, RecastingLanguageKeys.BUFF_IONIZATION)
    );

    public static final DeferredHolder<BuffLevelRenderConfig, BuffLevelRenderConfig> ENERGY_STORAGE = BUFF_LEVEL_RENDER_CONFIGS.register(
            "energy_storage",
            () -> new BuffLevelRenderConfig(RecastingBuffTypes.ENERGY_STORAGE, RecastingLanguageKeys.BUFF_ENERGY_STORAGE)
    );

    public static final DeferredHolder<BuffLevelRenderConfig, BuffLevelRenderConfig> TEAR = BUFF_LEVEL_RENDER_CONFIGS.register(
            "tear",
            () -> new BuffLevelRenderConfig(RecastingBuffTypes.TEAR, RecastingLanguageKeys.BUFF_TEAR)
    );

    public static final DeferredHolder<BuffLevelRenderConfig, BuffLevelRenderConfig> PHOTON_SCAR = BUFF_LEVEL_RENDER_CONFIGS.register(
            "photon_scar",
            () -> new BuffLevelRenderConfig(RecastingBuffTypes.PHOTON_SCAR, RecastingLanguageKeys.BUFF_PHOTON_SCAR)
    );

    public static final DeferredHolder<BuffLevelRenderConfig, BuffLevelRenderConfig> PHOTON_BURN = BUFF_LEVEL_RENDER_CONFIGS.register(
            "photon_burn",
            () -> new BuffLevelRenderConfig(RecastingBuffTypes.PHOTON_BURN, RecastingLanguageKeys.BUFF_PHOTON_BURN)
    );

    public static final DeferredHolder<BuffLevelRenderConfig, BuffLevelRenderConfig> SUNSET_CORE = BUFF_LEVEL_RENDER_CONFIGS.register(
            "sunset_core",
            () -> new BuffLevelRenderConfig(RecastingBuffTypes.SUNSET_CORE, RecastingLanguageKeys.BUFF_SUNSET_CORE)
    );

    public static final DeferredHolder<BuffLevelRenderConfig, BuffLevelRenderConfig> SUNSET_STACK = BUFF_LEVEL_RENDER_CONFIGS.register(
            "sunset_stack",
            () -> new BuffLevelRenderConfig(RecastingBuffTypes.SUNSET_STACK, RecastingLanguageKeys.BUFF_SUNSET_STACK)
    );

    public static final DeferredHolder<BuffLevelRenderConfig, BuffLevelRenderConfig> GOLDEN_HALBERD = BUFF_LEVEL_RENDER_CONFIGS.register(
            "golden_halberd",
            () -> new BuffLevelRenderConfig(RecastingBuffTypes.GOLDEN_HALBERD, RecastingLanguageKeys.BUFF_GOLDEN_HALBERD)
    );

    public static final DeferredHolder<BuffLevelRenderConfig, BuffLevelRenderConfig> TEA_AROMA = BUFF_LEVEL_RENDER_CONFIGS.register(
            "tea_aroma",
            () -> new BuffLevelRenderConfig(RecastingBuffTypes.TEA_AROMA, RecastingLanguageKeys.BUFF_TEA_AROMA)
    );

    public static final DeferredHolder<BuffLevelRenderConfig, BuffLevelRenderConfig> JADE_FIRE = BUFF_LEVEL_RENDER_CONFIGS.register(
            "jade_fire",
            () -> new BuffLevelRenderConfig(RecastingBuffTypes.JADE_FIRE, RecastingLanguageKeys.BUFF_JADE_FIRE)
    );

    public static final DeferredHolder<BuffLevelRenderConfig, BuffLevelRenderConfig> ETERNAL_GUARD = BUFF_LEVEL_RENDER_CONFIGS.register(
            "eternal_guard",
            () -> new BuffLevelRenderConfig(RecastingBuffTypes.ETERNAL_GUARD, RecastingLanguageKeys.BUFF_ETERNAL_GUARD)
    );

    public static final DeferredHolder<BuffLevelRenderConfig, BuffLevelRenderConfig> MORTAL_DUST = BUFF_LEVEL_RENDER_CONFIGS.register(
            "mortal_dust",
            () -> new BuffLevelRenderConfig(RecastingBuffTypes.MORTAL_DUST, RecastingLanguageKeys.BUFF_MORTAL_DUST)
    );

    private BuffLevelRendererRegistry() {
    }
}
